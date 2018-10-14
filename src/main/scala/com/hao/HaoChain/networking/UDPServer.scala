package com.hao.HaoChain.networking

import java.net._
import java.io._

object UDPServer extends App {
  val udpServer = new UDPServer("test")
}

class UDPServer(val nodeId: String) {
  val bufferSize = 1000
  val port = 6789
  var aSocket: Option[DatagramSocket] = None

  def listen(responseCallback: Option[(String) => Unit] = None) = {
    try {
      aSocket = Some(new DatagramSocket(port))
      val buffer: Array[Byte] = Array.ofDim[Byte](bufferSize)
      println("Server running on port "+ port.toString)

      while (true) {
        val request: DatagramPacket = new DatagramPacket(buffer, buffer.length)
        aSocket.get.receive(request)
        val requestStr = new String(request.getData()).trim()

        // perform a response when receive the message
        responseCallback.map(callback => callback(requestStr))

        // send an acknowledgement
        val requestBytes = requestStr.getBytes()
        val replyMessage = "ack"
        val replyBytes = replyMessage.getBytes
        val reply: DatagramPacket = new DatagramPacket(replyBytes,
          replyBytes.length, request.getAddress(), request.getPort())
        aSocket.get.send(reply)
      }
    } catch {
      case e: SocketException => println("Socket: " + e.getMessage())
      case e: IOException     => println("IO: " + e.getMessage())
    } finally {
      aSocket foreach (_.close())
    }
  }
}


