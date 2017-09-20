package controllers

import daos.AccountsDao
import models._
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future


class AccountsControllerSpec extends PlaySpec with MockitoSugar {

  trait Context {
    val accountsDao = mock[AccountsDao]
    val accountsControllers = new AccountsController(accountsDao)
    val account = new Account(1, 1000)
    val account2 = new Account(2, 2000)
    val accounts = Seq(account, account2)
  }

  "list accounts" must {
    "return 200 and the list of accounts" in new Context {
      when(accountsDao.list()).thenReturn(Future.successful(accounts))
      val result = accountsControllers.list()(FakeRequest())
      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(accounts)
    }
  }

  "get account" must {
    "return 200 and the account if exists" in new Context {
      when(accountsDao.read(1)).thenReturn(Future.successful(Some(account)))
      val result = accountsControllers.get(1)(FakeRequest())
      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(account)
    }

    "return 404 if not exists" in new Context {
      when(accountsDao.read(3)).thenReturn(Future.successful(None))
      val result = accountsControllers.get(3)(FakeRequest())
      status(result) mustBe NOT_FOUND
      contentAsJson(result) mustBe Json.obj("message" -> s"Account with id: 3 does not exist")
    }
  }

  "create account" must {
    "return 200 and the account creation message" in new Context {
      when(accountsDao.insert(1000)).thenReturn(Future.successful(account))
      val result = accountsControllers.create(1000)(FakeRequest())
      status(result) mustBe OK
      contentAsJson(result) mustBe Json.obj("message" -> s"Account #${account.id} created with balance: ${account.balance}")
    }
  }

  "update account" must {
    "return 200 and the updated account message" in new Context {
      when(accountsDao.update(1, 500)).thenReturn(Future.successful(1))
      val result = accountsControllers.update(1, 500)(FakeRequest())
      status(result) mustBe OK
      contentAsJson(result) mustBe Json.obj("message" -> s"Account with id: 1 was updated with balance: 500.0")
    }

    "return 404 if not exists" in new Context {
      when(accountsDao.update(3, 500)).thenReturn(Future.successful(0))
      val result = accountsControllers.update(3, 500)(FakeRequest())
      status(result) mustBe NOT_FOUND
      contentAsJson(result) mustBe Json.obj("message" -> s"Account with id: 3 does not exist")
    }
  }

  "delete account" must {
    "return 200 and the deleted account message" in new Context {
      when(accountsDao.delete(1)).thenReturn(Future.successful(1))
      val result = accountsControllers.delete(1)(FakeRequest())
      status(result) mustBe OK
      contentAsJson(result) mustBe Json.obj("message" -> s"Account with id: 1 was deleted")
    }

    "return 404 if not exists" in new Context {
      when(accountsDao.delete(3)).thenReturn(Future.successful(0))
      val result = accountsControllers.delete(3)(FakeRequest())
      status(result) mustBe NOT_FOUND
      contentAsJson(result) mustBe Json.obj("message" -> s"Account with id: 3 does not exist")    }
  }

}