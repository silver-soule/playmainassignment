package controllers

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints.{max, min}

/**
  * Created by Neelaksh on 12/8/17.
  */
class UserProfileForm {

  val minAge = 18
  val maxAge = 75
  val userProfileForm = Form(mapping(
    "firstName" -> nonEmptyText,
    "middleName" -> optional(text),
    "lastName" -> nonEmptyText,
    "mobileNumber" -> longNumber,
    "gender" -> nonEmptyText,
    "age" -> number.verifying(min(minAge), max(maxAge)),
    "hobbies" -> list(number)
  )(UserProfileDetails.apply)(UserProfileDetails.unapply))
}

case class UserProfileDetails(firstName: String, middleName: Option[String],
                              lastName: String, mobileNumber: Long
                              , gender: String, age: Int, hobbies: List[Int])

