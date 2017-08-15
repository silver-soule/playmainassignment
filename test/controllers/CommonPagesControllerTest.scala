

package controllers

/*
import akka.stream.Materializer
import akka.util.Timeout
import models._
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Logger
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, redirectLocation, status}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
*/

import akka.stream.Materializer
import com.typesafe.config.ConfigFactory
import models._
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.{DefaultLangs, DefaultMessagesApi}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Configuration, Environment, Logger}

import scala.concurrent.Future

/**
  * Created by Neelaksh on 14/8/17.
  */
class CommonPagesControllerTest extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite {
  val config: Configuration = Configuration(ConfigFactory.load("application.conf"))
  val defaultMessages: DefaultMessagesApi = new DefaultMessagesApi(Environment.simple(), config, new DefaultLangs(config))
  val mockUserProfileForm: UserProfileForm = mock[UserProfileForm]
  val mockUserRepository: UserRepository = mock[UserRepository]
  val mockHobbyRepository: HobbyRepository = mock[HobbyRepository]
  val mockHobbyToUserRepository: HobbyToUserRepository = mock[HobbyToUserRepository]
  val mockAssignmentRepository: AssignmentRepository = mock[AssignmentRepository]
  val commonPagesController = new CommonPagesController(defaultMessages, mockUserRepository, mockHobbyToUserRepository,
    mockHobbyRepository, mockUserProfileForm, mockAssignmentRepository)
  implicit lazy val materializer: Materializer = app.materializer

  "CommonPagesController" should {

    "render home" in {

      val actualUser = User("Neelaksh", None, "Chauhan", 995407, "nilaxch1@gmail.com", "Potato123", "male", 21)
      val user = UserProfileDetails("Neelaksh", None, "Chauhan", 995407, "male", 21, List(1))
      val form = new UserProfileForm().userProfileForm.fill(user)

      val request = FakeRequest(GET, "/home").withSession("emailid" -> "nilaxch1@gmail.com", "isadmin" -> "false")

      when(mockUserProfileForm.userProfileForm).thenReturn(form)
      when(mockUserRepository.getUserData(actualUser.emailId)) thenReturn Future.successful(Some(actualUser))
      when(mockHobbyRepository.getAllHobbies()).thenReturn(Future.successful(List(Hobby(1, "dance"), Hobby(2, "hockey"))))
      when(mockHobbyToUserRepository.getHobbies(actualUser.emailId)).thenReturn(Future.successful(List(1)))

      val result = call(commonPagesController.home(), request)
      status(result) mustBe 200
    }

    "deleteAssignment" should {
      "delete assignment" in {
        val assignmentName = "scala01"
        val request = FakeRequest(POST, "/assignments").withSession("emailid" -> "nilaxch1@gmail.com", "isadmin" -> "true")
        when(mockAssignmentRepository.deleteAssignment(1)) thenReturn Future.successful(true)
        val result = call(commonPagesController.deleteAssignment(1), request)
        redirectLocation(result) mustBe Some("/assignments")
      }
      "redirect invalid session" in {
        val assignmentName = "scala01"
        val request = FakeRequest(POST, "/assignments")
        when(mockAssignmentRepository.deleteAssignment(1)) thenReturn Future.successful(true)
        val result = call(commonPagesController.deleteAssignment(1), request)
        redirectLocation(result) mustBe Some("/")
      }

      "redirect normal user" in {
        val assignmentName = "scala01"
        val request = FakeRequest(POST, "/assignments").withSession("emailid" -> "nilaxch1@gmail.com", "isadmin" -> "false")
        when(mockAssignmentRepository.deleteAssignment(1)) thenReturn Future.successful(true)
        val result = call(commonPagesController.deleteAssignment(1), request)
        redirectLocation(result) mustBe Some("/home")
      }
      "give internal server error" in {
        val assignmentName = "scala01"
        val request = FakeRequest(POST, "/assignments").withSession("emailid" -> "nilaxch1@gmail.com", "isadmin" -> "true")
        when(mockAssignmentRepository.deleteAssignment(1)) thenReturn Future.successful(false)
        val result = call(commonPagesController.deleteAssignment(1), request)
        status(result) mustBe 500
      }
    }


    "displayAssignment" should {
      "render assignments page" in {
        val assignmentName = "scala01"
        val request = FakeRequest(GET, "/assignments").withSession("emailid" -> "nilaxch1@gmail.com", "isadmin" -> "true")
        when(mockAssignmentRepository.getAllAssignments()) thenReturn Future.successful(List(Assignment("scala101", "basic assignment")))
        //val result = controller.displayAssignments().apply(FakeRequest(GET, "/signup"))
        val result = call(commonPagesController.displayAssignments(), request)
        Logger.info(s"RESULT IS $result")
        status(result) mustBe 200
      }

      "not render assignment" in {
        val assignmentName = "scala01"
        val request = FakeRequest(GET, "/assignments")
        when(mockAssignmentRepository.getAllAssignments()) thenReturn Future.successful(List(Assignment("scala101", "basic assignment")))
        //val result = controller.displayAssignments().apply(FakeRequest(GET, "/signup"))
        val result = call(commonPagesController.displayAssignments(), request)
        Logger.info(s"RESULT IS $result")
        redirectLocation(result) mustBe Some("/")
      }
    }
  }


}
