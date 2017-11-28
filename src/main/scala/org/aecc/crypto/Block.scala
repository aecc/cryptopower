package org.aecc.crypto

import java.time.Instant

import com.typesafe.scalalogging.Logger
import org.apache.commons.codec.digest.Sha2Crypt
import org.slf4j.LoggerFactory

case class Block (
  previousHash: String,
  transactions: Seq[SignedTransaction],
  blockId: Int,
  message: String,
  difficulty: Int,
  version: Int = Block.DefaultVersion,
  timestamp: Long = Instant.now.getEpochSecond) {

  def hash: String = Sha2Crypt.sha256Crypt(
    Utils.encode(transactions.map(_.transaction.transactionHash).mkString("").concat(previousHash).concat(message)).getBytes, Common.Salt)
  def isGenesisBlock: Boolean = blockId == Block.GenesisBlockId
  def validateBlock(previousHash: String): Boolean = Block.validateMessage(message, difficulty, previousHash)

  override def toString: String = s"Block[$blockId]"

}

object Block {

  private val logger = Logger(LoggerFactory.getLogger(this.getClass))
  private val DefaultVersion = 1

  val GenesisBlockId = 0
  val GenesisWallet: (Wallet, String) = Wallet.generateWallet
  val InitialAmount = 20.0
  val MyWallet: (Wallet, String) = Wallet.generateWallet
  val NoBlockHash = "NO_PREVIOUS_BLOCK"
  val MiningAmount = 1.0

  def createGenesisBlock(difficulty: Int): Block = {
    val now = Instant.now.getEpochSecond
    new Block(NoBlockHash, List(
      new Transaction(GenesisWallet._1, MyWallet._1, InitialAmount, now).sign(GenesisWallet._2)), GenesisBlockId, GenesisBlockId.toString, difficulty)
  }

  def createBlock(chain: BlockChain, previousHash: String, transactions: Seq[SignedTransaction], blockId: Int, message: String, difficulty: Int): Block = {
    if (Block.validateMessage(message, difficulty, previousHash)) {
      val validTransactions = transactions
        .filter(_.validateTransaction(chain, previousHash))
        .sortBy(_.transaction.timestamp)

      logger.info("Adding to wallet %s mining amount for PoW: %.2f".format(MyWallet, MiningAmount))
      val enrichedTransactions = validTransactions :+
        new Transaction(GenesisWallet._1, MyWallet._1, MiningAmount, Instant.now.getEpochSecond).sign(GenesisWallet._2)

      new Block(previousHash, enrichedTransactions, blockId, message, difficulty)
    }
    else throw new InvalidBlockException
  }

  def validateMessage(message: String, difficulty: Int, previousBlockHash: String): Boolean = {
    logger.debug("Validating message: " + message)
    logger.debug(s"Previous block hash: $previousBlockHash")
    // Trying to reproduce hash using current message and salt
    val hash = Sha2Crypt.sha256Crypt(previousBlockHash.concat(message).getBytes, Common.Salt)
    logger.debug("Validating hash: " + hash)
    hash.endsWith(Utils.createNonce(difficulty))
  }
}
