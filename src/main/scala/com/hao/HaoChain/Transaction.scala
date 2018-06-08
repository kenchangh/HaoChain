package com.hao.HaoChain

import java.security.{PrivateKey, PublicKey}

import scala.collection.mutable.ArrayBuffer

trait GenericTransaction {
  val sender: Account
  val recipient: Account
  val value: Float
  val signature: Array[Byte]
}

class Transaction(val sender: Account, val recipient: Account,
                  val value: Float) extends GenericTransaction {
  var sequence = 0
  val outputs = ArrayBuffer[Int]()
  val signature = null

  val senderKey = sender.publicKey
  val recipientKey = recipient.publicKey

  def calculateHash(): String = {
    sender.nonce += 1
    return StringUtils.sha256(
      StringUtils.getKeyFromString(senderKey) +
        StringUtils.getKeyFromString(recipientKey) +
        value.toString +
        sender.nonce.toString
    )
  }

  def generateSignature(privateKey: PrivateKey): Array[Byte] = {
    val data: String = StringUtils.getKeyFromString(senderKey) +
      StringUtils.getKeyFromString(recipientKey) + value.toString
    return StringUtils.applyECDSASig(privateKey, data)
  }

  def verifySignature(signature: Array[Byte]): Boolean = {
    val data: String = StringUtils.getKeyFromString(senderKey) +
      StringUtils.getKeyFromString(recipientKey) + value.toString
    return StringUtils.verifyECDSASig(senderKey, data, signature)
  }
}
