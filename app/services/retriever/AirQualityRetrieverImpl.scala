package services.retriever

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

import models.AirIndex.AirIndex
import models.AirIndexDocument
import org.apache.http.HttpStatus
import org.apache.http.protocol.HTTP
import play.api.libs.json.{JsArray, Json}
import play.api.libs.ws.WSClient
import play.api.{Configuration, Logger}
import services.AirIndexDocumentService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * @author vadimbakaev
  */
class AirQualityRetrieverImpl @Inject()(
                                         indexDocumentService: AirIndexDocumentService,
                                         config: Configuration,
                                         ws: WSClient
                                       ) extends AirQualityRetriever {

  private lazy val airQualityUrl = config.getString(AirQualityRetrieverImpl.AirQualityUrl).get

  override def retrieve(airIndex: AirIndex): Future[Option[List[AirIndexDocument]]] = {

    val day = LocalDate.now().minusDays(1)

    Logger.info(s"Retrieve data for ${day.toString} - ${airIndex.toString}")

    val data = Json.obj(
      "pIdSens1" -> airIndex.id,
      "pIdSens2" -> 0,
      "pData" -> day.format(DateTimeFormatter.ISO_LOCAL_DATE)
    )

    ws.url(airQualityUrl)
      .withHeaders((HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"))
      .post(data)
      .map {
        case success if success.status == HttpStatus.SC_OK =>
          Some(success.json match {
            case array: JsArray =>
              array.value
                .map(AirIndexDocument.fromJson(day))
                .toList
                .filter(_.isDefined)
                .map(_.get)
                .map(indexDocument => {
                  indexDocumentService.save(indexDocument)

                  indexDocument
                })
            case _              =>
              Logger.error(s"Wrong body response: ${success.body}")
              Nil
          })
        case fail                                          =>
          Logger.error(s"Error in retrieve data with response code: ${fail.status} and body: ${fail.body}")
          None
      }
  }

}

object AirQualityRetrieverImpl {
  val AirQualityUrl = "airQualityUrl"
}
