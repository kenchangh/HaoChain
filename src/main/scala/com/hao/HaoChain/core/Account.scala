package com.hao.HaoChain.core

import java.security._
import java.security.spec.ECGenParameterSpec

import com.google.gson.GsonBuilder

class AccountState(val balance: Float, val nonce: Int) {

  def this() = this(0, 0)

  override def toString: String = {
    return new GsonBuilder().setPrettyPrinting().create().toJson(this)
  }
}

trait PublicKeyCryptoWallet {
  var privateKey: PrivateKey
  var publicKey: PublicKey
}

class Account extends PublicKeyCryptoWallet {

  val keyPair: KeyPair = generateKeyPair()
  var privateKey = keyPair.getPrivate
  var publicKey = keyPair.getPublic
  var nonce = 0

  def generateKeyPair(): KeyPair = {
    val keyGen = KeyPairGenerator.getInstance("ECDSA", "BC")
    val random = SecureRandom.getInstance("SHA1PRNG")
    val ecSpec = new ECGenParameterSpec("prime192v1")
    // Initialize the key generator and generate a KeyPair
    keyGen.initialize(ecSpec, random) //256 bytes provides an acceptable security level

    val keyPair = keyGen.generateKeyPair
    // Set the public and private keys from the keyPair
    privateKey = keyPair.getPrivate
    publicKey = keyPair.getPublic
    return keyPair
  }

  def transfer(recipient: Account, value: Float, nonce: Int): Transaction = {
    val transaction = new Transaction(this, recipient, value, nonce)
    transaction.generateSignature(privateKey)
    return transaction
  }
}
