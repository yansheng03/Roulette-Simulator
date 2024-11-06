package Roulette.controller

import Roulette.Main.stage
import scalafxml.core.macros.sfxml
import javafx.scene.control._
import Roulette.model.{Bet, ColorBet, ColumnBet, DozenBet, NumberBet, ParityBet, Player, RangeBet, RouletteWheelLogic}
import scalafx.event.ActionEvent
import javafx.scene.control.Alert.AlertType
import scala.collection.mutable.ListBuffer
import javafx.animation.RotateTransition
import javafx.scene.image.ImageView
import javafx.util.Duration


@sfxml
class RouletteGameController(
                              private val balanceLabel: Label,
                              private val placeBetButton: Button,
                              private val resultLabel: Label,
                              private val historyTextArea: TextArea,
                              private val resultHistoryTextArea: TextArea,
                              private val winRateLabel: Label,
                              private val rouletteWheel: ImageView
                            ) {

  // Start a new player off with 1000
  private val player = new Player("Player 1", 1000.0)
  private val bets = ListBuffer[Bet]()
  updateBalance()
  updateWinRate()

  // Final button to confirm all bets and spin the wheel
  def placeBet(): Unit = {
    if (bets.isEmpty) {
      resultLabel.setText("Please place a bet on the board!")
      return
    }

    // Spin the wheel and get the result
    val (number, color) = RouletteWheelLogic.spin()

    // Start the spinning animation
    val initialRotate = new RotateTransition(Duration.seconds(3), rouletteWheel)
    initialRotate.setByAngle(360 * 10) // Spin 10 times
    initialRotate.setInterpolator(javafx.animation.Interpolator.LINEAR)

    initialRotate.setOnFinished { _ =>
      val stopAngle = RouletteWheelLogic.calculateAngleForNumber(number)
      val finalDuration = Duration.seconds(1) // Adjust duration for slowing down
      val finalRotate = new RotateTransition(finalDuration, rouletteWheel)
      finalRotate.setToAngle(-stopAngle) // Negative because we rotate clockwise
      finalRotate.setInterpolator(new javafx.animation.Interpolator {
        override def curve(t: Double): Double = {
          // Custom interpolation: slows down gradually
          if (t < 0.5) 4 * t * t * t else 1 - Math.pow(-2 * t + 2, 3) / 2
        }
      })
      finalRotate.play()
      finalRotate.setOnFinished { _ =>
        evaluateBets(number, color)
      }
    }
    initialRotate.play()
  }

  private def evaluateBets(number: Int, color: String): Unit = {
    val win = bets.map {
      case bet: NumberBet if bet.number == number => bet.payout
      case bet: ColorBet if bet.color.equalsIgnoreCase(color) => bet.payout
      case bet: ParityBet if bet.parity.equalsIgnoreCase(RouletteWheelLogic.checkParity(number)) => bet.payout
      case bet: DozenBet if RouletteWheelLogic.checkDozen(bet.dozen, number) => bet.payout
      case bet: RangeBet if RouletteWheelLogic.checkRange(bet.range, number) => bet.payout
      case bet: ColumnBet if RouletteWheelLogic.checkColumn(bet.column, number) => bet.payout
      case _ => 0.0
    }.sum

    player.receiveWinnings(win)
    resultLabel.setText(f"Result: $number $color, Win: $$${win}")
    updateBalance()
    updateWinRate()

    // Log the result
    val result = s"Number: $number, Color: $color, Win: $$${win}"
    player.addResult(result)
    updateResultHistory()
    updateHistory()
    bets.clear() // Clear bets after spinning
  }

  // For betting numbers
  def handleBet(event: javafx.event.ActionEvent): Unit = {
    val button = event.getSource.asInstanceOf[javafx.scene.control.Button]
    val betValue = button.getId.replace("buttonNumber", "").toInt // Extracting the number from button ID

    val dialog = new TextInputDialog()
    dialog.setTitle("Bet Amount")
    dialog.setHeaderText(s"Bet on Number $betValue")
    dialog.setContentText("Please enter your bet amount:")

    val result = dialog.showAndWait()
    result.ifPresent { amount =>
      processBetAmount(amount, NumberBet(_, betValue))
    }
  }

  // For betting First, Second, Third Dozen
  def handleDozenBet(event: javafx.event.ActionEvent): Unit = {
    val button = event.getSource.asInstanceOf[javafx.scene.control.Button]
    val dozenId = button.getId.replace("button", "")

    val dozen: Int = dozenId match {
      case "FirstDozen" => 1
      case "SecondDozen" => 2
      case "ThirdDozen" => 3
      case _ =>
        showErrorDialog("Invalid Dozen Bet", "Please select a valid dozen bet.")
        return
    }

    val dialog = new TextInputDialog()
    dialog.setTitle("Bet Amount")
    dialog.setHeaderText(s"Bet on Dozen $dozenId")
    dialog.setContentText("Please enter your bet amount:")

    val result = dialog.showAndWait()
    result.ifPresent { amount =>
      processBetAmount(amount, DozenBet(_, dozen))
    }
  }

  // For Betting on First or Second Range
  def handleRangeBet(event: javafx.event.ActionEvent): Unit = {
    val button = event.getSource.asInstanceOf[javafx.scene.control.Button]
    val rangeId = button.getId.replace("button", "")

    val range: Int = rangeId match {
      case "FirstRange" => 1
      case "SecondRange" => 2
      case _ =>
        showErrorDialog("Invalid Range Bet", "Please select a valid range bet.")
        return
    }

    val dialog = new TextInputDialog()
    dialog.setTitle("Bet Amount")
    dialog.setHeaderText(s"Bet on $rangeId")
    dialog.setContentText("Please enter your bet amount:")

    val result = dialog.showAndWait()
    result.ifPresent { amount =>
      processBetAmount(amount, RangeBet(_, range))
    }
  }

  // For betting on Red or Black
  def handleColorBet(event: javafx.event.ActionEvent): Unit = {
    val button = event.getSource.asInstanceOf[javafx.scene.control.Button]
    val betValue = button.getId.replace("button", "").toLowerCase // Extracting the color from button ID

    // Show input dialog to enter the bet amount
    val dialog = new TextInputDialog()
    dialog.setTitle("Bet Amount")
    dialog.setHeaderText(s"Bet on Color $betValue")
    dialog.setContentText("Please enter your bet amount:")

    val result = dialog.showAndWait()
    result.ifPresent { amount =>
      processBetAmount(amount, ColorBet(_, betValue))
    }
  }

  // For betting on Odd or Even
  def handleParityBet(event: javafx.event.ActionEvent): Unit = {
    val button = event.getSource.asInstanceOf[javafx.scene.control.Button]
    val betValue = button.getId.replace("button", "").toLowerCase // Extracting the parity from button ID

    // Show input dialog to enter the bet amount
    val dialog = new TextInputDialog()
    dialog.setTitle("Bet Amount")
    dialog.setHeaderText(s"Bet on Parity $betValue")
    dialog.setContentText("Please enter your bet amount:")

    val result = dialog.showAndWait()
    result.ifPresent { amount =>
      processBetAmount(amount, ParityBet(_, betValue))
    }
  }

  // For betting on First, Second, Third Column
  def handleColumnBet(event: javafx.event.ActionEvent): Unit = {
    val button = event.getSource.asInstanceOf[javafx.scene.control.Button]
    val columnId = button.getId.replace("button", "")

    val column: Int = columnId match {
      case "FirstColumn" => 1
      case "SecondColumn" => 2
      case "ThirdColumn" => 3
      case _ =>
        showErrorDialog("Invalid Column Bet", "Please select a valid column bet.")
        return
    }

    val dialog = new TextInputDialog()
    dialog.setTitle("Bet Amount")
    dialog.setHeaderText(s"Bet on Column $columnId")
    dialog.setContentText("Please enter your bet amount:")

    val result = dialog.showAndWait()
    result.ifPresent { amount =>
      processBetAmount(amount, ColumnBet(_, column))
    }
  }

  // Check if balance is enough to bet
  private def processBetAmount(amount: String, betFactory: Double => Bet): Unit = {
    try {
      val betAmount = amount.toDouble
      if (betAmount > 0 && player.canAffordTotalBet(betAmount)) {
        val bet = betFactory(betAmount)
        bets += bet
        player.placeBet(bet)

        updateHistory()
        updateBalance()
      } else {
        showErrorDialog("Invalid Bet Amount", "Bet amount must be greater than zero and within your balance.")
      }
    } catch {
      case _: NumberFormatException =>
        showErrorDialog("Invalid Bet Amount", "Please enter a valid number for the bet amount.")
    }
  }

  // Undo the last bet
  def undoLastBet(): Unit = {
    if (bets.nonEmpty) {
      val lastBet = bets.remove(bets.size - 1)
      player.receiveWinnings(lastBet.amount) // Refund the last bet amount to balance
      updateHistory()
      updateBalance()
    } else {
      showErrorDialog("No Bets to Undo", "There are no bets to undo.")
    }
  }

  // Show any error dialogs
  private def showErrorDialog(title: String, content: String): Unit = {
    val alert = new Alert(Alert.AlertType.ERROR)
    alert.setTitle(title)
    alert.setHeaderText(null)
    alert.setContentText(content)
    alert.showAndWait()
  }

  // Update Balance after each bet or win/loss
  private def updateBalance(): Unit = {
    balanceLabel.setText(f"Balance: $$${player.balance}%.2f")
  }

  // Update Win Rate of player after each bet
  private def updateWinRate(): Unit = {
    val winRate = player.getWinRate
    winRateLabel.setText(f"Win Rate: $winRate%.2f%%")
  }

  // Display all current bets
  private def updateHistory(): Unit = {
    val history = new StringBuilder
    bets.reverse.foreach(bet => history.append(s"${bet.description}\n"))
    historyTextArea.setText(history.toString())
  }

  // Display all previous and current results
  private def updateResultHistory(): Unit = {
    resultHistoryTextArea.setText(player.getResultHistory)
  }

  // set new balance for players
  def editBalance(): Unit = {
    val dialog = new TextInputDialog()
    dialog.setTitle("Edit Balance")
    dialog.setHeaderText("Edit your current balance")
    dialog.setContentText("Please enter the new balance amount:")

    val result = dialog.showAndWait()
    result.ifPresent { amount =>
      try {
        val newBalance = amount.toDouble
        if (newBalance >= 0) {
          player.setBalance(newBalance)
          updateBalance()
        } else {
          showErrorDialog("Invalid Balance", "Balance must be a non-negative number.")
        }
      } catch {
        case _: NumberFormatException =>
          showErrorDialog("Invalid Input", "Please enter a valid number for the balance.")
      }
    }
  }

  // Set new Win Rate for players
  def editWinRate(): Unit = {
    val dialog = new TextInputDialog()
    dialog.setTitle("Edit Win Rate")
    dialog.setHeaderText("Set Desired Win Rate To Impress Your Friends!")
    dialog.setContentText("Please enter your desired win rate in (%):")

    val result = dialog.showAndWait()
    result.ifPresent { input =>
      try {
        val desiredWinRate = input.toDouble
        player.setWinRate(desiredWinRate)
        updateWinRate()
      } catch {
        case e: IllegalArgumentException =>
          showErrorDialog("Invalid Operation", e.getMessage)
        case _: NumberFormatException =>
          showErrorDialog("Invalid Input", "Please enter a valid number for the win rate.")
      }
    }
  }

  // Display how to play the game
  def showInstructions(event: ActionEvent): Unit = {
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

  // End Game
  def exitGame(event: ActionEvent): Unit = {
    stage.close()
  }
}
