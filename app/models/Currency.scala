package models

import play.api.libs.json.Json

/**
  * Created by Evangelos.
  */

/**
  * The currency model
  *
  * @param id the id of the account.
  * @param symbol the currency's symbol
  * @param price  the current parity in GBP.
  */
case class Currency(
  id: Long,
  symbol: String,
  price: Long
)

object Currency {
  implicit val currencyJsonFormat = Json.format[Currency]
}
