package com.hao.HaoChain

import java.security.{PrivateKey, PublicKey}

import scala.collection.mutable.ArrayBuffer

trait InputOutputTransaction {
  val sender: PublicKey
  val recipient: PublicKey
  val value: Float
  val signature: Array[Byte]
  val inputs: ArrayBuffer[Int]
  val outputs: ArrayBuffer[Int]
}

class Transaction(val sender: PublicKey, val recipient: PublicKey,
                  val value: Float, val inputs: ArrayBuffer[Int]) extends InputOutputTransaction {
  var sequence = 0
  val outputs = ArrayBuffer[Int]()
  val signature = null

  def calculateHash(): String = {
    sequence += 1
    return StringUtils.sha256(
      StringUtils.getKeyFromString(sender) +
        StringUtils.getKeyFromString(recipient) +
        value.toString +
        sequence
    )
  }

  def generateSignature(privateKey: PrivateKey): Array[Byte] = {
    val data: String = StringUtils.getKeyFromString(sender) +
      StringUtils.getKeyFromString(recipient) + value.toString
    return StringUtils.applyECDSASig(privateKey, data)
  }

  def verifySignature(signature: Array[Byte]): Boolean = {
    val data: String = StringUtils.getKeyFromString(sender) +
      StringUtils.getKeyFromString(recipient) + value.toString
    return StringUtils.verifyECDSASig(sender, data, signature)
  }
}
