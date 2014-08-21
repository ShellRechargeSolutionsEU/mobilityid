package com.thenewmotion.converter

import com.thenewmotion.model.{DinId, EvcoId}
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

import scalaz.{-\/, \/-}

class ContractIdServiceTest extends SpecificationWithJUnit {

  "ContractIdConverter" should {
    "validate right DIN ID" in new TestScope {
      val hyphenatedIdWithCheckDigit = "NL-TNM-123456-7 "
      testInstance.validateDinId(hyphenatedIdWithCheckDigit) must_== true

      val hyphenatedIdWithoutCheckDigit = "NL-TNM-123456 "
      testInstance.validateDinId(hyphenatedIdWithoutCheckDigit) must_== true

      val idWithCheckDigit = "NLTNM1234567 "
      testInstance.validateDinId(idWithCheckDigit) must_== true

      val idWithoutCheckDigit = "NLTNM123456 "
      testInstance.validateDinId(idWithoutCheckDigit) must_== true
    }

    "not validate wrong DIN ID" in new TestScope {
      val hyphenatedId = "NL-TNdM-1234d56-7 "
      testInstance.validateDinId(hyphenatedId) must_== false
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

      testInstance.normalizeDinId(hyphenatedId) must_== expectedNormalizedId
    }

    "normalize Evco ID" in new TestScope {
      val hyphenatedId = "DE-8AA-CA2B3C4D5-N "
      val expectedNormalizedId = "DE8AACA2B3C4D5N"

      testInstance.normalizeEvcoId(hyphenatedId) must_== expectedNormalizedId
    }

    "parse hyphenated EvcoId with check digit" in new TestScope {
      val hyphenatedId = "DE-8AA-CA2B3C4D5-N "
      val expectedEvcoId = EvcoId("DE", "8AA", "CA2B3C4D5", Some("N"))

      testInstance.parseEvcoId(hyphenatedId) must_== \/-(expectedEvcoId)
    }

    "parse hyphenated EvcoId without check digit" in new TestScope {
      val hyphenatedId = "DE-8AA-CA2B3C4D5 "
      val expectedEvcoId = EvcoId("DE", "8AA", "CA2B3C4D5", None)

      testInstance.parseEvcoId(hyphenatedId) must_== \/-(expectedEvcoId)
    }

    "parse EvcoId with check digit" in new TestScope {
      val id = "DE8AACA2B3C4D5N "
      val expectedEvcoId = EvcoId("DE", "8AA", "CA2B3C4D5", Some("N"))

      testInstance.parseEvcoId(id) must_== \/-(expectedEvcoId)
    }

    "parse EvcoId without check digit" in new TestScope {
      val id = "DE8AACA2B3C4D5 "
      val expectedEvcoId = EvcoId("DE", "8AA", "CA2B3C4D5", None)

      testInstance.parseEvcoId(id) must_== \/-(expectedEvcoId)
    }

    "not parse EvcoId" in new TestScope {
      val id = "qwqwwswqes"
      val errMsg = s"Can't parse contract id: $id."

      testInstance.parseEvcoId(id) must_== -\/(errMsg)
    }

    "convert EvcoId to DinId" in new TestScope {
      val evcoId = EvcoId("DE", "8AA", "001234567", Some("D"))
      val expectedDinId = DinId("DE", "8AA", "123456", Some("7"))

      testInstance.convertEvcoIdToDinId(evcoId) must_== \/-(expectedDinId)
    }

    "not convert EvcoId to DinId" in new TestScope {
      val evcoId = EvcoId("DE", "8AA", "CA1234567", Some("D"))
      val expectedDinId = DinId("DE", "8AA", "123456", Some("7"))

      testInstance.convertEvcoIdToDinId(evcoId) must_== -\/(s"EvcoId = ${evcoId.hyphenatedId} can't be converted to DinId.")
    }

    "convert DinId to EvcoId" in new TestScope {
      val dinId = DinId("DE", "8AA", "123456", Some("7"))
      //TODO check digit should be calculated!!!
      val expectedEvcoId = EvcoId("DE", "8AA", "001234567", None)

      testInstance.convertDinIdToEvcoId(dinId) must_== \/-(expectedEvcoId)
    }



    class TestScope extends Scope {
      val testInstance = new ContractIdService
    }
  }
}

