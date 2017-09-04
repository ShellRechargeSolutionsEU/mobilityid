package com.thenewmotion.mobilityid

import ContractIdStandard._
import ContractIdParser.DINParser

import scala.annotation.implicitNotFound
import scala.util.Try
import scala.util.matching.Regex

sealed trait ContractIdStandard

object ContractIdStandard {
  trait EMI3 extends ContractIdStandard
  trait ISO extends ContractIdStandard
  trait DIN extends ContractIdStandard
}

sealed trait ContractIdParser[T <: ContractIdStandard] {
  def InstanceRegex: Regex
  def FullRegex: Regex
  def name: String
  def computeCheckDigit(s: String): Char

  def validateInstanceValue(s: String): Unit = {
    val r = InstanceRegex
    s match {
      case r(_) =>
      case _ => throw new IllegalArgumentException(s"$s is not a valid instance value for $name format")
    }
  }
}

object ContractIdParser {

  private val CheckDigitRegex = "([A-Za-z0-9])".r

  implicit object ISOParser extends ContractIdParser[ISO] {
    override val InstanceRegex: Regex = "([A-Za-z0-9]{9})".r

    override val FullRegex: Regex =
      ("^" +
        CountryCode.Regex +
        "(?:-?)" +
        PartyCode.Regex +
        "(?:-?)" +
        InstanceRegex +
        "(?:(?:-?)" +
        CheckDigitRegex +
        ")?$").r

    override val name = "ISO 15118-1"

    override def computeCheckDigit(s: String): Char = CheckDigitIso(s)
  }

  implicit object EMI3Parser extends ContractIdParser[EMI3] {
    override val InstanceRegex: Regex = "([Cc][A-Za-z0-9]{8})".r

    override val FullRegex: Regex =
      ("^" +
        CountryCode.Regex +
        "(?:-?)" +
        PartyCode.Regex +
        "(?:-?)" +
        InstanceRegex +
        "(?:(?:-?)" +
        CheckDigitRegex +
        ")?$").r

    override val name = "EMI3"

    override def computeCheckDigit(s: String): Char = CheckDigitIso(s)
  }

  implicit object DINParser extends ContractIdParser[DIN] {
    override val InstanceRegex: Regex = "([A-Za-z0-9]{6})".r

    override val FullRegex: Regex =
      ("^" +
        CountryCode.Regex +
        "(?:[*-]?)" +
        PartyCode.Regex +
        "(?:[*-]?)" +
        InstanceRegex +
        "(?:(?:[*-]?)" +
        CheckDigitRegex +
        ")?$").r

    override val name = "DIN SPEC 91286"

    override def computeCheckDigit(s: String): Char = CheckDigitDin(s)
  }
}

@implicitNotFound(msg="It is not possible to convert ${B} to ${A}")
sealed trait ContractIdConverter[B <: ContractIdStandard, A <: ContractIdStandard]
  extends (ContractId[B] => ContractId[A])

object ContractIdConverter {
  implicit object DINtoEMI3 extends ContractIdConverter[DIN, EMI3] {
    override def apply(b: ContractId[DIN]): ContractId[EMI3] =
      ContractId[EMI3](b.countryCode, b.providerId, "C0" + b.instanceValue + b.checkDigit, None)
  }

  implicit object EMI3toDIN extends ContractIdConverter[EMI3, DIN] {
    override def apply(b: ContractId[EMI3]): ContractId[DIN] =
      if (b.instanceValue.startsWith("C0")) {
        val dinInstance = b.instanceValue.substring(2, 8)
        val dinCheck = b.instanceValue.substring(8, 9).charAt(0)
        ContractId[DIN](b.countryCode, b.providerId, dinInstance, Some(dinCheck))
      } else throw new IllegalStateException(s"$b cannot be converted to ${DINParser.name} format")
  }

  @deprecated("Use EMI3 instead of ISO format when converting to DIN", "0.18.0")
  implicit object ISOToDIN extends ContractIdConverter[ISO, DIN] {
    override def apply(b: ContractId[ISO]): ContractId[DIN] =
      if (b.instanceValue startsWith "00") {
        val dinInstance = b.instanceValue.substring(2, 8)
        val dinCheck = b.instanceValue.substring(8, 9).charAt(0)
        ContractId[DIN](b.countryCode, b.providerId, dinInstance, Some(dinCheck))
      } else throw new IllegalStateException(s"$b cannot be converted to ${DINParser.name} format")
  }

