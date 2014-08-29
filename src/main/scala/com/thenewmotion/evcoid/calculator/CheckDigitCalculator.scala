package com.thenewmotion.evcoid.calculator

import com.thenewmotion.evcoid.calculator.LookupTables._
import com.thenewmotion.evcoid.calculator.MatrixUtil._

private[evcoid] object CheckDigitCalculator {

  def calculateCheckDigit(code: String): Char = {

    @annotation.tailrec
    def sumEq(ps: Array[Mx], f: Mx => Vec, v: Vec = Vec(0, 0), i: Int = 0): Vec = {
      if (i < ps.length) {
        val ch = code.charAt(i)
        val qr = f(encoding.getOrElse(ch, sys.error(s"Invalid character: $ch.")))
        sumEq(ps, f, v + (qr * ps(i)), i + 1)
      } else v
    }

    val t1 = sumEq(p1s, m => Vec(m.m11, m.m12))
    val t2 = sumEq(p2s, m => Vec(m.m21, m.m22)) * negP2minus15
    val m15 = Mx(t1.v1 & 1, t1.v2 & 1, t2.v1 % 3, t2.v2 % 3)
    decoding.getOrElse(m15, sys.error(s"Undecodable matrix: $m15."))
  }
}

