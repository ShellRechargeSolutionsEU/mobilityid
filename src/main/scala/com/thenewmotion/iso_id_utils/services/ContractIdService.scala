package com.thenewmotion.iso_id_utils.services

import com.thenewmotion.iso_id_utils.Logging
import com.thenewmotion.iso_id_utils.model.{EvcoId, DinId}
import com.thenewmotion.iso_id_utils.calculator.CheckDigitCalculator._
import scalaz.{\/, \/-, -\/}


class ContractIdService extends Logging {

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

  def normalizeDinId(id: String): String \/ String = {
    parseDinId(id).map(_.normalizedId)
  }

  def normalizeEvcoId(id: String): String \/ String = {
    parseEvcoId(id).map(_.normalizedId)
  }

  def parseEvcoId(id: String): String \/ EvcoId = {
    RegexForEvcoId.unapplySeq(id) match {
      case Some(groups) =>
        \/-(EvcoId(countryCode = groups(0),
          providerId = groups(2),
          instanceValue = groups(4),
          checkDigit = Option(groups(6))))
      case None =>
        val msg = s"Can't parse EvcoId: $id."
        logger.error(msg)
        -\/(msg)
    }
  }

  def parseDinId(id: String): String \/ DinId = {
    RegexForDinId.unapplySeq(id) match {
      case Some(groups) =>
        \/-(DinId(countryCode = groups(0),
          providerId = groups(2),
          instanceValue = groups(4),
          checkDigit = groups(6)))
      case None =>
        val msg = s"Can't parse DinId: $id."
        logger.error(msg)
        -\/(msg)
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

  def convertEvcoIdToDinId(id: EvcoId): String \/ DinId = {
    if (id.instanceValue.startsWith(DivToEvcoPrefix)) {
      \/-(DinId(countryCode = id.countryCode,
                providerId = id.providerId,
                instanceValue = id.instanceValue.substring(DivToEvcoPrefix.length, id.instanceValue.length - 1),
                checkDigit = id.instanceValue.last.toString))
    } else {
      val msg = s"EvcoId = ${id.hyphenatedId} can't be converted to DinId."
      logger.error(msg)
      -\/(msg)
    }
  }
}
