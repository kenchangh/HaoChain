package com.hao.HaoChain.networking

import java.io.IOException
import java.net.{DatagramPacket, DatagramSocket, InetAddress, SocketException}

import scala.collection.mutable.ArrayBuffer

object UDPClient extends App {

  val knownNodes = ArrayBuffer(
    "localhost:6789",
    "localhost:7000"
  )

  var aSocket: Option[DatagramSocket] = None
  try {
    aSocket = Some(new DatagramSocket())
    val m: Array[Byte] = "how are you".getBytes
    val aHost: InetAddress = InetAddress.getByName("localhost")
    val serverPort: Int = 6789
    val request: DatagramPacket = new DatagramPacket(m, m.length, aHost, serverPort)
    aSocket.get.send(request)
    val buffer: Array[Byte] = Array.ofDim(1000)
    val reply: DatagramPacket = new DatagramPacket(buffer, buffer.length)
    aSocket.get.receive(reply)
    println("Reply: " + new String(reply.getData()).trim)
  } catch {
    case e: SocketException => println("Socket: " + e.getMessage());
    case e: IOException     => println("IO: " + e.getMessage());
  } finally {
    aSocket foreach (_.close())
  }
}


