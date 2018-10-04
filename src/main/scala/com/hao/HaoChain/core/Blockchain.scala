package com.hao.HaoChain.core

import java.util.Base64

import com.google.gson.GsonBuilder

import scala.collection.mutable.ArrayBuffer


class Blockchain {
  val blocks: ArrayBuffer[Block] = ArrayBuffer()
  var difficulty: Int = 1

  def printBlockchain(): Unit = {
    val blockJSONArray: Array[BlockJSON] = new Array[BlockJSON](blocks.size)
    for (idx <- 0 to blocks.length - 1) {
      val block = blocks(idx)

      val txJSONArray: Array[TransactionJSON] = new Array[TransactionJSON](block.transactions.size)
      for (txIdx <- 0 to block.transactions.length - 1) {
        val tx = block.transactions(txIdx)
        val txJSON = new TransactionJSON(
          StringUtils.getStringFromKey(tx.sender),
          StringUtils.getStringFromKey(tx.recipient),
          tx.value,
          Base64.getEncoder.encodeToString(tx.signature),
          tx.nonce,
          tx.transactionId
        )
        txJSONArray(txIdx) = txJSON
      }

      val blockJSON = new BlockJSON(block.timestamp, block.hash, txJSONArray)
      blockJSONArray(idx) = blockJSON
    }
    val blockchainJson: String = new GsonBuilder().setPrettyPrinting().create().toJson(blockJSONArray)
    println(blockchainJson)
  }

  def addBlock(newBlock: Block) = {
    newBlock.mineBlock(difficulty)
//    acceptBlock(newBlock)
    blocks.append(newBlock)
  }

  def acceptBlock(newBlock: Block) = {
    val isValidTransactions = newBlock.transactions.map(tx => newBlock.isTransactionValid(tx))
    val isValidBlock = isValidTransactions.forall(_ == true)
    if (isValidBlock) {
      blocks.append(newBlock)
    }
    println("Invalid transaction in block")
  }

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
