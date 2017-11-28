package org.aecc.crypto

import java.security.interfaces.{DSAPrivateKey, DSAPublicKey}
import java.security.spec.PKCS8EncodedKeySpec
import java.security.{KeyFactory, Signature}

import org.aecc.crypto.Wallet.decode
import sun.security.provider.DSAPublicKeyImpl

object Signer {

  def signature: Signature = Signature.getInstance("SHA1withDSA", "SUN")
  def privateKey(privatKey: String): DSAPrivateKey = {
    val spec = new PKCS8EncodedKeySpec(decode(privatKey))
    val rsaFact = KeyFactory.getInstance("DSA")
    rsaFact.generatePrivate(spec).asInstanceOf[DSAPrivateKey]
  }
  def publicKey(data: Array[Byte]): DSAPublicKey = new DSAPublicKeyImpl(data)
  def publicKey(wallet: Wallet): DSAPublicKey = publicKey(wallet.decode)

}
