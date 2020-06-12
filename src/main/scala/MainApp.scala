import DI.AppModule
import DataBase.Migration
import com.google.inject.Guice

import scala.concurrent.Await
import scala.concurrent.duration.Duration


object MainApp {
  def main(args: Array[String]) {

    Await.ready(Migration.migrate, Duration.Inf)

    val injector = Guice.createInjector(new AppModule)

    val bot = injector.getInstance(classOf[MainBot])

    val eol = bot.run()

    println("Press [ENTER] to shutdown the bot")
    scala.io.StdIn.readLine()
    bot.shutdown()
    Await.result(eol, Duration.Inf)
  }
}
