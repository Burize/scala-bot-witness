import java.net.{InetSocketAddress, Proxy}

import api.Api
import com.bot4s.telegram.api.RequestHandler
import com.bot4s.telegram.api.declarative.Commands
import com.bot4s.telegram.clients.ScalajHttpClient
import com.bot4s.telegram.future.{Polling, TelegramBot}
import com.bot4s.telegram.models.{KeyboardButton, Message, MessageEntityType, ReplyKeyboardMarkup}
import com.typesafe.config.ConfigFactory
import dao.{LastCommandDao, UserRegistrationDao}
import slogging.{LogLevel, LoggerConfig, PrintLoggerFactory}
import cats.effect._
import models.{BotCommand, RegistrationStep}

import scala.concurrent._
import scala.util.{Failure, Success}


class MainBot (userRegistrationDao: UserRegistrationDao, lastCommandDao: LastCommandDao, api: Api) extends TelegramBot
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

  override def receiveMessage(message: Message): Future[Unit] = {


    val isCommand = message.entities.map( entities => entities.head.`type` match {
      case MessageEntityType.BotCommand => true
      case _ => false
    }).getOrElse(false)

    if(isCommand) {
      return Future.successful()
    }

    message.from
      .map(_.id)
      .map(userId => lastCommandDao.getByUserId(userId).map(_.map({
        case BotCommand.Registration => nextRegistrationStep(message)
      })))

    Future.successful()
  }

  onCommand("/registration"){ implicit message =>
    message.from
        .map(_.id)
        .map( id => {
          val registrationF = userRegistrationDao.getById(id).flatMap({
            case Some(_) => userRegistrationDao.setStep(id, RegistrationStep.SetFirstName)
            case None => userRegistrationDao.create(id).flatMap(_ => userRegistrationDao.setStep(id, RegistrationStep.SetFirstName))
          })
          registrationF.map(_ => lastCommandDao.setLastCommand(id, Some(BotCommand.Registration)))
          registrationF
    })
      .getOrElse(Future.failed(new Exception("Empty sender")))
      .map(_ => IO{reply("Enter your first name")} )
      .recover({case _ => IO{reply("There is some error")}})
      .foreach(_.unsafeRunSync())

    Future.successful()
  }

  private def nextRegistrationStep(implicit message: Message) = {


    message.from.map(_.id).map(userId => {
      userRegistrationDao.getById(userId).map(_.map(_.step).flatMap({
        case Some(RegistrationStep.SetFirstName) => message.text.map(firstName => {
          userRegistrationDao.setFirstName(userId, firstName).map(_ => IO {reply("Enter your last name ")})
        })
        case Some(RegistrationStep.SetLastName) => message.text.map(lastName => {
          userRegistrationDao.setLastName(userId, lastName).map(_ => IO {requestPhone(message)})
        })
        case Some(RegistrationStep.SetPhone) => message.contact.map(contact => {
          userRegistrationDao.setPhone(userId, contact.phoneNumber)
            .flatMap(_ => lastCommandDao.setLastCommand(userId, None))
            .map(_ => IO{
            signUpUser(userId).onComplete({
              case Success(_) => {
                userRegistrationDao.complete(userId).map(_ => {
                  reply("You have successfully registered. Now you can send reports! Use /sendReport command")
                })
              }
              case Failure(_) => reply("Error on registration. Please try again later")
            })
          })
        })
      }).map(_.map(_.unsafeRunSync())))
    })
  }

  def requestPhone(implicit message: Message)  = {
    val button = ReplyKeyboardMarkup.singleButton(KeyboardButton.requestContact("Share contact"))
    reply(
      "Send your phone",
      replyMarkup = Some(button)
    )
    Future.successful()
  }

  private def signUpUser(id: Int) =  {
   userRegistrationDao.getById(id).map({
      case Some(user) => (for {
        firstName <- user.firstName
        lastName <- user.lastName
        phone <- user.phone
      } yield api.createUser(user.id, firstName, lastName, phone)
        ).getOrElse(Future.failed(new Exception("Incorrect user")))
      case None => Future.failed(new Exception("Empty registration"))
    }).flatten
  }

  private def buildProxySettings(): Proxy = {
    val config = ConfigFactory.load();
    val url = config.getString("proxy.url")
    val port = config.getInt("proxy.port")
    new Proxy(Proxy.Type.SOCKS, InetSocketAddress.createUnresolved(url, port))
  }

}