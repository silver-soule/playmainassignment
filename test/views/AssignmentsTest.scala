package views

/**
  * Created by Neelaksh on 15/8/17.
  */

import models.Assignment
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.mvc.Session
import org.mockito.Mockito.when

class AssignmentsTest extends PlaySpec with MockitoSugar {

  val mockMessage = mock[Messages]
  val mockSession = mock[Session]
  "assignment" should {
    "show assignments with delete button to admin " in {

      when(mockSession.get("isadmin")) thenReturn Some("true")
      val html = views.html.assignments.render(List(Assignment("potato","abc")), mockMessage,mockSession)
      assert(html.toString.contains("DELETE"))
    }

    "show normal assignment delete page for normal user" in {
      when(mockSession.get("isadmin")) thenReturn Some("false")
      val html = views.html.assignments.render(List(Assignment("potato","abc")), mockMessage,mockSession)
      assert(!html.toString.contains("DELETE"))
      assert(html.toString().contains("Assignment name"))
    }
  }

}