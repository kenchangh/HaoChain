package com.hao.HaoChain

import java.util.Date

import scala.collection.immutable.HashMap
import scala.collection.mutable.ArrayBuffer

class BlockJSON(val timestamp: Long, val hash: String, val transactions: Array[TransactionJSON])

class Block(var accounts: HashMap[String, AccountState], val previousHash: String, val data: String) {
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
    val accountState = accounts(senderPublicKey)
    if (previousHash != GenesisBlock.GENESIS_HASH) {
      if (transaction.isValidTransaction) {
        println("Transaction failed to process.")
        return false
      }
      if (accountState.balance < transaction.value) {
        println("Spending more than balance")
        return false
      }
      if (transaction.sender.nonce != accountState.nonce) {
        println("Nonce does not match, double spend")
        return false
      }
    }
    transactions.append(transaction)
    val newAccountBalance = accountState.balance - transaction.value
    val newAccountNonce = accountState.nonce + 1
    accounts += (senderPublicKey -> new AccountState(newAccountBalance, newAccountNonce))
    return true
  }
}
