package com.hao.HaoChain
import java.security.MessageDigest
import java.math.BigInteger

object StringUtils {
  def sha256(string: String): String = {
    val byteArray: Array[Byte] = MessageDigest.getInstance("SHA-256").digest(string.getBytes("UTF-8"))
    val hexString = new StringBuffer
    // This will contain hash as hexidecimal
    var i = 0
    while ( {
      i < byteArray.length
    }) {
      val hex = Integer.toHexString(0xff & byteArray(i))
      if (hex.length == 1) hexString.append('0')
      hexString.append(hex)

      {
        i += 1; i - 1
      }
    }
    return hexString.toString
  }
}
