package controllers

import akka.stream.Materializer
import akka.util.Timeout
import models.{User, UserRepository}
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.MessagesApi
import org.mockito.Mockito._
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, redirectLocation}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt


/**
  * Created by Neelaksh on 13/8/17.
  */
class LoginControllerTest extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite {

  implicit val mockmessageapi = mock[MessagesApi]
  val mockloginForm = mock[LoginForm]
  val mockuserRepository = mock[UserRepository]
  val controller = new LoginController(mockmessageapi, mockuserRepository, mockloginForm)
  implicit lazy val materializer: Materializer = app.materializer

  "LoginController" should {

    "login existing account" in {
      implicit val timeout = Timeout(10 seconds)
      val user = User("Neelaksh", None, "Chauhan", 995407, "nilaxch1@gmail.com", "Potato123", "male", 21)
      val form = new LoginForm().loginForm.fill(LoginDetails(user.emailId,user.password))
      when(mockloginForm.loginForm).thenReturn(form)
      when(mockuserRepository.getUserData(user.emailId,user.password)) thenReturn Future(Some(user))

      val result = controller.loginPost().apply(FakeRequest(GET, "/signup").withFormUrlEncodedBody(
        "emailId" -> "nilaxch1@gmail.com", "password" -> "Potato123"
      ))
      redirectLocation(result) mustBe Some("/home")

    }

    "redirect disabled account" in {
      implicit val timeout = Timeout(10 seconds)
      val user = User("Neelaksh", None, "Chauhan", 995407, "nilaxch1@gmail.com", "Potato123", "male", 21,isEnabled = false)
      val form = new LoginForm().loginForm.fill(LoginDetails(user.emailId,user.password))
      when(mockloginForm.loginForm).thenReturn(form)
      when(mockuserRepository.getUserData(user.emailId,user.password)) thenReturn Future(Some(user))

      val result = controller.loginPost().apply(FakeRequest(GET, "/signup").withFormUrlEncodedBody(
        "emailId" -> "nilaxch1@gmail.com", "password" -> "Potato123"
      ))
      redirectLocation(result) mustBe Some("/login")
    }

    "redirect if email and password don't match " in {
      implicit val timeout = Timeout(10 seconds)
      val user = User("Neelaksh", None, "Chauhan", 995407, "nilaxch1@gmail.com", "Potato123", "male", 21,isEnabled = false)
      val form = new LoginForm().loginForm.fill(LoginDetails(user.emailId,user.password))
      when(mockloginForm.loginForm).thenReturn(form)
      when(mockuserRepository.getUserData(user.emailId,user.password)) thenReturn Future(None)

      val result = controller.loginPost().apply(FakeRequest(GET, "/signup").withFormUrlEncodedBody(
        "emailId" -> "nilaxch1@gmail.com", "password" -> "Potato123"
      ))
      redirectLocation(result) mustBe Some("/login")
    }
  }

}
