package com.hao.HaoChain.controllers

import java.net.SocketException

import com.hao.HaoChain.core._
import com.hao.HaoChain.networking.UDPServer

class MinerController(myPublicKeyStr: String, globalAccountState: GlobalAccountState, haoChain: HaoChain) {

  val MINER_REWARD = 100
  val DEFAULT_EMPTY_DATA: String = "0x0"
  var mempool: Option[Block] = None

  def listenToMessages() = {
    val listenerThread = new Thread(() => {
      val udpServer = new UDPServer(myPublicKeyStr)
      udpServer.listen(Some(messageResponseCallback))
    })
    listenerThread.start()
  }

  def messageResponseCallback(message: String): Unit = {
    println(message)
  }

  def mine(): Unit = {
    val miningThread = new Thread(() => {
      val myPublicKey = StringUtils.getPublicKeyFromString(myPublicKeyStr)

      if (haoChain.blocks.length == 0) {
        val coinbasePublicKey = StringUtils.getPublicKeyFromString(GlobalAccountState.coinbaseAccount)
        val genesisTransaction = new Transaction(coinbasePublicKey, myPublicKey, MINER_REWARD, 0)
        val genesisBlock = new Block(GenesisBlock.GENESIS_HASH, GenesisBlock.GENESIS_DATA)
        genesisBlock.addTransaction(genesisTransaction)
        genesisBlock.mineBlock(haoChain.difficulty)
        genesisBlock.miner = myPublicKeyStr
        genesisBlock.broadcast(myPublicKeyStr)
      } else {
        // get the last block from the blockchain
        // use that to start mining the next block
        try {
          if (!mempool.isDefined) {
            val previousHash = haoChain.blocks(haoChain.blocks.length - 1).hash
            mempool = Some(new Block(previousHash, DEFAULT_EMPTY_DATA))
          }
        } catch {
          case e: SocketException => println()
        }
      }
    })
    miningThread.start()
  }

}
