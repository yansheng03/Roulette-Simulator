package Roulette.controller

import Roulette.Main.{getClass, stage}
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import scalafx.event.ActionEvent
import scalafx.scene.control.Button
import scalafxml.core.macros.sfxml
import scalafx.scene.Scene
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.image.Image
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import scalafx.Includes._
import javafx.{scene => jfxs}

@sfxml
class RouletteGameStartController(private val startButton: Button) {

  def startGame(event: ActionEvent): Unit = {

    val rootResource = getClass.getResourceAsStream("/view/RouletteGame.fxml")
    // initialize the loader object.
    val loader = new FXMLLoader(null, NoDependencyResolver)
    // Load root layout from fxml file.
    loader.load(rootResource);
    // retrieve the root component BorderPane from the FXML
    val roots = loader.getRoot[jfxs.layout.Pane]


    stage = new PrimaryStage {
      title = "Casino Roulette Game"
      icons += (new Image(getClass.getResourceAsStream("/images/RouletteIcon.png")))
      scene = new Scene {
        root = roots
      }
    }
  }

  def showInstructions(event: ActionEvent): Unit = {
    // Create an alert of type INFORMATION
    val alert = new Alert(AlertType.INFORMATION)
    alert.setTitle("How to Play Roulette")
    alert.setHeaderText("Roulette Game Instructions")

    // Set the content of the alert with game instructions
    alert.setContentText(
      """Welcome to the Casino Roulette game! This Game serves as a simulator to prepare for the real life equivalent. Here's how to play:
        |
        |1. Place Bets:
        |   - Click on any area on the roulette table to place bets on numbers, colors, columns, dozens, ranges, or parities.
        |   - A dialog will prompt you to enter the amount you wish to bet.
        |   - You can bet however many areas you want as long as you have the balance to do so.
        |
        |2. Spin the Wheel:
        |   - Press the "Place Bet" button to spin the roulette wheel.
        |   - The result of the spin (number and color) will be displayed.
        |
        |3. History:
        |   - Bets will show all bets placed to keep track of your current bets
        |   - Result History shows all current and previous results.
        |
        |4. Winning and Losing:
        |   - Your bets will be evaluated based on the result of the spin.
        |   - Winnings will be added to your balance if you win.
        |
        |5. Win Rate:
        |   - Calculates and shows your win rate for all your individual bets.
        |
        |6. Undo Bets:
        |   - Use the "Undo Bet" button to remove your last placed bet and refund the balance.
        |
        |7. Edit Game:
        |   - Use the "Edit Balance" option in the menu bar to set your new balance to continue betting or bet bigger.
        |   - Use the "Edit Win Rate" option in the menu bar to set your win rate when you show off to your friends.
        |
        |8. Payouts:
        |   - A Reward of x2 when betting on Colors, Parities (Odd or Even), Range, Columns,
        |   - A Reward of x3 when betting on Dozens
        |   - A Reward of x35 when betting on any numbers
        |
        |Enjoy playing and good luck!
        |""".stripMargin
    )
    alert.getDialogPane.setMinWidth(800)
    alert.show()
  }

  def exitGame(event: ActionEvent): Unit = {
    stage.close()
  }
}
