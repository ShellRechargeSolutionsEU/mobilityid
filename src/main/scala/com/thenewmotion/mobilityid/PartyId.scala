package com.thenewmotion.mobilityid

import java.util.Locale

/**
  * An generic EV market party ID, derived from the EMA-ID and EVSE-ID as specified in ISO 15118-1.
  *
  * @param countryCode A two-letter country code according to ISO 3166-1
  * @param id A three-digit identifier of the organisation
  * @throws IllegalArgumentException If any of the fields are of the wrong length or contain illegal characters
  */
@SerialVersionUID(0)
abstract case class PartyId private (
  countryCode: String,
  id: String )

object PartyId {

  lazy val isoCountries = Locale.getISOCountries

  def apply(countryCode: String, id: String): PartyId =
    applyToUpperCase(countryCode.toUpperCase, id.toUpperCase)

  private[this] def applyToUpperCase(countryCode: String, id: String) = {
    require(countryCode.length == 2 && countryCode.forall(_.isAsciiUpper),
      s"Country Code must have a length of 2 and be ASCII letters. (Was: $countryCode)")
    require(isoCountries.contains(countryCode),
      "Country Code must be valid according to ISO 3166-1 alpha-2")
    require(id.length == 3 && id.forall(_.isAsciiUpperOrDigit),
      "PartyId must have a length of 3 and be ASCII letters or digits")
    new PartyId(countryCode, id) {}
  }
}
