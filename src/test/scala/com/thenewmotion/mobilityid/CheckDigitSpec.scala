package com.thenewmotion.mobilityid

import org.specs2.mutable.SpecificationWithJUnit

class CheckDigitSpec extends SpecificationWithJUnit {

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

      contractIds.map(CheckDigit(_)) must_== "T24RZDM0".toList
    }

    "fail with IllegalArgumentException on malformed input" in {
      CheckDigit("Европарулит123") must throwA[IllegalArgumentException]
      CheckDigit("DE٨٣DUIEN٨٣QGZ") must throwA[IllegalArgumentException]
      CheckDigit("Å∏@*(Td\uD83D\uDE3BgaR^&(%") must throwA[IllegalArgumentException]
      CheckDigit("Å∏@*(Td\uD83D\uDE3BgR^&(%") must throwA[IllegalArgumentException]
      CheckDigit("") must throwA[IllegalArgumentException]
      CheckDigit("DE8AA0012345678") must throwA[IllegalArgumentException]
    }
  }
}
