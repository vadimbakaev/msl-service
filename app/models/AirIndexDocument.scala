package models

import java.time.LocalDate

import akka.util.ByteString
import play.api.Logger
import play.api.libs.json._
import redis.ByteStringFormatter

import scala.util.{Failure, Try}

/**
  * @author vadimbakaev
  */
case class AirIndexDocument(
                          date: String,
                          indexType: String,
                          value: Double,
                          unity: String,
                          valueMax: Double,
                          latitude: Double,
                          longitude: Double,
                          sensorId: Long,
                          stationId: Long,
                          state: Long,
                          station: String,
                          province: String,
                          address: String
                        )

object AirIndexDocument {

  implicit val airIndexDocumentFormat: OFormat[AirIndexDocument] = Json.format[AirIndexDocument]
  implicit val airIndexDocumentWrites   : OWrites[AirIndexDocument] = Json.writes[AirIndexDocument]

  implicit val byteStringFormat = new ByteStringFormatter[AirIndexDocument] {
    def serialize(data: AirIndexDocument): ByteString =
      ByteString(Json.toJson(data).toString)

    def deserialize(bs: ByteString): AirIndexDocument =
      Json.fromJson[AirIndexDocument](Json.parse(bs.utf8String)).get
  }

  def fromJson(date: LocalDate)(jsValue: JsValue): Option[AirIndexDocument] = {

    Try {
      val indexType = (jsValue \ IndexType).asOpt[String].get match {
        case "NO2"     => AirIndex.NO2.toString
        case "SO2"     => AirIndex.SO2.toString
        case "O3"      => AirIndex.O3.toString
        case "CO"      => AirIndex.CO.toString
        case "Benzene" => AirIndex.C6H6.toString
        case "PM2_5"   => AirIndex.PM2_5.toString
        case "PM10"    => AirIndex.PM10.toString
        case _         => "Undefined"
      }
      val latitude = (jsValue \ Latitude).asOpt[Double].getOrElse(Double.MinValue)
      val longitude = (jsValue \ Longitude).asOpt[Double].getOrElse(Double.MinValue)
      val value = (jsValue \ Value).asOpt[Double].getOrElse(Double.MinValue)
      val unity = (jsValue \ Unity).as[String]
      val valueMax = (jsValue \ ValueMax).asOpt[Double].getOrElse(Double.MinValue)
      val sensorId = (jsValue \ SensorId).asOpt[Long].getOrElse(Long.MinValue)
      val stationId = (jsValue \ StationId).asOpt[Long].getOrElse(Long.MinValue)
      val state = (jsValue \ State).asOpt[Long].getOrElse(Long.MinValue)
      val station = (jsValue \ Station).as[String]
      val province = (jsValue \ Province).as[String]
      val address = (jsValue \ Address).as[String]

      AirIndexDocument(
        date = date.toString,
        indexType = indexType,
        latitude = latitude,
        longitude = longitude,
        value = value,
        unity = unity,
        valueMax = valueMax,
        sensorId = sensorId,
        stationId = stationId,
        state = state,
        station = station,
        province = province,
        address = address
      )
    }.recoverWith {
      case e: Throwable =>
        Logger.error("Error parsing AirIndexDocument", e)
        Failure(e)
    }.toOption
  }

  val Date      = "Data"
  val SensorId  = "IdSensore"
  val StationId = "IdStazione"
  val Address   = "Indirizzo"
  val Latitude  = "Latitudine"
  val Longitude = "Longitudine"
  val IndexType = "NomeTabella"
  val Province  = "Provincia"
  val State     = "Stato"
  val Station   = "Stazione"
  val Unity     = "UnitaMisura"
  val Value     = "Valore"
  val ValueMax  = "ValoreSoglia"

}