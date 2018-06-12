package com.hao.HaoChain

import com.hao.HaoChain.views.{Globals, LoginScene}

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{PasswordField, TextField}
import scalafx.scene.effect.DropShadow
import scalafx.scene.layout.{BorderPane, HBox, StackPane, VBox}
import scalafx.scene.paint.{LinearGradient, Stops}
import scalafx.scene.text.Text
import scalafx.scene.paint.Color._


object HaoChainWallet extends JFXApp {

  val stageTitle = "Haocoin Wallet"
  val loginStyles = getClass.getResource("/login-styles.css").toExternalForm
  val appStyles = getClass.getResource("/styles.css").toExternalForm

  stage = new PrimaryStage {
    title = stageTitle
    scene = new LoginScene(Globals.sceneWidth, Globals.sceneHeight)
  }
}

