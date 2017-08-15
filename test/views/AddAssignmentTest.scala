package views

import controllers.AddAssignmentForm
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages

/**
  * Created by Neelaksh on 15/8/17.
  */
class AddAssignmentTest extends PlaySpec with MockitoSugar {

  "addassignment" should {
    "show assignment form " in {
      val mockMessage = mock[Messages]
      val addAssignmentForm = new AddAssignmentForm
      val html = views.html.addassignment.render(addAssignmentForm.addAssignmentForm, mockMessage)
      assert(html.toString.contains("Add Assignment"))
    }
  }

}