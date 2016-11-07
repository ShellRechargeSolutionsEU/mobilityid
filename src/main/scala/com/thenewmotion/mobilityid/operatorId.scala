package com.thenewmotion.mobilityid

sealed trait OperatorId {
  def id: String
}

sealed trait OperatorIdIso extends OperatorId

private case class OperatorIdIsoImpl(id: String) extends OperatorIdIso {
  override def toString = id
}

object OperatorIdIso {
  val Regex = """([A-Za-z0-9]{3})""".r

  def apply(id: String): OperatorIdIso = id match {
    case Regex(_) => OperatorIdIsoImpl(id.toUpperCase)
    case _ => throw new IllegalArgumentException(
      "OperatorId must have a length of 3 and be ASCII letters or digits")
  }
}

sealed trait OperatorIdDin extends OperatorId

private case class OperatorIdDinImpl(id: String) extends OperatorIdDin {
  override def toString = id
}

object OperatorIdDin {
  val Regex = """([0-9]{3,6})""".r

  def apply(id: String): OperatorIdDin =
    id match {
      case Regex(_) => OperatorIdDinImpl(id.toUpperCase)
      case _ => throw new IllegalArgumentException(
        "OperatorId must have a length of 3-6 chars and be digits")
    }
}