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
  
  def apply(countryCode: String, operatorId: String, powerOutletId: String): T
  def apply(evseId: String): Option[T] = {
    evseId match {
      case EvseIdRegex(c, o, po) => Some(apply(c, o, po))
      case _ => None
    }
  }

  // _Could_ be done much nicer with scalaz disjunction, but I don't want to increase the size of the lib :)
  private[mobilityid] def isValid(countryCode: String, operatorId: String, powerOutletId: String): Either[Error, EvseId] = {
    CountryCode.unapplySeq(countryCode) match {
      case Some(_) =>
        OperatorCode.unapplySeq(operatorId) match {
          case Some(_) =>
            PowerOutletId.unapplySeq(powerOutletId) match {
              case Some(_) => Right(apply(countryCode, operatorId, powerOutletId))
              case _ => Left(Error(3, s"Invalid powerOutletId for $Description format"))
            }
          case _ => Left(Error(2, s"Invalid operatorId for $Description format"))
        }
      case _ => Left(Error(1, "Invalid countryCode for ISO or DIN format"))
    }
  }
}

object EvseId {
  def apply(countryCode: String, operatorId: String, powerOutletId: String): EvseId = {
    (EvseIdIso.isValid(countryCode, operatorId, powerOutletId),
      EvseIdDin.isValid(countryCode, operatorId, powerOutletId)) match {
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
      case EvseIdIso.EvseIdRegex(c, o, po) => Some(EvseIdIso.apply(c, o, po))
      case EvseIdDin.EvseIdRegex(c, o, po) => Some(EvseIdDin.apply(c, o, po))
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
  def apply(countryCode: String, operatorId: String, powerOutletId: String): EvseIdDin =
    new EvseIdDinImpl(countryCode, operatorId, powerOutletId)
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
  def apply(countryCode: String, operatorId: String, powerOutletId: String): EvseIdIso =
    new EvseIdIsoImpl(countryCode.toUpperCase, operatorId.toUpperCase, powerOutletId.toUpperCase)
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

