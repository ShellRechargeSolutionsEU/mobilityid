package com.thenewmotion.calculator

import com.thenewmotion.calculator.LookupTables._
import com.thenewmotion.calculator.MatrixUtil._

object CheckDigitCalculator {

  def calculateCheckDigit(code: String): Char = {

    def sumEq(ps: Array[Mx], part: Mx => Vec, mod: Int, code: String): Vec = {
      val vecs = 0.to(13).map{ x =>
        val qr: Vec = part(encoding.getOrElse(code.charAt(x), sys.error("Unencodable character")))
        val p: Mx = ps(x)
        vecXMx(qr, p, mod)
      }
      vecs.foldLeft((0, 0))(sumVec(_, _, mod))
    }

    val t1 = sumEq(p1s, _._1, 2, code)
    val t2 = vecXMx(sumEq(p2s, _._2, 3, code), negP2minus15, 3)

    decoding.getOrElse((t1, t2), sys.error("Undecodable matrix"))
  }
}

