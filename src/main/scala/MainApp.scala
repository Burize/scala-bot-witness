import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import dao.UserDao

object MainApp {
  def main(args: Array[String]) {
    val bot = new MainBot(new UserDao(buildDB()))
    val eol = bot.run()
    println("Press [ENTER] to shutdown the bot")
    scala.io.StdIn.readLine()
    bot.shutdown()
    Await.result(eol, Duration.Inf)
  }


  private def buildDB() = {
    import slick.jdbc.PostgresProfile.api._

    val config = ConfigFactory.load();
    val url = config.getString("db.url")
    val driver = config.getString("db.driver")
    val user = config.getString("db.user")
    val password = config.getString("db.password")

    Database.forURL(url = url, driver = driver, user=user, password = password)

  }
}
