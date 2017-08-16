package controllers

import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
/**
  * Created by Neelaksh on 15/8/17.
  */
class UpdatePasswordForm {

  val updatePasswordForm = Form(mapping(
    "emailId" -> email,
    "password" -> nonEmptyText.verifying(validPassword),
    "verifyPassword" -> nonEmptyText.verifying(validPassword)
  )(UpdatePasswordDetails.apply)(UpdatePasswordDetails.unapply)
    .verifying("Passwords do not match", data =>{
      data.password == data.verifyPassword
    }))

  def validPassword: Constraint[String] = {
    val validPassword = """^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$""".r
    Constraint({
      password =>
        val errors =
          password match {
            case validPassword() =>
              Logger.info("valid password")
              Nil
            case _ =>
              Logger.error("invalid password")
              Seq(ValidationError("Password must contain at least 1 number  and 1 capital case letter"))
          }
        if (errors.isEmpty) Valid else Invalid(errors)
    })
  }
}

case class UpdatePasswordDetails(emailId: String, password: String,verifyPassword:String)
