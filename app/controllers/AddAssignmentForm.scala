package controllers
import play.api.data.Form
import play.api.data.Forms._

/**
  * Created by Neelaksh on 15/8/17.
  */
class AddAssignmentForm {
  val addAssignmentForm = Form(mapping(
    "title"->nonEmptyText,
    "description"->nonEmptyText
    )(AssignmentDetails.apply)(AssignmentDetails.unapply))
}

case class AssignmentDetails(title:String,description:String)