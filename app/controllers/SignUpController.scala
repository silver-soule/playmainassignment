package controllers

import play.api.mvc._
import com.google.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}

/**
  * Created by Neelaksh on 9/8/17.
  */
class SignUpController @Inject()(implicit val messagesApi: MessagesApi) extends Controller with I18nSupport {

  val signUpForm = new SignupForm()

  def signUpPost() = Action { implicit request: Request[AnyContent] =>
    signUpForm.signupFormConstraints.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.signup(formWithErrors))
      },
      userData => {
        println(userData)
        Redirect(routes.HomeController.success())
      })
  }

  def signUp() = Action {
    Ok(views.html.signup(signUpForm.signupFormConstraints))
  }

}
