package services

import com.google.inject.ImplementedBy
import models.AirIndexDocument
import org.mongodb.scala.Completed

import scala.concurrent.Future

/**
  * @author vadimbakaev
  */
@ImplementedBy(classOf[AirIndexDocumentServiceImpl])
trait AirIndexDocumentService {

  def save(doc: AirIndexDocument): Future[Completed]

  def getDocumentsAfterDate(date:String): Future[List[AirIndexDocument]]

}
