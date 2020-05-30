import scala.concurrent.Await
import scala.concurrent.duration.Duration

object MainApp {
  def main(args: Array[String]) {
    val bot = new MainBot()
    val eol = bot.run()
    println("Press [ENTER] to shutdown the bot")
    scala.io.StdIn.readLine()
    bot.shutdown()
    Await.result(eol, Duration.Inf)
  }
}
