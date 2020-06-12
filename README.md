# Telegram bot written in Scala

This bot is called witness - user can register in the system and then send some reports, by telegram messenger, to external api (this api as placed in another [repository](https://github.com/Burize/rest-api-for-witness-bot)).

## Features
- Multistep registration via telegram messenger: first name, last name and user's phone
- Sending report to external api


## Used features
- [Play2](https://www.playframework.com/) - framework for Scala
- [Slick](http://scala-slick.org/) - database query and access library for Scala
- [Guice](https://github.com/google/guice) -  dependency injection framework
- [bot4s.telegram](https://github.com/bot4s/telegram) - as wrapper for the Telegram Bot API

## Getting started 

1. For launch project you need:
[Scala](https://www.scala-lang.org/download/) 
[Sbt](https://www.scala-sbt.org/download.html)
[Postgress](https://www.postgresql.org/)

2. There is `conf/application-template.conf`. Copy it to the same directory and rename to `application.cong`. Then fill parameters:

- bot.token - your telegram token
- proxy(url/port) - proxy for telegram api, it can be blocked in some country
- db(url/user/password) - credentials for your database
- api.url - url to working REST api

3. Run project: if you use intellij IDEA, you can run the project by it or via `sbt run` сomamnd.

4. Now you can interact with bot by telegram messenger. All tables are needed for the database will be created on the first launch.

### Available commands:
- `/registration` - for user registration
- `/sendReport` - for sending report to rest api(allowed only for registered user)

<strong><em>You need working [REST API](https://github.com/Burize/rest-api-for-witness-bot) to complete registration and send report </strong></em>



