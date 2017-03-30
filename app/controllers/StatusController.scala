package controllers

import javax.inject._

import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author vadimbakaev
  */
@Singleton
class StatusController @Inject()()(implicit exec: ExecutionContext) extends Controller {

  def status: Action[AnyContent] = Action.async {
    Future(Ok("Ok"))
  }

}
