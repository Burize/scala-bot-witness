package DI

import api.Api
import com.google.inject.AbstractModule
import com.typesafe.config.ConfigFactory
import dao.{LastCommandDao, UserRegistrationDao}
import slick.jdbc.PostgresProfile
import DataBase.DB

class AppModule extends AbstractModule {
  override def configure() = {
    val dataBase = DB.connection;

    bind(classOf[PostgresProfile.backend.DatabaseDef]).toInstance(dataBase)
    bind(classOf[Api]).asEagerSingleton()
    bind(classOf[UserRegistrationDao]).asEagerSingleton()
    bind(classOf[LastCommandDao]).asEagerSingleton()
  }
}