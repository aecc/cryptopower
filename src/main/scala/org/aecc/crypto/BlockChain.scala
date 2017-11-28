package org.aecc.crypto

import com.typesafe.scalalogging.Logger
import org.apache.commons.codec.digest.Sha2Crypt
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.util.{Success, Try}
import BlockChain._

object BlockChain {
  private val logger = Logger(LoggerFactory.getLogger(this.getClass))
}

case class BlockChain(
  currentDifficulty: Int = Common.MinDifficulty,
  blocks: List[Block] = List()) extends POW {

  def setBlocks(newBlocks: List[Block]): BlockChain = {
    this.copy(blocks = newBlocks)
  }
  
  def createGenesisBlock(): BlockChain = {
    logger.info("Creating genesis block...")
    this.copy(blocks = List(Block.createGenesisBlock(currentDifficulty)))
  } 

  def addTransactions(transactions: Seq[SignedTransaction]): BlockChain = {
    val lastHash = String.valueOf(getLastBlock.hash)
    logger.info("Adding block...")
    val message = doWork(currentDifficulty, lastHash)
    val newBlock = Try(Block.createBlock(this, getLastBlock.hash, transactions, getLastBlock.blockId + 1, message, currentDifficulty))

    newBlock match {
      case Success(s) =>
        logger.info("Block added: " + s.blockId)
        logTransactions()
        copy(blocks = blocks :+ s)
      case _ =>
        logger.warn("Block couldn't be added because it's invalid.")
        this
    }

  }

  def getLastBlock: Block = blocks.last

  def logTransactions(): Unit = {
    println("*** Start of Transactions ***")
    blocks.foreach(block => {
      println(s"- ${block.toString}")
      block.transactions.foreach(transaction => println(transaction.toString))
    })
    println("*** End of Transactions ***")
  }

  def printWalletsBalance(): Unit = {
    println("Wallets balances: ")
    val map = new mutable.HashMap[String, Double]
    blocks.foreach(block => {
        block.transactions.foreach(sigTransaction => {
          map(sigTransaction.transaction.toWallet.address) = sigTransaction.transaction.amount + map.getOrElse(sigTransaction.transaction.toWallet.address, 0.0)
          map(sigTransaction.transaction.fromWallet.address) = map.getOrElse(sigTransaction.transaction.getFromWallet.address, 0.0) - sigTransaction.transaction.amount
        })
      })
    map.toStream.sortBy(_._2).reverse.foreach(t => {
      val newWallet = new Wallet(t._1)
      if (t._1 == Block.MyWallet._1.address)
        println("* %s: %.2f *".format(newWallet , t._2))
      else
        println("%s: %.2f".format(newWallet, t._2))
    })
    val totalBalance = map.values.sum
    println("Total system balance: %.2f".format(totalBalance))
  }

  override def doWork(difficulty: Int, previousHash: String): String = {

    val nonce = Utils.createNonce

    logger.debug("Performing proof of work...")
    logger.debug("Difficulty is " + difficulty)
    logger.debug("Nonce is " + nonce)

    var hash = ""
    val startTime = System.nanoTime
    var i = 0

    import util.control.Breaks._

    breakable {
      while (true) {
        hash = Sha2Crypt.sha256Crypt(previousHash.concat(i.toString).getBytes, Common.Salt)
        if (hash.endsWith(nonce)) break
        i += 1
      }
    }

    val endTime = System.nanoTime
    val duration = (endTime - startTime) / 1000000000L
    val message = i.toString
    val encrypted = Sha2Crypt.sha256Crypt(previousHash.concat(message).getBytes, Common.Salt)

    logger.debug(s"Iterations: $i")
    logger.debug(s"Previous block hash: $previousHash")
    logger.debug(s"Checking hash: ${encrypted == hash}")
    logger.debug(s"Message: $message")
    logger.debug(s"Hash: $encrypted")
    logger.debug(s"Proof of work finished. Time (s): $duration")
    message
  }

}
