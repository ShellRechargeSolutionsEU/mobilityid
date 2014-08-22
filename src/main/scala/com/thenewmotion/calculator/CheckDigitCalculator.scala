package com.thenewmotion.calculator

import com.thenewmotion.calculator.LookupTables._
import com.thenewmotion.calculator.MatrixUtil._

object CheckDigitCalculator {

  def calculateCheckDigit(code: String): Char = {

    def sumEq(ps: Array[Mx], part: Mx => Vec, code: String): Vec = {
      val vecs = 0.to(13).map{ x =>
        val char = code.charAt(x)
        val qr: Vec = part(encoding.getOrElse(char, sys.error(s"Unencodable character: $char.")))
        val p: Mx = ps(x)
        vecXMx(qr, p)
      }
      vecs.foldLeft((0, 0))(sumVec)
    }

    val t1 = sumEq(p1s, _._1, code) match {
      case (q1 ,q2) => (q1 % 2, q2 % 2)
    }

    val t2 = vecXMx(sumEq(p2s, _._2, code), negP2minus15) match {
      case (r1, r2) => (r1 % 3, r2 % 3)
    }

    decoding.getOrElse((t1, t2), sys.error(s"Undecodable matrix: ${(t1, t2)}."))
  }
}

