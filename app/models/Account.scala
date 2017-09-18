package models

import play.api.libs.json.Json

/**
  * Created by Evangelos.
  */

/**
  * The account model
  *
  * @param id the id of the account.
  * @param balance the account balance

  */
case class Account(
  id: Long,
  balance: Double
)

object Account {
  implicit val userJsonFormat = Json.format[Account]
}
