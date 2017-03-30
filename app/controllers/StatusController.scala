package controllers

import javax.inject._

import akka.actor.ActorSystem
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author vadimbakaev
  */
@Singleton
class StatusController @Inject()(
                                  actorSystem: ActorSystem
                                )(implicit exec: ExecutionContext) extends Controller {

  def status: Action[AnyContent] = Action.async {
    Future(Ok("Ok"))
  }

}
