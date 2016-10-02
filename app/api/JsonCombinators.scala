package api

import models._
import java.util.Date
import play.api.libs.json._
import play.api.libs.json.Reads.{ DefaultDateReads => _, _ } // Custom validation helpers
import play.api.libs.functional.syntax._

/*
* Set of every Writes[A] and Reads[A] for render and parse JSON objects
*/
object JsonCombinators {

  implicit val dateWrites = Writes.dateWrites("dd-MM-yyyy HH:mm:ss")
  implicit val dateReads = Reads.dateReads("dd-MM-yyyy HH:mm:ss")

}