package com.hao.HaoChain

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.{PasswordField, TextField}
import scalafx.scene.effect.DropShadow
import scalafx.scene.layout.HBox
import scalafx.scene.paint.{LinearGradient, Stops}
import scalafx.scene.text.Text
import scalafx.scene.paint.Color._


object HaoChainWallet extends JFXApp {

  stage = new PrimaryStage {
    title = "HaoChain Wallet"
    scene = new Scene(850, 500) {
      fill = Black
      content = Seq(
        new HBox {
          padding = Insets(20)
          children = Seq(
            new Text {
              text = "Hello "
              style = "-fx-font-size: 48pt"
              fill = new LinearGradient(
                endX = 0,
                stops = Stops(PaleGreen, SeaGreen))
            },
            new Text {
              text = "World!!!"
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
            }
          )
        }, new HBox {
          padding = Insets(20)
          children = Seq(
            new PasswordField {
            }
          )
        })
    }
  }
}

