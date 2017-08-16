package views

import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.mvc.{Flash, Session}

/**
  * Created by Neelaksh on 15/8/17.
  */
class UserListTest extends PlaySpec with MockitoSugar {

  "UserList" should {
    "render userlist for  " in {
      val mockMessage = mock[Messages]
      val mockSession = mock[Session]
      val html = views.html.userlist.render(Nil, mockMessage, mockSession)
      assert(html.toString.contains("Permissions"))
    }
  }

}
