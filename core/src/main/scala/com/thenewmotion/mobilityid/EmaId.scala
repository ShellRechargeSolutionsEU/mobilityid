package com.thenewmotion.mobilityid

/**
 * An EMA-ID (electric mobility account identifier) as specified in ISO 15118-1.
 *
 * These are identifier strings for accounts that users of electric vehicles have to pay for the electricity they use
 * when charging their vehicles.
 *
 * @param countryCode A two-letter country code according to ISO 3166-1
 * @param providerId A three-digit identifier of the provider of this account
 * @param instanceValue An identifier of the account, unique for the provider
 * @param checkDigit A check digit
 */
// the constructor is private so we don't have a user-supplied check digit to check in it
@SerialVersionUID(0)
case class EmaId private (
  countryCode: CountryCode,
  providerId: ProviderId,
  instanceValue: String,
  checkDigit: Character) {

  import com.thenewmotion.mobilityid.EmaId._

  require(instanceValue.length == 9 && instanceValue.forall(_.isAsciiUpperOrDigit),
    "Instance Value must have a length of 9 and be ASCII letters or digits")

  private val normalizedId =
    List(countryCode, providerId, instanceValue, checkDigit).mkString(separator)

  override def toString = normalizedId

  def toCompactString = toCompactStringWithoutCheckDigit + checkDigit
  def toCompactStringWithoutCheckDigit = countryCode.toString + providerId.toString + instanceValue

  def toDinString =
    if (instanceValue startsWith "00") {
      val dinInstance = instanceValue.substring(2, 8)
      // XXX not checking DIN check digit
      val dinCheck = instanceValue.substring(8, 9)
      Some(List(countryCode, providerId, dinInstance, dinCheck).mkString(separator))
    } else None
}

object EmaId {
  private val separator = "-"

  private val RegexForEvcoId =
    "^([A-Za-z]{2})(?:-?)([A-Za-z0-9]{3})(?:-?)([A-Za-z0-9]{9})(?:(?:-?)([A-Za-z0-9]))?$".r

  private val RegexForDinId  =
    "^([A-Za-z]{2})(?:[*-]?)([A-Za-z0-9]{3})(?:[*-]?)([A-Za-z0-9]{6})(?:(?:[*-]?)([A-Za-z0-9]))?$".r

  /**
   * Create an EmaId with the given field values
   *
   * @param countryCode
   * @param providerId
   * @param instanceValue
   * @return The EmaId object
   * @throws IllegalArgumentException If any of the fields are of the wrong length or contain illegal characters
   */
  def apply(countryCode: String, providerId: String, instanceValue: String): EmaId = {
    applyToUpperCase(countryCode, providerId, instanceValue.toUpperCase)
  }

  private[this] def applyToUpperCase(cc: String, providerId: String, instanceValue: String) = {
    val checkDigit = CheckDigit(cc + providerId + instanceValue)
    new EmaId(CountryCode(cc), ProviderId(providerId), instanceValue, checkDigit)
  }

  /**
   * Create an EmaId with the given field values
   *
   * @param countryCode
   * @param providerId
   * @param instanceValue
   * @return The EmaId object
   * @throws IllegalArgumentException If any of the fields are of the wrong length or contain illegal characters or if
   *                                  the given check digit is incorrect
   */
  def apply(countryCode: String, providerId: String, instanceValue: String, checkDigit: Char): EmaId = {
    applyToUpperCase(countryCode, providerId, instanceValue.toUpperCase, Character.toUpperCase(checkDigit))
  }

  private[this] def applyToUpperCase(cc: String, providerId: String, instanceValue: String, checkDigit: Char): EmaId = {
    val computedCheckDigit = CheckDigit(cc + providerId + instanceValue)
    require(
      computedCheckDigit == checkDigit,
      s"Given check digit '$checkDigit' is not equal to computed '$computedCheckDigit'"
    )

    new EmaId(CountryCode(cc), ProviderId(providerId), instanceValue, checkDigit)
  }

  /**
   * Create an EMAID from a string representation
   *
   * @param emaId An EMAID in DIN SPEC 91286 or ISO 15118 format, with or without dashes and with or without check digit
   *              For a DIN SPEC 91286 ID the check digit is required.
   * @return An EmaId object representing the same EMAID as the input string, or None if the given string is invalid
   *         (ie wrong length, illegal characters or wrong check digit)
   */
  def apply(emaId: String): Option[EmaId] = {
    val matchWithRegex: PartialFunction[String, EmaId] = {
      case RegexForEvcoId(country, prov, instance, check) =>
        if (check != null) {
          require(check.length == 1, "check length must equal 1")
          apply(country, prov, instance, check.head)
        } else
          apply(country, prov, instance)
      case RegexForDinId(country, prov, instance, check) if check != null =>
        apply(country, prov, dinInstanceToIsoInstance(instance, check.toCharArray.head))
    }

    try {
      matchWithRegex.lift(emaId)
    } catch {
      case e: IllegalArgumentException =>
        None
    }
  }

  private def dinInstanceToIsoInstance(dinInstance: String, dinCheck: Char): String = {
    require(dinInstance.length == 6, "DIN instance must be 6 chars")
    "00" + dinInstance + dinCheck
  }
}
