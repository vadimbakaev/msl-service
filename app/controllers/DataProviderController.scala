package controllers

import java.time.LocalDate
import javax.inject.{Inject, Singleton}

import play.api.libs.json.Json
import play.api.mvc._
import services.AirIndexDocumentService

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * @author vadimbakaev
  */
@Singleton
class DataProviderController @Inject()(
                                        airIndexDocumentService: AirIndexDocumentService
                                      )
  extends Controller {


  def getLastDaysAvg: Action[AnyContent] = Action.async { request =>

    val day = LocalDate.now().minusDays(2)

    airIndexDocumentService.getDocumentsAfterDate(day.toString).map(list => Ok(Json.toJson(list)))

  }

}
