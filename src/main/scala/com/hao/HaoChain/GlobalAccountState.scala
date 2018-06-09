package com.hao.HaoChain

import java.security.PublicKey

import scala.collection.immutable.HashMap

class GlobalAccountState {
  var accounts: HashMap[String, AccountState] = new HashMap[String, AccountState]()
}

object GlobalAccountState {

  var instance: GlobalAccountState = null

  def initialize(): GlobalAccountState = {
    instance =  new GlobalAccountState()
    return instance
  }

  def addAccount(account: Account) = {
    val publicKey = StringUtils.getKeyFromString(account.publicKey)
    val freshAccountState = new AccountState()
    instance.accounts += (publicKey -> freshAccountState)
  }

  def newAccount(): Account = {
    val account = new Account()
    addAccount(account)
    return account
  }

  def getAccountState(key: String): AccountState = {
    return instance.accounts(key)
  }

  def getBalance(account: Account): Float = {
    val accountState: AccountState = instance.accounts(StringUtils.getKeyFromString(account.publicKey))
    return accountState.balance
  }
}
