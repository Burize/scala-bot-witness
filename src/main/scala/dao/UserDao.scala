package dao


import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

object RegistrationStatus extends Enumeration {
  val FirstName,LastName,Phone, completed = Value
}

case class User(id: Int, status: String)

class UserDao(db: PostgresProfile.backend.DatabaseDef) {

  def all(): Future[Seq[User]] = {
    db.run(users.result)
  }

  def getById(id: Int) = {
    db.run(users.filter(_.id === id).result.headOption)
  }

  def setUserStatus(userId: Int, status: String): Future[Int] = {
    db.run(users.filter(_.id === userId).map(_.status).update(status))
  }

  private class UserTable(tag: Tag) extends Table[User](tag, "users") {
    def id = column[Int]("id")

    def status = column[String]("status")

    override def * = (id, status) <> (User.tupled, User.unapply)
  }

  private val users = TableQuery[UserTable]
}