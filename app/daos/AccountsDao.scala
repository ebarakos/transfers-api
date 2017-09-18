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
    * List all the accounts.
    */
  def transfer(from: Long, to: Long, balance: Double): Future[Unit] =
    db.run((for {
      _ <- accounts.filter(_.id === from).map(res => (res.balance)).update(to)
      _ <- accounts.filter(_.id === to).map(res => (res.balance)).update(from)
    } yield ()).transactionally)
}
