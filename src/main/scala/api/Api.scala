package api

import akka.http.scaladsl.model.{HttpMethods, HttpResponse, StatusCodes}
import http.HttpClient
import models.UserRequest
import io.circe.generic.auto._, io.circe.syntax._
import scala.concurrent.Future
import  scala.concurrent.ExecutionContext.Implicits.global

import javax.inject.Singleton

case class ReportRequest(userId: Int, message: String)

@Singleton
class Api {
  def createUser(id: Int, firstName: String, lastName: String,  phone: String): Future[Unit]  = {
    HttpClient.send(HttpMethods.POST, "witness", UserRequest(id, firstName, lastName, phone).asJson.toString())
      .map({
        case _ @ HttpResponse(StatusCodes.OK, _, _, _) => Future.successful()
        case _ => Future.failed(new Exception("Error on create user"))
      }).flatten
  }

  def sendReport(userId: Int, message: String): Future[Unit]  = {
    HttpClient.send(HttpMethods.POST, "reports", ReportRequest(userId, message).asJson.toString())
      .map({
        case _ @ HttpResponse(StatusCodes.OK, _, _, _) => Future.successful()
        case _ => Future.failed(new Exception("Error on send user"))
      }).flatten
  }
}
