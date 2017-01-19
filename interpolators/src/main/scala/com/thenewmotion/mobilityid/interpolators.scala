package com.thenewmotion.mobilityid

import contextual.{Interpolator, Prefix}

object EvseIdInterpolator extends Interpolator {
  def contextualize(interpolation: StaticInterpolation) = {
    val lit@Literal(_, evseIdString) = interpolation.parts.head
    EvseId(evseIdString) match {
      case None => interpolation.abort(lit, 0, "not a valid EvseId")
      case _ =>
    }

    Nil
  }

  def evaluate(interpolation: RuntimeInterpolation): EvseId =
    EvseId(interpolation.literals.head).get
}

object EvseIdIsoInterpolator extends Interpolator {
  def contextualize(interpolation: StaticInterpolation) = {
    val lit@Literal(_, evseIdString) = interpolation.parts.head
    EvseIdIso(evseIdString) match {
      case None => interpolation.abort(lit, 0, "not a valid EvseIdIso")
      case _ =>
    }

    Nil
  }

  def evaluate(interpolation: RuntimeInterpolation): EvseIdIso =
    EvseIdIso(interpolation.literals.head).get
}

object EvseIdDinInterpolator extends Interpolator {
  def contextualize(interpolation: StaticInterpolation) = {
    val lit@Literal(_, evseIdString) = interpolation.parts.head
    EvseIdDin(evseIdString) match {
      case None => interpolation.abort(lit, 0, "not a valid EvseIdDin")
      case _ =>
    }

    Nil
  }

  def evaluate(interpolation: RuntimeInterpolation): EvseIdDin =
    EvseIdDin(interpolation.literals.head).get
}

object EmaIdInterpolator extends Interpolator {
  def contextualize(interpolation: StaticInterpolation) = {
    val lit@Literal(_, emaIdString) = interpolation.parts.head
    EmaId(emaIdString) match {
      case None => interpolation.abort(lit, 0, "not a valid EmaId")
      case _ =>
    }

    Nil
  }

  def evaluate(interpolation: RuntimeInterpolation): EmaId =
    EmaId(interpolation.literals.head).get
}

object ProviderIdInterpolator extends Interpolator {
  def contextualize(interpolation: StaticInterpolation) = {
    val lit@Literal(_, providerIdString) = interpolation.parts.head
    if (!ProviderId.isValid(providerIdString)) interpolation.abort(lit, 0, "not a valid ProviderId")
    Nil
  }

  def evaluate(interpolation: RuntimeInterpolation): ProviderId =
    ProviderId(interpolation.literals.head)
}

object CountryCodeInterpolator extends Interpolator {
  def contextualize(interpolation: StaticInterpolation) = {
    val lit@Literal(_, countryCodeString) = interpolation.parts.head
    if (!CountryCode.isValid(countryCodeString)) interpolation.abort(lit, 0, "not a valid CountryCode")
    Nil
  }

  def evaluate(interpolation: RuntimeInterpolation): CountryCode =
    CountryCode(interpolation.literals.head)
}

object PhoneCountryCodeInterpolator extends Interpolator {
  def contextualize(interpolation: StaticInterpolation) = {
    val lit@Literal(_, phoneCountryCodeString) = interpolation.parts.head
    if (!PhoneCountryCode.isValid(phoneCountryCodeString)) interpolation.abort(lit, 0, "not a valid PhoneCountryCode")
    Nil
  }

  def evaluate(interpolation: RuntimeInterpolation): PhoneCountryCode =
    PhoneCountryCode(interpolation.literals.head)
}

object OperatorIdIsoInterpolator extends Interpolator {
  def contextualize(interpolation: StaticInterpolation) = {
    val lit@Literal(_, operatorIdIso) = interpolation.parts.head
    if (!OperatorIdIso.isValid(operatorIdIso)) interpolation.abort(lit, 0, "not a valid OperatorIdIso")
    Nil
  }

  def evaluate(interpolation: RuntimeInterpolation): OperatorIdIso =
    OperatorIdIso(interpolation.literals.head)
}

object OperatorIdDinInterpolator extends Interpolator {
  def contextualize(interpolation: StaticInterpolation) = {
    val lit@Literal(_, operatorIdDin) = interpolation.parts.head
    if (!OperatorIdDin.isValid(operatorIdDin)) interpolation.abort(lit, 0, "not a valid OperatorIdDin")
    Nil
  }

  def evaluate(interpolation: RuntimeInterpolation): OperatorIdDin =
    OperatorIdDin(interpolation.literals.head)
}

object interpolators {
  implicit class MobilityIdStringContext(sc: StringContext) {
    val evseId = Prefix(EvseIdInterpolator, sc)
    val evseIdIso = Prefix(EvseIdIsoInterpolator, sc)
    val evseIdDin = Prefix(EvseIdDinInterpolator, sc)
    val emaId = Prefix(EmaIdInterpolator, sc)
    val providerId = Prefix(ProviderIdInterpolator, sc)
    val countryCode = Prefix(CountryCodeInterpolator, sc)
    val phoneCountryCode = Prefix(PhoneCountryCodeInterpolator, sc)
    val operatorIdIso = Prefix(OperatorIdIsoInterpolator, sc)
    val operatorIdDin = Prefix(OperatorIdDinInterpolator, sc)
  }
}