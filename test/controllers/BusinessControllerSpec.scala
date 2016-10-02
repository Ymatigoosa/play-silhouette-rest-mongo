import org.specs2.mutable._
import org.specs2.matcher._
import org.specs2.runner._
import play.api.test._
import play.api.test.Helpers._
import play.api.inject.guice.GuiceApplicationBuilder
import api._
import api.Api._
import api.ApiError._
import org.joda.time.DateTime
import play.api.mvc.{ Headers, AnyContentAsEmpty }
import play.api.http.Writeable
import play.api.mvc.Result
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try
import play.api.libs.json.{ Json, JsNull }

class BusinessControllerSpec extends PlaySpecification with JsonMatchers {

  sequential

  lazy val app = new GuiceApplicationBuilder().build

  val businessjson =
    """
      |{
      |  "businessID": "3e75e00f-45c8-4d6f-a10c-727e8d91b187",
      |  "name": "String",
      |  "address": "String",
      |  "country": "String",
      |  "city": "String",
      |  "email": "String",
      |  "contry": "String"
      |}
    """.stripMargin

  val badbusinessjson =
    """
      |{
      |  "businessID": "3e75e00f-45c8-4d6f-a10c-727e8d91b187",
      |  "name": "String",
      |}
    """.stripMargin

  val basicHeaders = Headers(
    "Content-Type" -> "application/json"
  )
  def basicHeadersWithToken(token: String) = basicHeaders.add("X-Auth-Token" -> token)

  def routeGET(uri: String, headers: Headers = basicHeaders) = getRoute(GET, uri, AnyContentAsEmpty, headers)
  def routePOST[A](uri: String, body: A, headers: Headers = basicHeaders)(implicit w: Writeable[A]) = getRoute(POST, uri, body, headers)
  def routePUT[A](uri: String, body: A, headers: Headers = basicHeaders)(implicit w: Writeable[A]) = getRoute(PUT, uri, body, headers)
  def routePATCH[A](uri: String, body: A, headers: Headers = basicHeaders)(implicit w: Writeable[A]) = getRoute(PATCH, uri, body, headers)
  def routeDELETE(uri: String, headers: Headers = basicHeaders) = getRoute(DELETE, uri, AnyContentAsEmpty, headers)
  def routeSecuredGET(token: String)(uri: String, headers: Headers = basicHeadersWithToken(token)) = routeGET(uri, headers)
  def routeSecuredPOST[A](token: String)(uri: String, body: A, headers: Headers = basicHeadersWithToken(token))(implicit w: Writeable[A]) = routePOST(uri, body, headers)
  def routeSecuredPUT[A](token: String)(uri: String, body: A, headers: Headers = basicHeadersWithToken(token))(implicit w: Writeable[A]) = routePUT(uri, body, headers)
  def routeSecuredPATCH[A](token: String)(uri: String, body: A, headers: Headers = basicHeadersWithToken(token))(implicit w: Writeable[A]) = routePATCH(uri, body, headers)
  def routeSecuredDELETE(token: String)(uri: String, headers: Headers = basicHeadersWithToken(token)) = routeDELETE(uri, headers)
  def getRoute[A](method: String, uri: String, body: A, headers: Headers)(implicit w: Writeable[A]) = route(app, FakeRequest(method, uri, headers, body)).get

  def mustBeError(code: Int, result: Future[Result]) = {
    status(result) must equalTo(ApiError.statusFromCode(code))
    contentAsString(result) must /("code" -> code)
  }

  def signIn: Option[String] = {
    val result = routePOST("/signIn", Json.obj("email" -> "asd","password" -> "asd","rememberMe" -> true))
    status(result) must equalTo(OK)
    val response = Json.parse(contentAsString(result))
    (response \ "token").asOpt[String]
  }

  "API" should {

    "warn if auth header is not present" in new Scope {
        val result = routePOST("/api/business", businessjson)
        status(result) must equalTo(401)
    }
    "update business if authirized" in new Scope {
        signIn.map { token =>
          val result = routeSecuredPOST(token)("/api/business", businessjson)
          status(result) must equalTo(OK)
      }
    }
    "dont update business if json is broken" in new Scope {
      signIn.map { token =>
        val result = routeSecuredPOST(token)("/api/business", badbusinessjson)
        status(result) must equalTo(BAD_REQUEST)
      }
    }
    "show full list" in new Scope {
      val result = routeGET("/api/businesses")
      status(result) must equalTo(OK)
      contentAsString(result) must contain("3e75e00f-45c8-4d6f-a10c-727e8d91b187")
      contentAsString(result) must contain("3e75e00f-45c8-4d6f-a10c-727e8d91b186")
    }

    "filter list by substring" in new Scope {
      val result = routeGET("/api/businesses?nameSubstring=ri")
      status(result) must equalTo(OK)
      contentAsString(result) must contain("3e75e00f-45c8-4d6f-a10c-727e8d91b187")
      contentAsString(result) must contain("3e75e00f-45c8-4d6f-a10c-727e8d91b186")
    }

    "filter list by substring 2" in new Scope {
      val result = routeGET("/api/businesses?nameSubstring=22")
      status(result) must equalTo(OK)
      contentAsString(result) must not contain("3e75e00f-45c8-4d6f-a10c-727e8d91b187")
      contentAsString(result) must not contain("3e75e00f-45c8-4d6f-a10c-727e8d91b186")
    }

    "filter list by country" in new Scope {
      val result = routeGET("/api/businesses?country=String")
      status(result) must equalTo(OK)
      contentAsString(result) must contain("3e75e00f-45c8-4d6f-a10c-727e8d91b187")
      contentAsString(result) must contain("3e75e00f-45c8-4d6f-a10c-727e8d91b186")
    }

    "filter list by country 2" in new Scope {
      val result = routeGET("/api/businesses?country=2")
      status(result) must equalTo(OK)
      contentAsString(result) must not contain("3e75e00f-45c8-4d6f-a10c-727e8d91b187")
      contentAsString(result) must not contain("3e75e00f-45c8-4d6f-a10c-727e8d91b186")
    }

    "filter list by city" in new Scope {
      val result = routeGET("/api/businesses?city=String")
      status(result) must equalTo(OK)
      contentAsString(result) must contain("3e75e00f-45c8-4d6f-a10c-727e8d91b187")
      contentAsString(result) must contain("3e75e00f-45c8-4d6f-a10c-727e8d91b186")
    }

    "filter list by city 2" in new Scope {
      val result = routeGET("/api/businesses?city=2")
      status(result) must equalTo(OK)
      contentAsString(result) must not contain("3e75e00f-45c8-4d6f-a10c-727e8d91b187")
      contentAsString(result) must not contain("3e75e00f-45c8-4d6f-a10c-727e8d91b186")
    }

  }
}