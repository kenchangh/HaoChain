package com.hao.HaoChain.models

import java.awt.TrayIcon.MessageType

import com.google.gson.{GsonBuilder, JsonObject}

class KnownNodesMessage(val nodeId: String) extends Message("get_known_nodes") {
  def serializeToJSON(): String = {
    val jsonObject = Message.serializeToJSON(this)
    jsonObject.addProperty("node_id", nodeId)
    return jsonObject.toString
  }
}
