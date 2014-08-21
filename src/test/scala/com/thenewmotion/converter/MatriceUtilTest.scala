package com.thenewmotion.converter

import com.thenewmotion.calculator.{LookupTables, MatriceUtil}
import org.apache.commons.math3.linear.Array2DRowRealMatrix
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

class MatriceUtilTest extends SpecificationWithJUnit {

  "MatriceUtil" should {
    "calculate check digit" in {
      val contractIds = List(
        "NN123ABCDEFGHI",
        "FRXYZ123456789",
        "ITA1B2C3E4F5G6",
        "ESZU8WOX834H1D",
        "PT73902837ABCZ",
        "DE83DUIEN83QGZ",
        "DE83DUIEN83ZGQ"
      )
      //val contractId = "DE8AA1A2B3C4D5"

      val x = contractIds.map(MatriceUtil.calculateCheckDigit)

      print(x.toString)
      x must_== "T24RZDM".toList
    }

      class TestScope extends Scope {
      val testInstance = MatriceUtil
    }
  }

}
