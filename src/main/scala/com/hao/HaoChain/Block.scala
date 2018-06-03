package com.hao.HaoChain

import java.util.Date

class Block(val previousHash: String, val data: String) {
  val timestamp: Long = new Date().getTime()
  var hash: String = this.calculateHash()
  private var nonce: Int = 0

  def calculateHash(): String = {
    return StringUtils.sha256(
      previousHash + timestamp.toString + nonce.toString + data)
  }

  def mineBlock(difficulty: Int): String = {
    val target: String = "0" * difficulty
    while (hash.substring(0, difficulty) != target) {
      nonce += 1
      hash = calculateHash()
    }
    return hash
  }
}
