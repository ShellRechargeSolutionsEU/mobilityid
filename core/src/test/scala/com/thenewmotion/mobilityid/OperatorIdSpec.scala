package com.thenewmotion.mobilityid

import org.specs2.mutable.Specification

class OperatorIdSpec extends Specification {

  "OperatorIdDin" should {
    "Reject less than 3 digits" in {
      OperatorIdDin("12") must throwA[IllegalArgumentException]
    }

    "Reject letters" in {
      OperatorIdDin("12A") must throwA[IllegalArgumentException]
    }

    "Reject more than 6 digits" in {
      OperatorIdDin("1234567") must throwA[IllegalArgumentException]
    }

    "Accept correct format" in {
      OperatorIdDin("12345") must not(throwA[IllegalArgumentException])
    }
  }

  "OperatorIdIso" should {
    "Reject less than 3 digits" in {
      OperatorIdIso("AB") must throwA[IllegalArgumentException]
    }

    "Reject more than 3 digits" in {
      OperatorIdIso("ABCD") must throwA[IllegalArgumentException]
    }

    "Accept correct format" in {
      OperatorIdIso("AB2") must not(throwA[IllegalArgumentException])
    }
  }
}