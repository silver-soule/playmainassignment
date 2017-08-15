package views

import controllers.LoginForm
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.mvc.Flash
import org.mockito.Mockito.when

/**
  * Created by Neelaksh on 15/8/17.
  */


class LoginTest extends PlaySpec with MockitoSugar{
  "Login" should {
    "render login page" in{
      val loginForm = new LoginForm
      val mockFlash = mock[Flash]
      val mockMessage = mock[Messages]
      when(mockFlash.get("success")) thenReturn None
      when(mockFlash.get("error")) thenReturn None
      val html = views.html.login.render(loginForm.loginForm, mockMessage,mockFlash)
      assert(html.toString.contains("login"))
    }
  }
}