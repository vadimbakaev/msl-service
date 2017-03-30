package controllers

import javax.inject._

import models.AirIndex
import play.api.Configuration
import play.api.mvc._
import services.retriever.AirQualityRetriever

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * @author vadimbakaev
  */
@Singleton
class RetrieveController @Inject()(
                                    configuration: Configuration,
                                    airQualityRetriever: AirQualityRetriever
                                  ) extends Controller {

  private lazy val key = configuration.getString("retrieveKey").get

  def retrieve: Action[AnyContent] = Action.async { request =>

    if (request.headers.get("Authorization").contains(key)) {

      airQualityRetriever.retrieve(AirIndex.NO2)
      airQualityRetriever.retrieve(AirIndex.SO2)
      airQualityRetriever.retrieve(AirIndex.O3)
      airQualityRetriever.retrieve(AirIndex.CO)
      airQualityRetriever.retrieve(AirIndex.C6H6)
      airQualityRetriever.retrieve(AirIndex.PM2_5)
      airQualityRetriever.retrieve(AirIndex.PM10)

      Future(Ok("Ok"))
    } else {
      Future(Unauthorized("Ko"))
    }

  }

}
