package controllers


import com.google.inject.Inject
import models.{Assignment, AssignmentRepository, MinUser, UserRepository}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller, Request}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Neelaksh on 15/8/17.
  */
class AdminPagesController @Inject()(val messagesApi: MessagesApi, userRepository: UserRepository
                                     , addAssignmentForm: AddAssignmentForm, assignmentRepository: AssignmentRepository) extends Controller with I18nSupport {


  implicit val messages = messagesApi

  def displayUsers(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    request.session.get("emailid").fold(Future.successful(Redirect(routes.HomeController.index()))) {
      emailId =>
        request.session("isadmin") match {
          case "false" => Future.successful(Redirect(routes.CommonPagesController.home()))
          case "true" =>
            val allUsers = userRepository.getAllNormalUsers()
            allUsers.map {
              allUsers =>
                Logger.info(s"$allUsers")
                Ok(views.html.userlist(allUsers.map(user => MinUser(user.firstName, user.emailId, user.isEnabled))))
            }
        }
    }
  }

  def flipPermissions(emailId: String, isEnabled: Boolean): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    request.session.get("emailid").fold(Future.successful(Redirect(routes.HomeController.index()))) {
      _ =>
        request.session("isadmin") match {
          case "false" => Future.successful(Redirect(routes.CommonPagesController.home()))
          case "true" =>
            Logger.info(s"--------${emailId.trim}-----------${!isEnabled}-------")
            val flipUser = userRepository.setPermissions(emailId.trim(), !isEnabled)
            flipUser.map {
              case true =>
                Logger.info(s"flipping permissions")
                Redirect(routes.AdminPagesController.displayUsers())
              case false =>
                Logger.info("WRONG")
                InternalServerError("500")
            }
        }
    }
  }

  def addAssignment(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    request.session.get("emailid").fold(Redirect(routes.HomeController.index())) {
      _ =>
        Logger.info("checking if admin")
        request.session("isadmin") match {
          case "false" => Redirect(routes.CommonPagesController.home())
          case "true" => Ok(views.html.addassignment(addAssignmentForm.addAssignmentForm))
        }
    }
  }

  def addAssignmentPost(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    request.session.get("emailid").fold(Future.successful(Redirect(routes.HomeController.index()))) {
      _ =>
        request.session("isadmin") match {
          case "false" => Future.successful(Redirect(routes.CommonPagesController.home()))
          case "true" =>
            addAssignmentForm.addAssignmentForm.bindFromRequest.fold(
              formWithErrors => {
                Logger.error(s"invalid input! $formWithErrors")
                Future.successful(BadRequest(views.html.addassignment(formWithErrors)))
              },
              assignmentData => {
                Logger.info(s"added assignment")
                val addAssignment = assignmentRepository.addAssignment(Assignment(assignmentData.title, assignmentData.description))
                addAssignment.map {
                  case false => InternalServerError("500")
                  case true => Redirect(routes.AdminPagesController.addAssignment())
                }
              })
        }
    }
  }
}
