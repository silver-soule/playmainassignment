package controllers

import play.api.mvc._
import com.google.inject.Inject
import models.{User, UserRepository}
import org.mindrot.jbcrypt.BCrypt
import play.api.i18n.{I18nSupport, MessagesApi}

import scala.concurrent.Future
import play.api.Logger

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Neelaksh on 9/8/17.
  */
class SignUpController @Inject()(implicit val messagesApi: MessagesApi, userRepository: UserRepository) extends Controller with I18nSupport {

  val signUpForm = new SignupForm()

  def signUpPost() : Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    signUpForm.signupFormConstraints.bindFromRequest.fold(
      formWithErrors => {
        Logger.error(s" form error => $formWithErrors")
        Future.successful(BadRequest(views.html.signup(formWithErrors)))
      },
      userData => {
        val checkId = userRepository.getEmailId(userData.emailId)
        checkId.flatMap {
          case Some(userData.emailId) =>
            Logger.error(s" error => user already exists")
            Future.successful(Redirect(routes.SignUpController.signUp())
              .flashing("error" -> "emailId is already in use!"))
          case None =>
            val addedUser = userRepository.store(User.apply(userData.name, userData.middleName, userData.lastName, userData.mobileNumber, userData.emailId,
              BCrypt.hashpw(userData.password,BCrypt.gensalt()), userData.gender, userData.age))
            addedUser.map {
              case true => Redirect(routes.HomeController.success()).withSession("emailId"->userData.emailId,"name"->userData.name)
              case false => InternalServerError("500")
            }
        }
      })
  }

  def signUp() : Action[AnyContent] = Action { implicit request =>
    Ok(views.html.signup(signUpForm.signupFormConstraints))
  }

}
