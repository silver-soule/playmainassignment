package controllers

import akka.stream.Materializer
import com.typesafe.config.ConfigFactory
import models.{Assignment, AssignmentRepository, User, UserRepository}
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.{Configuration, Environment}
import play.api.i18n.{DefaultLangs, DefaultMessagesApi, MessagesApi}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Neelaksh on 15/8/17.
  */
class AdminPagesControllerTest extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite {

  val config: Configuration = Configuration(ConfigFactory.load("application.conf"))
  val messagesApi: DefaultMessagesApi = new DefaultMessagesApi(Environment.simple(), config, new DefaultLangs(config))
  val mockUserRepository: UserRepository = mock[UserRepository]
  val mockAssignmentRepository: AssignmentRepository = mock[AssignmentRepository]
  val mockaddAssignmentForm: AddAssignmentForm = mock[AddAssignmentForm]
  val adminPagesController = new AdminPagesController(messagesApi, mockUserRepository, mockaddAssignmentForm, mockAssignmentRepository)
  implicit lazy val materializer: Materializer = app.materializer

  "displayUsers" should {
    "redirect if user is not logged in" in {
      val request = FakeRequest(GET, "/users")
      val result = adminPagesController.displayUsers().apply(request)
      redirectLocation(result) mustBe Some("/login")
    }

    "redirect if user is not admin" in {
      val request = FakeRequest(GET, "/users").withSession("emailid" -> "nilaxch1@gmail.com", "isadmin" -> "false")
      val result = adminPagesController.displayUsers().apply(request)
      redirectLocation(result) mustBe Some("/home")
    }

    "display page if user is admin" in {
      val request = FakeRequest(GET, "/users").withSession("emailid" -> "nilaxch1@gmail.com", "isadmin" -> "true")
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
      val request = FakeRequest(POST, "/users")
      val result = adminPagesController.flipPermissions(user.emailId, user.isEnabled).apply(request)
      redirectLocation(result) mustBe Some("/login")
    }

    "redirect if user is not admin" in {
      val request = FakeRequest(POST, "/users").withSession("emailid" -> "nilaxch1@gmail.com", "isadmin" -> "false")
      val result = adminPagesController.flipPermissions(user.emailId, user.isEnabled).apply(request)
      redirectLocation(result) mustBe Some("/home")
    }

    "flip user permissions" in {
      val request = FakeRequest(POST, "/users").withSession("emailid" -> "nilaxch1@gmail.com", "isadmin" -> "true")
      when(mockUserRepository.setPermissions(user2.emailId, !user2.isEnabled)) thenReturn Future.successful(true)
      val result = adminPagesController.flipPermissions(user2.emailId, user2.isEnabled).apply(request)
      redirectLocation(result) mustBe Some("/users")
    }

    "throw internal server error" in {
      val request = FakeRequest(POST, "/users").withSession("emailid" -> "nilaxch1@gmail.com", "isadmin" -> "true")
      when(mockUserRepository.setPermissions(user2.emailId, !user2.isEnabled)) thenReturn Future.successful(false)
      val result = adminPagesController.flipPermissions(user2.emailId, user2.isEnabled).apply(request)
      status(result) mustEqual 500
    }
  }

  "add assignments" should {
    "render add assignment form" in {
      val form = new AddAssignmentForm().addAssignmentForm
      val request = FakeRequest(GET, "/addassignment").withSession("emailid" -> "nilaxch1@gmail.com", "isadmin" -> "true")
      when(mockaddAssignmentForm.addAssignmentForm).thenReturn(form)
      val result = adminPagesController.addAssignment().apply(request)
      status(result) mustEqual OK
    }

    "redirect if user not admin" in {
      val form = new AddAssignmentForm().addAssignmentForm
      val request = FakeRequest(GET, "/addassignment").withSession("emailid" -> "nilaxch1@gmail.com", "isadmin" -> "false")
      when(mockaddAssignmentForm.addAssignmentForm).thenReturn(form)
      val result = adminPagesController.addAssignment().apply(request)
      redirectLocation(result) mustBe Some("/home")
    }
    "redirect if user not logged " in {
      val form = new AddAssignmentForm().addAssignmentForm
      val request = FakeRequest(GET, "/addassignment")
      when(mockaddAssignmentForm.addAssignmentForm).thenReturn(form)
      val result = adminPagesController.addAssignment().apply(request)
      redirectLocation(result) mustBe Some("/login")
    }
  }

  "add assignment post " should {
    "succesfully add assignment" in {
      val form = new AddAssignmentForm().addAssignmentForm.fill(AssignmentDetails("scala01", "big assignment"))
      when(mockaddAssignmentForm.addAssignmentForm) thenReturn form
      when(mockAssignmentRepository.addAssignment(Assignment("scala01","big assignment"))) thenReturn Future.successful(true)
      val request = FakeRequest(POST, "/addassignment").withSession("emailid" -> "nilaxch1@gmail.com", "isadmin" -> "true")
        .withFormUrlEncodedBody("title" -> "scala01", "description" -> "big assignment")
      val result = adminPagesController.addAssignmentPost().apply(request)
      redirectLocation(result) mustBe  Some("/addassignment")
    }

    "throw internalerror" in {
      val form = new AddAssignmentForm().addAssignmentForm.fill(AssignmentDetails("scala01", "big assignment"))
      when(mockaddAssignmentForm.addAssignmentForm) thenReturn form
      when(mockAssignmentRepository.addAssignment(Assignment("scala01","big assignment"))) thenReturn Future.successful(false)
      val request = FakeRequest(POST, "/addassignment").withSession("emailid" -> "nilaxch1@gmail.com", "isadmin" -> "true")
        .withFormUrlEncodedBody("title" -> "scala01", "description" -> "big assignment")
      val result = adminPagesController.addAssignmentPost().apply(request)
      status(result) mustBe  500
    }

    "redirect normal user" in {
        val form = new AddAssignmentForm().addAssignmentForm.fill(AssignmentDetails("scala01", "big assignment"))
        when(mockaddAssignmentForm.addAssignmentForm) thenReturn form
        when(mockAssignmentRepository.addAssignment(Assignment("scala01","big assignment"))) thenReturn Future.successful(false)
        val request = FakeRequest(POST, "/addassignment").withSession("emailid" -> "nilaxch1@gmail.com", "isadmin" -> "false")
          .withFormUrlEncodedBody("title" -> "scala01", "description" -> "big assignment")
        val result = adminPagesController.addAssignmentPost().apply(request)
        redirectLocation(result) mustBe  Some("/home")
    }

    "redirect if not logged in" in {
      val form = new AddAssignmentForm().addAssignmentForm.fill(AssignmentDetails("scala01", "big assignment"))
      when(mockaddAssignmentForm.addAssignmentForm) thenReturn form
      when(mockAssignmentRepository.addAssignment(Assignment("scala01","big assignment"))) thenReturn Future.successful(false)
      val request = FakeRequest(POST, "/addassignment")
        .withFormUrlEncodedBody("title" -> "scala01", "description" -> "big assignment")
      val result = adminPagesController.addAssignmentPost().apply(request)
      redirectLocation(result) mustBe  Some("/login")
    }
  }
}
