package controllers

import com.google.inject.Inject
import models.AssignmentRepository
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller, Request}
import play.api.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Neelaksh on 13/8/17.
  */
class AssignmentController @Inject()(val messagesApi: MessagesApi, assignmentRepository: AssignmentRepository,
                                     assignmentDeleteForm: AssignmentDeleteForm)
  extends Controller with I18nSupport {

  //authenticate
  def displayAssignments(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    request.session.get("emailid").fold(Future.successful(Redirect(routes.HomeController.index()))) {
          Logger.info("get and display assignments")
      _ =>
        val assignments = assignmentRepository.getAllAssignments()
        assignments.map {
          assignments =>
            Ok(views.html.assignments(assignmentDeleteForm,assignments))
        }
    }
  }

  def deleteAssignment(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val assignments = assignmentRepository.getAllAssignments()
      assignmentDeleteForm.assignmentDeleteForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.error(s"Error in Form => $formWithErrors")
        assignments.map {
          assignments=> {
            BadRequest(views.html.assignments(assignmentDeleteForm, assignments))
          }
        }
      }
      ,
      assignmentData => {
        Logger.info(s"something about => $assignmentData")
        val deleted = assignmentRepository.deleteAssignment(assignmentData.trim())
        deleted.map {
          case true =>
          Redirect(routes.AssignmentController.displayAssignments())
          case false =>
            InternalServerError("500")
        }
      }
      )
  }

}
