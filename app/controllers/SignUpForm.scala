package controllers

import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints.{max, min}
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}


/**
  * Created by Neelaksh on 9/8/17.
  */
class SignUpForm {

  val minAge = 18
  val maxAge = 75

  val signUpForm = Form(mapping(
    "firstName" -> nonEmptyText,
    "middleName" -> optional(text),
    "lastName" -> nonEmptyText,
    "mobileNumber" -> longNumber,
    "emailId" -> email,
    "password" -> nonEmptyText.verifying(validPassword),
    "verifyPassword" -> nonEmptyText.verifying(validPassword),
    "gender" -> nonEmptyText,
    "age" -> number.verifying(min(minAge), max(maxAge))
  )(SignUpDetails.apply)(SignUpDetails.unapply)
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

case class SignUpDetails(firstName: String, middleName: Option[String], lastName: String, mobileNumber: Long
                         , emailId: String, password: String, verifyPassword: String, gender: String, age: Int)
