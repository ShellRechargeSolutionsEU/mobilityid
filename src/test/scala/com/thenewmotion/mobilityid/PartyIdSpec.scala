package com.thenewmotion.mobilityid

import org.specs2.mutable.Specification

class PartyIdSpec extends Specification {

  "PartyId" should {

    "disallow too short or too long country codes and national party identifiers" in {
      PartyId("NLD", "TNM") must throwA[IllegalArgumentException]
      PartyId("NL", "TheNM") must throwA[IllegalArgumentException]
      PartyId("N", "TNM") must throwA[IllegalArgumentException]
      PartyId("NL", "NM") must throwA[IllegalArgumentException]
    }

    "be case insensitive" in {
      PartyId("Nl", "tnM") mustEqual PartyId("NL", "TNM")
    }

    "be a valid ISO code" in {
      PartyId("XX", "tnM") must throwA[IllegalArgumentException]
    }
  }

}
