package controllers

import com.google.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller, Request}

/**
  * Created by Neelaksh on 9/8/17.
  */
class LoginController @Inject()(implicit val messagesApi: MessagesApi) extends Controller with I18nSupport {
  val loginForm = new LoginForm()

  def loginPost() = Action { implicit request: Request[AnyContent] =>
    loginForm.loginFormConstraints.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.login(formWithErrors))
      },
      userData => {
        println(userData)
        Redirect(routes.HomeController.success())
      })
  }

  def login() = Action {
    Ok(views.html.login(loginForm.loginFormConstraints))
  }
}
