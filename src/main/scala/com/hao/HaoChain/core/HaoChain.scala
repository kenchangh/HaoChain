package com.hao.HaoChain.core

import java.io.File
import java.security.{PrivateKey, PublicKey, Security}

import scala.io.StdIn
import com.hao.HaoChain.controllers.{AuthController, MinerController}
import com.hao.HaoChain.networking.{UDPClient, UDPServer}
import org.bouncycastle.jce.provider.BouncyCastleProvider


class HaoChain extends Blockchain {
  difficulty = 5
  HaoChain.createChainDirectory()
}

object HaoChain {
  Security.addProvider(new BouncyCastleProvider())

  def createChainDirectory() = {
    val chainDir = chainDirectoryPath
    new File(chainDir).mkdirs()
  }

  def chainDirectoryPath: String = {
    val homeDir = System.getProperty("user.home")
    return StringUtils.concatPath(homeDir, "/haoChain").toString
  }

  def userWalletPath: String = {
    return StringUtils.concatPath(chainDirectoryPath, "wallet.json")
  }

  //  def main(args: Array[String]): Unit = {
  //    var haochain = new HaoChain()
  //    var globalAccountState = GlobalAccountState.initialize()
  ////    AuthController.registerAccount("12345")
  //    val account = AuthController.loginAccount("12345")
  //  }

  def main(args: Array[String]): Unit = {

    var haoChain = new HaoChain()
    var globalAccountState = GlobalAccountState.initialize()

    val banner = "\n /$$   /$$                   /$$$$$$ /$$               /$$         \n| $$  | $$                  /$$__  $| $$              |__/         \n| $$  | $$ /$$$$$$  /$$$$$$| $$  \\__| $$$$$$$  /$$$$$$ /$$/$$$$$$$ \n| $$$$$$$$|____  $$/$$__  $| $$     | $$__  $$|____  $| $| $$__  $$\n| $$__  $$ /$$$$$$| $$  \\ $| $$     | $$  \\ $$ /$$$$$$| $| $$  \\ $$\n| $$  | $$/$$__  $| $$  | $| $$    $| $$  | $$/$$__  $| $| $$  | $$\n| $$  | $|  $$$$$$|  $$$$$$|  $$$$$$| $$  | $|  $$$$$$| $| $$  | $$\n|__/  |__/\\_______/\\______/ \\______/|__/  |__/\\_______|__|__/  |__/\n                                                                   \n                                                                   \n                                                                   "

    println(banner)
    println("Welcome to HaoChain, a new cryptocurrency.")
    print("To begin, create a new account by entering a password: ")
    val password: String = StdIn.readLine()
    val myAccount = AuthController.registerAccount(password)
    val myPublicKeyStr = StringUtils.getStringFromKey(myAccount.publicKey)
    haoChain.nodeId = myPublicKeyStr

    StringUtils.clearScreen()

    val minerController = new MinerController(myPublicKeyStr, globalAccountState, haoChain)
    minerController.mine()
    minerController.listenToMessages()

    while (true) {
      val balance = GlobalAccountState.getBalance(myAccount)
      println("Welcome! Your account balance is: " + balance.toString + " HAO\n")

      println("Enter the commands (1 or 2) to get started")
      println("(1) Send HAO")
      println("(2) Receive HAO\n")

      val x: Int = StdIn.readInt()
      StringUtils.clearScreen()
    }
  }

  //  def testMain(args: Array[String]): Unit = {
  //    var haochain = new HaoChain()
  //    var globalAccountState = GlobalAccountState.initialize()
  //
  //    val account1 = GlobalAccountState.newAccount()
  //    val account2 = GlobalAccountState.newAccount()
  //
  //    val account1PublicKeyStr = StringUtils.getStringFromKey(account1.publicKey)
  //
  //    // Genesis block
  ////    val genesisTransaction = new Transaction(coinbase.publicKey, account1.publicKey, 100, coinbase.nonce)
  ////    genesisTransaction.generateSignature(coinbase.privateKey)
  //    val genesisBlock: Block = new Block(GenesisBlock.GENESIS_HASH, GenesisBlock.GENESIS_DATA)
  ////    genesisBlock.addTransaction(genesisTransaction)
  //    haochain.addBlock(genesisBlock)
  //
  //    val message = Block.serializeToJSON(genesisBlock)
  //    //    udpClient.sendMessage(message)
  //
  //    // 1st block
  //    val block1 = new Block(genesisBlock.hash, "Block 1")
  //    block1.addTransaction(account1.transfer(account2.publicKey, 20, account1.nonce + 1))
  //    haochain.addBlock(block1)
  //
  //    // 2nd block
  //    val block2 = new Block(block1.hash, "Block 2")
  //    block2.addTransaction(account2.transfer(account1.publicKey, 10, account2.nonce + 1))
  //    block2.addTransaction(account1.transfer(account2.publicKey, 40, account2.nonce + 1))
  //    //    block2.addTransaction(account1.transfer(account2.publicKey, 50, account1.nonce+1))
  //    haochain.addBlock(block2)
  //
  //    //    haochain.printBlockchain()
  //  }

  //  def miningTest(args: Array[String]): Unit = {
  //    var hash: String = ""
  //    var blockchain: com.hao.HaoChain.core.HaoChain =  new com.hao.HaoChain.core.HaoChain()
  //
  //    var startTime: Long = System.currentTimeMillis()
  //    blockchain.blocks.append(new Block(GenesisBlock.GENESIS_HASH, "First block"))
  //    hash = blockchain.blocks(0).mineBlock(blockchain.difficulty)
  //    var endTime: Long = System.currentTimeMillis()
  //    println("Block " + hash + "mined in: " + (endTime - startTime).toString)
  //
  //    startTime = System.currentTimeMillis()
  //    blockchain.blocks.append(
  //      new Block(blockchain.blocks(blockchain.blocks.length - 1).hash, "Second block"))
  //    blockchain.blocks(1).mineBlock(blockchain.difficulty)
  //    endTime = System.currentTimeMillis()
  //    println("Block " + hash + "mined in: " + (endTime - startTime).toString)
  //
  //    startTime = System.currentTimeMillis()
  //    blockchain.blocks.append(
  //      new Block(blockchain.blocks(blockchain.blocks.length - 1).hash, "Third block"))
  //    blockchain.blocks(2).mineBlock(blockchain.difficulty)
  //    endTime = System.currentTimeMillis()
  //    println("Block " + hash + "mined in: " + (endTime - startTime).toString)
  //
  //    blockchain.printBlockchain()
  //  }
}

