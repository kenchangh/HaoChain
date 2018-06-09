import java.security.Security

import com.hao.HaoChain._
import org.bouncycastle.jce.provider.BouncyCastleProvider


class HaoChain extends Blockchain {
  difficulty = 3
}

object HaoChain {

  def main(args: Array[String]): Unit = {
    Security.addProvider(new BouncyCastleProvider())
    var haochain = new HaoChain()
    var globalAccountState = GlobalAccountState.initialize()

    val coinbase = GlobalAccountState.newAccount()
    val account1 = GlobalAccountState.newAccount()
    val account2 = GlobalAccountState.newAccount()

    // Genesis block
    val genesisTransaction = new Transaction(coinbase, account1, 100, coinbase.nonce)
    genesisTransaction.generateSignature(coinbase.privateKey)
    val genesisBlock: Block = new Block(GenesisBlock.GENESIS_HASH, GenesisBlock.GENESIS_DATA)
    genesisBlock.addTransaction(genesisTransaction)
    haochain.addBlock(genesisBlock)
    haochain.printBlockchain()
//    println(GlobalAccountState.instance.accounts.toString())

    // 1st block
    val block1 = new Block(genesisBlock.hash, "Block 1")
    block1.addTransaction(account1.transfer(account2, 20, account1.nonce))
//    println(GlobalAccountState.instance.accounts.toString())
    block1.addTransaction(account2.transfer(account1, 10, account2.nonce))
//    println(GlobalAccountState.instance.accounts.toString())
    haochain.addBlock(block1)
    haochain.printBlockchain()
  }

  def miningTest(args: Array[String]): Unit = {
//    var hash: String = ""
//    var blockchain: HaoChain = new HaoChain()
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
  }
}

