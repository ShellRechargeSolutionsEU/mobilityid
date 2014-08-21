package com.thenewmotion.calculator

import scalaz.syntax.equal._
import scalaz.std.anyVal._

private[calculator] object MatrixUtil {

  type Vec = (Int, Int)
  type Mx = (Vec, Vec)

  def vecXMx(vec: Vec, mx: Mx, mod: Int): Vec = (vec, mx) match {
    case ((v1, v2), ((m11, m12), (m21, m22))) =>
      ((v1 * m11 + v2 * m21) % mod,
       (v1 * m12 + v2 * m22) % mod)
  }

  def sumVec(v: Vec, w: Vec, mod: Int): Vec = (v, w) match {
    case ((v1, v2), (w1, w2)) => ((v1 + w1) % mod, (v2 + w2) % mod)
  }

  def mxXmx(m: Mx, n: Mx, mod: Int): Mx = (m, n) match {
    case (((m11, m12), (m21, m22)), ((n11, n12), (n21, n22))) =>
      (((m11 * n11 + m12 * n21) % mod, (m11 * n12 + m12 * n22) % mod),
       ((m21 * n11 + m22 * n21) % mod, (m21 * n12 + m22 * n22) % mod))
  }
  def mxPow(m: Mx, pow: Int, mod: Int): Mx = if (pow === 1) m else mxXmx(mxPow(m, pow - 1, mod), m, mod)

  private val p1 = ((0, 1), (1, 1))
  private val p2 = ((0, 1), (1, 2))

  val p1s: Array[Mx] = 1.to(14).map(mxPow(p1, _, 2)).toArray
  val p2s: Array[Mx] = 1.to(14).map(mxPow(p2, _, 3)).toArray

  val negP2minus15 = ((0, 2), (2, 1)) // -p2^(-15)
}
