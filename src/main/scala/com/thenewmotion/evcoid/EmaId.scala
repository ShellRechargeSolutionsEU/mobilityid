package com.thenewmotion.evcoid

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
class EmaId private (
  val countryCode: String,
  val providerId: String,
  val instanceValue: String,
  val checkDigit: Character) {

  import com.thenewmotion.evcoid.EmaId._

  require(countryCode.size == 2 && countryCode.forall(_.isLetter))
  require(providerId.size == 3 && providerId.forall(_.isLetterOrDigit))
  require(instanceValue.size == 9 && instanceValue.forall(_.isLetterOrDigit))

  override def toString = normalizedId

  override def equals(o: Any) = o match {
    case otherId: EmaId =>
      otherId.countryCode == countryCode && otherId.providerId == providerId && otherId.instanceValue == instanceValue
    case _ => false
  }

  override def hashCode = toString.hashCode

  private val normalizedId = List(countryCode, providerId, instanceValue, checkDigit).mkString(separator)

  def toCompactString = toCompactStringWithoutCheckDigit + checkDigit
  def toCompactStringWithoutCheckDigit = countryCode + providerId + instanceValue

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

  private val RegexForEvcoId = "^([A-Za-z]{2})(?:-?)([A-Za-z0-9]{3})(?:-?)([A-Za-z0-9]{9})(?:(?:-?)([A-Za-z0-9]))?$".r

  private val RegexForDinId  = "^([A-Za-z]{2})(?:-?)([A-Za-z0-9]{3})(?:-?)([A-Za-z0-9]{6})(?:(?:-?)([A-Za-z0-9]))?$".r

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
    // TODO: move this rearrangement to check digit calculator
    val checkDigit = CheckDigit(countryCode + providerId + instanceValue)
    new EmaId(countryCode, providerId, instanceValue, checkDigit)
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
  def apply(countryCode: String, providerId: String, instanceValue: String, checkDigit: Character): EmaId = {
    val computedCheckDigit = CheckDigit(countryCode + providerId + instanceValue)
    require(computedCheckDigit == checkDigit)

    new EmaId(countryCode, providerId, instanceValue, checkDigit)
  }

  /**
   * Create an EMAID from a string representation
   *
   * @param emaId An EMAID in DIN SPEC 91286 or ISO 15118 format, with or without dashes and with or without check digit
   * @return An EmaId object representing the same EMAID as the input string, or None if the given string is invalid
   *         (ie wrong length, illegal characters or wrong check digit)
   */
  def apply(emaId: String): Option[EmaId] = {
    val matchWithRegex: PartialFunction[String, EmaId] = {
      case RegexForEvcoId(country, prov, instance, check) =>
        if (check != null) {
          require(check.size == 1)
          apply(country, prov, instance, check.head)
        } else
          apply(country, prov, instance)
      case RegexForDinId(country, prov, instance, check) =>
        apply(country, prov, dinInstanceToIsoInstance(instance, check))
    }

    try {
      matchWithRegex.lift(emaId)
    } catch {
      case _: IllegalArgumentException => None
    }
  }

  private def dinInstanceToIsoInstance(dinInstance: String, dinCheck: String): String = {
    require(dinInstance.size == 6)
    require(dinCheck.size == 1)
    "00" + dinInstance + dinCheck
  }

  def unapply(i: EmaId): Option[(String, String, String, Character)] =
    Some(i.countryCode, i.providerId, i.instanceValue, i.checkDigit)

}
