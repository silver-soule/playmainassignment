package views

/**
  * Created by Neelaksh on 15/8/17.
  */


import controllers.UserProfileForm
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.mvc.{Flash, Session}

class UserProfileTest extends PlaySpec with MockitoSugar {

  private val mockMessage = mock[Messages]
  private val mockSession = mock[Session]
  private val mockFlash = mock[Flash]
  val userProfileForm = new UserProfileForm
  "UserProfile" should {
    "render userprofile for" in {
      when(mockFlash.get("error")) thenReturn None
      when(mockFlash.get("success")) thenReturn None
      when(mockSession.get("isadmin")) thenReturn Some("false")
      val html = views.html.userprofile.render(userProfileForm.userProfileForm,Nil, mockMessage, mockFlash, mockSession)
      assert(html.toString.contains("Update Profile"))
    }
  }

}
