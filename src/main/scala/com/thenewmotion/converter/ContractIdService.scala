package com.thenewmotion.converter

import com.thenewmotion.model.{EvcoId, DinId}

import scalaz.{-\/, \/-, \/}

class ContractIdService {

  private val RegexForEvcoId = "^([A-Za-z]{2})(-?)([A-Za-z0-9]{3})(-?)([A-Za-z0-9]{9})(?:(-?)([A-Za-z0-9]))?(\\s+)?$".r

  private val RegexForDinId = "^([A-Za-z]{2})(-?)([A-Za-z0-9]{3})(-?)([A-Za-z0-9]{6})(?:(-?)([A-Za-z0-9]))?(\\s+)?$".r

  private val DivToEvcoPrefix = "00"

  def validateDinId(id: String): Boolean = {
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
      case None => -\/(s"Can't parse EvcoId: $id.")
    }
  }

  def parseDinId(id: String): String \/ DinId = {
    RegexForEvcoId.unapplySeq(id) match {
      case Some(groups) =>
        \/-(DinId(countryCode = groups(0),
          providerId = groups(2),
          instanceValue = groups(4),
          checkDigit = Option(groups(6))))
      case None => -\/(s"Can't parse DinId: $id.")
    }
  }

  def convertDinIdToEvcoId(id: DinId): String \/ EvcoId = {
    //TODO check digit should be calculated
    \/-(EvcoId(countryCode = id.countryCode,
           providerId = id.providerId,
           instanceValue = DivToEvcoPrefix + id.instanceValue + id.checkDigit.getOrElse(""),
           checkDigit = None))
  }

  def convertEvcoIdToDinId(id: EvcoId): String \/ DinId = {
    if (!id.instanceValue.startsWith(DivToEvcoPrefix)) -\/(s"EvcoId = ${id.hyphenatedId} can't be converted to DinId.")
    else {
      \/-(DinId(countryCode = id.countryCode,
        providerId = id.providerId,
        instanceValue = id.instanceValue.substring(DivToEvcoPrefix.length, id.instanceValue.length - 1),
        checkDigit = Some(id.instanceValue.last.toString)))
    }
  }
}
