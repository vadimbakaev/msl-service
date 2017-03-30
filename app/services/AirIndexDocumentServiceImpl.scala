package services

import javax.inject.Inject

import models.AirIndex._
import models.AirIndexDocument
import play.api.Configuration
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.play.json.collection.JSONCollection
import services.AirIndexDocumentServiceImpl.{Date, Env, StationId}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * @author vadimbakaev
  */
class AirIndexDocumentServiceImpl @Inject()(
                                             config: Configuration,
                                             reactiveMongoApi: ReactiveMongoApi
                                           ) extends AirIndexDocumentService {

  private lazy val env = config.getString(Env).get

  private lazy val nO2CollectionFuture  : Future[JSONCollection] = setUpCollection(NO2)
  private lazy val sO2CollectionFuture  : Future[JSONCollection] = setUpCollection(SO2)
  private lazy val o3CollectionFuture   : Future[JSONCollection] = setUpCollection(O3)
  private lazy val cOCollectionFuture   : Future[JSONCollection] = setUpCollection(CO)
  private lazy val c6H6CollectionFuture : Future[JSONCollection] = setUpCollection(C6H6)
  private lazy val pM2_5CollectionFuture: Future[JSONCollection] = setUpCollection(PM2_5)
  private lazy val pM10CollectionFuture : Future[JSONCollection] = setUpCollection(PM10)

  private def setUpCollection(index: AirIndex): Future[JSONCollection] = reactiveMongoApi.database.map {
    database =>
      database.collection[JSONCollection](env + "_" + index.toString)
  }.map {
    collection =>
      Seq(
        Index(key = Seq(
          (Date, IndexType.Ascending),
          (StationId, IndexType.Ascending)
        ), unique = true)
      ).foreach {
        collection.indexesManager.ensure(_)
      }
      collection
  }

  override def save(doc: AirIndexDocument): Future[WriteResult] = doc match {
    case nO2 if nO2.indexType == NO2.toString       =>
      save(nO2CollectionFuture)(nO2)
    case sO2 if sO2.indexType == SO2.toString       =>
      save(sO2CollectionFuture)(sO2)
    case o3 if o3.indexType == O3.toString          =>
      save(o3CollectionFuture)(o3)
    case cO if cO.indexType == CO.toString          =>
      save(cOCollectionFuture)(cO)
    case c6H6 if c6H6.indexType == C6H6.toString    =>
      save(c6H6CollectionFuture)(c6H6)
    case pM2_5 if pM2_5.indexType == PM2_5.toString =>
      save(pM2_5CollectionFuture)(pM2_5)
    case pM10 if pM10.indexType == PM10.toString    =>
      save(pM10CollectionFuture)(pM10)
    case _                                          =>
      Future.failed(new IllegalArgumentException("AirIndexDocument not matched for save"))
  }

  def save(collectionFuture: Future[JSONCollection])(doc: AirIndexDocument): Future[WriteResult] = {
    collectionFuture.flatMap(_.insert(doc))
  }

}

object AirIndexDocumentServiceImpl {
  val Env       = "env"
  val Date      = "date"
  val StationId = "stationId"
}
