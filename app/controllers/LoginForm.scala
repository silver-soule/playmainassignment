package controllers

import play.api.data.Form
import play.api.data.Forms._

/**
  * Created by Neelaksh on 9/8/17.
  */
class LoginForm {

  val loginFormConstraints = Form(mapping(
    "emailId" -> email,
    "password" -> nonEmptyText
  )(LoginDetails.apply)(LoginDetails.unapply))

}

case class LoginDetails(emailId: String, password: String)
