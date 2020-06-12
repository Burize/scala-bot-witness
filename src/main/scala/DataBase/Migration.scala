package DataBase

import dao.{LastCommandTable, UserRegistrationTable}
import slick.jdbc.PostgresProfile.api._
import slick.migration.api._

import scala.concurrent.Future


object Migration {
  implicit val dialect: PostgresDialect = new PostgresDialect

  def migrate: Future[Unit] = {

    val lasCommandTable = TableQuery[LastCommandTable]
    val initCommands =
      TableMigration(lasCommandTable)
        .create
        .addColumns(_.userId, _.command)

    val userRegistrationTable = TableQuery[UserRegistrationTable]

    val initUserRegistrations =
      TableMigration(userRegistrationTable)
        .create
        .addColumns(_.id, _.step, _.complete, _.firstName, _.lastName, _.phone)

    val migration = initCommands & initUserRegistrations

    DB.connection.run(migration())
  }
}
