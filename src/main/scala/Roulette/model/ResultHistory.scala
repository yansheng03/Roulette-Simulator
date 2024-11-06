package Roulette.model

class ResultHistory {
  private var history: List[String] = List()

  def addRecord(record: String): Unit = {
    history = record :: history
  }

  def getHistory: List[String] = history

  def getHistoryAsString: String = history.mkString("\n")
}
