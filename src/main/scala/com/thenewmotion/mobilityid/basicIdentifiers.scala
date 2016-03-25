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

  def apply(id: String): OperatorId = {
    require(id.length == 3 && id.forall(_.isAsciiLetterOrDigit),
      "OperatorId must have a length of 3 and be ASCII letters or digits")
    new OperatorId(id.toUpperCase){}
  }
}

trait CountryId

abstract case class CountryCode private (private val cc: String ) extends CountryId {
  override def toString = cc
}

object CountryCode {

  lazy val isoCountries = Locale.getISOCountries

  def apply(countryCode: String): CountryCode = {
    require(countryCode.length == 2 && countryCode.forall(_.isAsciiLetter),
      s"Country Code must have a length of 2 and be ASCII letters. (Was: $countryCode)")
    require(isoCountries.contains(countryCode.toUpperCase),
      "Country Code must be valid according to ISO 3166-1 alpha-2")
    new CountryCode(countryCode.toUpperCase){}
  }
}

abstract case class PhoneCountryCode private (private val cc: String ) extends CountryId {
  override def toString = cc
}

object PhoneCountryCode {

  def apply(countryCode: String): PhoneCountryCode = {
    require(countryCode.startsWith("+") && countryCode.length == 3 && countryCode.substring(1).forall(_.isAsciiDigit),
      s"phone Country Code must start with a '+' sign and be followed by 2 digits. (Was: $countryCode)")
    new PhoneCountryCode(countryCode.toUpperCase){}
  }
}
