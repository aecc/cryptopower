package org.aecc.crypto

import com.typesafe.scalalogging.Logger
import org.aecc.crypto.Transaction.logger
import org.slf4j.LoggerFactory

object Transaction {

  private val logger = Logger(LoggerFactory.getLogger(this.getClass))

}

case class Transaction(fromWallet: Wallet, toWallet: Wallet, amount: Double, timestamp: Long) {

  def transactionHash: String = s"${fromWallet.address},${toWallet.address},$amount,$timestamp"

  def check(chain: BlockChain, previousBlockHash: String): Boolean = {

    val chainFromHash = chain.blocks.reverse.dropWhile(_.hash != previousBlockHash)

    val balance = -amount + chainFromHash.map(b => {
      b.transactions.reverse.map(st => {
        if (st.transaction.toWallet.address == fromWallet.address) st.transaction.amount
        else if (st.transaction.getFromWallet.address == getFromWallet.address) -st.transaction.amount
        else 0.0
      }).sum
    }).sum
    if (balance >= 0 && validateHash) true
    else {
      logger.warn("Transaction %s is invalid, balance is not enough.".format(this))
      false
    }
  }

  def sign(privateKey: String): SignedTransaction = {

    logger.debug(s"Signing transaction ${this}...")
    val sig = Signer.signature
    sig.initSign(Signer.privateKey(privateKey))
    var data = transactionHash.getBytes
    sig.update(data)
    val signature = sig.sign()
    logger.debug(s"Transaction signed: $signature")
    SignedTransaction(this, Wallet.encode(signature))

  }

  def validateHash: Boolean = true

  override def toString: String = "Transaction[%s to %s: %.2f]".format(fromWallet, toWallet, amount)

  def getFromWallet: Wallet = fromWallet

  def setFromWallet(fromWallet: Wallet): Unit = {
    copy(fromWallet = fromWallet)
  }

  def setToWallet(toWallet: Wallet): Unit = {
    copy(toWallet = toWallet)
  }

  def setAmount(amount: Double): Unit = {
    copy(amount = amount)
  }

  def setTimestamp(timestamp: Long): Unit = {
    copy(timestamp = timestamp)
  }
}

