package com.hao.HaoChain.networking

import java.io.IOException
import java.net.{DatagramPacket, DatagramSocket, InetAddress, SocketException}
import scala.collection.mutable.ArrayBuffer
import com.hao.HaoChain.models.KnownNodesMessage

object UDPClient extends App {
  val udpClient = new UDPClient("asdas")
  udpClient.sendMessage("testing")
}

class UDPClient(val nodeId: String) {

  var knownNodes = ArrayBuffer[String](
    "localhost:6789",
//    "localhost:7000"
  )
  val bufferSize = 1000

  def sendMessageToNode(uriString: String, message: String): Unit = {
    val splitUri = uriString.split(':')
    //    if (splitUri > 2)
    //      throw new Exception("URI should only have 2 components")
    val host = splitUri(0)
    val port = splitUri(1)
    val hostAddress: InetAddress = InetAddress.getByName(host)
    val portNumber: Int = port.toInt

    var socket: Option[DatagramSocket] = None
    try {
      socket = Some(new DatagramSocket())
      val bytes: Array[Byte] = message.getBytes
      val request: DatagramPacket = new DatagramPacket(
        bytes, bytes.length, hostAddress, portNumber)
      socket.get.send(request)
      val buffer: Array[Byte] = Array.ofDim(bufferSize)
      val reply: DatagramPacket = new DatagramPacket(buffer, buffer.length)
      socket.get.receive(reply)
      println("Reply: " + new String(reply.getData()).trim)
    } catch {
      case e: SocketException => println("Socket: " + e.getMessage());
      case e: IOException => println("IO: " + e.getMessage());
      case e: Exception => println("Exception: "+e.getMessage())
    } finally {
      socket foreach (_.close())
    }
  }

  def sendMessage(message: String): Unit = {
    for (node <- knownNodes) {
      sendMessageToNode(node, message)
    }
  }

  def queryForKnownNodes(): Unit = {
    new Thread(() => {
      val message = new KnownNodesMessage(nodeId)
      this.sendMessage(message.serializeToJSON())
    })
  }

}


