package services.retriever

import com.google.inject.ImplementedBy
import models.AirIndex.AirIndex
import models.AirIndexDocument

import scala.concurrent.Future

/**
  * @author vadimbakaev
  */
@ImplementedBy(classOf[AirQualityRetrieverImpl])
trait AirQualityRetriever {

  def retrieve(index: AirIndex): Future[Option[List[AirIndexDocument]]]

}
