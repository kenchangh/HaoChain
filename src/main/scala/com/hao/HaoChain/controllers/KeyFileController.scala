package com.hao.HaoChain.controllers

import java.security.{MessageDigest, PrivateKey, PublicKey, SecureRandom}
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.io.PrintWriter
import javax.xml.bind.annotation.adapters.HexBinaryAdapter

import com.google.gson.GsonBuilder
import com.hao.HaoChain.controllers.KeyFileController.generateSafeToken
import com.hao.HaoChain.core.{HaoChain, StringUtils}

class KeyFile(val publicKey: String, val encryptedPrivateKey: String, val initVector: String)

object AESEncryptor {

  def serializePasswordToAESKey(password: String): String = {
    val digest = MessageDigest.getInstance("MD5").digest(password.getBytes("UTF-8"))
    var passwordHash= (new HexBinaryAdapter()).marshal(digest)
    passwordHash = passwordHash.substring(0, passwordHash.length/2)
    return passwordHash
  }

  def encrypt(password: String, initVector: String, value: String): String = try {
    val key = serializePasswordToAESKey(password)
    val iv = new IvParameterSpec(initVector.getBytes("UTF-8"))
    val skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES")
    val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
    cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)
    val encrypted = cipher.doFinal(value.getBytes)
    return Base64.getEncoder.encodeToString(encrypted)
  } catch {
    case e: Exception =>
      throw new RuntimeException(e)
  }

  def decrypt(password: String, initVector: String, encrypted: String): String = try {
    val key = serializePasswordToAESKey(password)
    val iv = new IvParameterSpec(initVector.getBytes("UTF-8"))
    val skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES")
    val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
    cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv)
    val original = cipher.doFinal(Base64.getDecoder.decode(encrypted))
    return new String(original)
  } catch {
    case e: Exception =>
      throw new RuntimeException(e)
  }

}

object KeyFileController {

  private def generateSafeToken(): String = {
    val random = new SecureRandom()
    val bytes = new Array[Byte](12)
    random.nextBytes(bytes)
    val encoder = Base64.getUrlEncoder.withoutPadding
    val token = encoder.encodeToString(bytes)
    return token
  }

  def writeKeyFile(publicKey: PublicKey, privateKey: PrivateKey, password: String) = {
    val publicKeyStr = StringUtils.getKeyFromString(publicKey)
    val privateKeyStr = StringUtils.getKeyFromString(privateKey)
    val initVector = generateSafeToken()

    // AES key has to be 16/32/64 bytes, so we have to MD5 the password first, get the first half of the hash
    val encrypted = AESEncryptor.encrypt(password, initVector, privateKeyStr)

    val keyFile = new KeyFile(publicKeyStr, encrypted, initVector)
    val keyFileJson = new GsonBuilder().create().toJson(keyFile)
    val keyFilePath = StringUtils.concatPath(HaoChain.chainDirectoryPath, "wallet.json")

    val writer = new PrintWriter(keyFilePath, "UTF-8")
    writer.println(keyFileJson)
    writer.close()
  }

}
