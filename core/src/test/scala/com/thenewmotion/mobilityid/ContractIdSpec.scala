package com.thenewmotion.mobilityid

import ContractIdStandard.{DIN, EMI3, ISO}
import org.specs2.mutable.Specification

class ContractIdSpec extends Specification {

  "ContractId" >> {
    "construction" should {
      "refuse input fields with wrong lengths" in {
        ContractId[ISO]("A", "TNM", "000122045") must throwA[IllegalArgumentException]
        ContractId[ISO]("NL", "TNMN", "000722345") must throwA[IllegalArgumentException]
        ContractId[ISO]("NL", "TNM", "72245") must throwA[IllegalArgumentException]
        ContractId[ISO]("NL", "T|M", "000122045") must throwA[IllegalArgumentException]
        ContractId[DIN]("NL", "TNM", "000122045") must throwA[IllegalArgumentException]
      }

      "be case insensitive" in {
        ContractId[ISO]("Nl", "tnM", "000122045") mustEqual ContractId[ISO]("NL", "TNM", "000122045")
      }

      "create a ContractId from a string in ISO 15118 format" in {
        List(
          ContractId[ISO]("NL-TNM-000122045-U"),
          ContractId[ISO]("NL-TNM-000122045-U"),
          ContractId[ISO]("Nl-TnM-000122045-U"),
          ContractId[ISO]("nl-TNm-000122045-u"),
          ContractId[ISO]("NLTNM000122045")
        ) mustEqual List.fill(5)(ContractId[ISO]("NL", "TNM", "000122045"))
      }

      "create a ContractId from a string in EMI3 format" in {
        List(
          ContractId[EMI3]("NL-TNM-C00122045-K"),
          ContractId[EMI3]("NL-TNM-C00122045-K"),
          ContractId[EMI3]("Nl-TnM-C00122045-K"),
          ContractId[EMI3]("nl-TNm-C00122045-k"),
          ContractId[EMI3]("NLTNMC00122045")
        ) mustEqual List.fill(5)(ContractId[EMI3]("NL", "TNM", "C00122045"))
      }

      "create a ContractId from a string in DIN format" in {
        List(
          ContractId[DIN]("NL-TNM-122045-0"),
          ContractId[DIN]("NL-TNM-122045-0"),
          ContractId[DIN]("Nl-TnM-122045-0"),
          ContractId[DIN]("nl-TNm-122045-0"),
          ContractId[DIN]("NL*TNM*122045*0"),
          ContractId[DIN]("NLTNM122045")
        ) mustEqual List.fill(6)(ContractId[DIN]("NL", "TNM", "122045"))
      }

      "throw error when trying to create an ContractId from an invalid string" in {
        ContractId[ISO]("NL-TNM-000122045-X") must throwA[IllegalArgumentException] // wrong check digit
        ContractId[ISO]("NLTNM076") must throwA[IllegalArgumentException] // wrong length
        ContractId[ISO]("X-aargh-131331234") must throwA[IllegalArgumentException] // wrong length of fields
        ContractId[ISO](" \u0000t24\u2396a\t") must throwA[IllegalArgumentException] // whatever nonsense
        ContractId[ISO]("NL-T|M-000122045-U") must throwA[IllegalArgumentException] // right lengths but illegal char
      }

      "be equal to an object representing the same ID created with different arguments" in {
        ContractId[ISO]("NL-TNM-000122045") mustEqual ContractId[ISO]("NL", "TNM", "000122045", 'U')
        ContractId[ISO]("NLTNM012345678") mustNotEqual ContractId[ISO]("NLTNM876543210")
      }
    }

    "render" should {
      "render a contract id in the normalized form with dashes and check digit" in {
        ContractId[ISO]("NL", "TNM", "000722345").toString mustEqual "NL-TNM-000722345-X"
      }

      "render an ContractId as a compact string without dashes" in {
        ContractId[ISO]("NL", "TNM", "000722345").toCompactString mustEqual "NLTNM000722345X"
      }

      "render an ContractId as a compact string without dashes and without check digit" in {
        ContractId[ISO]("NL", "TNM", "000722345").toCompactStringWithoutCheckDigit mustEqual "NLTNM000722345"
      }
    }

    "conversion" should {
      "convert a ISO15118 to a DIN SPEC 91286" in {
        ContractId[ISO]("NL", "TNM", "000122045").convertTo[DIN] must beEqualTo(
          ContractId[DIN]("NL-TNM-012204-5")
        )
      }

      "convert a DIN SPEC 91286 to ISO15118" in {
        ContractId[DIN]("NL-TNM-012204-5").convertTo[ISO] must beEqualTo(
          ContractId[ISO]("NL", "TNM", "000122045")
        )
      }

      "throw error when trying to convert a non-DIN ISO to DIN format" in {
        ContractId[ISO]("NL", "TNM", "012345678").convertTo[DIN] must throwA[IllegalStateException]
      }

      "convert a DIN SPEC 91286 to an EMI3" in {
        ContractId[DIN]("NL-TNM-012204-5").convertTo[EMI3] must beEqualTo(
          ContractId[EMI3]("NL-TNM-C00122045-K")
        )
      }

      "convert an EMI3 to a DIN SPEC 91286" in {
        ContractId[EMI3]("NL-TNM-C00122045-K").convertTo[DIN] must beEqualTo(
          ContractId[DIN]("NL-TNM-012204-5")
        )
      }

      "convert an EMI3 to an ISO15118" in {
        ContractId[EMI3]("NL-TNM-C00122045-K").convertTo[ISO] must beEqualTo(
          ContractId[ISO]("NL-TNM-C00122045-K")
        )
      }
    }

    "should be unapplicable" in {
      ContractId[ISO]("NL", "TNM","000122045") match {
        case ContractId(c, p, i, ch) =>
          c mustEqual CountryCode("NL")
          p mustEqual ProviderId("TNM")
          i mustEqual "000122045"
          ch mustEqual 'U'
      }
    }

    "should expose provider's party ID" in {
      ContractId[ISO]("NL", "TNM", "000722345").partyId mustEqual PartyId("NLTNM").get
    }
  }
}
