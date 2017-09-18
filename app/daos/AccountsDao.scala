package daos

import javax.inject.{Inject, Singleton}

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.driver.H2Driver.api._
import models.Account

import scala.concurrent.Future

/**
  * The accounts DAO.
  *
  */
@Singleton
class AccountsDao @Inject() (protected implicit val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile]{

  private class AccountsTable(tag: Tag) extends Table[Account](tag, "accounts") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def balance = column[Long]("balance")
    def currencyId = column[Long]("currency_id")

    def * = (id, balance, currencyId) <> ((Account.apply _).tupled, Account.unapply)
  }

  private val accounts = TableQuery[AccountsTable]

  /**
    * Create an account with a balance and a certain currency.
    *
    */
  def create(balance: Long, currencyId: Long): Future[Account] = db.run {
    (accounts.map(p => (p.balance, p.currencyId))
      returning accounts.map(_.id)
      into ((nameAge, id) => Account(id, nameAge._1, nameAge._2))
      ) += (balance, currencyId)
  }

  /**
    * List all the accounts.
    */
  def list(): Future[Seq[Account]] = db.run {
    accounts.result
  }
}
