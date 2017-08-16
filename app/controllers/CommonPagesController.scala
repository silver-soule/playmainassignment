package controllers

import com.google.inject.Inject
import models._
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
  * Created by Neelaksh on 12/8/17.
  */
class CommonPagesController @Inject()(val messagesApi: MessagesApi, userRepository: UserRepository,
                                      hobbyToUserRepository: HobbyToUserRepository,
                                      hobbyRepository: HobbyRepository, userProfileForm: UserProfileForm,
                                      assignmentRepository: AssignmentRepository
                                     )
  extends Controller with I18nSupport {

  implicit val x = messagesApi

  def home(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    Logger.info(s"user logged in ${request.session.get("emailid")} ")
    request.session.get("emailid").fold(Future.successful(Redirect(routes.HomeController.index()))) {
      emailId =>
        //fix this
        val userData = userRepository.getUserData(emailId)
        val hobbies = hobbyToUserRepository.getHobbies(emailId)
        val allHobbies = hobbyRepository.getAllHobbies()
        userData.flatMap {
          case userdataopt@Some(_) =>
            Logger.info(userdataopt.toString)
            val userdata = userdataopt.get
            hobbies.flatMap {
              hobbies =>
                Logger.info(s"$hobbies")
                allHobbies.map {
                  allHobbies =>
                    Logger.info(s"hobbies for $userdataopt ARE $hobbies out of $allHobbies")
                    Ok(views.html.userprofile(userProfileForm.userProfileForm.fill(UserProfileDetails(
                      userdata.firstName, userdata.middleName, userdata.lastName, userdata.mobileNumber,
                      userdata.gender, userdata.age, hobbies)
                    ), allHobbies))
                }
            }
          case None =>
            Future.successful(Redirect(routes.SignUpController.signUp()))
        }
    }
  }


  def userInfoUpdatePost(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    request.session.get("emailid").fold(Future.successful(Redirect(routes.HomeController.index()))) {
      emailId =>
        Logger.info(request.body.asFormUrlEncoded.toString)
        val allHobbies = hobbyRepository.getAllHobbies()
        userProfileForm.userProfileForm.bindFromRequest.fold(
          formWithErrors => {
            Logger.error(s"invalid input! $formWithErrors")
            allHobbies.map {
              allHobbies =>
                BadRequest(views.html.userprofile(formWithErrors, allHobbies))
            }
          },
          userData => {
            Logger.info(s"valid form $userData")
            val updateUserData = userRepository.updateUserData(UserWithoutPassword(
              userData.firstName, userData.middleName, userData.lastName, userData.mobileNumber, emailId,
              userData.gender, userData.age
            ))
            val updateHobbies = hobbyToUserRepository.update(userData.hobbies.map(hobby => HobbyToEmail(hobby, emailId)), emailId)
            updateUserData.flatMap {
              case true =>
                allHobbies.flatMap {
                  allHobbies =>
                    updateHobbies.map {
                      case true =>
                        Ok(views.html.userprofile(userProfileForm.userProfileForm.fill(UserProfileDetails(
                          userData.firstName, userData.middleName, userData.lastName, userData.mobileNumber,
                          userData.gender, userData.age, userData.hobbies
                        )), allHobbies))
                      case false =>
                        InternalServerError("500")
                    }
                }
              case false =>
                Future.successful(InternalServerError("500"))
            }
          })
    }
  }

  def displayAssignments(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    request.session.get("emailid").fold(Future.successful(Redirect(routes.HomeController.index()))) {
      Logger.info(s"get and display assignments")
      _ =>
        val assignments = assignmentRepository.getAllAssignments()
        assignments.map {
          assignment =>
            Logger.info(s"get and display assignments$assignment")
            Ok(views.html.assignments(assignment))
        }
    }
  }

  def deleteAssignment(assignmentId: Int): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    request.session.get("emailid").fold(Future.successful(Redirect(routes.HomeController.index()))) {
      _ =>
        Logger.info("aackjajkcajinvalid")
        request.session("isadmin") match {
          case "false" =>
            Future.successful(Redirect(routes.CommonPagesController.home()))
          case "true" =>
            Logger.info(s"something about => $assignmentId")
            val deleted = assignmentRepository.deleteAssignment(assignmentId)
            deleted.map {
              case true =>
                Redirect(routes.CommonPagesController.displayAssignments())
              case false =>
                InternalServerError("500")
            }
        }
    }
  }
}
