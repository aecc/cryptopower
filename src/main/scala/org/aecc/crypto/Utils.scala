package org.aecc.crypto

import org.apache.commons.codec.binary.Base64
import org.apache.commons.text.{CharacterPredicates, RandomStringGenerator}

object Utils {

  def randomStringGenerator: RandomStringGenerator =
    new RandomStringGenerator.Builder().withinRange('0', 'z').filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS).build
  def createNonce: String = createNonce(Common.MinDifficulty)
  def createNonce(difficulty: Int): String = (0 until difficulty).map(_ => "0").mkString("")
  def encode(string: Array[Byte]): String = new String(Base64.encodeBase64(string))
  def encode(string: String): String = new String(Base64.encodeBase64(string.getBytes))
  def decode(encodedString: String): Array[Byte] = Base64.decodeBase64(encodedString)

}
