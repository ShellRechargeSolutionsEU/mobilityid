package com.thenewmotion.evcoid.calculator

import MatrixUtil.Mx

private[calculator] object LookupTables {

  def decode(x: Int): Mx = Mx(x & 1, (x >> 1) & 1, (x >> 2) & 3, x >> 4)

  def encode(mx: Mx): Int = mx.m11 + (mx.m12 << 1) + (mx.m21 << 2) + (mx.m22 << 4)

  val encoding: Map[Char, Mx] = Map[Char, Int](
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

  val decoding: Map[Mx, Char] = encoding.map(_.swap)
}
