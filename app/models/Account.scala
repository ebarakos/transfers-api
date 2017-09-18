package models

import play.api.libs.json.Json

/**
  * Created by Evangelos.
  */

/**
  * The account model
  *
  * @param id the id of the account.
  * @param currencyId the accepted currency

  */
case class Account(
  id: Long,
  balance: Long,
  currencyId: Long
)

object Account {
  implicit val userJsonFormat = Json.format[Account]
}
