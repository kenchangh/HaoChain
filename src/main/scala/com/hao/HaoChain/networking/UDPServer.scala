package com.hao.HaoChain.networking

import java.net._
import java.io._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UDPServer(val port: Int, val nodeId: String) {
  val bufferSize = 1000
  var aSocket: Option[DatagramSocket] = None

  def listen(responseCallback: Option[(String) => Unit] = None) = {
    try {
      aSocket = Some(new DatagramSocket(port))
      while (true) {
//        Future {
          val mySocket = aSocket
          val buffer: Array[Byte] = Array.ofDim[Byte](bufferSize)

          val request: DatagramPacket = new DatagramPacket(buffer, buffer.length)
          mySocket.get.receive(request)
          val requestStr = new String(request.getData()).trim()

          // perform a response when receive the message
          responseCallback.foreach(callback => callback(requestStr))

          // send an acknowledgement
          val requestBytes = requestStr.getBytes()
          val replyMessage = "ack"
          val replyBytes = replyMessage.getBytes
          val reply: DatagramPacket = new DatagramPacket(replyBytes,
            replyBytes.length, request.getAddress(), request.getPort())
          mySocket.get.send(reply)
//        }
      }
    } catch {
      case e: BindException => println("BindException: " + e.getMessage)
      case e: SocketException => println("Socket: " + e.getMessage())
      case e: IOException => println("IO: " + e.getMessage())
    } finally {
      aSocket foreach (_.close())
    }
  }
}

