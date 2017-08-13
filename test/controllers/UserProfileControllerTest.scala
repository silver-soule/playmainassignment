package controllers

import akka.stream.Materializer
import akka.util.Timeout
import models._
import org.mockito.Mockito.when
import org.scalatest.FunSuite
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, redirectLocation}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import org.mockito.Mockito._

import scala.concurrent.Future

/**
  * Created by Neelaksh on 14/8/17.
  */
class UserProfileControllerTest extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite {
  implicit val mockmessageapi = mock[MessagesApi]
  val mockUserProfileForm = mock[UserProfileForm]
  val mockUserRepository = mock[UserRepository]
  val mockHobbyRepository = mock[HobbyRepository]
  val mockHobbyToUserRepository = mock[HobbyToUserRepository]

  val controller = new UserProfileController(mockmessageapi, mockUserRepository, mockHobbyToUserRepository, mockHobbyRepository, mockUserProfileForm)
  implicit lazy val materializer: Materializer = app.materializer

  "UserProfileController" should {

    "updateuserprofile home successfully" in {
      implicit val timeout = Timeout(10 seconds)
      val actualUser = User("Neelaksh", None, "Chauhan", 995407, "nilaxch1@gmail.com", "Potato123", "male", 21)
      val user = UserProfileDetails("Neelaksh", None, "Chauhan", 995407, "male", 21, List(1, 2))
      val form = new UserProfileForm().userProfileForm.fill(user)
      when(mockUserProfileForm.userProfileForm).thenReturn(form)
      when(mockHobbyRepository.getAllHobbies()).thenReturn(Future(List(Hobby(1, "dance"), Hobby(2, "hockey"))))
      when(mockHobbyToUserRepository.getHobbies(actualUser.emailId)).thenReturn(Future(List(1)))
      val result = controller.userInfoUpdatePost().apply(FakeRequest(GET, "/signup").withFormUrlEncodedBody(
        "firstName" -> "Neelaksh", "middleName" -> "", "lastName" -> "Chauhan", "mobileNumber" -> "995407",
        "gender" -> "male", "age" -> "21", "hobbies" -> "1,2"
      ))
      redirectLocation(result) mustBe Some("/home")

    }
  }
}
