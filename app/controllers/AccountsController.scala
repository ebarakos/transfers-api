package controllers

import javax.inject.Inject

import daos.AccountsDao
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

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
  def list() =  Action.async { implicit request =>
    accountsDao.list().map{ accounts => Ok(Json.toJson(accounts)) }
  }

  /** Creates a [[models.Account]]
    *
    * @param balance the account balance
    * @param currencyId the currency id
    * @return a JSON with a success/fail String message
    */
  def create(balance: Long, currencyId: Option[Long]) = Action.async { implicit request =>
    accountsDao.create(balance, currencyId.getOrElse(1)).map {
      case _ => Ok(Json.obj("message" -> s"Account created in currency id: ${currencyId} with balance: ${balance}"))
    }
  }


}
