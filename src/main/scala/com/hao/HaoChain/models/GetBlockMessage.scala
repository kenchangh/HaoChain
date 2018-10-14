package com.hao.HaoChain.models

import com.google.gson.JsonObject

class GetBlockMessage(val blockHeight: Int) extends Message("get_block") {
  def serializeToJSON(): String = {
    val jsonObject: JsonObject = Message.serializeToJSON(this)
    jsonObject.addProperty("height", blockHeight)
    return jsonObject.toString
  }
}
