package com.hao.HaoChain.models

import com.google.gson.{GsonBuilder, JsonObject, JsonParser}
import com.hao.HaoChain.core.{Block, BlockJSON, TransactionJSON}

class NewBlockMessage(val block: Block) extends Message("new_block") {
  def serializeToJSON(): String = {
    val jsonString = Block.serializeToJSON(block)
    val jsonObject = new JsonParser().parse(jsonString).getAsJsonObject
    jsonObject.addProperty("message_type", "new_block")
    return jsonObject.toString
  }
}
