package com.thenewmotion.mobilityid

sealed trait OperatorId {
  def id: String
}

sealed trait OperatorIdIso extends OperatorId

private case class OperatorIdIsoImpl(partyCode: PartyCode) extends OperatorIdIso {
  override def toString = partyCode.toString
  def id = partyCode.id
}

object OperatorIdIso {
  def isValid(id: String): Boolean = PartyCode.isValid(id)

  def apply(id: String): OperatorIdIso = OperatorIdIsoImpl(PartyCode(id))
}

sealed trait OperatorIdDin extends OperatorId

private case class OperatorIdDinImpl(id: String) extends OperatorIdDin {
  override def toString = id
}

object OperatorIdDin {
  val Regex = """([0-9]{3,6})""".r

  def isValid(id: String): Boolean = id match {
    case Regex(_) => true
    case _ => false
  }

  def apply(id: String): OperatorIdDin =
    if (isValid(id)) {
      OperatorIdDinImpl(id.toUpperCase)
    } else throw new IllegalArgumentException(
        "OperatorId must have a length of 3-6 chars and be digits")
}