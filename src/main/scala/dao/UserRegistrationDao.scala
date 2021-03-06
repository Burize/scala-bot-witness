package dao


import models.{RegistrationStep, UserRegistration}
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future
import javax.inject.Singleton
import com.google.inject.Inject


trait WithEnum {
  implicit val myEnumMapper = MappedColumnType.base[RegistrationStep.Value, String](
    e => e.toString,
    s => RegistrationStep.withName(s)
  )
}

@Singleton
class UserRegistrationDao @Inject()(db: PostgresProfile.backend.DatabaseDef) extends WithEnum {
  def all(): Future[Seq[UserRegistration]] = {
    db.run(users.result)
  }

  def getById(id: Int): Future[Option[UserRegistration]] = {
    db.run(users.filter(_.id === id).result.headOption)
  }

  def create(userId: Int): Future[Int] = {
    db.run(users insertOrUpdate  UserRegistration(userId, Some(RegistrationStep.SetPhone), complete = false, None, None, None))
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

  private val users = TableQuery[UserRegistrationTable]
}


class UserRegistrationTable(tag: Tag) extends Table[UserRegistration](tag, "UserRegistrations") with WithEnum {

  def id = column[Int]("id", O.PrimaryKey)

  def step = column[Option[RegistrationStep.Value]]("step")

  def complete = column[Boolean]("complete")

  def firstName = column[Option[String]]("firstName")

  def lastName = column[Option[String]]("lastName")

  def phone = column[Option[String]]("phone")


  override def * = (id, step, complete, firstName, lastName, phone) <> (UserRegistration.tupled, UserRegistration.unapply)
}