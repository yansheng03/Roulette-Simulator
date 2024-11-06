package Roulette.model

class BettingHistory {
  private var history: List[String] = List()

  def addRecord(record: String): Unit = {
    history = record :: history
  }

  def getHistory: List[String] = history
}
