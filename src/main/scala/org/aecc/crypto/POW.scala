package org.aecc.crypto

trait POW {
  def doWork(difficulty: Int, previousHash: String): String
}
