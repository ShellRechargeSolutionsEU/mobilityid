package com.thenewmotion.evcoid.services

import com.thenewmotion.evcoid.model.{EvcoId, DinId}
import com.thenewmotion.evcoid.calculator.CheckDigitCalculator._
import scala.util.{Failure, Success, Try}

class ContractIdService {

  private val RegexForEvcoId = "^([A-Za-z]{2})(-?)([A-Za-z0-9]{3})(-?)([A-Za-z0-9]{9})(?:(-?)([A-Za-z0-9]))?(\\s+)?$".r

  private val RegexForDinId = "^([A-Za-z]{2})(-?)([A-Za-z0-9]{3})(-?)([A-Za-z0-9]{6})(-?)([A-Za-z0-9]{1})(\\s+)?$".r

  private val DivToEvcoPrefix = "00"

  def validateDinId(id: String): Boolean = RegexForDinId.pattern.matcher(id).matches()

  def validateEvcoId(id: String): Boolean = RegexForEvcoId.pattern.matcher(id).matches()

  def normalizeDinId(id: String): Option[String] = parseDinId(id).map(_.normalizedId)

  def normalizeEvcoId(id: String): Option[String] = parseEvcoId(id).map(_.normalizedId)

  def parseEvcoId(id: String): Option[EvcoId] =
    RegexForEvcoId.unapplySeq(id.toUpperCase).map { groups =>
      EvcoId(countryCode = groups(0),
        providerId = groups(2),
        instanceValue = groups(4),
        checkDigit = Option(groups(6)))
    }

  def parseDinId(id: String): Option[DinId] =
    RegexForDinId.unapplySeq(id.toUpperCase).map { groups =>
      DinId(countryCode = groups(0),
        providerId = groups(2),
        instanceValue = groups(4),
        checkDigit = groups(6))
    }

  def convertDinIdToEvcoId(id: DinId): Option[EvcoId] = {
    val countryCode = id.countryCode
    val providerId = id.providerId
    val checkDigit = id.checkDigit
    val instanceValue = DivToEvcoPrefix + id.instanceValue + checkDigit
    Try(calculateCheckDigit(countryCode + providerId + instanceValue).toString) match {
      case Success(isoCheckDigit) =>
        Some(EvcoId(countryCode = countryCode,
                   providerId = providerId,
                   instanceValue = instanceValue,
                   checkDigit = Some(isoCheckDigit)))
      case Failure(_) => None
    }
  }

  def convertEvcoIdToDinId(id: EvcoId): Option[DinId] = {
    if (id.instanceValue.startsWith(DivToEvcoPrefix)) {
      Some(DinId(countryCode = id.countryCode,
                providerId = id.providerId,
                instanceValue = id.instanceValue.substring(DivToEvcoPrefix.length, id.instanceValue.length - 1),
                checkDigit = id.instanceValue.last.toString))
    } else {
      None
    }
  }

  def dinIdToEvcoId(id: String): Option[EvcoId] =
    parseDinId(id).flatMap(convertDinIdToEvcoId)

  def evcoIdToDinId(id: String): Option[DinId] =
    parseEvcoId(id).flatMap(convertEvcoIdToDinId)
}
