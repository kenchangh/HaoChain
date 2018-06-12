package com.hao.HaoChain.views

import com.hao.HaoChain.HaoChainWallet.{appStyles, loginStyles}

import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.PasswordField
import scalafx.scene.effect.DropShadow
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.paint.Color.{Cyan, DodgerBlue}
import scalafx.scene.paint.{LinearGradient, Stops}
import scalafx.scene.text.Text


class LoginScene(width: Double, height: Double) extends Scene(width, height) {

  val appStyles = getClass.getResource("/styles.css").toExternalForm
  val loginStyles = getClass.getResource("/login-styles.css").toExternalForm

  stylesheets = List(appStyles, loginStyles)
  content = Seq(
    new VBox {
      prefWidth = Globals.sceneWidth
      prefHeight = Globals.sceneHeight
      styleClass = List("background", "login-container")
      fillWidth = true
      alignment = Pos.Center
      padding = Insets(20)
      margin = Insets(30)
      children = Seq(
        new Text {
          alignmentInParent = Pos.Center
          text = "HAOCOIN"
          style = "-fx-font-size: 48pt"
          fill = new LinearGradient(
            endX = 0,
            stops = Stops(Cyan, DodgerBlue)
          )
          effect = new DropShadow {
            color = DodgerBlue
            radius = 25
            spread = 0.25
          }
        },
        new HBox {
          padding = Insets(20)
          alignment = Pos.Center
          children = Seq(
            new PasswordField {
              alignment = Pos.Center
              alignmentInParent = Pos.Center
              styleClass = List("text-input")
              promptText = "Type Your Password"
              style = "-fx-prompt-text-fill: derive(-fx-control-inner-background,-30%); }"
            }
          )
        },
        new HBox {
          alignment = Pos.Center
          padding = Insets(20)
          children = Seq(
            new Text {
              fill = scalafx.scene.paint.Color.White
              alignmentInParent = Pos.Center
              text = "Click Enter to continue"
            }
          )
        }
      )
    })
}
