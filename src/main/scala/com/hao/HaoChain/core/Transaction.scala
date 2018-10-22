package com.hao.HaoChain.core

import java.security.{PrivateKey, PublicKey}
import java.util.Base64

import com.google.gson.{Gson, GsonBuilder}
import com.hao.HaoChain.models.NewTxnMessage
import com.hao.HaoChain.networking.UDPClient

class TransactionJSON(val sender: String, val recipient: String,
                      val value: Float, val signature: String,
                      val nonce: Int, val transactionId: String)

trait GenericTransaction {
  val sender: PublicKey
  val recipient: PublicKey
  val value: Float
  var signature: Array[Byte]
}

object Transaction {
  def toTransactionJSON(tx: Transaction): TransactionJSON = {
    var signature = ""
    if (!StringUtils.getStringFromKey(tx.sender).equals(GlobalAccountState.coinbaseAccount)) {
      signature = Base64.getEncoder.encodeToString(tx.signature)
    }

    val txJSON = new TransactionJSON(
      StringUtils.getStringFromKey(tx.sender),
      StringUtils.getStringFromKey(tx.recipient),
      tx.value,
      signature,
      tx.nonce,
      tx.transactionId
    )
    return txJSON
  }

  def fromTransactionJSON(txJSON: TransactionJSON): Transaction = {
    val senderPublicKey = StringUtils.getPublicKeyFromString(txJSON.sender)
    val recipientPublicKey = StringUtils.getPublicKeyFromString(txJSON.recipient)
    val txn = new Transaction(
      senderPublicKey, recipientPublicKey, txJSON.value, txJSON.nonce
    )
    txn.signature = Base64.getDecoder.decode(txJSON.signature)
    txn.transactionId = txJSON.transactionId
    return txn
  }

  def serializeToJSON(tx: Transaction): String = {
    val txJSON = Transaction.toTransactionJSON(tx)
    val jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(txJSON)
    return jsonString
  }

  def deserializeFromJSON(jsonString: String): Transaction = {
    val gson = new Gson();
    val txn = gson.fromJson(jsonString, classOf[Transaction])
    return txn
  }
}

class Transaction(val sender: PublicKey, val recipient: PublicKey,
                  val value: Float, val nonce: Int) extends GenericTransaction {
  var signature: Array[Byte] = null

  var transactionId: String = calculateHash()

  def calculateHash(): String = {
    return StringUtils.sha256(
      StringUtils.getStringFromKey(sender) +
        StringUtils.getStringFromKey(recipient) +
        value.toString +
        nonce.toString
    )
  }

  def isValidTransaction: Boolean = {
    if (StringUtils.getStringFromKey(sender).equals(GlobalAccountState.coinbaseAccount)) {
      return true
    }
    if (!verifySignature()) {
      println("Transaction failed to verify")
      return false
    }
    return true
  }

  def generateSignature(privateKey: PrivateKey): Array[Byte] = {
    val data: String = StringUtils.getStringFromKey(sender) +
      StringUtils.getStringFromKey(recipient) + value.toString
    signature = StringUtils.applyECDSASig(privateKey, data)
    return signature
  }

  def verifySignature(): Boolean = {
    val data: String = StringUtils.getStringFromKey(sender) +
      StringUtils.getStringFromKey(recipient) + value.toString
    return StringUtils.verifyECDSASig(sender, data, signature)
  }

  def broadcast(nodeId: String): Unit = {
    val sendingThread = new Thread(() => {
      val newTxnMessage = new NewTxnMessage(this)
      val udpClient = new UDPClient(nodeId)
      udpClient.sendMessage(newTxnMessage.serializeToJSON())
    })
    sendingThread.start()
  }

}
