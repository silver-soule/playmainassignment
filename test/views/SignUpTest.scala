package views

import controllers.SignUpForm
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.mvc.Flash

/**
  * Created by Neelaksh on 15/8/17.
  */
class SignUpTest extends PlaySpec with MockitoSugar {

  "signup should" should {
    "render signup form " in {
      val mockMessage = mock[Messages]
      val mockFlash = mock[Flash]
      val signUpForm = new SignUpForm
      when(mockFlash.get("error")) thenReturn None
      val html = views.html.signup.render(signUpForm.signUpForm, mockMessage, mockFlash)
      assert(html.toString.contains("signup"))
    }
  }
}
