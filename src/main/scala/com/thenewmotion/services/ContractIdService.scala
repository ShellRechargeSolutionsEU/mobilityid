package com.thenewmotion.services

import com.thenewmotion.model.{EvcoId, DinId}
import com.thenewmotion.calculator.CheckDigitCalculator._


class ContractIdService {

  private val RegexForEvcoId = "^([A-Za-z]{2})(-?)([A-Za-z0-9]{3})(-?)([A-Za-z0-9]{9})(?:(-?)([A-Za-z0-9]))?(\\s+)?$".r

  private val RegexForDinId = "^([A-Za-z]{2})(-?)([A-Za-z0-9]{3})(-?)([A-Za-z0-9]{6})(-?)([A-Za-z0-9]{1})(\\s+)?$".r

  private val DivToEvcoPrefix = "00"

  def validateDinId(id: String): Boolean = {
    val x = RegexForDinId.unapplySeq(id)
    RegexForDinId.pattern.matcher(id).matches()
  }

  def validateEvcoId(id: String): Boolean = {
    RegexForEvcoId.pattern.matcher(id).matches()
  }

  def normalizeDinId(id: String): String = {
    parseDinId(id).normalizedId
  }

  def normalizeEvcoId(id: String): String = {
    parseEvcoId(id).normalizedId
  }

  def parseEvcoId(id: String): EvcoId = {
    RegexForEvcoId.unapplySeq(id) match {
      case Some(groups) =>
        EvcoId(countryCode = groups(0),
          providerId = groups(2),
          instanceValue = groups(4),
          checkDigit = Option(groups(6)))
      case None => sys.error(s"Can't parse EvcoId: $id.")
    }
  }

  def parseDinId(id: String): DinId = {
    RegexForDinId.unapplySeq(id) match {
      case Some(groups) =>
        DinId(countryCode = groups(0),
          providerId = groups(2),
          instanceValue = groups(4),
          checkDigit = groups(6))
      case None => sys.error(s"Can't parse DinId: $id.")
    }
  }

  def convertDinIdToEvcoId(id: DinId): EvcoId = {
    val countryCode = id.countryCode
    val providerId = id.providerId
    val checkDigit = id.checkDigit
    val instanceValue = DivToEvcoPrefix + id.instanceValue + checkDigit

    EvcoId(countryCode = countryCode,
           providerId = providerId,
           instanceValue = instanceValue,
           checkDigit = Some(calculateCheckDigit(countryCode + providerId + instanceValue).toString))
  }

  def convertEvcoIdToDinId(id: EvcoId): DinId = {
    if (!id.instanceValue.startsWith(DivToEvcoPrefix)) sys.error(s"EvcoId = ${id.hyphenatedId} can't be converted to DinId.")
    else {
      DinId(countryCode = id.countryCode,
        providerId = id.providerId,
        instanceValue = id.instanceValue.substring(DivToEvcoPrefix.length, id.instanceValue.length - 1),
        checkDigit = id.instanceValue.last.toString)
    }
  }
}
