package com.thenewmotion

package object mobilityid {
  implicit def char2CharChecks(c: Char) = new CharacterChecks(c)
}