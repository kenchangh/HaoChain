package com.hao.HaoChain

import java.math.BigInteger
import java.security.{PrivateKey, PublicKey}

import scala.collection.mutable.ArrayBuffer

class TransactionJSON(val sender: String, val recipient: String,
                      val value: Float, val signature: String, val nonce: Int)

trait GenericTransaction {
  val sender: Account
  val recipient: Account
  val value: Float
  var signature: Array[Byte]
}

class Transaction(val sender: Account, val recipient: Account,
                  val value: Float, val nonce: Int) extends GenericTransaction {
  val outputs = ArrayBuffer[Int]()
  var signature: Array[Byte] = null

  val senderKey = sender.publicKey
  val recipientKey = recipient.publicKey

  def calculateHash(): String = {
    return StringUtils.sha256(
      StringUtils.getKeyFromString(senderKey) +
        StringUtils.getKeyFromString(recipientKey) +
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
    val data: String = StringUtils.getKeyFromString(senderKey) +
      StringUtils.getKeyFromString(recipientKey) + value.toString
    signature = StringUtils.applyECDSASig(privateKey, data)
    return signature
  }

  def verifySignature(): Boolean = {
    val data: String = StringUtils.getKeyFromString(senderKey) +
      StringUtils.getKeyFromString(recipientKey) + value.toString
    return StringUtils.verifyECDSASig(senderKey, data, signature)
  }
}
