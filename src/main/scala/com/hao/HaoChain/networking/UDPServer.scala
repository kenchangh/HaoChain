package com.hao.HaoChain.networking


import java.net._
import java.io._
object UDPServer extends App {
  var aSocket: Option[DatagramSocket] = None
  try {
    aSocket = Some(new DatagramSocket(6789))
    val buffer: Array[Byte] = Array.ofDim[Byte](1000)
    while (true) {
      val request: DatagramPacket = new DatagramPacket(buffer, buffer.length)
      aSocket.get.receive(request)
      val requestStr = new String(request.getData()).trim()
      val newStr = requestStr.concat("testing")
      val newStrBytes = newStr.getBytes()
      val reply: DatagramPacket = new DatagramPacket(newStrBytes,
        newStrBytes.length, request.getAddress(), request.getPort())
      aSocket.get.send(reply)
    }
  } catch {
    case e: SocketException => println("Socket: " + e.getMessage())
    case e: IOException     => println("IO: " + e.getMessage())
  } finally {
    aSocket foreach (_.close())
  }
}


