package com.thenewmotion.calculator

import MatrixUtil.Mx

private[calculator] object LookupTables {

  def decode(x: Int): Mx = {
    val r2 = x / 16
    val x2 = x % 16
    val r1 = x2 / 4
    val x3 = x2 % 4
    val q2 = x3 / 2
    val q1 = x3 % 2
    ((q1, q2), (r1, r2))
  }

  def encode(x: Mx): Int = x match {
    case ((q1, q2), (r1, r2)) => q1 + q2 * 2 + r1 * 4 + r2 * 16
  }

  val encoding = Map[Char, Int](
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

  val decoding = encoding.map(_.swap)
}
