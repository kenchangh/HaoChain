package com.hao.HaoChain

import scala.collection.mutable.ArrayBuffer

class Blockchain {
  val blocks: ArrayBuffer[Block] = ArrayBuffer()
  var difficulty: Int = 1

  def isChainValid(): Boolean = {
    var currentBlock: Block = null
    var previousBlock: Block = null
    val hashTarget: String = "0" * difficulty

    for (idx <- 1 to blocks.length - 1) {
      currentBlock = blocks(idx)
      previousBlock = blocks(idx - 1)

      println(currentBlock.hash, currentBlock.calculateHash())
      println(previousBlock.hash, currentBlock.previousHash)

      if (currentBlock.hash != currentBlock.calculateHash()) {
        return false
      }
      if (previousBlock.hash != currentBlock.previousHash) {
        return false
      }
      // check if block is mined
      if (currentBlock.hash.substring(0, difficulty) != hashTarget) {
        return false
      }
    }
    return true
  }
}
