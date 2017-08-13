package controllers

import akka.stream.Materializer
import akka.util.Timeout
import models.{User, UserRepository}
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play._
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, redirectLocation}
import org.mockito.Mockito._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

/**
  * Created by Neelaksh on 11/8/17.
  */
class SignUpControllerTest extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite {

  implicit val mockmessageapi = mock[MessagesApi]
  val mocksignUpForm = mock[SignUpForm]
  val mockuserRepository = mock[UserRepository]
  val controller = new SignUpController(mockmessageapi, mockuserRepository, mocksignUpForm)
  implicit lazy val materializer: Materializer = app.materializer

  "SignupController" should {

    "register new account" in {
      val user = SignUpDetails("Neelaksh", None, "Chauhan", 995407, "nilaxch@gmail.com", "Potato123", "Potato123", "male", 21)
      val form = new SignUpForm().signUpForm.fill(user)
      when(mocksignUpForm.signUpForm).thenReturn(form)
      when(mockuserRepository.getEmailId("nilaxch1@gmail.com")).thenReturn(Future.successful(None))
      when(mockuserRepository.store(User("Neelaksh", None, "Chauhan", 995407, "nilaxch1@gmail.com", "Potato123", "male", 21)))
        .thenReturn(Future.successful(true))
      implicit val timeout = Timeout(10 seconds)
      val result = controller.signUpPost().apply(FakeRequest(GET, "/signup").withFormUrlEncodedBody(
        "firstName" -> "Neelaksh", "middleName" -> "", "lastName" -> "Chauhan", "mobileNumber" -> "995407",
        "emailId" -> "nilaxch1@gmail.com", "password" -> "Potato123", "verifyPassword" -> "Potato123",
        "gender" -> "male", "age" -> "21"
      ))
      redirectLocation(result) mustBe Some("/home")
    }

    "invalidate account register " in {
      val user = SignUpDetails("Neelaksh", None, "Chauhan", 995407, "nilaxch@gmail.com", "Potato123", "Potato123", "male", 21)
      val form = new SignUpForm().signUpForm.fill(user)
      when(mocksignUpForm.signUpForm).thenReturn(form)
      when(mockuserRepository.getEmailId("nilaxch1@gmail.com")).thenReturn(Future.successful(Some("nilaxch1@gmail.com")))
      implicit val timeout = Timeout(10 seconds)
      val result = controller.signUpPost().apply(FakeRequest(GET, "/signup").withFormUrlEncodedBody(
        "firstName" -> "Neelaksh", "middleName" -> "", "lastName" -> "Chauhan", "mobileNumber" -> "995407",
        "emailId" -> "nilaxch1@gmail.com", "password" -> "Potato123", "verifyPassword" -> "Potato123",
        "gender" -> "male", "age" -> "21"
      ))
      redirectLocation(result) mustBe Some("/signup")
    }


    //DOUBT HOW TO CHECK INTERNAL SERVER ERROR
    "give internal server error " in {
      val user = SignUpDetails("Neelaksh", None, "Chauhan", 995407, "nilaxch@gmail.com", "Potato123", "Potato123", "male", 21)
      val form = new SignUpForm().signUpForm.fill(user)
      when(mocksignUpForm.signUpForm).thenReturn(form)
      when(mockuserRepository.getEmailId("nilaxch1@gmail.com")).thenReturn(Future.successful(None))
      when(mockuserRepository.store(User("Neelaksh", None, "Chauhan", 995407, "nilaxch1@gmail.com", "Potato123", "male", 21)))
        .thenReturn(Future.successful(false))
      implicit val timeout = Timeout(10 seconds)
      val result = controller.signUpPost().apply(FakeRequest(GET, "/signup").withFormUrlEncodedBody(
        "firstName" -> "Neelaksh", "middleName" -> "", "lastName" -> "Chauhan", "mobileNumber" -> "995407",
        "emailId" -> "nilaxch1@gmail.com", "password" -> "Potato123", "verifyPassword" -> "Potato123",
        "gender" -> "male", "age" -> "21"
      ))
      redirectLocation(result) mustBe Some("/signup")
    }

  }
}
