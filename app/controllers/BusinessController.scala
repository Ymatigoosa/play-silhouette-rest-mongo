package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{ LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import models.Business
import models.services.BusinessService
import play.api.i18n.{ I18nSupport, MessagesApi }
import play.api.libs.json.Json
import play.api.mvc.{ Action, Controller }
import utils.auth.DefaultEnv
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

/**
 * The basic application controller.
 *
 * @param messagesApi The Play messages API.
 * @param silhouette The Silhouette stack.
 * @param businessService service to deal with businessed entities.
 */
class BusinessController @Inject() (
  val messagesApi: MessagesApi,
  silhouette: Silhouette[DefaultEnv],
  businessService: BusinessService)
  extends Controller with I18nSupport {

  /**
   * getting subset of businesses
   *
   * @param nameSubstring substring that must be presented in a name
   * @param country target counry
   * @param city target city
   * @return subset of businesses
   */
  def list(nameSubstring: Option[String], country: Option[String], city: Option[String]) = Action.async { implicit request =>
    businessService.list(nameSubstring, country, city)
      .map(list => Ok(Json.toJson(list)))
  }

  /**
   * Saves a business. If there is existing businesswith the same id
   * it will be overwritten
   *
   * @return The saved business.
   */
  def save = silhouette.SecuredAction.async(parse.json) { implicit request =>
    request.body.validate[Business].map { business =>
      businessService.save(business)
        .map(_ => Ok)
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }
}
