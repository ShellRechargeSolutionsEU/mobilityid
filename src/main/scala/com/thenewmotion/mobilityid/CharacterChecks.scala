package com.thenewmotion.mobilityid

private[mobilityid] class CharacterChecks(c: Char) {
  def isAsciiUpper: Boolean = c >= 'A' && c <= 'Z'
  def isAsciiDigit: Boolean = c >= '0' && c <= '9'
  def isAsciiUpperOrDigit: Boolean = isAsciiUpper || isAsciiDigit
}
