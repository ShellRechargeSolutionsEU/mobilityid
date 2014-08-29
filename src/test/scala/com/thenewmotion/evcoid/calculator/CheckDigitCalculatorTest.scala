package com.thenewmotion.evcoid.calculator

import org.specs2.mutable.SpecificationWithJUnit

class CheckDigitCalculatorTest extends SpecificationWithJUnit {

  "CheckDigitCalculator" should {
    "calculate check digits" in {
      val contractIds = List(
        "NN123ABCDEFGHI",
        "FRXYZ123456789",
        "ITA1B2C3E4F5G6",
        "ESZU8WOX834H1D",
        "PT73902837ABCZ",
        "DE83DUIEN83QGZ",
        "DE83DUIEN83ZGQ",
        "DE8AA001234567"
      )

      contractIds.map(CheckDigitCalculator.calculateCheckDigit) must_== "T24RZDM0".toList
    }
  }
}
