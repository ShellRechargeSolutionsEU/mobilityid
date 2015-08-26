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

private trait EvseIdFormat {
  def Description: String
  def CountryCode: Regex
  def OperatorCode: Regex
  def PowerOutletId: Regex
  def EvseId: Regex
  def create(countryCode: String, operatorId: String, powerOutletId: String): EvseId

  // _Could_ be done much nicer with scalaz disjunction, but I don't want to increase the size of the lib :)
  def isValid(countryCode: String, operatorId: String, powerOutletId: String): Either[Error, EvseId] = {
    CountryCode.unapplySeq(countryCode) match {
      case Some(_) =>
        OperatorCode.unapplySeq(operatorId) match {
          case Some(_) =>
            PowerOutletId.unapplySeq(powerOutletId) match {
              case Some(_) => Right(create(countryCode, operatorId, powerOutletId))
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
      case EvseIdIso.EvseId(c, o, po) => Some(EvseIdIso.create(c, o, po))
      case EvseIdDin.EvseId(c, o, po) => Some(EvseIdDin.create(c, o, po))
      case _ => None
    }
  }

  object AsEvseId {
    def unapply(evseId: String) = Some(EvseId(evseId))
  }

  def unapply(x: EvseId): Option[String] = Some(x.toString)
}

private object EvseIdDin extends EvseIdFormat {
  val Description = "DIN"
  val CountryCode = """\+?([0-9]{1,3})""".r
  val OperatorCode = """([0-9]{3,6})""".r
  val PowerOutletId = """([0-9\*]{1,32})""".r
  val EvseId = s"""$CountryCode\\*$OperatorCode\\*$PowerOutletId""".r
  def create(countryCode: String, operatorId: String, powerOutletId: String) =
    new EvseIdDin(countryCode, operatorId, powerOutletId)
}

case class EvseIdDin private(
  countryCode: String,
  operatorId: String,
  powerOutletId: String
) extends EvseId

private object EvseIdIso extends EvseIdFormat {
  val Description = "ISO"
  val CountryCode = """([A-Za-z]{2})""".r
  val OperatorCode = """([A-Za-z0-9]{3})""".r
  val PowerOutletId = """(E[A-Za-z0-9\*]{1,30})""".r
  val EvseId = s"""$CountryCode\\*?$OperatorCode\\*?$PowerOutletId""".r
  def create(countryCode: String, operatorId: String, powerOutletId: String) =
    new EvseIdIso(countryCode.toUpperCase, operatorId.toUpperCase, powerOutletId.toUpperCase)
}

case class EvseIdIso private(
  countryCode: String,
  operatorId: String,
  powerOutletId: String
) extends EvseId {
  def toCompactString =
      countryCode + operatorId + powerOutletId.replace(separator, "")
}

