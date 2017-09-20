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
  * The transactions DAO.
  *
  */
@Singleton
class TransactionsDao @Inject() (protected implicit val dbConfigProvider: DatabaseConfigProvider)
  extends AccountsComponent with TransactionsComponent with HasDatabaseConfigProvider[JdbcProfile] {

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
    // .transactionally method is used to execute the above queries in a single transaction.
    // This means that if any query fails, all prior operations will be rolled back,
    // leaving the database in a consistent state.
    } yield transaction).transactionally
    db.run(atomicTransactionQuery)
  }

  /**
    * List all the transactions.
    */
  def listTransactions(): Future[Seq[Transaction]] = db.run {
    transactions.result
  }

}
