package com.thenewmotion.evcoid.services

import com.thenewmotion.evcoid.model.{DinId, EvcoId}
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

class ContractIdServiceTest extends SpecificationWithJUnit {

  "ContractIdConverter" should {
    "validate right DIN ID" in new TestScope {
      val hyphenatedIdWithCheckDigit = "NL-TNM-123456-7 "
      testInstance.validateDinId(hyphenatedIdWithCheckDigit) must_== true

      val idWithCheckDigit = "NLTNM1234567 "
      testInstance.validateDinId(idWithCheckDigit) must_== true
    }

    "not validate wrong DIN ID" in new TestScope {
      val hyphenatedId = "NL-TNdM-1234d56-7 "
      testInstance.validateDinId(hyphenatedId) must_== false

      val hyphenatedIdWithoutCheckDigit = "NL-TNM-123456 "
      testInstance.validateDinId(hyphenatedIdWithoutCheckDigit) must_== false

      val idWithoutCheckDigit = "NLTNM123456 "
      testInstance.validateDinId(idWithoutCheckDigit) must_== false
    }

    "validate right EvcoId" in new TestScope {
      val hyphenatedIdWithCheckDigit = "DE-8AA-CA2B3C4D5-N "
      testInstance.validateEvcoId(hyphenatedIdWithCheckDigit) must_== true

      val hyphenatedIdWithoutCheckDigit = "DE-8AA-CA2B3C4D5 "
      testInstance.validateEvcoId(hyphenatedIdWithoutCheckDigit) must_== true

      val idWithCheckDigit = "DE8AACA2B3C4D5N "
      testInstance.validateEvcoId(idWithCheckDigit) must_== true

      val idWithoutCheckDigit = "DE8AACA2B3C4D5 "
      testInstance.validateEvcoId(idWithoutCheckDigit) must_== true
    }

    "not validate wrong EvcoId" in new TestScope {
      val hyphenatedId = "DER-8AAR-CA2B3C4D5-N "
      testInstance.validateEvcoId(hyphenatedId) must_== false
    }

    "normalize Din ID" in new TestScope {
      val hyphenatedId = "DE-8AA-123456-7 "
      val expectedNormalizedId = "DE8AA1234567"

      testInstance.normalizeDinId(hyphenatedId) must_== Some(expectedNormalizedId)
    }

    "normalize Evco ID" in new TestScope {
      val hyphenatedId = "DE-8AA-CA2B3C4D5-N "
      val expectedNormalizedId = "DE8AACA2B3C4D5N"

      testInstance.normalizeEvcoId(hyphenatedId) must_== Some(expectedNormalizedId)
    }

    "parse hyphenated EvcoId with check digit" in new TestScope {
      val hyphenatedId = "DE-8AA-CA2B3C4D5-N "
      val expectedEvcoId = EvcoId("DE", "8AA", "CA2B3C4D5", Some("N"))

      testInstance.parseEvcoId(hyphenatedId) must_== Some(expectedEvcoId)
    }

    "parse hyphenated EvcoId without check digit" in new TestScope {
      val hyphenatedId = "DE-8AA-CA2B3C4D5 "
      val expectedEvcoId = EvcoId("DE", "8AA", "CA2B3C4D5", None)

      testInstance.parseEvcoId(hyphenatedId) must_== Some(expectedEvcoId)
    }

    "parse EvcoId with check digit" in new TestScope {
      val id = "DE8AACA2B3C4D5N "
      val expectedEvcoId = EvcoId("DE", "8AA", "CA2B3C4D5", Some("N"))

      testInstance.parseEvcoId(id) must_== Some(expectedEvcoId)
    }

    "parse EvcoId without check digit" in new TestScope {
      val id = "DE8AACA2B3C4D5 "
      val expectedEvcoId = EvcoId("DE", "8AA", "CA2B3C4D5", None)

      testInstance.parseEvcoId(id) must_== Some(expectedEvcoId)
    }

    "not parse EvcoId" in new TestScope {
      val id = "qwqwwswqes"

      testInstance.parseEvcoId(id) must_== None
    }

    "convert EvcoId to DinId" in new TestScope {
      val evcoId = EvcoId("DE", "8AA", "001234567", Some("D"))
      val expectedDinId = DinId("DE", "8AA", "123456", "7")

      testInstance.convertEvcoIdToDinId(evcoId) must_== Some(expectedDinId)
    }

    "not convert EvcoId to DinId" in new TestScope {
      val evcoId = EvcoId("DE", "8AA", "CA1234567", Some("D"))
      val expectedDinId = DinId("DE", "8AA", "123456", "7")

      testInstance.convertEvcoIdToDinId(evcoId) must_== None
    }

    "convert DinId to EvcoId" in new TestScope {
      val dinId = DinId("DE", "8AA", "123456", "7")
      val expectedEvcoId = EvcoId("DE", "8AA", "001234567", Some("0"))

      testInstance.convertDinIdToEvcoId(dinId) must_== Some(expectedEvcoId)
    }

    "convert text number to Evco ID" in new TestScope {
      val dinIdNumber = "DE8AA1234567"
      val expectedEvcoId = EvcoId("DE", "8AA", "001234567", Some("0"))

      testInstance.dinIdToEvcoId(dinIdNumber) must_== Some(expectedEvcoId)
    }

    "convert text number to Din ID" in new TestScope {
      val evcoIdNumber = "DE8AA0012345670"
      val expectedDinId = DinId("DE", "8AA", "123456", "7")

      testInstance.evcoIdToDinId(evcoIdNumber) must_== Some(expectedDinId)
    }

    class TestScope extends Scope {
      val testInstance = new ContractIdService
    }
  }
}

