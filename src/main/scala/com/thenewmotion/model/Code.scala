package com.thenewmotion.model

import scalaz.syntax.equal._
import scalaz.std.string._
import scalaz.std.option._
import scalaz.std.anyVal._

object Code {
  val separator = "-"
}

class DinId private(
   val countryCode: String,
   val providerId: String,
   val instanceValue: String,
   val checkDigit: String) {

  override def toString = normalizedId

  val normalizedId = countryCode + providerId + instanceValue + checkDigit

  val hyphenatedId = countryCode + Code.separator + providerId + Code.separator + instanceValue + Code.separator + checkDigit

  override def equals(other: Any): Boolean = other match {
    case that: DinId =>
      that.isInstanceOf[DinId] &&
        normalizedId === that.normalizedId &&
        hyphenatedId === that.hyphenatedId &&
        countryCode === that.countryCode &&
        providerId === that.providerId &&
        instanceValue === that.instanceValue &&
        checkDigit === that.checkDigit
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(normalizedId, hyphenatedId, countryCode, providerId, instanceValue, checkDigit)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

object DinId {
  def apply(countryCode: String, providerId: String, instanceValue: String, checkDigit: String): DinId = {
    assert(null != countryCode && countryCode.length === 2)
    assert(null != providerId && providerId.length === 3)
    assert(null != instanceValue && instanceValue.length === 6)
    assert(null != checkDigit && checkDigit.length === 1)

    new DinId(countryCode = countryCode.toUpperCase,
      providerId = providerId.toUpperCase,
      instanceValue = instanceValue.toUpperCase,
      checkDigit = checkDigit.toUpperCase
    )
  }
}

class EvcoId private (
  val countryCode: String,
  val providerId: String,
  val instanceValue: String,
  val checkDigit: Option[String]) {

  override def toString = normalizedId

  val normalizedId = countryCode + providerId + instanceValue + checkDigit.getOrElse("")

  val hyphenatedId = countryCode + Code.separator + providerId + Code.separator + instanceValue + checkDigit.fold("")(Code.separator + _)

  override def equals(other: Any): Boolean = other match {
    case that: EvcoId =>
      that.isInstanceOf[EvcoId] &&
        normalizedId === that.normalizedId &&
        hyphenatedId === that.hyphenatedId &&
        countryCode === that.countryCode &&
        providerId === that.providerId &&
        instanceValue === that.instanceValue &&
        checkDigit === that.checkDigit
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(normalizedId, hyphenatedId, countryCode, providerId, instanceValue, checkDigit)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

object EvcoId {
  def apply(countryCode: String, providerId: String, instanceValue: String, checkDigit: Option[String]): EvcoId = {

    assert(null != countryCode && countryCode.length === 2)
    assert(null != providerId && providerId.length === 3)
    assert(null != instanceValue && instanceValue.length === 9)
    if (checkDigit.isDefined) assert(checkDigit.get.length === 1)

    new EvcoId(countryCode = countryCode.toUpperCase,
               providerId = providerId.toUpperCase,
               instanceValue = instanceValue.toUpperCase,
               checkDigit = checkDigit.map(_.toUpperCase)
    )
  }
}