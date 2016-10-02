package models.services

import javax.inject.Inject

import models.Business
import models.daos.{ BusinessDAO, UserDAO }

import scala.concurrent.Future

/**
 * Give access to the business object.
 */
class BusinessServiceImpl @Inject() (businessDAO: BusinessDAO) extends BusinessService {

  /**
   * Gets list of Businesses with optional filtration
   *
   * @param nameSubstring substring that must be presented in a name
   * @param country target counry
   * @param city target city
   * @return subset of businesses
   */
  def list(nameSubstring: Option[String], country: Option[String], city: Option[String]): Future[List[Business]] =
    businessDAO.list(nameSubstring, country, city)

  /**
   * Saves a business. If there is existing businesswith the same id
   * it will be overwritten
   *
   * @param business The business to save.
   * @return The saved business.
   */
  def save(business: Business): Future[Business] =
    businessDAO.save(business)
}
