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

  private val p1 = ((0, 1), (1, 1))
  private val p2 = ((0, 1), (1, 2))

  val p1s: Array[Mx] = Stream.iterate(p1)(mxXmx(_, p1, 2)).take(14).toArray
  val p2s: Array[Mx] = Stream.iterate(p2)(mxXmx(_, p2, 3)).take(14).toArray

  val negP2minus15 = ((0, 2), (2, 1)) // -p2^(-15)
}
