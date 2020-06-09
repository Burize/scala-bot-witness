package models

case class LastCommand(userId: Int, command: Option[BotCommand.Value])
