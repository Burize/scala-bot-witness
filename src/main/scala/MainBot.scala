import java.net.{InetSocketAddress, Proxy}

import com.bot4s.telegram.api.RequestHandler
import com.bot4s.telegram.api.declarative.Commands
import com.bot4s.telegram.clients.ScalajHttpClient
import com.bot4s.telegram.future.{Polling, TelegramBot}
import com.typesafe.config.ConfigFactory
import slogging.{LogLevel, LoggerConfig, PrintLoggerFactory}

import scala.concurrent.Future

class MainBot() extends TelegramBot
  with Polling
  with Commands[Future] {

  LoggerConfig.factory = PrintLoggerFactory()
  LoggerConfig.level = LogLevel.TRACE

  val token: String = ConfigFactory.load().getString("bot.token")

  override val client: RequestHandler[Future] = new ScalajHttpClient(token, buildProxySettings())

  onCommand("/hello"){ implicit msg =>
    reply("main booot")
    Future.successful()
  }

  private def buildProxySettings(): Proxy = {
    val config = ConfigFactory.load();
    val url = config.getString("proxy.url")
    val port = config.getInt("proxy.port")
    new Proxy(Proxy.Type.SOCKS, InetSocketAddress.createUnresolved(url, port))
  }

}