package com.hao.HaoChain.core

import java.security.PrivateKey

class TransactionJSON(val sender: String, val recipient: String,
                      val value: Float, val signature: String,
                      val nonce: Int, val transactionId: String)

trait GenericTransaction {
  val sender: Account
  val recipient: Account
  val value: Float
  var signature: Array[Byte]
}

class Transaction(val sender: Account, val recipient: Account,
                  val value: Float, val nonce: Int) extends GenericTransaction {
  var signature: Array[Byte] = null

  val senderKey = sender.publicKey
  val recipientKey = recipient.publicKey
  val transactionId: String = calculateHash()

  def calculateHash(): String = {
    return StringUtils.sha256(
      StringUtils.getStringFromKey(senderKey) +
        StringUtils.getStringFromKey(recipientKey) +
        value.toString +
        nonce.toString
    )
  }

  def isValidTransaction: Boolean = {
    if (!verifySignature()) {
      println("Transaction failed to verify")
      return false
    }
    return true
  }

  def generateSignature(privateKey: PrivateKey): Array[Byte] = {
    val data: String = StringUtils.getStringFromKey(senderKey) +
      StringUtils.getStringFromKey(recipientKey) + value.toString
    signature = StringUtils.applyECDSASig(privateKey, data)
    return signature
  }

  def verifySignature(): Boolean = {
    val data: String = StringUtils.getStringFromKey(senderKey) +
      StringUtils.getStringFromKey(recipientKey) + value.toString
    return StringUtils.verifyECDSASig(senderKey, data, signature)
  }
}
