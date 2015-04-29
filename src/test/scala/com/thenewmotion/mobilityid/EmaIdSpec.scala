package com.thenewmotion.mobilityid

import org.specs2.mutable.Specification

class EmaIdSpec extends Specification {

  "EmaId" should {
    "render an EMAID in the normalized form with dashes and check digit" in {
      EmaId("NL", "TNM", "000722345").toString mustEqual "NL-TNM-000722345-X"
    }

    "refuse input fields with wrong lengths" in {
      EmaId("A", "TNM", "000122045") must throwA[IllegalArgumentException]
      EmaId("NL", "TNMN", "000722345") must throwA[IllegalArgumentException]
      EmaId("NL", "TNM", "72245") must throwA[IllegalArgumentException]
      EmaId("NL", "T|M", "000122045") must throwA[IllegalArgumentException]
    }

    "be case insensitive" in {
      EmaId("Nl", "tnM", "000122045") mustEqual EmaId("NL", "TNM", "000122045")
    }

    "create an EmaId from a string in any DIN SPEC 91286 or ISO 15118 format" in {
      List(EmaId("NL-TNM-000122045-U"),
        EmaId("NL-TNM-000122045-U"),
        EmaId("Nl-TnM-000122045-U"),
        EmaId("nl-TNm-000122045-u"),
        EmaId("NLTNM000122045"),
        /*           EmaId("NL-TNM-012204"), */ // TODO check digit computation for DIN IDs?
        EmaId("NL-TNM-012204-5")) mustEqual List.fill(6)(Some(EmaId("NL", "TNM", "000122045")))
    }

    "return None when trying to create an EmaId from an invalid string" in {
      List(EmaId("NL-TNM-000122045-X"), // wrong check digit
           EmaId("NLTNM076"), // wrong length
           EmaId("X-aargh-131331234"), // wrong length of fields
           EmaId(" \u0000t24\u2396a\t"), // whatever nonsense
           EmaId("NL-T|M-000122045-U") // right lengths but illegal char
      ) mustEqual List.fill(5)(None)
    }

    // ideally we'd compute the check digit but the algorithm is behind a paywall
    "return None when trying to create an EmaId from a DIN ID without a check digit" in {
      EmaId("NL-TNM-123456") must beNone
    }

    "render an EMAID as a compact string without dashes" in {
      EmaId("NL", "TNM", "000722345").toCompactString mustEqual "NLTNM000722345X"
    }

    "render an EMAID as a compact string without dashes and without check digit" in {
      EmaId("NL", "TNM", "000722345").toCompactStringWithoutCheckDigit mustEqual "NLTNM000722345"
    }

    "render an EMAID as a DIN SPEC 91286 string if applicable" in {
      EmaId("NL", "TNM", "000122045").toDinString mustEqual Some("NL-TNM-012204-5")
    }

    "return None when trying to render a non-DIN EMAID as a DIN string" in {
      EmaId("NL", "TNM", "012345678").toDinString must beNone
    }

    "be unapplicable" in {
      EmaId("NL", "TNM","000122045") match {
        case EmaId(c, p, i, ch) => (c, p, i, ch) mustEqual ("NL", "TNM", "000122045", 'U')
      }
    }

    "be equal to an object representing the same ID created with different arguemnts" in {
      Some(EmaId("NL", "TNM", "000122045")) mustEqual EmaId("NL-TNM-012204-5")

      EmaId("NL-TNM-000122045") mustEqual Some(EmaId("NL", "TNM", "000122045", 'U'))

      EmaId("NLTNM012345678") mustNotEqual EmaId("NLTNM876543210")
    }
  }
}
