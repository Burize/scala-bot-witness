package DataBase

import slick.jdbc.PostgresProfile.api._
import com.typesafe.config.ConfigFactory

object DB {
  val connection = buildDB()

  private def buildDB() = {
    val config = ConfigFactory.load();
    val url = config.getString("db.url")
    val driver = config.getString("db.driver")
    val user = config.getString("db.user")
    val password = config.getString("db.password")

    Database.forURL(url = url, driver = driver, user=user, password = password)
  }
}
