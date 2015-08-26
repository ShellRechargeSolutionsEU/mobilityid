package com.thenewmotion.mobilityid

import org.specs2.mutable.Specification

class EvseIdSpec extends Specification {

  "EvseId" should {

    "Parse ISO string format" should {
      "Accept an ISO EvseId String with separators" in {
        EvseId("DE*AB7*E840*6487") match {
          case Some(e: EvseIdIso) =>
            e.countryCode mustEqual "DE"
            e.operatorId mustEqual "AB7"
            e.powerOutletId mustEqual "E840*6487"
          case _ => ko
        }
      }

      "Accept an ISO EvseId String without separators" in {
        EvseId("DEAB7E8406487") match {
          case Some(e: EvseIdIso) =>
            e.countryCode mustEqual "DE"
            e.operatorId mustEqual "AB7"
            e.powerOutletId mustEqual "E8406487"
          case _ => ko
        }
      }

      "Accept a minimum length ISO EvseId String" in {
        EvseId("DEAB7E1") must beSome
      }

      "Accept a maximum length ISO EvseId String" in {
        EvseId("DE*AB7*E1234567890ABCDEFGHIJ1234567890") must beSome
      }

      "Reject an ISO EvseId String that is too long" in {
        EvseId("DE*AB7*E1234567890ABCDEFGHIJ1234567890A") must beNone
      }

      "Reject an ISO EvseId String with incorrect powerOutletId (must begin with E)" in {
        EvseId("NL*TNM*840*6487") must beNone
      }
    }

    "Parse DIN string format" should {

      "Accept a DIN EvseId String" in {
        EvseId("+49*810*000*438") match {
          case Some(e: EvseIdDin) =>
            e.countryCode mustEqual "49"
            e.operatorId mustEqual "810"
            e.powerOutletId mustEqual "000*438"
          case _ => ko
        }
      }

      "Accept a minimum length DIN EvseId String" in {
        EvseId("+49*810*1") must beSome
      }

      "Accept a maximum length DIN EvseId String" in {
        EvseId("+49*810*12345678901234567890123456789012") must beSome
      }

      "Reject an DIN EvseId String that is too long" in {
        EvseId("+49*810*123456789012345678901234567890123") must beNone
      }

      "Reject a DIN EvseId String with incorrect operator id" in {
        EvseId("+49*AB7*840*6487") must beNone
      }

      "Reject a DIN EvseId String with incorrect powerOutletId" in {
        EvseId("+49*645*E840*6487") must beNone
      }
    }

    "Parse individual fields" should {
      "Accept valid combination of ISO parameters" in {
        val evseId = EvseId("NL", "TNM", "E840*6487")
        evseId must haveClass[EvseIdIso]
        evseId.countryCode mustEqual "NL"
        evseId.operatorId mustEqual "TNM"
        evseId.powerOutletId mustEqual "E840*6487"
      }

      "Accept valid combination of DIN parameters" in {
        val evseId = EvseId("+31", "745", "840*6487")
        evseId must haveClass[EvseIdDin]
        evseId.countryCode mustEqual "+31"
        evseId.operatorId mustEqual "745"
        evseId.powerOutletId mustEqual "840*6487"
      }

      "Reject mixed ISO/DIN formats" in {
        EvseId("+31", "ABC", "840*6487") must throwA[IllegalArgumentException]
        EvseId("+31", "745", "E840*6487") must throwA[IllegalArgumentException]
        EvseId("+31", "745", "840*6487E") must throwA[IllegalArgumentException]
        EvseId("NL", "745", "840*6487") must throwA[IllegalArgumentException]
      }

      "Reject input fields with wrong lengths" in {
        EvseId("A", "TNM", "000122045") must throwA[IllegalArgumentException]
        EvseId("NL", "TNMN", "000722345") must throwA[IllegalArgumentException]
        EvseId("NL", "T|M", "000122045") must throwA[IllegalArgumentException]
      }
    }

    "Render" should {
      "Render an EvseId in the ISO form with asterisks" in {
        EvseId("NL", "TNM", "E840*6487").toString mustEqual "NL*TNM*E840*6487"
      }

      "Render an EvseId in the Compact ISO form without asterisks" in {
        EvseId("NL", "TNM", "E840*6487") match {
          case evseId: EvseIdIso => evseId.toCompactString mustEqual "NLTNME8406487"
          case _ => ko
        }
      }

      "Render an EvseId in DIN format when created in DIN Format" in {
        EvseId("+31", "745", "840*6487").toString mustEqual "+31*745*840*6487"
      }
    }

    "be case insensitive" in {
      EvseId("Nl", "tnM", "E000122045") mustEqual EvseId("NL", "TNM", "E000122045")
    }
  }
}
