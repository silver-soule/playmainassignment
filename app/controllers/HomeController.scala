package controllers

import play.api.mvc._

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
class HomeController extends Controller {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Redirect(routes.LoginController.login())
  }

  def logout(): Action[AnyContent] = Action { implicit request =>
    Redirect(routes.LoginController.login()).withNewSession
  }

  def redirectToLoginWithFlash : Action[AnyContent] = Action{ implicit request =>
    Redirect(routes.LoginController.login()).flashing("error"->"Please login to access this page")
  }
}
