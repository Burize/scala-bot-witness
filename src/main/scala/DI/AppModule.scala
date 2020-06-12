package DI

import api.Api
import com.google.inject.AbstractModule
import com.typesafe.config.ConfigFactory
import dao.{LastCommandDao, UserRegistrationDao}
import slick.jdbc.PostgresProfile

class AppModule extends AbstractModule {
  override def configure() = {


    val dataBase = buildDB();

    bind(classOf[PostgresProfile.backend.DatabaseDef]).toInstance(dataBase)
    bind(classOf[Api]).asEagerSingleton()
    bind(classOf[UserRegistrationDao]).asEagerSingleton()
    bind(classOf[LastCommandDao]).asEagerSingleton()
//    bind(classOf[Hello])
//      .annotatedWith(Names.named("de"))
//      .to(classOf[GermanHello])
  }

  private def buildDB() = {
    import slick.jdbc.PostgresProfile.api._

    val config = ConfigFactory.load();
    val url = config.getString("db.url")
    val driver = config.getString("db.driver")
    val user = config.getString("db.user")
    val password = config.getString("db.password")

    Database.forURL(url = url, driver = driver, user=user, password = password)

  }
}