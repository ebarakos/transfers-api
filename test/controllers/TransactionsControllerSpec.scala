package controllers

import daos._
import models._
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future


class TransactionsControllerSpec extends PlaySpec with MockitoSugar {

  trait Context {
    val transactionsDao = mock[TransactionsDao]
    val transactionsControllers = new TransactionsController(transactionsDao)
    val transaction = new Transaction(1, 1, 2, 200)
    val transaction2 = new Transaction(2, 2, 1, 300)
    val transactions = Seq(transaction, transaction2)
  }

  "transfer amount" must {
    "return 200 if from account has sufficient balance and both accounts exist" in new Context {
      val from = 1
      val to = 2
      val amount = 200
      when(transactionsDao.transfer(from, to, amount)).thenReturn(Future.successful(transaction))
      val result = transactionsControllers.transfer(from, to, amount)(FakeRequest())
      status(result) mustBe OK
    }
  }

  "list transactions" must {
    "return 200 and the list of transactions" in new Context {
      when(transactionsDao.listTransactions()).thenReturn(Future.successful(transactions))
      val result = transactionsControllers.listTransactions()(FakeRequest())
      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(transactions)
    }
  }

}