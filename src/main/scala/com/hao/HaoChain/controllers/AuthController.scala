package com.hao.HaoChain.controllers

import com.hao.HaoChain.core.{Account, GlobalAccountState, StringUtils}

object AuthController {
  def registerAccount(password: String): Account = {
    val userAccount = GlobalAccountState.newAccount()
    KeyFileController.writeKeyFile(userAccount.publicKey, userAccount.privateKey, password)
    userAccount
  }

  def loginAccount(password: String): Account = {
    val (publicKey, privateKey) = KeyFileController.readKeyFile(password)
    Account.fromKeypair(publicKey, privateKey)
  }
}
