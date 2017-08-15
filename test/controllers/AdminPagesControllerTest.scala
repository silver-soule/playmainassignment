package controllers

import akka.stream.Materializer
import akka.util.Timeout
import models.{AssignmentRepository, User, UserRepository}
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.MessagesApi
import org.mockito.Mockito._
import play.Logger
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, POST, redirectLocation, status}
import util.Hasher
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

/**
  * Created by Neelaksh on 15/8/17.
  */
class AdminPagesControllerTest extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite {

  val mockmessageapi: MessagesApi = mock[MessagesApi]

  val mockUserRepository: UserRepository = mock[UserRepository]
  val mockAssignmentRepository : AssignmentRepository = mock[AssignmentRepository]
  val mockaddAssignmentForm : AddAssignmentForm = mock[AddAssignmentForm]
  val adminPagesController = new AdminPagesController(mockmessageapi,mockUserRepository,mockaddAssignmentForm,mockAssignmentRepository)
  implicit lazy val materializer: Materializer = app.materializer
  "displayUsers" should {
    "redirect if user is not logged in" in {
      implicit val timeout = Timeout(10 seconds)
      val request = FakeRequest(GET, "/home")
      val result = adminPagesController.displayUsers().apply(request)
      redirectLocation(result) mustBe Some("/")
    }

    "redirect if user is not admin" in {
      implicit val timeout = Timeout(10 seconds)
      val request = FakeRequest(GET, "/home").withSession("emailid" -> "nilaxch1@gmail.com", "isadmin" -> "false")
      val result = adminPagesController.displayUsers().apply(request)
      redirectLocation(result) mustBe Some("/home")
    }

    "display page if user is admin" in {
      implicit val timeout = Timeout(10 seconds)
      val request = FakeRequest(GET, "/home").withSession("emailid" -> "nilaxch1@gmail.com", "isadmin" -> "true")
      val user = User("Neelaksh", None, "Chauhan", 995407, "nilaxch1@gmail.com", "Potato123", "male", 21)
      when(mockUserRepository.getAllNormalUsers()) thenReturn Future(List(user))
      val result = adminPagesController.displayUsers().apply(request)
      status(result) mustEqual 200
    }
  }

  "setPermissions" should {
    val user = User("Neelaksh", None, "Chauhan", 995407, "nilaxch1@gmail.com", "Potato123", "male", 21)
    val user2 = User("Neelaksh", None, "Chauhan", 995407, "nilaxch@gmail.com", "Potato123", "male", 21)

    "redirect if user is not logged in" in {
      implicit val timeout = Timeout(10 seconds)
      val request = FakeRequest(GET, "/home")
      val result = adminPagesController.flipPermissions(user.emailId,user.isEnabled).apply(request)
      redirectLocation(result) mustBe Some("/")
    }

    "redirect if user is not admin" in {
      implicit val timeout = Timeout(10 seconds)
      val request = FakeRequest(GET, "/home").withSession("emailid" -> "nilaxch1@gmail.com", "isadmin" -> "false")
      val result = adminPagesController.flipPermissions(user.emailId,user.isEnabled).apply(request)
      redirectLocation(result) mustBe Some("/home")
    }

    "flip user permissions" in {
      implicit val timeout = Timeout(10 seconds)
      val request = FakeRequest(POST, "/users").withSession("emailid" -> "nilaxch1@gmail.com", "isadmin" -> "true")
      when(mockUserRepository.setPermissions(user2.emailId,!user2.isEnabled)) thenReturn Future.successful(true)
      val result = adminPagesController.flipPermissions(user2.emailId,user2.isEnabled).apply(request)
      redirectLocation(result) mustBe Some("/users")
    }

    "throw internal server error" in {
      implicit val timeout = Timeout(10 seconds)
      val request = FakeRequest(POST, "/users").withSession("emailid" -> "nilaxch1@gmail.com", "isadmin" -> "true")
      when(mockUserRepository.setPermissions(user2.emailId,!user2.isEnabled)) thenReturn Future.successful(false)
      val result = adminPagesController.flipPermissions(user2.emailId,user2.isEnabled).apply(request)
      status(result) mustEqual 500
    }
  }


}

