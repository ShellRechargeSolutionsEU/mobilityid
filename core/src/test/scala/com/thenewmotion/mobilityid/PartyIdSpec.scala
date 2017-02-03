package com.thenewmotion.mobilityid

import org.specs2.mutable.Specification

class PartyIdSpec extends Specification {

  "PartyId" should {

    "parse party-IDs with dash" in {
      PartyId("NL-TNM") should beSome.which(_.toCompactString == "NLTNM")
    }

    "parse party-IDs with asterisk" in {
      PartyId("NL*TNM") should beSome.which(_.toCompactString == "NLTNM")
    }

    "parse party-IDs without dash or asterisk" in {
      PartyId("NLTNM") should beSome.which(_.toCompactString == "NLTNM")
    }

    "render to String with a dash" in {
      PartyId("NL*TNM") should beSome.which(_.toString == "NL-TNM")
    }

    "not parse various nonsense input strings" in {
      val nonsenseIds = List("NLTNMA", "XYTNM", "NL%(@$", " NLTNM", "\u000aLTNM", "", "XY-TNMaargh", "НЛ-TNM", "NLT-NM")

      nonsenseIds.map(PartyId.apply) must contain(beNone).foreach
    }
  }
}

