package org.aecc.crypto

import java.security.{KeyPairGenerator, SecureRandom}

import org.apache.commons.codec.binary.Base64

object Wallet {

  private val kpg: KeyPairGenerator = KeyPairGenerator.getInstance("DSA", "SUN")
  val random: SecureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN")
  kpg.initialize(512, random)

  def generateWallet: (Wallet, String) = {
    val keyPair = kpg.generateKeyPair()
    val address = encode(keyPair.getPublic.getEncoded)
    val privateKey = encode(keyPair.getPrivate.getEncoded)
    (new Wallet(address), privateKey)
  }

  def encode(string: Array[Byte]): String = new String(Base64.encodeBase64(string))
  def decode(encodedString: String): Array[Byte] = Base64.decodeBase64(encodedString)

}

case class Wallet(address: String) {

  override def toString: String = String.format("Wallet[%s]", address)
  def decode: Array[Byte] = Wallet.decode(address)

}