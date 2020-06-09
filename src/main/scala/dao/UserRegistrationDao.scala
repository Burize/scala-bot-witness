package dao


import models.{RegistrationStep, UserRegistration}
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

class UserRegistrationDao(db: PostgresProfile.backend.DatabaseDef) {
  implicit val myEnumMapper = MappedColumnType.base[RegistrationStep.Value, String](
    e => e.toString,
    s => RegistrationStep.withName(s)
  )
  def all(): Future[Seq[UserRegistration]] = {
    db.run(users.result)
  }

  def getById(id: Int) = {
    db.run(users.filter(_.id === id).result.headOption)
  }

  def create(userId: Int): Future[Int] = {
    db.run(users insertOrUpdate  UserRegistration(userId, Some(RegistrationStep.SetPhone), false, None, None, None))
  }

  def setStep(userId: Int, step: RegistrationStep.Value): Future[Int] = {
    db.run(users.filter(_.id === userId).map(_.step).update(Some(step)))
  }

  def setFirstName(userId: Int, firstName: String ): Future[Int] = {
    db.run(users.filter(_.id === userId).map(x => (x.step, x.firstName)).update(( Some(RegistrationStep.SetLastName), Some(firstName))))
  }

  def setLastName(userId: Int, lastName: String ): Future[Int] = {
    db.run(users.filter(_.id === userId).map(x => (x.step, x.lastName)).update(( Some(RegistrationStep.SetPhone), Some(lastName))))
  }

  def setPhone(userId: Int, phone: String ): Future[Int] = {
    db.run(users.filter(_.id === userId).map(x => (x.step, x.phone)).update(( None, Some(phone))))
  }

  def complete(userId: Int): Future[Int] = {
    db.run(users.filter(_.id === userId).map(u => (u.step, u.complete)).update((None, true)))
  }

  private class UserTable(tag: Tag) extends Table[UserRegistration](tag, "users") {

    def id = column[Int]("id", O.PrimaryKey)

    def step = column[Option[RegistrationStep.Value]]("step")

    def complete = column[Boolean]("complete")

    def firstName = column[Option[String]]("firstName")

    def lastName = column[Option[String]]("lastName")

    def phone = column[Option[String]]("phone")


    override def * = (id, step, complete, firstName, lastName, phone) <> (UserRegistration.tupled, UserRegistration.unapply)
  }

  private val users = TableQuery[UserTable]
}