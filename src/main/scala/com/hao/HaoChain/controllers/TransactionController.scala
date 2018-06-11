package com.hao.HaoChain.controllers

import java.security.PublicKey

import com.hao.HaoChain.core.{Account, GlobalAccountState, StringUtils, Transaction}

class TransactionController {

  def generateTransaction(currentUser: Account, recipient: String, value: Float): Unit = {
//    val genesisTransaction = new Transaction(coinbase, account1, 100, coinbase.nonce)
    val recipientPublicKey = StringUtils.getPublicKeyFromString(recipient)
    val userPublicKeyStr = StringUtils.getStringFromKey(currentUser.publicKey)
    val accountState = GlobalAccountState.getAccountState(userPublicKeyStr)
    val transaction = currentUser.transfer(recipientPublicKey, value, accountState.nonce)
  }

}
