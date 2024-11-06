package Roulette.model

abstract class Bet(val amount: Double) {
  def payout: Double
  def description: String
}

case class NumberBet(override val amount: Double, number: Int) extends Bet(amount) {
  override def payout: Double = amount * 35.0
  override def description: String = s"Number Bet on $number with amount $$${amount}"
}

case class ColorBet(override val amount: Double, color: String) extends Bet(amount) {
  override def payout: Double = amount * 2.0
  override def description: String = s"Color Bet on $color with amount $$${amount}"
}

case class ParityBet(override val amount: Double, parity: String) extends Bet(amount) {
  override def payout: Double = amount * 2.0
  override def description: String = s"Parity Bet on $parity with amount $$${amount}"
}

case class DozenBet(override val amount: Double, dozen: Int) extends Bet(amount) {
  override def payout: Double = amount * 3.0
  override def description: String = s"Dozen Bet on $dozen with amount $$${amount}"
}

case class RangeBet(override val amount: Double, range: Int) extends Bet(amount) {
  override def payout: Double = amount * 2.0
  override def description: String = s"Range Bet on $range with amount $$${amount}"
}

case class ColumnBet(override val amount: Double, column: Int) extends Bet(amount) {
  override def payout: Double = amount * 2.0
  override def description: String = s"Column bet on $column with amount $$${amount}"
}
