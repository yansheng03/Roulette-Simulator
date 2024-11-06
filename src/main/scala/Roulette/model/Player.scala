package Roulette.model

class Player(val name: String, var balance: Double) {
  private var bettingHistory: List[Bet] = List()
  private val resultHistory = new ResultHistory
  private var totalBets = 0
  private var totalWins: Double = 0.0

  def canAffordTotalBet(amount: Double): Boolean = {
    balance >= amount
  }

  def placeBet(bet: Bet): Boolean = {
    if (canAffordTotalBet(bet.amount)) {
      totalBets += 1
      balance -= bet.amount
      bettingHistory = bet :: bettingHistory
      true
    } else {
      false
    }
  }


  def receiveWinnings(amount: Double): Unit = {
    if (amount > 0) {
      totalWins += 1
    }
    balance += amount
  }

  def setBalance(newBalance: Double): Unit = {
    balance = newBalance
  }

  def getWinRate: Double = {
    if (totalBets == 0) 0.0
    else (totalWins / totalBets) * 100
  }

  def getBettingHistory: List[Bet] = bettingHistory

  def addResult(result: String): Unit = {
    resultHistory.addRecord(result)
  }

  def getResultHistory: String = resultHistory.getHistoryAsString


  def setWinRate(desiredWinRate: Double): Unit = {
    if (totalBets == 0) {
      throw new IllegalArgumentException("Cannot change the win rate percentage before placing any bets.")
    }
    if (desiredWinRate < 0.0 || desiredWinRate > 100.0) {
      throw new IllegalArgumentException("Win rate must be between 0 and 100")
    }
    // Calculate the totalWins required to achieve the desired win rate
    totalWins = (desiredWinRate / 100.0) * totalBets
  }
}

