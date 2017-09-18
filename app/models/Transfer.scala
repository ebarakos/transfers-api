package models
import java.sql.Date

import play.api.libs.json.Json

/**
  * Created by Evangelos.
  */

/**
  * The transfer model
  *
  * @param id the id of the account.
  * @param fromAccount the account that balance is transferred from
  * @param toAccount  the account that balance is transferred to
  * @param date  the date that the transaction took place
  */
case class Transfer(
  id: Long,
  fromAccount: Long,
  toAccount: Long,
  date: Date
)

object Transfer {
  implicit val transferJsonFormat = Json.format[Transfer]
}
