package com.hao.HaoChain.models

import com.google.gson.{GsonBuilder, JsonObject, JsonParser}
import com.hao.HaoChain.core.{Block, BlockJSON, Transaction, TransactionJSON}

class NewTxnMessage(val txn: Transaction) extends Message("new_txn") {
  def serializeToJSON(): String = {
    val jsonString = Transaction.serializeToJSON(txn)
    val jsonObject = new JsonParser().parse(jsonString).getAsJsonObject
    jsonObject.addProperty("message_type", "new_txn")
    return jsonObject.toString
  }
}
