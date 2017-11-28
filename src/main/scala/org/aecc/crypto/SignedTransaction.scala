package org.aecc.crypto

import com.typesafe.scalalogging.Logger
import org.aecc.crypto.SignedTransaction.logger
import org.slf4j.LoggerFactory

case class SignedTransaction(
  transaction: Transaction,
  signature: String) {

  def validateSignature: Boolean = {

    logger.debug(s"Verifying transaction ${this}...")
    val sig = Signer.signature
    sig.initVerify(Signer.publicKey(transaction.fromWallet))
    var data = transaction.transactionHash.getBytes
    sig.update(data)
    val verified = sig.verify(Wallet.decode(signature))
    logger.debug(s"Transaction ${if (verified) "" else "not"} valid.")
    verified
  }

  def validateTransaction(chain: BlockChain, previousBlockHash: String): Boolean =
    transaction.check(chain, previousBlockHash) && validateSignature

}

object SignedTransaction {

  private val logger = Logger(LoggerFactory.getLogger(this.getClass))

}
