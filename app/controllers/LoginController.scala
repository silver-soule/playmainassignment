package controllers

import com.google.inject.Inject
import models.{User, UserRepository}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller, Request}
import play.api.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Neelaksh on 9/8/17.
  */
class LoginController @Inject()(val messagesApi: MessagesApi, userRepository: UserRepository, loginForm: LoginForm) extends Controller with I18nSupport {
  /*
    val loginForm = new LoginForm()
  */

  implicit val x = messagesApi

  def loginPost(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    loginForm.loginForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.error(s"invalid input! $formWithErrors")
        Future.successful(BadRequest(views.html.login(formWithErrors)))
      },
      userData => {
        val userInfo = userRepository.getUserData(userData.emailId, userData.password)
        userInfo.map {
          case Some(user: User) =>
            if (user.isEnabled) {
              Logger.info(s"Logged in ${userData.emailId}")
              Redirect(routes.UserProfileController.home()).withSession("name" -> user.firstName,
                "emailid" -> user.emailId, "isadmin" -> user.isAdmin.toString)
            }
            else {
              Logger.error(s" account was disabled for ${user.emailId}")
              Redirect(routes.LoginController.login())
                .flashing("error" -> "Your account has been disabled")
            }
          case None =>
            Logger.error(s"emailid and password don't match")
            Redirect(routes.LoginController.login())
              .flashing("error" -> "emailId and password do not match")
        }
      })
  }

  def login(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.login(loginForm.loginForm))
  }
}
