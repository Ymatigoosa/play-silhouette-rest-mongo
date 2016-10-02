package models.daos

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import models.Business
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{ JsObject, _ }
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.ReadPreference

import scala.collection.Seq
import scala.concurrent.Future

// BSON-JSON conversions/collection
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection
import scala.collection.mutable.ListBuffer

/**
 * Give access to the user object.
 */
class BusinessDAOImpl @Inject() (val reactiveMongoApi: ReactiveMongoApi) extends BusinessDAO {

  def collection: Future[JSONCollection] =
    reactiveMongoApi.database.map(_.collection[JSONCollection]("business"))

  /**
   * Gets list of Businesses with optional filtration
   *
   * @param nameSubstring substring that must be presented in a name
   * @param country target counry
   * @param city target city
   * @return subset of businesses
   */
  def list(nameSubstring: Option[String], country: Option[String], city: Option[String]): Future[List[Business]] = {

    // create filters accumulator
    val filter = new ListBuffer[(String, JsValue)]

    // if nameSubstring exists - we need add filter
    nameSubstring match {
      case Some(s) => filter.+=(("name", Json.obj("$regex" -> s)))
      case None => {}
    }

    // if country exists - we need add filter
    country match {
      case Some(s) => filter.+=(("country", JsString(s)))
      case None => {}
    }

    // if city exists - we need add filter
    city match {
      case Some(s) => filter.+=(("city", JsString(s)))
      case None => {}
    }

    for {
      c <- collection
      result <- c.find(JsObject(filter))
        .cursor[Business](ReadPreference.primary)
        .collect[List]()
    } yield result
  }

  /**
   * Saves a business. If there is existing businesswith the same id
   * it will be overwritten
   *
   * @param business The business to save.
   * @return The saved business.
   */
  def save(business: Business): Future[Business] = {
    val selector = Json.obj("businessID" -> business.businessID)

    val modifier = business

    for {
      c <- collection
      _ <- c.update(selector, modifier, upsert = true)
    } yield business
  }
}
