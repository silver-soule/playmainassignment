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

import scala.concurrent.Future

/**
  * Created by Neelaksh on 11/8/17.
  */
class SignUpControllerTest extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite {

  implicit lazy val materializer: Materializer = app.materializer
  val config: Configuration = Configuration(ConfigFactory.load("application.conf"))
  val messagesApi: DefaultMessagesApi = new DefaultMessagesApi(Environment.simple(), config, new DefaultLangs(config))
  private val mockSignUpForm = mock[SignUpForm]
  private val mockUserRepository = mock[UserRepository]
  private val mockHasher = mock[Hasher]
  val controller = new SignUpController(messagesApi, mockUserRepository, mockSignUpForm, mockHasher)
  val userDetails = SignUpDetails("Neelaksh", None, "Chauhan", 995407, "nilaxch@gmail.com", "Potato123", "Potato123", "male", 21)
  val user = User("Neelaksh", None, "Chauhan", 995407, "nilaxch1@gmail.com", "Potato123", "male", 21)

  "SignUpController" should {

    "register new account" in {
      val form = new SignUpForm().signUpForm.fill(userDetails)
      Logger.info(s"----------$form")
      when(mockSignUpForm.signUpForm).thenReturn(form)
      when(mockUserRepository.checkIfExists("nilaxch1@gmail.com")).thenReturn(Future.successful(false))
      when(mockUserRepository.store(user)).thenReturn(Future.successful(true))
      when(mockHasher.hashPassword(user.password)) thenReturn user.password
      val result = controller.signUpPost().apply(FakeRequest(POST, "/signup").withFormUrlEncodedBody(
        "firstName" -> "Neelaksh", "middleName" -> "", "lastName" -> "Chauhan", "mobileNumber" -> "995407",
        "emailId" -> "nilaxch1@gmail.com", "password" -> "Potato123", "verifyPassword" -> "Potato123",
        "gender" -> "male", "age" -> "21"
      ))
      redirectLocation(result) mustBe Some("/home")
    }

    "invalidate account register " in {
      val form = new SignUpForm().signUpForm.fill(userDetails)
      when(mockSignUpForm.signUpForm).thenReturn(form)
      when(mockUserRepository.checkIfExists("nilaxch1@gmail.com")).thenReturn(Future.successful(true))
      when(mockHasher.hashPassword(user.password)) thenReturn user.password
      val result = controller.signUpPost().apply(FakeRequest(POST, "/signup").withFormUrlEncodedBody(
        "firstName" -> "Neelaksh", "middleName" -> "", "lastName" -> "Chauhan", "mobileNumber" -> "995407",
        "emailId" -> "nilaxch1@gmail.com", "password" -> "Potato123", "verifyPassword" -> "Potato123",
        "gender" -> "male", "age" -> "21"
      ))
      redirectLocation(result) mustBe Some("/signup")
    }


    "give internal server error " in {
      val form = new SignUpForm().signUpForm.fill(userDetails)
      when(mockSignUpForm.signUpForm).thenReturn(form)
      when(mockUserRepository.checkIfExists("nilaxch1@gmail.com")).thenReturn(Future.successful(false))
      when(mockUserRepository.store(user)).thenReturn(Future.successful(false))
      when(mockHasher.hashPassword(user.password)) thenReturn user.password
      val result = controller.signUpPost().apply(FakeRequest(POST, "/signup").withFormUrlEncodedBody(
        "firstName" -> "Neelaksh", "middleName" -> "", "lastName" -> "Chauhan", "mobileNumber" -> "995407",
        "emailId" -> "nilaxch1@gmail.com", "password" -> "Potato123", "verifyPassword" -> "Potato123",
        "gender" -> "male", "age" -> "21"
      ))
      status(result) mustEqual 500
    }
  }

    "render signup page" in {
      val form = new SignUpForm().signUpForm
      Logger.error(s"$form")
      when(mockSignUpForm.signUpForm) thenReturn form
      val request = FakeRequest(GET, "/signup")
      val result = controller.signUp().apply(request)
      status(result) mustEqual 200
    }
}
