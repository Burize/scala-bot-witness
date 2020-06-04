import java.net.{InetSocketAddress, Proxy}

import com.bot4s.telegram.api.RequestHandler
import com.bot4s.telegram.api.declarative.Commands
import com.bot4s.telegram.clients.ScalajHttpClient
import com.bot4s.telegram.future.{Polling, TelegramBot}
import com.bot4s.telegram.models.{Message}
import com.typesafe.config.ConfigFactory
import dao.UserDao
import slogging.{LogLevel, LoggerConfig, PrintLoggerFactory}
import cats.effect._
import scala.concurrent._

class MainBot (userDao: UserDao) extends TelegramBot
  with Polling
  with Commands[Future] {

  LoggerConfig.factory = PrintLoggerFactory()
  LoggerConfig.level = LogLevel.TRACE

  val token: String = ConfigFactory.load().getString("bot.token")

  override val client: RequestHandler[Future] = new ScalajHttpClient(token, buildProxySettings())

  onCommand("/start"){ implicit msg =>

    val greetingMessage =
      """
        |Welcome!
        |
        |The duty of every law-abiding citizen is not to stand aside. Every violation of order must be punished. Only in this way we can  maintain our present security and order of our society. Everyone must keep and maintain this order.
        |
        |You can be a Witness. A person who takes part in the preservation of our society. You must inform the public about what happened and prevent the crimes from being left without punishment.
        |
        |We don’t have data about you in our database, so you need to register - choose the /registration command.
        |""".stripMargin
    reply(greetingMessage)
    Future.successful()
  }

  override def receiveMessage(msg: Message): Future[Unit] = {
    Future.successful()
  }

  onCommand("/registration"){ implicit message =>
    message.from
      .map(_.id)
      .map(id => userDao.getById(id))
      .getOrElse(Future.failed(exception = new Exception()))
      .map({
        case Some(_) => IO{reply("user exist")}
        case None => IO{reply("user not exist")}
      })
      .recover({case _ => IO{reply("There is some error")}})
      .foreach(f => f.unsafeRunSync())

    Future.successful()
  }

  private def buildProxySettings(): Proxy = {
    val config = ConfigFactory.load();
    val url = config.getString("proxy.url")
    val port = config.getInt("proxy.port")
    new Proxy(Proxy.Type.SOCKS, InetSocketAddress.createUnresolved(url, port))
  }

}