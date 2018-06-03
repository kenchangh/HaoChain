package com.hao.HaoChain
import java.util.Date

class Block(val prevHash: String, val data: String) {
  val hash: String = null
  val timestamp = new Date().getTime()

  def calculateHash() = {

  }
}
