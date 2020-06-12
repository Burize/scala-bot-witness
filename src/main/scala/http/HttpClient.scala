package http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContextExecutor


object HttpClient {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  def send(method: HttpMethod, path: String, json: String) = {
    Http().singleRequest(
      HttpRequest(
        uri = s"http://localhost:9000/$path",
        method = method,
        entity = HttpEntity(ContentTypes.`application/json`, json)
      ))
  }
}
