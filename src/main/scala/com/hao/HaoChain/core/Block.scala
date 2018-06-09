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

  def mineBlock(difficulty: Int): String = {
    val target: String = "0" * difficulty
    while (hash.substring(0, difficulty) != target) {
      nonce += 1
      hash = calculateHash()
    }
    return hash
  }


  def addTransaction(transaction: Transaction): Boolean = {
    if (transaction == null) {
      return false
    }
    val senderPublicKey = StringUtils.getKeyFromString(transaction.senderKey)
    val recipientPublicKey = StringUtils.getKeyFromString(transaction.recipientKey)
    val senderAccountState = GlobalAccountState.getAccountState(senderPublicKey)
    val recipientAccountState = GlobalAccountState.getAccountState(recipientPublicKey)
    if (previousHash != GenesisBlock.GENESIS_HASH) {
      if (!transaction.isValidTransaction) {
        println("Transaction failed to process.")
        return false
      }
      if (transaction.value > senderAccountState.balance) {
        println("Spending more than account balance")
        return false
      }
      if (transaction.nonce != senderAccountState.nonce) {
        println("Nonce does not match, double spend")
        return false
      }
    }
    transactions.append(transaction)
    val senderNewNonce = senderAccountState.nonce + 1
    val senderNewBalance = senderAccountState.balance - transaction.value
    val recipientNewBalance = recipientAccountState.balance + transaction.value
    GlobalAccountState.instance.accounts += (
      senderPublicKey -> new AccountState(senderNewBalance, senderNewNonce),
      recipientPublicKey -> new AccountState(recipientNewBalance, recipientAccountState.nonce),
    )
    return true
  }
}
