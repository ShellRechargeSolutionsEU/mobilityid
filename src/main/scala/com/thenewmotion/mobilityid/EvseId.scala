package com.thenewmotion.mobilityid

import scala.util.matching.Regex

@SerialVersionUID(0)
trait EvseId {
  val countryCode: String
  val operatorId: String
  val powerOutletId: String

  protected val separator = "*"

  private val normalizedId =
    List(countryCode, operatorId, powerOutletId).mkString(separator)

  override def toString = normalizedId
}

private case class Error(priority: Int, desc: String)

trait EvseIdFormat[T <: EvseId] {
  def Description: String
  val CountryCode: Regex
  val OperatorCode: Regex
  val PowerOutletId: Regex
  val EvseIdRegex: Regex
  
  def apply(countryCode: String, operatorId: String, powerOutletId: String): T =
    validateAndCreate(countryCode.toUpperCase, operatorId.toUpperCase, powerOutletId.toUpperCase)

  private[mobilityid] def create(countryCode: String, operatorId: String, powerOutletId: String): T

  def apply(evseId: String): Option[T] = {
    evseId match {
      case EvseIdRegex(c, o, po) => Some(apply(c, o, po))
      case _ => None
    }
  }

  // _Could_ be done much nicer with scalaz disjunction, but I don't want to increase the size of the lib :)
  private[mobilityid] def validate(countryCode: String, operatorId: String, powerOutletId: String): Either[Error, T] = {
    CountryCode.unapplySeq(countryCode) match {
      case Some(_) =>
        OperatorCode.unapplySeq(operatorId) match {
          case Some(_) =>
            PowerOutletId.unapplySeq(powerOutletId) match {
              case Some(_) => Right(create(countryCode.toUpperCase, operatorId.toUpperCase, powerOutletId.toUpperCase))
              case _ => Left(Error(3, s"Invalid powerOutletId for $Description format"))
            }
          case _ => Left(Error(2, s"Invalid operatorId for $Description format"))
        }
      case _ => Left(Error(1, "Invalid countryCode for ISO or DIN format"))
    }
  }

  private[mobilityid] def validateAndCreate(countryCode: String, operatorId: String, powerOutletId: String): T =
    validate(countryCode, operatorId, powerOutletId) match {
      case Right(evseId) => evseId
      case Left(error) => throw new IllegalArgumentException(error.desc)
    }
}

object EvseId {
  def apply(countryCode: String, operatorId: String, powerOutletId: String): EvseId = {
    (EvseIdIso.validate(countryCode, operatorId, powerOutletId),
      EvseIdDin.validate(countryCode, operatorId, powerOutletId)) match {
      case (Right(evseId), _) => evseId
      case (_, Right(evseId)) => evseId
      case (Left(error1), Left(error2)) if error1.priority >= error2.priority =>
        throw new IllegalArgumentException(error1.desc)
      case (Left(error1), Left(error2)) if error2.priority > error1.priority =>
        throw new IllegalArgumentException(error2.desc)
    }
  }

  def apply(evseId: String): Option[EvseId] = {
    evseId match {
      case EvseIdIso.EvseIdRegex(c, o, po) => Some(EvseIdIso.create(c.toUpperCase, o.toUpperCase, po.toUpperCase))
      case EvseIdDin.EvseIdRegex(c, o, po) => Some(EvseIdDin.create(c.toUpperCase, o.toUpperCase, po.toUpperCase))
      case _ => None
    }
  }

  object AsEvseId {
    def unapply(evseId: String) = Some(EvseId(evseId))
  }

  def unapply(x: EvseId): Option[String] = Some(x.toString)
}

object EvseIdDin extends EvseIdFormat[EvseIdDin] {
  val Description = "DIN"
  val CountryCode = """\+?([0-9]{1,3})""".r
  val OperatorCode = """([0-9]{3,6})""".r
  val PowerOutletId = """([0-9\*]{1,32})""".r
  val EvseIdRegex = s"""$CountryCode\\*$OperatorCode\\*$PowerOutletId""".r

  private[mobilityid] override def create(countryCode: String, operatorId: String, powerOutletId: String): EvseIdDin = {
    val ccWithPlus = if (countryCode.startsWith("+")) countryCode else s"+$countryCode"
    EvseIdDinImpl(ccWithPlus, operatorId, powerOutletId)
  }
}

trait EvseIdDin extends EvseId

private case class EvseIdDinImpl(
  countryCode: String,
  operatorId: String,
  powerOutletId: String
) extends EvseIdDin

object EvseIdIso extends EvseIdFormat[EvseIdIso] {
  val Description = "ISO"
  val CountryCode = """([A-Za-z]{2})""".r
  val OperatorCode = """([A-Za-z0-9]{3})""".r
  val PowerOutletId = """(E[A-Za-z0-9\*]{1,30})""".r
  val EvseIdRegex = s"""$CountryCode\\*?$OperatorCode\\*?$PowerOutletId""".r

  private[mobilityid] override def create(countryCode: String, operatorId: String, powerOutletId: String): EvseIdIso = {
    val pId = PartyId(countryCode, operatorId)
    EvseIdIsoImpl(pId.countryCode, pId.id, powerOutletId)
  }
}

trait EvseIdIso extends EvseId {
  def toCompactString: String
}

private case class EvseIdIsoImpl (
  countryCode: String,
  operatorId: String,
  powerOutletId: String
) extends EvseIdIso {
  def toCompactString =
      countryCode + operatorId + powerOutletId.replace(separator, "")
}

