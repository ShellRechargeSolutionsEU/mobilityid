package com.thenewmotion.iso_id_utils.model

object Code {
  val separator = "-"
}

case class DinId(
   countryCode: String,
   providerId: String,
   instanceValue: String,
   checkDigit: String) {

  require(null != countryCode && countryCode.length == 2)
  require(null != providerId && providerId.length == 3)
  require(null != instanceValue && instanceValue.length == 6)
  require(null != checkDigit && checkDigit.length == 1)

  override def toString = normalizedId

  val normalizedId = countryCode + providerId + instanceValue + checkDigit

  val hyphenatedId = countryCode + Code.separator + providerId + Code.separator + instanceValue + Code.separator + checkDigit
}

case class EvcoId(
  countryCode: String,
  providerId: String,
  instanceValue: String,
  checkDigit: Option[String]) {

  require(null != countryCode && countryCode.length == 2)
  require(null != providerId && providerId.length == 3)
  require(null != instanceValue && instanceValue.length == 9)
  if (checkDigit.isDefined) require(checkDigit.get.length == 1)

  override def toString = normalizedId

  val normalizedId = countryCode + providerId + instanceValue + checkDigit.getOrElse("")

  val hyphenatedId = countryCode + Code.separator + providerId + Code.separator + instanceValue + checkDigit.fold("")(Code.separator + _)
}