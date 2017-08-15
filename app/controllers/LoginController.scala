package controllers

import com.google.inject.Inject
import models.{User, UserRepository}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller, Request}
import util.Hasher

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Neelaksh on 9/8/17.
  */
class LoginController @Inject()(userRepository: UserRepository, loginForm: LoginForm,
                                hasher: Hasher, updatePasswordForm: UpdatePasswordForm,
                                val messagesApi: MessagesApi) extends Controller with I18nSupport {


  implicit val messages = messagesApi

  def loginPost(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    loginForm.loginForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.error(s"invalid input! $formWithErrors")
        Future.successful(BadRequest(views.html.login(formWithErrors)))
      },
      userData => {
        val userInfo = userRepository.getUserData(userData.emailId)
        userInfo.map {
          case Some(user: User) =>
            if (hasher.checkpw(userData.password, user.password)) {
              if (user.isEnabled) {
                Logger.info(s"Logged in ${userData.emailId}")
                Redirect(routes.CommonPagesController.home()).flashing("success" -> "logged in").withSession("name" -> user.firstName,
                  "emailid" -> user.emailId, "isadmin" -> user.isAdmin.toString)
              }
              else {
                Logger.error(s" account was disabled for ${user.emailId}")
                Redirect(routes.LoginController.login())
                  .flashing("error" -> "Your account has been disabled")
              }
            }
            else {
              Logger.error("password and emaild don't match")
              Redirect(routes.LoginController.login())
                .flashing("error" -> "emailId and password do not match")
            }
          case None =>
            Logger.error(s"User does not exist")
            Redirect(routes.SignUpController.signUp())
              .flashing("error" -> "user does not exist")
        }
      })
  }

  def login(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.login(loginForm.loginForm))
  }

  def updatePassword(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.updatepassword(updatePasswordForm.updatePasswordForm))
  }

  def updatePasswordPost(): Action[AnyContent] = Action.async { implicit request =>
    updatePasswordForm.updatePasswordForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.info("BAD REQUEST FOR UPDATE PASSWORD")
        Logger.error(s"invalid input! $formWithErrors")
        Future.successful(BadRequest(views.html.updatepassword(formWithErrors)))
      }, {
        passwordUpdateInfo =>
          Logger.info(s"${passwordUpdateInfo}")
          val updated = userRepository.updatePassword(passwordUpdateInfo.emailId, hasher.hashpw(passwordUpdateInfo.password))
          updated.map {
            case false =>
              Redirect(routes.LoginController.login())
                .flashing("error" -> "user does not exist")
            case true =>
              Redirect(routes.LoginController.login())
                .flashing("success" -> "successfully changed password")
          }

      })
  }
}
