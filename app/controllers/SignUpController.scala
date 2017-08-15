package controllers

import com.google.inject.Inject
import models.{User, UserRepository}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import util.Hasher

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Neelaksh on 9/8/17.
  */
class SignUpController @Inject()(val messagesApi: MessagesApi, userRepository: UserRepository,
                                 signUpForm: SignUpForm, hasher: Hasher) extends Controller with I18nSupport {

  implicit val x = messagesApi

  def signUpPost(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    signUpForm.signUpForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.error(s" form error => $formWithErrors")
        Future.successful(BadRequest(views.html.signup(formWithErrors)))
      },
      userData => {
        Logger.info(s"data is $userData")
        val checkId = userRepository.checkIfExists(userData.emailId)
        Logger.info(checkId.toString)
        checkId.flatMap {
          case true =>
            Logger.error(s" error => user already exists")
            Future.successful(Redirect(routes.SignUpController.signUp())
              .flashing("error" -> "emailId is already in use!"))
          case false =>
            val addedUser = userRepository.store(User(userData.firstName, userData.middleName,
              userData.lastName, userData.mobileNumber, userData.emailId,
              hasher.hashpw(userData.password), userData.gender, userData.age))
            addedUser.map {
              case true => Redirect(routes.CommonPagesController.home()).flashing("success" -> "New account created")
                .withSession("emailid" -> userData.emailId, "name" -> userData.firstName, "isadmin" -> false.toString)
              case false => InternalServerError("500")
            }
        }
      })
  }

  def signUp(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.signup(signUpForm.signUpForm))
  }

}
