package com.thenewmotion.mobilityid

import org.specs2.mutable.Specification

class EvseIdSpec extends Specification {

  "EvseId" should {

    "Parse ISO string format" should {
      "Accept an ISO EvseId String with separators" in {
        EvseId("DE*AB7*E840*6487") must beSome(EvseId("DE", "AB7", "840*6487"))
      }

      "Accept an ISO EvseId String without separators" in {
        EvseId("DEAB7E8406487") must beSome(EvseId("DE", "AB7", "8406487"))
      }

      "Accept a minimum length ISO EvseId String" in {
        EvseId("DEAB7E1") must beSome
      }

      "Accept a maximum length ISO EvseId String" in {
        EvseId("DE*AB7*E1234567890ABCDEFGHIJ1234567890") must beSome
      }

      "Reject an ISO EvseId String that is too long" in {
        val tooLongOutledId = Seq.fill(32)(7).mkString
        EvseId(s"DE*AB7*E$tooLongOutledId") must beNone
      }

      "Reject an ISO EvseId String with incorrect powerOutletId (must begin with E)" in {
        EvseId("NL*TNM*840*6487") must beNone
      }

      "Reject to construct an EvseIdIso directly from valid DIN String" in {
        EvseIdIso("+49*810*000*438") must beNone
      }
    }

    "Parse DIN string format" should {

      "Accept a DIN EvseId String" in {
        EvseId("+49*810*000*438") match {
          case Some(e: EvseIdDin) =>
            e.countryCode mustEqual PhoneCountryCode("+49")
            e.operatorId mustEqual OperatorId("810")
            e.powerOutletId mustEqual "000*438"
          case _ => ko
        }
      }

      "Allow to construct an EvseIdDin directly" in {
        EvseIdDin("+49*810*000*438") match {
          case Some(e: EvseId) =>
            e.countryCode mustEqual PhoneCountryCode("+49")
            e.operatorId mustEqual OperatorId("810")
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

      "Reject to construct an EvseIdDin directly from valid ISO String" in {
        EvseIdDin("DE*AB7*E840*6487") must beNone
      }

      "Accept country codes with and without plus sign" in {
        EvseId("+49*810*000*438") mustEqual EvseId("49*810*000*438")
      }
    }

    "Parse individual fields" should {
      "Accept valid combination of ISO parameters" in {
        val evseId = EvseId("NL", "TNM", "E840*6487")
        evseId must beAnInstanceOf[EvseIdIso]
        evseId.countryCode mustEqual CountryCode("NL")
        evseId.operatorId mustEqual OperatorId("TNM")
        evseId.powerOutletId mustEqual "E840*6487"
      }

      "Accept valid combination of ISO parameters when creating EvseIdIso directly" in {
        val evseId = EvseIdIso("NL", "TNM", "E840*6487")
        evseId.countryCode mustEqual CountryCode("NL")
        evseId.operatorId mustEqual OperatorId("TNM")
        evseId.powerOutletId mustEqual "E840*6487"
      }

      "Accept valid combination of DIN parameters" in {
        val evseId = EvseId("+31", "745", "840*6487")
        evseId must beAnInstanceOf[EvseIdDin]
        evseId.countryCode mustEqual PhoneCountryCode("+31")
        evseId.operatorId mustEqual OperatorId("745")
        evseId.powerOutletId mustEqual "840*6487"
      }

      "Accept valid combination of DIN parameters when creating EvseIdDin directly" in {
        val evseId = EvseIdDin("+31", "745", "840*6487")
        evseId.countryCode mustEqual PhoneCountryCode("+31")
        evseId.operatorId mustEqual OperatorId("745")
        evseId.powerOutletId mustEqual "840*6487"
      }

      "Reject EvseIdIso's country/operator codes when creating EvseIdDin" in {
        EvseIdDin("NL", "TNM", "840*6487") must throwA[IllegalArgumentException]
      }

      "Reject EvseIdDin's country/operator codes when creating EvseIdIso" in {
        EvseIdIso("+31", "745", "840*6487") must throwA[IllegalArgumentException]
      }

      "Reject mixed ISO/DIN formats" in {
        EvseId("+31", "ABC", "840*6487") must throwA[IllegalArgumentException]
        EvseId("+31", "745", "E840*6487") must throwA[IllegalArgumentException]
        EvseId("+31", "745", "840*6487E") must throwA[IllegalArgumentException]
      }

      "Reject input fields with wrong lengths" in {
        EvseId("A", "TNM", "000122045") must throwA[IllegalArgumentException]
        EvseId("NL", "TNMN", "000722345") must throwA[IllegalArgumentException]
        EvseId("NL", "T|M", "000122045") must throwA[IllegalArgumentException]
      }

      "Accept country codes with and without plus sign" in {
        EvseId("+31", "745", "840*6487") mustEqual EvseId("31", "745", "840*6487")
      }
    }

    "Render" should {
      "Render an EvseId in the ISO form with asterisks" in {
        EvseId("NL", "TNM", "840*6487").toString mustEqual "NL*TNM*E840*6487"
      }

      "Render an EvseId in the Compact ISO form without asterisks" in {
        EvseId("NL", "TNM", "840*6487") match {
          case evseId: EvseIdIso => evseId.toCompactString mustEqual "NLTNME8406487"
          case _ => ko
        }
      }

      "Render an EvseId in DIN format when created in DIN Format" in {
        EvseId("+31", "745", "840*6487").toString mustEqual "+31*745*840*6487"
        EvseId("+31*745*840*6487").get.toString mustEqual "+31*745*840*6487"
      }
    }

    "be case insensitive" in {
      EvseId("Nl", "tnM", "E000122045") mustEqual EvseId("NL", "TNM", "E000122045")
    }
  }
}
