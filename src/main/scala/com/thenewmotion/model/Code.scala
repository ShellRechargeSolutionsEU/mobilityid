package com.thenewmotion.model

import scalaz.syntax.equal._
import scalaz.std.string._
import scalaz.std.anyVal._

object Code {
  val separator = "-"
}

case class DinId(
   countryCode: String,
   providerId: String,
   instanceValue: String,
   checkDigit: Option[String]) {

  assert(null != countryCode && countryCode.length === 2)
  assert(null != providerId && providerId.length === 3)
  assert(null != instanceValue && instanceValue.length === 6)
  if (checkDigit.isDefined) assert(checkDigit.get.length === 1)

  val normalizedId = countryCode + providerId + instanceValue + checkDigit.getOrElse("")

  val hyphenatedId = countryCode + Code.separator + providerId + Code.separator + instanceValue + checkDigit.fold("")(Code.separator + _)
}

case class EvcoId(
  countryCode: String,
  providerId: String,
  instanceValue: String,
  checkDigit: Option[String]) {

    assert(null != countryCode && countryCode.length === 2)
    assert(null != providerId && providerId.length === 3)
    assert(null != instanceValue && instanceValue.length === 9)
    if (checkDigit.isDefined) assert(checkDigit.get.length === 1)
  
  val normalizedId = countryCode + providerId + instanceValue + checkDigit.getOrElse("")

  val hyphenatedId = countryCode + Code.separator + providerId + Code.separator + instanceValue + checkDigit.fold("")(Code.separator + _)
}