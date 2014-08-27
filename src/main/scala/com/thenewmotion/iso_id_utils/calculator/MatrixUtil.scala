package com.thenewmotion.iso_id_utils.calculator

private[calculator] object MatrixUtil {

  case class Vec(v1: Int, v2: Int) {
    def +(v: Vec): Vec = Vec(v1 + v.v1, v2 + v.v2)

    def *(m: Mx): Vec = new Vec(v1 * m.m11 + v2 * m.m21, v1 * m.m12 + v2 * m.m22)
  }
  case class Mx(m11: Int, m12: Int, m21: Int, m22: Int) {
    def *(m: Mx): Mx =
      Mx(m11 * m.m11 + m12 * m.m21, m11 * m.m12 + m12 * m.m22,
        m21 * m.m11 + m22 * m.m21, m21 * m.m12 + m22 * m.m22)
  }

  private val p1 = Mx(0, 1, 1, 1)
  private val p2 = Mx(0, 1, 1, 2)

  val p1s: Array[Mx] = Stream.iterate(p1)(_ * p1).take(14).toArray
  val p2s: Array[Mx] = Stream.iterate(p2)(_ * p2).take(14).toArray

  val negP2minus15 = Mx(0, 2, 2, 1) // -p2^(-15)
}
