package com.thenewmotion

package object mobilityid {
  implicit private[mobilityid] class CharacterChecks(val c: Char) extends AnyVal {
    def isAsciiUpper: Boolean = c >= 'A' && c <= 'Z'
    def isAsciiLower: Boolean = c >= 'a' && c <= 'z'
    def isAsciiLetter:Boolean = isAsciiUpper || isAsciiLower
    def isAsciiDigit: Boolean = c >= '0' && c <= '9'
    def isAsciiUpperOrDigit: Boolean = isAsciiUpper || isAsciiDigit
    def isAsciiLetterOrDigit: Boolean = isAsciiLetter || isAsciiDigit
  }
}
