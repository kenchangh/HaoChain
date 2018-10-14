package com.hao.HaoChain.core

import java.io._
import java.lang.IllegalStateException

import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson._

import scala.collection.JavaConversions._
import scala.collection.immutable.HashMap

class GlobalAccountState {
  var accounts: HashMap[String, AccountState] = new HashMap[String, AccountState]()
}

object GlobalAccountState {

  val accountsPath = StringUtils.concatPath(HaoChain.chainDirectoryPath, "accounts.json")
  var instance: GlobalAccountState = null
  val coinbaseAccount = "MEkwEwYHKoZIzj0CAQYIKoZIzj0DAQEDMgAEUS41g2uxmAiVENfauThrKkTitI81q0gKK8w4VGeHxsg/FNixg/jfI5K/14Ipb1Kj"

  /**
    * initialize will either
    * a) read from existing accounts.json file for state
    * b) create a new state
    *
    * @return GlobalAccountState
    */
  def initialize(): GlobalAccountState = {
    instance = new GlobalAccountState()

    // must add coinbase account first to process miner rewards
    val key = coinbaseAccount
    val accountState = new AccountState(0, 0)
    instance.accounts += (key -> accountState)

    val file = new File(accountsPath)
    if (file.exists()) {
      try {
        readAccountsFile()
      } catch {
        case e: IllegalStateException =>
        case e: FileNotFoundException =>
          StringUtils.writeToPath(accountsPath, "{"+coinbaseAccount+":{\"nonce\": 0, \"balance\":0} }")
          return instance
      }
    }
    return instance
  }

  def readAccountsFile(): Unit = {
    val reader = new JsonReader(new FileReader(accountsPath))
    val jsonElement = new JsonParser().parse(reader)
    val jsonObject = jsonElement.getAsJsonObject()

    for (entry: java.util.Map.Entry[String, JsonElement] <- jsonObject.entrySet()) {
      val key = entry.getKey
      val accountStateJson = entry.getValue.getAsJsonObject
      val balance = accountStateJson.getAsJsonPrimitive("balance").getAsFloat
      val nonce = accountStateJson.getAsJsonPrimitive("nonce").getAsInt
      val accountState = new AccountState(balance, nonce)
      instance.accounts += (key -> accountState)
    }
  }

  def updateAccountsFile(): Unit = {
    val accountsDict = new JsonObject()

    for ((key, accountState) <- instance.accounts) {
      val accountStateJson = new JsonObject()
      accountStateJson.addProperty("nonce", accountState.nonce)
      accountStateJson.addProperty("balance", accountState.balance)
      accountsDict.add(key, accountStateJson)
    }
    val accountsJson = new Gson().toJson(accountsDict)
    StringUtils.writeToPath(accountsPath, accountsJson)
  }

  def addAccount(account: Account) = {
    val publicKey = StringUtils.getStringFromKey(account.publicKey)
    val freshAccountState = new AccountState()
    instance.accounts += (publicKey -> freshAccountState)
    updateAccountsFile()
  }

  def newAccount(): Account = {
    val account = new Account()
    addAccount(account)
    account
  }

  def getAccountState(key: String): AccountState = {
    return instance.accounts(key)
  }

  def getBalance(account: Account): Float = {
    val accountState: AccountState = instance.accounts(StringUtils.getStringFromKey(account.publicKey))
    return accountState.balance
  }
}
