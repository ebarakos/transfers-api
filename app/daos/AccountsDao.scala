package daos

import javax.inject.{Inject, Singleton}

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.driver.H2Driver.api._
import models._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * The accounts DAO.
  *
  */
@Singleton
class AccountsDao @Inject() (protected implicit val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile]{

  private class AccountsTable(tag: Tag) extends Table[Account](tag, "accounts") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def balance = column[Double]("balance")

    def * = (id, balance) <> ((Account.apply _).tupled, Account.unapply)
  }

  private val accounts = TableQuery[AccountsTable]

  private class TransactionsTable(tag: Tag) extends Table[Transaction](tag, "transfers") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def from = column[Long]("from")
    def to = column[Long]("to")
    def amount = column[Double]("amount")

    def * = (id, from, to, amount) <> ((Transaction.apply _).tupled, Transaction.unapply)
  }

  private val transactions = TableQuery[TransactionsTable]

  /**
    * Create an account with a balance and a certain currency.
    *
    */
  def insert(balance: Double): Future[Account] = db.run {
    (accounts.map(a => a.balance) returning accounts.map(_.id) into ((res, id) => Account(id, res))) += (balance)
  }

  /**
    * Update an account.
    *
    */
  def update(id: Long, balance: Double): Future[Int] = db.run {
     accounts.filter(_.id === id).map(_.balance).update(balance)
  }

  /**
    * Delete an account using id.
    */
  def delete(id: Long): Future[Int] = db.run {
    accounts.filter(_.id === id).delete
  }

  /**
    * Retrieve a single account using id.
    *
    */
  def read(id: Long): Future[Option[Account]] = db.run {
    accounts.filter(_.id === id).result.headOption
  }

  /**
    * List all the accounts.
    */
  def list(): Future[Seq[Account]] = db.run {
    accounts.result
  }

  /**
    * Transfer amount from one account to another
    */
  def transfer(from: Long, to: Long, amount: Double): Future[Transaction] = {
    val atomicTransactionQuery = (for {
      oldFromBalance <- accounts.filter(_.id === from).map(_.balance).result.headOption
        .map(_.getOrElse(throw new Exception(s"Account #${from} does not exist")))
      oldToBalance <- accounts.filter(_.id === to).map(_.balance).result.headOption
        .map(_.getOrElse(throw new Exception(s"Account #${to} does not exist")))
      newFromBalance = (oldFromBalance - amount).signum match {
        case -1 => throw new Exception(s"Insufficient balance: ${oldFromBalance} on account #${from}")
        case _ => oldFromBalance - amount
      }
      newToBalance = oldToBalance + amount
      _ <- accounts.filter(_.id === from).map(_.balance).update(newFromBalance)
      _ <- accounts.filter(_.id === to).map(_.balance).update(newToBalance)
      transaction <- (transactions returning transactions.map(_.id)
        into ((transfer,id) => transfer.copy(id=id))
        ) += Transaction(0, from, to, amount)
    } yield transaction).transactionally
      db.run(atomicTransactionQuery)
  }

  /**
    * List all the accounts.
    */
  def listTransactions(): Future[Seq[Transaction]] = db.run {
    transactions.result
  }

}
