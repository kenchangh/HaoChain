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

object Account {
  def fromKeypair(publicKey: PublicKey, privateKey: PrivateKey): Account = {
    val publicKeyStr = StringUtils.getStringFromKey(publicKey)
    val accountState = GlobalAccountState.getAccountState(publicKeyStr)
    new Account(publicKey, privateKey, accountState.nonce)
  }
}

class Account extends PublicKeyCryptoWallet {

  val keyPair: KeyPair = generateKeyPair()
  var privateKey = keyPair.getPrivate
  var publicKey = keyPair.getPublic
  var nonce = 0

  /*
  Construct from GlobalAccountState
   */
  def this(publicKey: PublicKey, privateKey: PrivateKey, nonce: Int) {
    this()
  }

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

  def transfer(recipient: PublicKey, value: Float, nonce: Int): Transaction = {
    val transaction = new Transaction(this.publicKey, recipient, value, nonce)
    transaction.generateSignature(privateKey)
    return transaction
  }
}
