package controllers

import play.api.mvc._
import com.google.inject.Inject
import models.{User, UserRepository}
import play.api.i18n.{I18nSupport, MessagesApi}

import scala.concurrent.Future
import play.api.Logger

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Neelaksh on 9/8/17.
  */
class SignUpController @Inject()(val messagesApi: MessagesApi, userRepository: UserRepository,
                                 signUpForm: SignUpForm) extends Controller with I18nSupport {

  implicit val x = messagesApi

  def signUpPost(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    signUpForm.signUpForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.error(s" form error => $formWithErrors")
        Future.successful(BadRequest(views.html.signup(formWithErrors)))
      },
      userData => {
        Logger.info(s"data is $userData")
        val checkId = userRepository.getEmailId(userData.emailId)
        Logger.info(checkId.toString)
        checkId.flatMap {
          case Some(userData.emailId) =>
            Logger.error(s" error => user already exists")
            Future.successful(Redirect(routes.SignUpController.signUp())
              .flashing("error" -> "emailId is already in use!"))
          case None =>
            val addedUser = userRepository.store(User(userData.firstName, userData.middleName,
              userData.lastName, userData.mobileNumber, userData.emailId,
              userData.password, userData.gender, userData.age))
            addedUser.map {
              case true => Redirect(routes.UserProfileController.home())
                .withSession("emailid" -> userData.emailId, "name" -> userData.firstName,"isadmin"->false.toString)
              case false => InternalServerError("500")
            }
        }
      })
  }

  def signUp(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.signup(signUpForm.signUpForm))
  }

}
