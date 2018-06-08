import java.security.Security

import com.google.gson.GsonBuilder
import com.hao.HaoChain._
import org.bouncycastle.jce.provider.BouncyCastleProvider

import scala.collection.immutable.HashMap

trait GlobalBlockchainState {
  var accounts: HashMap[String, AccountState] = new HashMap[String, AccountState]()
}

class HaoChain extends Blockchain with GlobalBlockchainState {
  difficulty = 3
}

object HaoChain {

  def printBlockchain(blockchain: Blockchain): Unit = {
    for (block <- blockchain.blocks) {
      val blockchainJson: String = new GsonBuilder().setPrettyPrinting().create().toJson(block)
      println(blockchainJson)
    }
  }

  def main(args: Array[String]): Unit = {
    Security.addProvider(new BouncyCastleProvider())
    val wallet1: Account = new Account()
    val wallet2: Account = new Account()
    println(StringUtils.getKeyFromString(wallet1.publicKey), StringUtils.getKeyFromString(wallet2.publicKey))
    val txn: Transaction = new Transaction(wallet1, wallet2, 50)
    val signature: Array[Byte] = txn.generateSignature(wallet1.privateKey)
    println("Signature verified", txn.verifySignature())
  }

  def miningTest(args: Array[String]): Unit = {
    var hash: String = ""
    var blockchain: HaoChain = new HaoChain()

    var startTime: Long = System.currentTimeMillis()
    blockchain.blocks.append(new Block(GenesisBlock.GENESIS_HASH, "First block"))
    hash = blockchain.blocks(0).mineBlock(blockchain.difficulty)
    var endTime: Long = System.currentTimeMillis()
    println("Block " + hash + "mined in: " + (endTime - startTime).toString)

    startTime = System.currentTimeMillis()
    blockchain.blocks.append(
      new Block(blockchain.blocks(blockchain.blocks.length - 1).hash, "Second block"))
    blockchain.blocks(1).mineBlock(blockchain.difficulty)
    endTime = System.currentTimeMillis()
    println("Block " + hash + "mined in: " + (endTime - startTime).toString)

    startTime = System.currentTimeMillis()
    blockchain.blocks.append(
      new Block(blockchain.blocks(blockchain.blocks.length - 1).hash, "Third block"))
    blockchain.blocks(2).mineBlock(blockchain.difficulty)
    endTime = System.currentTimeMillis()
    println("Block " + hash + "mined in: " + (endTime - startTime).toString)

    printBlockchain(blockchain)
  }
}

