package com.thenewmotion.iso_id_utils.calculator

import com.thenewmotion.iso_id_utils.calculator.LookupTables._
import com.thenewmotion.iso_id_utils.calculator.MatrixUtil._

private[iso_id_utils] object CheckDigitCalculator {

  def calculateCheckDigit(code: String): Char = {

    def sumEq(ps: Array[Mx], part: Mx => Vec): Vec = {
      var sum = Vec(0, 0)
      var x = 0
      while(x < ps.length) {
        val char = code.charAt(x)
        val qr: Vec = part(encoding.getOrElse(char, sys.error(s"Invalid character: $char.")))
        sum = sum + (qr * ps(x))
        x += 1
      }
      sum
    }

    val mx15 = {
      val t1 = sumEq(p1s, mx => Vec(mx.m11, mx.m12))
      val t2 = sumEq(p2s, mx => Vec(mx.m21, mx.m22)) * negP2minus15
      Mx(t1.v1 & 1, t1.v2 & 1, t2.v1 % 3, t2.v2 % 3)
    }

    decoding.getOrElse(mx15, sys.error(s"Undecodable matrix: $mx15."))
  }
}

