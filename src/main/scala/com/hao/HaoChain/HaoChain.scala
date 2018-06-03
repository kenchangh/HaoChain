import java.security.Security

import com.google.gson.GsonBuilder
import com.hao.HaoChain._
import org.bouncycastle.jce.provider.BouncyCastleProvider


class HaoChain extends Blockchain {
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
    val wallet1: Wallet = new Wallet()
    val wallet2: Wallet = new Wallet()
    println(StringUtils.getKeyFromString(wallet1.publicKey), StringUtils.getKeyFromString(wallet2.publicKey))
    val txn: Transaction = new Transaction(wallet1.publicKey, wallet2.publicKey, 50, null)
    val signature: Array[Byte] = txn.generateSignature(wallet1.privateKey)
    println("Signature verified", txn.verifySignature(signature))
  }

  def miningTest(args: Array[String]): Unit = {
    val genesisHash = "A6D72BAA3DB900B03E70DF880E503E9164013B4D9A470853EDC115776323A098"
    var hash: String = ""
    var blockchain: HaoChain = new HaoChain()

    var startTime: Long = System.currentTimeMillis()
    blockchain.blocks.append(new Block(genesisHash, "First block"))
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

