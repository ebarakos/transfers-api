package daos

import javax.inject.{Inject, Singleton}

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.driver.H2Driver.api._
import models._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

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

  /**
    * Create an account with a balance and a certain currency.
    *
    */
  def insert(balance: Double): Future[Account] = db.run {
    (accounts.map(p => p.balance)
      returning accounts.map(_.id)
      into ((res, id) => Account(id, res))
      ) += (balance)
  }

  /**
    * Update an account.
    *
    */
  def update(id: Long, balance: Double): Future[Account] = db.run {
    (accounts.map(p => p.balance)
      returning accounts.map(_.id)
      into ((res, id) => Account(id, res))
      ) += (balance)
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
    * Transfer balance from one account to another
    */
  def transfer(from: Long, to: Long, balance: Double): Future[Unit] = {
    val atomicTransactionQuery = (for {
      oldFromBalance <- accounts.filter(_.id === from).map(_.balance).result.headOption
        .map(_.getOrElse(throw new Exception(s"Account #${from} does not exist")))
      oldToBalance <- accounts.filter(_.id === to).map(_.balance).result.headOption
        .map(_.getOrElse(throw new Exception(s"Account #${to} does not exist")))
      newFromBalance = (oldFromBalance - balance).signum match {
            case 1 => oldFromBalance - balance
            case _ => throw new Exception(s"Insufficient balance on account #${from}")
          }
      newToBalance = oldToBalance + balance
      _ <- accounts.filter(_.id === from).map(_.balance).update(newFromBalance)
      _ <- accounts.filter(_.id === to).map(_.balance).update(newToBalance)
    } yield ()).transactionally
      db.run(atomicTransactionQuery)
  }
}
