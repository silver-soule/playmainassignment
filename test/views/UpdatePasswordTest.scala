package views

/**
  * Created by Neelaksh on 15/8/17.
  */

import controllers.UpdatePasswordForm
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.mvc.Flash

class UpdatePasswordTest extends PlaySpec with MockitoSugar {

  "updatepassword" should {
    "show update password page " in {
      val mockMessage = mock[Messages]
      val mockFlash = mock[Flash]
      val updatepasswordForm = new UpdatePasswordForm
      when(mockFlash.get("error")) thenReturn None
      val html = views.html.updatepassword.render(updatepasswordForm.updatePasswordForm, mockMessage,mockFlash)
      assert(html.toString.contains("Change Password"))
    }
  }
}
