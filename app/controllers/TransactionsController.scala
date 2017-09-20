package controllers

import javax.inject.Inject

import daos.TransactionsDao
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Endpoints to manage transactions.
  *
  * Created by Evangelos.
  */
class TransactionsController @Inject()(
    transactionsDao: TransactionsDao) extends Controller
      {

  /** Transfers balances between accounts
    *
    * @param from the account to transfer from
    * @param to the account to transfer to
    * @param amount the account balance
    * @return a JSON with a success/fail message
    */
  def transfer(from: Long, to: Long, amount: Double) = Action.async { implicit request =>
    transactionsDao.transfer(from, to, amount).map {
      case t => Ok(Json.obj("message" -> s"Transferred ${t.amount} from account #${t.from} to #${t.to}. Transaction id: ${t.id}"))
    } recover {
      case ex: Exception => NotFound(Json.obj("message" -> s"Transfer not completed: ${ex.getMessage}"))
    }
  }

  /** Retrieves the list of all Transactions.
    *
    * @return a JSON with the Seq of [[models.Transaction]].
    */
  def listTransactions() = Action.async { implicit request =>
    transactionsDao.listTransactions().map{ transactions => Ok(Json.toJson(transactions)) }
  }
}
