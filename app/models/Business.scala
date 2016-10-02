package models

import java.util.UUID

import play.api.libs.json.Json

/**
 * Business entiry
 *
 * @param businessID identifier
 * @param name name
 * @param address address of business
 * @param country country
 * @param city city
 * @param email email
 * @param contry contry
 */
case class Business(
  businessID: UUID,
  name: String,
  address: String,
  country: String,
  city: String,
  email: String,
  contry: String
)

object Business {
  /**
   * Converts the [User] object to Json and vice versa.
   */
  implicit val jsonFormat = Json.format[Business]
}
