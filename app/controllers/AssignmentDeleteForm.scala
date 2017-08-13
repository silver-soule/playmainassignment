package controllers

import play.api.data.Form
import play.api.data.Forms._
/**
  * Created by Neelaksh on 13/8/17.
  */
class AssignmentDeleteForm {
  val assignmentDeleteForm = Form{single{
    "assignmentname" ->text
  }}
}
