package com.thenewmotion.mobilityid

import java.util.Locale

sealed trait ProviderId {
  def id: String
}

private case class ProviderIdImpl(partyCode: PartyCode) extends ProviderId {
  override def toString = partyCode.toString
  def id = partyCode.id
}

object ProviderId {
  def isValid(id: String): Boolean = PartyCode.isValid(id)

  def apply(id: String): ProviderId = ProviderIdImpl(PartyCode(id))
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

/**
 * A party ID is the part of an EMA-ID or ISO-style EVSE-ID that identifies the provider or operator. It is the
 * combination of a country code and a party code. Examples are "NL-TNM" or "DE*AW8".
 */
sealed trait PartyId {
  def countryCode: CountryCode
  def partyCode: String
}

private case class PartyIdImpl(countryCode: CountryCode, party: PartyCode) extends PartyId {

  override def toString = countryCode.toString + partyCode.toString

  def partyCode = party.toString
}

object PartyId {
  def apply(partyId: String): Option[PartyId] = {
    val withoutSeparator = partyId.filterNot(separators.contains)
    if (withoutSeparator.length != 5)
      None
    else try {
      val cc = withoutSeparator.take(2)
      val providerOrOperator = withoutSeparator.drop(2)
      Some(PartyIdImpl(CountryCode(cc), PartyCode(providerOrOperator)))
    } catch {
      case e: IllegalArgumentException => None
    }
  }

  def apply(countryCode: CountryCode, providerId: ProviderId): PartyId = providerId match {
    case ProviderIdImpl(partyCode) => PartyIdImpl(countryCode, partyCode)
  }

  def apply(countryCode: CountryCode, providerId: OperatorIdIso): PartyId = providerId match {
    case OperatorIdIsoImpl(partyCode) => PartyIdImpl(countryCode, partyCode)
  }

  // TODO isn't this defined in mobilityid somewhere already?
  private val separators = Set('*', '-')
}

/**
 * A party code is the three-letter identifier for parties in the EV market, like "TNM" for NewMotion
 */
private sealed trait PartyCode {
  def id: String
}

private case class PartyCodeImpl(id: String) extends PartyCode {
  override def toString = id
}

private object PartyCode {

  val Regex = """([A-Za-z0-9]{3})""".r

  def isValid(id: String): Boolean = id match {
    case Regex(_) => true
    case _ => false
  }

  def apply(id: String): PartyCode =
    if (isValid(id)) {
      PartyCodeImpl(id.toUpperCase)
    } else throw new IllegalArgumentException(
      "OperatorId must have a length of 3 and be ASCII letters or digits")
}
