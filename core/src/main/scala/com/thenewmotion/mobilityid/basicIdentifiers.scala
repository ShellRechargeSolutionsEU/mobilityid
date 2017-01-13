package com.thenewmotion.mobilityid

import java.util.Locale

sealed trait ProviderId {
  def id: String
}

private case class ProviderIdImpl(id: String) extends ProviderId {
  override def toString = id
}

object ProviderId {
  val Regex = """([A-Za-z0-9]{3})""".r

  def isValid(id: String): Boolean = id match {
    case Regex(_) => true
    case _ => false
  }

  def apply(id: String): ProviderId =
    if (isValid(id)) {
      ProviderIdImpl(id.toUpperCase)
    } else throw new IllegalArgumentException(
      "OperatorId must have a length of 3 and be ASCII letters or digits")

}

sealed trait CountryId {
  def cc: String
}

sealed trait CountryCode extends CountryId

private case class CountryCodeImpl(cc: String) extends CountryCode {
  override def toString = cc
}

object CountryCode {
  val Regex = """([A-Za-z]{2})""".r

  lazy val isoCountries = Locale.getISOCountries

  def isValid(countryCode: String): Boolean = countryCode match {
    case Regex(_) if isoCountries.contains(countryCode.toUpperCase) => true
    case _ => false
  }

  def apply(countryCode: String): CountryCode =
    if (isValid(countryCode)) {
      CountryCodeImpl(countryCode.toUpperCase)
    } else throw new IllegalArgumentException(
      "Country Code must be valid according to ISO 3166-1 alpha-2")

}

sealed trait PhoneCountryCode extends CountryId

private case class PhoneCountryCodeImpl(cc: String) extends PhoneCountryCode {
  override def toString = cc
}

object PhoneCountryCode {
  val Regex = """\+?([0-9]{1,3})""".r

  def isValid(id: String): Boolean = id match {
    case Regex(_) => true
    case _ => false
  }

  def apply(countryCode: String): PhoneCountryCode =
    if (isValid(countryCode)) {
      PhoneCountryCodeImpl(countryCode.toUpperCase)
    } else throw new IllegalArgumentException(
      s"phone Country Code must start with a '+' sign and be followed by 1-3 digits. (Was: $countryCode)")

}
