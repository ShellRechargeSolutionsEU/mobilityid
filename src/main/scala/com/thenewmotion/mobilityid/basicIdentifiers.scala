package com.thenewmotion.mobilityid

import java.util.Locale

abstract case class ProviderId private (private val id: String ) {
  override def toString = id
}

object ProviderId {
  def apply(id: String): ProviderId = {
    require(id.length == 3 && id.forall(_.isAsciiLetterOrDigit),
      "ProviderId must have a length of 3 and be ASCII letters or digits")
    new ProviderId(id.toUpperCase){}
  }
}


abstract case class OperatorId private (private val id: String ) {
  override def toString = id
}

object OperatorId {
  val Regex = """([A-Za-z0-9]{3})""".r

  def apply(id: String): OperatorId = {
    if (Regex.unapplySeq(id).isDefined) new OperatorId(id.toUpperCase){}
    else throw new IllegalArgumentException(
      "OperatorId must have a length of 3 and be ASCII letters or digits")

  }
}

trait CountryId

abstract case class CountryCode private (private val cc: String ) extends CountryId {
  override def toString = cc
}

object CountryCode {
  val Regex = """([A-Za-z]{2})""".r

  lazy val isoCountries = Locale.getISOCountries

  def apply(countryCode: String): CountryCode = {
    if (Regex.unapplySeq(countryCode).isDefined && isoCountries.contains(countryCode.toUpperCase))
      new CountryCode(countryCode.toUpperCase){}
    else
      throw new IllegalArgumentException("Country Code must be valid according to ISO 3166-1 alpha-2")
  }
}

abstract case class PhoneCountryCode private (private val cc: String ) extends CountryId {
  override def toString = cc
}

object PhoneCountryCode {
  val Regex = """\+?([0-9]{1,3})""".r

  def apply(countryCode: String): PhoneCountryCode = {
    if (Regex.unapplySeq(countryCode).isDefined)
      new PhoneCountryCode(countryCode.toUpperCase){}
    else
      throw new IllegalArgumentException(
        s"phone Country Code must start with a '+' sign and be followed by 1-3 digits. (Was: $countryCode)")
  }
}
