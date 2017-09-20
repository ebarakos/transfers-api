package modules.components

import models._
import slick.driver.MySQLDriver.api._
import slick.lifted.{TableQuery, Tag}

/**
  * Created by Evangelos.
  */
object TableComponents {
  trait AccountsComponent {
    protected class AccountsTable(tag: Tag) extends Table[Account](tag, "accounts") {
      def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
      def balance = column[Double]("balance")
      def * = (id, balance) <> ((Account.apply _).tupled, Account.unapply)
    }
    val accounts = TableQuery[AccountsTable]
  }

  trait TransactionsComponent {
    protected class TransactionsTable(tag: Tag) extends Table[Transaction](tag, "transactions") {
      def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
      def from = column[Long]("from")
      def to = column[Long]("to")
      def amount = column[Double]("amount")
      def * = (id, from, to, amount) <> ((Transaction.apply _).tupled, Transaction.unapply)
    }
    val transactions = TableQuery[TransactionsTable]
  }
}
