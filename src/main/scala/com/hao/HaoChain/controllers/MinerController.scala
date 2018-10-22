package com.hao.HaoChain.controllers

import java.net.SocketException

import com.hao.HaoChain.core._
import com.hao.HaoChain.networking.UDPServer

import scala.collection.mutable.ArrayBuffer

class MinerController(port: Int, myAccount: Account, globalAccountState: GlobalAccountState, haoChain: HaoChain) {

  val coinbasePublicKey = StringUtils.getPublicKeyFromString(GlobalAccountState.coinbaseAccount)
  val MINER_REWARD = 100
  val DEFAULT_EMPTY_DATA: String = "0x0"
  var mempool: Option[Block] = None
  val myPublicKeyStr: String = StringUtils.getStringFromKey(myAccount.publicKey)
  val logs: ArrayBuffer[String] = ArrayBuffer[String]()

  def listenToMessages() = {
    val listenerThread = new Thread(() => {
      val udpServer = new UDPServer(port, myPublicKeyStr)
      udpServer.listen(Some(messageResponseCallback))
    })
    listenerThread.start()
  }

  def messageResponseCallback(message: String): Unit = {
    // new block is added
    // check if block is not mined by self
    val newBlock: Block = Block.deserializeFromJSON(message)
    if (newBlock.miner == StringUtils.getStringFromKey(myAccount.publicKey)) {
      haoChain.acceptBlock(newBlock)
      val blockHeight = haoChain.blocks.length - 1
      logs.append("Mined block " + blockHeight.toString + ": Earned 100 HAO")
      printLogs()
    } else {
      haoChain.acceptBlock(newBlock)
      val blockHeight = haoChain.blocks.length - 1
      val miner = newBlock.miner
      val minerKey = miner.substring(miner.length - 10, miner.length - 1)
      logs.append("Block " + blockHeight.toString + " mined by: " + minerKey)
      printLogs()
    }
  }

  def printLogs(): Unit = {
    HaoChain.printWalletBalance(myAccount, Some(() => {
      println("Logs:")
      for (log <- logs.slice(logs.length - 10, logs.length - 1)) {
        println(log)
      }
      println()
    }))
  }

  def mine(): Unit = {
    val miningThread = new Thread(() => {
      var lock: Boolean = false

      while (true) {
        val myPublicKey = StringUtils.getPublicKeyFromString(myPublicKeyStr)

        if (haoChain.blocks.isEmpty) {
          val genesisTransaction = new Transaction(coinbasePublicKey, myPublicKey, MINER_REWARD, 0)
          val genesisBlock = new Block(GenesisBlock.GENESIS_HASH, GenesisBlock.GENESIS_DATA)
          genesisBlock.addTransaction(genesisTransaction)
          genesisBlock.mineBlock(haoChain.difficulty)
          genesisBlock.miner = myPublicKeyStr
          Block.serializeToJSON(genesisBlock)
          genesisBlock.broadcast(myAccount, 0)
          haoChain.acceptBlock(genesisBlock)
          logs.append("Mined block genesis block: Earned 100 HAO")
          printLogs()
        } else {
          // get the last block from the blockchain
          // use that to start mining the next block
          try {
            if (!mempool.isDefined) {
              val newBlockHeight = haoChain.blocks.length
              val previousBlockHeight = haoChain.blocks.length - 1
              val previousHash = haoChain.blocks(previousBlockHeight).hash
              mempool = Some(new Block(previousHash, DEFAULT_EMPTY_DATA))
            } else {
              if (!lock) {
                mempool.foreach(newBlock => {
                  lock = true
                  val newBlockHeight = haoChain.blocks.length
                  val coinbaseTransaction = new Transaction(coinbasePublicKey, myPublicKey, MINER_REWARD, newBlockHeight)
                  newBlock.addTransaction(coinbaseTransaction)
                  newBlock.mineBlock(haoChain.difficulty)
                  newBlock.miner = myPublicKeyStr
                  //                  println(Block.serializeToJSON(newBlock))
                  newBlock.broadcast(myAccount, newBlockHeight)
                  //                  haoChain.acceptBlock(newBlock)
                  //                  logs.append("Mined block " + newBlockHeight.toString + ": Earned 100 HAO")
                  mempool = Some(new Block(newBlock.hash, DEFAULT_EMPTY_DATA))
                  lock = false
                })
              }
            }
          } catch {
            case e: SocketException => println()
          }
        }
      }
    })
    miningThread.start()
  }

}
