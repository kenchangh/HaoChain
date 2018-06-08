package com.hao.HaoChain

import java.math.BigInteger
import java.security.{KeyPair, KeyPairGenerator, PrivateKey, PublicKey, SecureRandom}
import java.security.spec.ECGenParameterSpec

abstract class AccountState {
  val balance: BigInteger
  val nonce: Int
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
}
