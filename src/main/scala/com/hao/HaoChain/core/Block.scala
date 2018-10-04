package com.hao.HaoChain.core

import java.util.Date

import scala.collection.mutable.ArrayBuffer

class BlockJSON(val timestamp: Long, val hash: String, val transactions: Array[TransactionJSON])

/**
  * TODO: NOT ALTERING ACCOUNTS STATE, NEEDS TO BE GLOBAL STATE
  * @param accounts
  * @param previousHash
  * @param data
  */
class Block(val previousHash: String, val data: String) {
  val timestamp: Long = new Date().getTime()
  var hash: String = this.calculateHash()
  var transactions: ArrayBuffer[Transaction] = new ArrayBuffer[Transaction]()
  private var nonce: Int = 0

  def calculateHash(): String = {
    return StringUtils.sha256(
      previousHash + timestamp.toString + nonce.toString + data)
  }

  def processTransactionInBlock(transaction: Transaction): Unit = {
    val senderPublicKey = StringUtils.getStringFromKey(transaction.sender)
    val recipientPublicKey = StringUtils.getStringFromKey(transaction.recipient)
    val senderAccountState = GlobalAccountState.getAccountState(senderPublicKey)
    val recipientAccountState = GlobalAccountState.getAccountState(recipientPublicKey)

    if (!isTransactionValid(transaction)) {
      return
    }
    //    val senderNewNonce = senderAccountState.nonce + 1
    val senderNewBalance = senderAccountState.balance - transaction.value
    val recipientNewBalance = recipientAccountState.balance + transaction.value
    GlobalAccountState.instance.accounts += (
      senderPublicKey -> new AccountState(senderNewBalance, senderAccountState.nonce),
      recipientPublicKey -> new AccountState(recipientNewBalance, recipientAccountState.nonce),
    )
  }

  def mineBlock(difficulty: Int): String = {
    val target: String = "0" * difficulty
    while (hash.substring(0, difficulty) != target) {
      nonce += 1
      hash = calculateHash()
    }

    for (transaction <- transactions) {
      processTransactionInBlock(transaction)
    }

    return hash
  }

  // how can you check the validity of the transaction
  // if the user's nonce cannot be checked at Point in Time
  // because right now it's checking the GlobalAccountState, which is the most updated state
  def isTransactionValid(transaction: Transaction): Boolean = {
    if (transaction == null) {
      return false
    }
    val recipientPublicKey = StringUtils.getStringFromKey(transaction.recipient)
    val senderPublicKey = StringUtils.getStringFromKey(transaction.sender)
    val senderAccountState = GlobalAccountState.getAccountState(senderPublicKey)
    if (previousHash != GenesisBlock.GENESIS_HASH) {
      if (!transaction.isValidTransaction) {
        println("Transaction failed to process.")
        return false
      }

      if (senderPublicKey == recipientPublicKey) {
        println("Recipient address cannot be the same as sender")
        return false
      }
      if (transaction.value > senderAccountState.balance) {
        println("Spending more than account balance")
        return false
      }
      if (transaction.nonce <= senderAccountState.nonce) {
        println(transaction.nonce, senderAccountState.nonce)
        println("Nonce does not match, double spend")
        return false
      }
    }
    true
  }

  /**
    * This adds the transaction into the 'mempool'. Which is a list of transactions to
    * be added into the next block
    * @param transaction
    */
  def addTransaction(transaction: Transaction): Unit = {
    val senderPublicKey = StringUtils.getStringFromKey(transaction.sender)
    val recipientPublicKey = StringUtils.getStringFromKey(transaction.recipient)
    val senderAccountState = GlobalAccountState.getAccountState(senderPublicKey)
    val recipientAccountState = GlobalAccountState.getAccountState(recipientPublicKey)

    if (!isTransactionValid(transaction)) {
      return
    }
    transactions.append(transaction)
  }
}
