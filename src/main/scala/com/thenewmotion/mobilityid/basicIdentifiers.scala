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

  def apply(id: String): ProviderId = id match {
    case Regex(_) => ProviderIdImpl(id.toUpperCase)
    case _ => throw new IllegalArgumentException(
      "OperatorId must have a length of 3 and be ASCII letters or digits")
  }
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

  def apply(countryCode: String): CountryCode = countryCode match {
    case Regex(_) if isoCountries.contains(countryCode.toUpperCase) => CountryCodeImpl(countryCode.toUpperCase)
    case _ => throw new IllegalArgumentException(
      "Country Code must be valid according to ISO 3166-1 alpha-2")
  }
}

sealed trait PhoneCountryCode extends CountryId

private case class PhoneCountryCodeImpl(cc: String) extends PhoneCountryCode {
  override def toString = cc
}

object PhoneCountryCode {
  val Regex = """\+?([0-9]{1,3})""".r

  def apply(countryCode: String): PhoneCountryCode = countryCode match {
    case Regex(_) => PhoneCountryCodeImpl(countryCode.toUpperCase)
    case _ => throw new IllegalArgumentException(
      s"phone Country Code must start with a '+' sign and be followed by 1-3 digits. (Was: $countryCode)")
  }
}
