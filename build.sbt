name := "bot"

version := "0.1"

scalaVersion := "2.12.10"

scalacOptions += "-Ypartial-unification"

libraryDependencies += "com.bot4s" %% "telegram-core" % "4.4.0-RC2"
libraryDependencies += "com.typesafe" % "config" % "1.4.0"
libraryDependencies += "org.postgresql" % "postgresql" % "9.4-1206-jdbc42"
libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0"
libraryDependencies += "org.typelevel" %% "cats-effect" % "2.1.3"

libraryDependencies += "com.typesafe.akka" %% "akka-http"   % "10.1.12"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.6.5"

libraryDependencies += "com.google.inject" % "guice" % "4.2.3"


libraryDependencies += "io.github.nafg" %% "slick-migration-api" % "0.7.0"
resolvers += Resolver.jcenterRepo
libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.3.1",
  "org.slf4j" % "slf4j-nop" % "1.7.26",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.3.1"
)

Compile / resourceDirectory := baseDirectory.value / "conf"

addCompilerPlugin(
  "org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full
)