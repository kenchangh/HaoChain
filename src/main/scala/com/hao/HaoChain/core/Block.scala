package com.hao.HaoChain.core

import java.util.{Base64, Date}

import com.google.gson.{Gson, GsonBuilder}
import com.hao.HaoChain.models.{NewBlockMessage, NewTxnMessage}
import com.hao.HaoChain.networking.UDPClient

import scala.collection.mutable.ArrayBuffer

class BlockJSON(val timestamp: Long, val previousHash: String, val hash: String,
                val nonce: Int, val transactions: Array[TransactionJSON], val data: String, val miner: String)

object Block {

  def toBlockJson(block: Block): BlockJSON = {
    val txJSONArray: Array[TransactionJSON] = new Array[TransactionJSON](block.transactions.size)
    for (txIdx <- 0 to block.transactions.length - 1) {
      val tx = block.transactions(txIdx)
      val txJSON = Transaction.toTransactionJSON(tx)
      txJSONArray(txIdx) = txJSON
    }

    val blockJSON = new BlockJSON(
      block.timestamp, block.previousHash,
      block.hash, block.nonce, txJSONArray, block.data, block.miner)
    return blockJSON
  }

  def serializeToJSON(block: Block): String = {
    val blockJSON = Block.toBlockJson(block)
    val jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(blockJSON)
    return jsonString
  }

  def deserializeFromJSON(jsonString: String): Block = {
    val gson = new Gson()
    val blockJSON = gson.fromJson(jsonString, classOf[BlockJSON])
    val block = new Block(blockJSON.previousHash, blockJSON.data)
    block.miner = blockJSON.miner
    block.hash = blockJSON.hash
    block.timestamp = blockJSON.timestamp
    block.nonce = blockJSON.nonce
    block.transactions = ArrayBuffer[Transaction]()
    for (txIdx <- blockJSON.transactions.indices) {
      val txJSON = blockJSON.transactions(txIdx)
      block.transactions.append(Transaction.fromTransactionJSON(txJSON))
    }
    return block
  }
}

/**
  * TODO: NOT ALTERING ACCOUNTS STATE, NEEDS TO BE GLOBAL STATE
  *
  * @param previousHash
  * @param data
  */
class Block(val previousHash: String, val data: String) {
  var timestamp: Long = new Date().getTime()
  var hash: String = this.calculateHash()
  var transactions: ArrayBuffer[Transaction] = new ArrayBuffer[Transaction]()
  var miner: String = ""
  private var nonce: Int = 0

  def calculateHash(): String = {
    return StringUtils.sha256(
      previousHash + timestamp.toString + nonce.toString + data)
  }

  def broadcast(myAccount: Account, blockHeight: Int): Unit = {
    val sendingThread = new Thread(() => {
      val myPublicKey = StringUtils.getStringFromKey(myAccount.publicKey)
      val udpClient = new UDPClient(myPublicKey)
      val newBlockMessage = new NewBlockMessage(this)
      udpClient.sendMessage(newBlockMessage.serializeToJSON())
    })
    sendingThread.start()
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

      // only non-coinbase transactions go through validation
      if (senderPublicKey != GlobalAccountState.coinbaseAccount) {
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
    }
    true
  }

  /**
    * This adds the transaction into the 'mempool'. Which is a list of transactions to
    * be added into the next block
    *
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
