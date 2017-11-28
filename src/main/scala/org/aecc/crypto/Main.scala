package org.aecc.crypto

import java.time.Instant
import java.util.Random

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

object Main extends App {

  val logger = Logger(LoggerFactory.getLogger(this.getClass))

  val random = new Random
  val N_TRANSACTIONS = 5
  val N_BLOCKS = 5

  def newAmount = random.nextDouble

  def generateTransactions = {
    val n = random.nextInt(N_TRANSACTIONS)
    Range(0, n).flatMap(_ => {
      Seq(
        new Transaction(Block.MyWallet._1, Wallet.generateWallet._1, newAmount, Instant.now.getEpochSecond).sign(Block.MyWallet._2)
        //new Transaction(Wallet.generateWallet, Wallet.generateWallet, newAmount, Instant.now.getEpochSecond)
      )
    })
  }

  val blockChain = new BlockChain()
  logger.info(s"Creating blockchain with starting difficulty ${blockChain.currentDifficulty}")
  val initBlockChain = blockChain.createGenesisBlock()

  Range(0, N_BLOCKS).foldLeft(initBlockChain)((chain, _) => {
    val updatedChain = chain.addTransactions(generateTransactions)
    updatedChain.printWalletsBalance()
    updatedChain
  })

}
