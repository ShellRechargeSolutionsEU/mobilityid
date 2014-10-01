package com.thenewmotion.mobilityid

private[mobilityid] object CheckDigit {

  import MatrixUtil._
  import LookupTables._

  def apply(code: String): Char = {
    require(code.size == p1s.size && code.size == p2s.size && code.forall(_.isLetterOrDigit))

    @annotation.tailrec
    def sumEq(ps: Array[Matrix], f: Matrix => Vec, v: Vec = Vec(0, 0), i: Int = 0): Vec = {
      if (i < ps.length) {
        val ch = code.charAt(i)
        val qr = f(encoding.getOrElse(ch, sys.error(s"Invalid character: $ch.")))
        sumEq(ps, f, v + (qr * ps(i)), i + 1)
      } else v
    }

    val t1 = sumEq(p1s, m => Vec(m.m11, m.m12))
    val t2 = sumEq(p2s, m => Vec(m.m21, m.m22)) * negP2minus15
    val m15 = Matrix(t1.v1 & 1, t1.v2 & 1, t2.v1 % 3, t2.v2 % 3)
    decoding.getOrElse(m15, sys.error(s"Undecodable matrix: $m15."))
  }

}

private[mobilityid] object LookupTables {
  import MatrixUtil.Matrix

  def decode(x: Int): Matrix = Matrix(x & 1, (x >> 1) & 1, (x >> 2) & 3, x >> 4)

  def encode(mx: Matrix): Int = mx.m11 + (mx.m12 << 1) + (mx.m21 << 2) + (mx.m22 << 4)

  val encoding: Map[Char, Matrix] = Map[Char, Int](
    '0' -> 0, '1' -> 16, '2' -> 32,
    '3' -> 4, '4' -> 20, '5' -> 36,
    '6' -> 8, '7' -> 24, '8' -> 40,
    '9' -> 2, 'A' -> 18, 'B' -> 34,
    'C' -> 6, 'D' -> 22, 'E' -> 38,
    'F' -> 10, 'G' -> 26, 'H' -> 42,
    'I' -> 1, 'J' -> 17, 'K' -> 33,
    'L' -> 5, 'M' -> 21, 'N' -> 37,
    'O' -> 9, 'P' -> 25, 'Q' -> 41,
    'R' -> 3, 'S' -> 19, 'T' -> 35,
    'U' -> 7, 'V' -> 23, 'W' -> 39,
    'X' -> 11, 'Y' -> 27, 'Z' -> 43
  ).mapValues(decode)

  val decoding: Map[Matrix, Char] = encoding.map(_.swap)
}

private[mobilityid] object MatrixUtil {

  case class Matrix(m11: Int, m12: Int, m21: Int, m22: Int) {
    def *(m: Matrix): Matrix =
      Matrix(m11 * m.m11 + m12 * m.m21, m11 * m.m12 + m12 * m.m22,
        m21 * m.m11 + m22 * m.m21, m21 * m.m12 + m22 * m.m22)
  }

  case class Vec(v1: Int, v2: Int) {
    def +(v: Vec): Vec = Vec(v1 + v.v1, v2 + v.v2)

    def *(m: Matrix): Vec = new Vec(v1 * m.m11 + v2 * m.m21, v1 * m.m12 + v2 * m.m22)
  }
  private val p1 = Matrix(0, 1, 1, 1)
  private val p2 = Matrix(0, 1, 1, 2)

  val p1s: Array[Matrix] = Stream.iterate(p1)(_ * p1).take(14).toArray
  val p2s: Array[Matrix] = Stream.iterate(p2)(_ * p2).take(14).toArray

  val negP2minus15 = Matrix(0, 2, 2, 1) // -p2^(-15)
}