  @deprecated("Use EMI3 instead of ISO format when converting from DIN", "0.18.0")
  implicit object DINtoISO extends ContractIdConverter[DIN, ISO] {
    override def apply(b: ContractId[DIN]): ContractId[ISO] =
      ContractId[ISO](b.countryCode, b.providerId, "00" + b.instanceValue + b.checkDigit, None)
  }

  implicit object EMI3ToISO extends ContractIdConverter[EMI3, ISO] {
    override def apply(b: ContractId[EMI3]): ContractId[ISO] =
      ContractId[ISO](b.countryCode, b.providerId, b.instanceValue, Some(b.checkDigit))
  }
}

sealed trait ContractId[T <: ContractIdStandard] {
  def countryCode: CountryCode
  def providerId: ProviderId
  def instanceValue: String
  def checkDigit: Char

  private val normalizedId =
    List(countryCode, providerId, instanceValue, checkDigit).mkString(ContractId.separator)

  override def toString: String = normalizedId

  def toCompactString: String = toCompactStringWithoutCheckDigit + checkDigit
  def toCompactStringWithoutCheckDigit: String = countryCode.toString + providerId.toString + instanceValue

  def partyId: PartyId = PartyId(countryCode, providerId)

  def convertTo[A <: ContractIdStandard](implicit converter: ContractIdConverter[T, A]) = converter(this)
}

object ContractId {

  private val separator = "-"

  private[this] case class ContractIdImpl[T <: ContractIdStandard](
    countryCode: CountryCode,
    providerId: ProviderId,
    instanceValue: String,
    checkDigit: Char
  ) extends ContractId[T]

  private[this] def applyToUpperCase[T <: ContractIdStandard](
    cc: CountryCode,
    providerId: ProviderId,
    instanceValue: String,
    checkDigit: Option[Char]
  )(implicit p: ContractIdParser[T]): ContractId[T] = {
    p.validateInstanceValue(instanceValue)

    val computedCheckDigit = p.computeCheckDigit(cc + providerId.id + instanceValue)
    checkDigit.foreach {c =>
      if (c != computedCheckDigit) throw
        new IllegalArgumentException(s"Given check digit '$c' is not equal to computed '$computedCheckDigit'")
    }

    ContractIdImpl[T](cc, providerId, instanceValue, computedCheckDigit)
  }

  private[mobilityid] def apply[T <: ContractIdStandard](
    countryCode: CountryCode,
    providerId: ProviderId,
    instanceValue: String,
    checkDigit: Option[Char]
  )(implicit p: ContractIdParser[T]): ContractId[T] =
    applyToUpperCase(countryCode, providerId, instanceValue.toUpperCase, checkDigit.map(_.toUpper))

  def apply[T <: ContractIdStandard](
    countryCode: String,
    providerId: String,
    instanceValue: String
  )(implicit p: ContractIdParser[T]): ContractId[T] =
    apply(CountryCode(countryCode), ProviderId(providerId), instanceValue, None)

  def apply[T <: ContractIdStandard](
    countryCode: String,
    providerId: String,
    instanceValue: String,
    checkDigit: Char
  )(implicit p: ContractIdParser[T]): ContractId[T] =
    apply(CountryCode(countryCode), ProviderId(providerId), instanceValue, Some(checkDigit))

  def apply[T <: ContractIdStandard](s: String)(implicit p: ContractIdParser[T]): ContractId[T] = {
    val Matcher = p.FullRegex

    s match {
      case Matcher(country, prov, instance, check) =>
        if (check != null) {
          require(check.length == 1, "check length must equal 1")
          apply(country, prov, instance, check.head)
        } else
          apply(country, prov, instance)
      case x => throw new IllegalArgumentException(s"$x is not a valid Contract Id for ${p.name}")
    }
  }

  def opt[T <: ContractIdStandard](s: String)(implicit p: ContractIdParser[T]): Option[ContractId[T]] =
    Try(apply[T](s)).toOption

  def unapply(c: ContractId[_]): Option[(CountryCode, ProviderId, String, Char)] =
    Some((c.countryCode, c.providerId, c.instanceValue, c.checkDigit))

}
