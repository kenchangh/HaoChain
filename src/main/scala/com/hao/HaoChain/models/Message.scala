package com.hao.HaoChain.models

import com.google.gson.{GsonBuilder, JsonObject}

class Message(val messageType: String)

object Message {
  def serializeToJSON(message: Message): JsonObject = {
    var jsonObject = new JsonObject()
    jsonObject.addProperty("message_type", message.messageType)
    return jsonObject
  }
}
