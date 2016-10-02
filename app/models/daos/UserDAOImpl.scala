package models.daos

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import models.User

import scala.collection.mutable
import scala.concurrent.Future
import javax.inject.Inject

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import reactivemongo.api._

import play.modules.reactivemongo.{ // ReactiveMongo Play2 plugin
  ReactiveMongoApi
}

// BSON-JSON conversions/collection
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

/**
 * Give access to the user object.
 */
class UserDAOImpl @Inject() (val reactiveMongoApi: ReactiveMongoApi) extends UserDAO {

  def collection: Future[JSONCollection] =
    reactiveMongoApi.database.map(_.collection[JSONCollection]("user"))

  /**
   * Finds a user by its login info.
   *
   * @param loginInfo The login info of the user to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  def find(loginInfo: LoginInfo): Future[Option[User]] = {
    collection.flatMap(_.find(Json.obj("loginInfo" -> loginInfo)).one[User])
  }

  /**
   * Finds a user by its user ID.
   *
   * @param userID The ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  def find(userID: UUID): Future[Option[User]] = {
    collection.flatMap(_.find(Json.obj("userID" -> userID)).one[User])
  }

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def save(user: User) = {
    for {
      c <- collection
      _ <- c.insert(user)
    } yield user
  }
}
