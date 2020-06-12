package dao

import models.{LastCommand, BotCommand}
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future
import  scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Singleton
import com.google.inject.Inject

@Singleton
class LastCommandDao @Inject() (db: PostgresProfile.backend.DatabaseDef) {

  def all(): Future[Seq[LastCommand]] = {
    db.run(commands.result)
  }

  def getByUserId(id: Int): Future[Option[BotCommand.Value]] = {
    db.run(commands.filter(_.userId === id).result.headOption).map(a => a.flatMap(_.command))
  }

  def setLastCommand(userId: Int, command: Option[BotCommand.Value]): Future[Int] = {
    db.run(commands insertOrUpdate LastCommand(userId, command))
  }

  private val commands = TableQuery[LastCommandTable]
}

class LastCommandTable(tag: Tag) extends Table[LastCommand](tag, "LastCommands") {
  implicit val myEnumMapper = MappedColumnType.base[BotCommand.Value, String](
    e => e.toString,
    s => BotCommand.withName(s)
  )

  def userId = column[Int]("userId", O.PrimaryKey)

  def command = column[Option[BotCommand.Value]]("command")

  override def * = (userId, command) <> (LastCommand.tupled, LastCommand.unapply)
}