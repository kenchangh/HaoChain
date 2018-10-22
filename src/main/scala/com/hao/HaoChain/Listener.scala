package com.hao.HaoChain

import com.hao.HaoChain.core.HaoChain

object Listener extends App {
  val notMiner = false
  HaoChain.runHaochain(5000, notMiner)
}
