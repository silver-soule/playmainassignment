package controllers


import akka.stream.Materializer
import com.typesafe.config.ConfigFactory
import models.{User, UserRepository}
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.{DefaultLangs, DefaultMessagesApi}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Configuration, Environment, Logger}
import util.Hasher

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
  * Created by Neelaksh on 13/8/17.
  */
class LoginControllerTest extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite {

  implicit lazy val materializer: Materializer = app.materializer
  val config: Configuration = Configuration(ConfigFactory.load("application.conf"))
  val messagesApi: DefaultMessagesApi = new DefaultMessagesApi(Environment.simple(), config, new DefaultLangs(config))
  private val mockloginForm = mock[LoginForm]
  private val mockUserRepository = mock[UserRepository]
  private val mockHasher = mock[Hasher]
  private val mockUpdatePasswordForm = mock[UpdatePasswordForm]
  val controller = new LoginController(mockUserRepository, mockloginForm, mockHasher, mockUpdatePasswordForm, messagesApi)
  val user = User("Neelaksh", None, "Chauhan", 995407, "nilaxch1@gmail.com", "Potato123", "male", 21)

  "LoginController" should {

    "login existing account" in {
      //implicit val timeout = Timeout(10 seconds)
      val form = new LoginForm().loginForm.fill(LoginDetails(user.emailId, user.password))
      when(mockloginForm.loginForm).thenReturn(form)
      when(mockUserRepository.getUserData(user.emailId)) thenReturn Future(Some(user))
      when(mockHasher.checkpw(user.password, user.password)) thenReturn true
      val result = controller.loginPost().apply(FakeRequest(POST, "/login").withFormUrlEncodedBody(
        "emailId" -> user.emailId, "password" -> user.password
      ))
      redirectLocation(result) mustBe Some("/home")

    }

    "redirect disabled account" in {
      val user2 = user.copy(isEnabled = false)
      //implicit val timeout = Timeout(10 seconds)
      val form = new LoginForm().loginForm.fill(LoginDetails(user2.emailId, user2.password))
      when(mockloginForm.loginForm).thenReturn(form)
      when(mockUserRepository.getUserData(user2.emailId)) thenReturn Future(Some(user2))
      when(mockHasher.checkpw(user2.password, user2.password)) thenReturn true
      val result = controller.loginPost.apply(FakeRequest(POST, "/login").withFormUrlEncodedBody(
        "emailId" -> "nilaxch1@gmail.com", "password" -> "Potato123"
      ))
      redirectLocation(result) mustBe Some("/login")
    }

    "redirect BadRequest " in {
      //implicit val timeout = Timeout(10 seconds)
      val form = new LoginForm().loginForm.fill(LoginDetails("neelaksh", user.password))
      when(mockloginForm.loginForm).thenReturn(form)
      val result = controller.loginPost().apply(FakeRequest(GET, "/login").withFormUrlEncodedBody(
        "emailId" -> "neelaksh", "password" -> "Potato123"
      ))
      status(result) mustEqual BAD_REQUEST
    }

    "redirect if user does not exist" in {
      val form = new LoginForm().loginForm.fill(LoginDetails(user.emailId, user.password))
      when(mockloginForm.loginForm).thenReturn(form)
      when(mockUserRepository.getUserData(user.emailId)) thenReturn Future.successful(None)
      val result = controller.loginPost().apply(FakeRequest(POST, "/login").withFormUrlEncodedBody(
        "emailId" -> "nilaxch1@gmail.com", "password" -> "Potato123"
      ))
      redirectLocation(result) mustBe Some("/signup")
    }

    "redirect to login if email and password don't match" in {
      val form = new LoginForm().loginForm.fill(LoginDetails(user.emailId, user.password))
      when(mockloginForm.loginForm).thenReturn(form)
      when(mockUserRepository.getUserData(user.emailId)) thenReturn Future.successful(Some(user))
      when(mockHasher.checkpw(user.password, user.password)) thenReturn false
      val result = controller.loginPost().apply(FakeRequest(POST, "/login").withFormUrlEncodedBody(
        "emailId" -> "nilaxch1@gmail.com", "password" -> "Potato123"
      ))
      redirectLocation(result) mustBe Some("/login")
    }

    "render login" in {
      val form = new LoginForm().loginForm.fill(LoginDetails(user.emailId, user.password))
      Logger.error(s"$form")
      when(mockloginForm.loginForm) thenReturn form
      val request = FakeRequest(GET, "/signup")
      val result = controller.login().apply(request)
      status(result) mustEqual 200
    }

    "render update password page" in {
      val form = new UpdatePasswordForm().updatePasswordForm
      when(mockUpdatePasswordForm.updatePasswordForm) thenReturn form
      val request = FakeRequest(GET, "/updatepassword")
      val result = controller.updatePassword().apply(request)
      status(result) mustEqual OK

    }

    "update password successfuly" in {
      val form = new UpdatePasswordForm().updatePasswordForm.fill(UpdatePasswordDetails(user.emailId,user.password,user.password))
      when(mockUpdatePasswordForm.updatePasswordForm) thenReturn form
      when(mockHasher.hashpw(user.password)) thenReturn user.password
      when(mockUserRepository.updatePassword(user.emailId,user.password)) thenReturn Future.successful(true)
      val request = FakeRequest(POST, "/updatepassword").withFormUrlEncodedBody(
        "emailId" -> user.emailId, "password" -> user.password,"verifyPassword" -> user.password
      )
      val result = controller.updatePasswordPost().apply(request)
      redirectLocation(result) mustBe Some("/login")
    }

    "tell can't update password as user doesnt exist" in {
      val form = new UpdatePasswordForm().updatePasswordForm.fill(UpdatePasswordDetails(user.emailId,user.password,user.password))
      when(mockUpdatePasswordForm.updatePasswordForm) thenReturn form
      when(mockHasher.hashpw(user.password)) thenReturn user.password
      when(mockUserRepository.updatePassword(user.emailId,user.password)) thenReturn Future.successful(false)
      val request = FakeRequest(POST, "/updatepassword").withFormUrlEncodedBody(
        "emailId" -> user.emailId, "password" -> user.password,"verifyPassword" -> user.password
      )
      val result = controller.updatePasswordPost().apply(request)
      redirectLocation(result) mustBe Some("/login")
    }

  }

}
