package services

import javax.inject.Inject

import com.mongodb.ConnectionString
import models.AirIndex._
import models.AirIndexDocument
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.connection.ClusterSettings
import org.mongodb.scala.model.{IndexOptions, Indexes}
import org.mongodb.scala.{Completed, MongoClient, MongoClientSettings, MongoCollection, MongoCredential, MongoDatabase}
import play.api.Configuration
import services.AirIndexDocumentServiceImpl._

import scala.collection.JavaConversions._
import scala.concurrent.Future

/**
  * @author vadimbakaev
  */
class AirIndexDocumentServiceImpl @Inject()(
                                             config: Configuration
                                           ) extends AirIndexDocumentService {

  private lazy val env          = config.getString(Env).get
  private lazy val mongodbURI   = config.getString(Uri).get
  private lazy val databaseName = config.getString(Db).get
  private lazy val userName     = config.getString(UserName).get
  private lazy val password     = config.getString(Password).get

  private lazy val clusterSettings = ClusterSettings.builder()
    .applyConnectionString(new ConnectionString(mongodbURI))
    .build()

  private lazy val clientSettings: MongoClientSettings = MongoClientSettings.builder()
    .credentialList(List(MongoCredential.createCredential(userName, databaseName, password.toCharArray)))
    .clusterSettings(clusterSettings)
    .build()

  private lazy val database: MongoDatabase = MongoClient(clientSettings)
    .getDatabase(databaseName)
    .withCodecRegistry(fromRegistries(fromProviders(classOf[AirIndexDocument]), DEFAULT_CODEC_REGISTRY))

  private lazy val nO2Collection   = setUpCollection(database, NO2)
  private lazy val sO2Collection   = setUpCollection(database, SO2)
  private lazy val o3Collection    = setUpCollection(database, O3)
  private lazy val cOCollection    = setUpCollection(database, CO)
  private lazy val c6H6Collection  = setUpCollection(database, C6H6)
  private lazy val pM2_5Collection = setUpCollection(database, PM2_5)
  private lazy val pM10Collection  = setUpCollection(database, PM10)

  private def setUpCollection(database: MongoDatabase, index: AirIndex): MongoCollection[AirIndexDocument] = {
    val collection = database.getCollection[AirIndexDocument](env + "_" + index.toString)
    collection.createIndex(Indexes.ascending(Date, StationId), IndexOptions().background(true).unique(true))
    collection
  }

  override def save(doc: AirIndexDocument): Future[Completed] = doc match {
    case nO2 if nO2.indexType == NO2.toString       =>
      save(nO2Collection)(nO2)
    case sO2 if sO2.indexType == SO2.toString       =>
      save(sO2Collection)(sO2)
    case o3 if o3.indexType == O3.toString          =>
      save(o3Collection)(o3)
    case cO if cO.indexType == CO.toString          =>
      save(cOCollection)(cO)
    case c6H6 if c6H6.indexType == C6H6.toString    =>
      save(c6H6Collection)(c6H6)
    case pM2_5 if pM2_5.indexType == PM2_5.toString =>
      save(pM2_5Collection)(pM2_5)
    case pM10 if pM10.indexType == PM10.toString    =>
      save(pM10Collection)(pM10)
    case _                                          =>
      Future.failed(new IllegalArgumentException("AirIndexDocument not matched for save"))
  }

  def save(collection: MongoCollection[AirIndexDocument])(doc: AirIndexDocument): Future[Completed] = {
    collection.insertOne(doc).toFuture()
  }

}

object AirIndexDocumentServiceImpl {
  val Env       = "env"
  val Date      = "date"
  val StationId = "stationId"
  val Uri       = "mongo.uri"
  val Db        = "mongo.database"
  val UserName  = "mongo.username"
  val Password  = "mongo.password"
}
