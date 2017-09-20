package daos

import javax.inject.{Inject, Singleton}

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.driver.H2Driver.api._
import models._
import modules.components.TableComponents._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * The accounts DAO.
  *
  */
@Singleton
class AccountsDao @Inject() (protected implicit val dbConfigProvider: DatabaseConfigProvider)
  extends AccountsComponent with HasDatabaseConfigProvider[JdbcProfile]{

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

}
