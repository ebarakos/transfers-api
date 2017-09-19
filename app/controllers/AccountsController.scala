package controllers

import javax.inject.Inject

import daos.AccountsDao
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Endpoints to manage accounts.
  *
  * Created by Evangelos.
  */
class AccountsController @Inject() (
    accountsDao: AccountsDao) extends Controller
      {

  /** Retrieves the list of all the Accounts.
    *
    * @return a JSON with the Seq of [[models.Account]].
    */
  def list() = Action.async { implicit request =>
    accountsDao.list().map{ accounts => Ok(Json.toJson(accounts)) }
  }

  /** Retrieves a single [[models.Account]] based on id.
    *
    * @return the [[models.Account]].
    */
  def get(id: Long) = Action.async { implicit request =>
    accountsDao.read(id).map{
      case Some(account) => Ok(Json.toJson(account))
      case _ => Ok(Json.obj("message" -> s"Account with id: ${id} does not exist"))
    }
  }


  /** Updates an [[models.Account]] balance based on id.
    *
    * @return a JSON with a success/fail message
    */
  def update(id: Long, balance: Double) = Action.async { implicit request =>
    accountsDao.update(id, balance).map{
      case 1 => Ok(Json.obj("message" -> s"Account with id: ${id} was updated with balance: ${balance}"))
      case _ => Ok(Json.obj("message" -> s"Account with id: ${id} was not found"))
    }
  }

  /** Deletes a single [[models.Account]] based on id.
    *
    * @return a JSON with a success/fail message
    */
  def delete(id: Long) = Action.async { implicit request =>
    accountsDao.delete(id).map{
      case 1 => Ok(Json.obj("message" -> s"Account with id: ${id} was deleted"))
      case _ => Ok(Json.obj("message" -> s"Account with id: ${id} was not found"))
    }
  }

  /** Creates a [[models.Account]]
    *
    * @param balance the account balance
    * @return a JSON with a success/fail message
    */
  def create(balance: Double) = Action.async { implicit request =>
    accountsDao.insert(balance).map {
      case account => Ok(Json.obj("message" -> s"Account #${account.id} created with balance: ${account.balance}"))
    }
  }

  /** Transfers balances between accounts
    *
    * @param from the account to transfer from
    * @param to the account to transfer to
    * @param amount the account balance
    * @return a JSON with a success/fail message
    */
  def transfer(from: Long, to: Long, amount: Double) = Action.async { implicit request =>
    accountsDao.transfer(from, to, amount).map {
      case t => Ok(Json.obj("message" -> s"Transferred ${t.amount} from account #${t.from} to #${t.to}. Transaction id: ${t.id}"))
    } recover {
      case ex: Exception => Ok(Json.obj("message" -> s"Transfer not completed: ${ex.getMessage}"))
    }
  }

  /** Retrieves the list of all Transactions.
    *
    * @return a JSON with the Seq of [[models.Transaction]].
    */
  def listTransactions() = Action.async { implicit request =>
    accountsDao.listTransactions().map{ transactions => Ok(Json.toJson(transactions)) }
  }
}
