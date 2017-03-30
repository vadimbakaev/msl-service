package services

import com.google.inject.ImplementedBy
import models.AirIndexDocument
import reactivemongo.api.commands.WriteResult

import scala.concurrent.Future

/**
  * @author vadimbakaev
  */
@ImplementedBy(classOf[AirIndexDocumentServiceImpl])
trait AirIndexDocumentService {

  def save(doc: AirIndexDocument): Future[WriteResult]

}
