package models

import play.api.libs.json.Json

/**
  * Created by Evangelos.
  */

/**
  * The transaction model
  *
  * @param id the id of the account.
  * @param from the account that balance is transferred from
  * @param to  the account that balance is transferred to
  */
case class Transaction(
  id: Long,
  from: Long,
  to: Long,
  amount: Double
)

object Transaction {
  implicit val transferJsonFormat = Json.format[Transaction]
}
