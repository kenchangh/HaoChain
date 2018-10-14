package com.hao.HaoChain.controllers

object OSValidator {
  private val OS = System.getProperty("os.name").toLowerCase

  def isWindows: Boolean = OS.indexOf("win") >= 0

  def isMac: Boolean = OS.indexOf("mac") >= 0

  def isUnix: Boolean = OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0

  def isSolaris: Boolean = OS.indexOf("sunos") >= 0

  def getOS: String = if (isWindows) "win"
  else if (isMac) "osx"
  else if (isUnix) "uni"
  else if (isSolaris) "sol"
  else "err"
}
