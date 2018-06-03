package com.hao.HaoChain
import java.security.MessageDigest
import java.math.BigInteger

object StringUtils {
  def sha256(string: String): String = {
    val byteArray: Array[Byte] = MessageDigest.getInstance("SHA-256").digest(string.getBytes("UTF-8"))
    return String.format("%032x", new BigInteger(1, byteArray))
  }
}
