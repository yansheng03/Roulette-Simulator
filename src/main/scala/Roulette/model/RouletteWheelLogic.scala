package Roulette.model

import scala.util.Random

object RouletteWheelLogic {
  // Updated list to match European Roulette wheel order
  val wheelNumbers: List[Int] = List(0, 32, 15, 19, 4, 21, 2, 25, 17, 34, 6, 27, 13, 36, 11, 30, 8, 23, 10, 5, 24, 16, 33, 1, 20, 14, 31, 9, 22, 18, 29, 7, 28, 12, 35, 3, 26)

  def generateNumber(): Int = wheelNumbers(Random.nextInt(wheelNumbers.length))

  def spin(): (Int, String) = {
    val number = generateNumber()
    val color = checkColor(number)
    (number, color)
  }

  def checkNumberRed(number: Int): Boolean = {
    val redNumbers = Set(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36)
    redNumbers.contains(number)
  }

  def checkColor(number: Int): String = {
    if (number == 0) "green"

    else if (checkNumberRed(number)) "red"
    else "black"
  }

  def checkParity(number: Int): String = {
    if (number == 0) "zero"
    else if (number % 2 == 0) "even"
    else "odd"
  }

  def checkDozen(dozen: Int, number: Int): Boolean = {
    dozen match {
      case 1 => number >= 1 && number <= 12
      case 2 => number >= 13 && number <= 24
      case 3 => number >= 25 && number <= 36
      case _ => false
    }
  }

  def checkRange(range: Int, number: Int): Boolean = {
    range match {
      case 1 => number >= 1 && number <= 18
      case 2 => number >= 19 && number <= 36
      case _ => false
    }
  }

  def checkColumn(column: Int, number: Int): Boolean = {
    column match {
      case 1 => List(3, 6, 9, 12, 15, 18, 21, 24, 27, 30, 33, 36).contains(number)
      case 2 => List(2, 5, 8, 11, 14, 17, 20, 23, 26, 29, 32, 35).contains(number)
      case 3 => List(1, 4, 7, 10, 13, 16, 19, 22, 25, 28, 31, 34).contains(number)
      case _ => false
    }
  }


  def calculateAngleForNumber(number: Int): Double = {
    val position = wheelNumbers.indexOf(number)
    val anglePerNumber = 360.0 / wheelNumbers.size
    position * anglePerNumber
  }
}
