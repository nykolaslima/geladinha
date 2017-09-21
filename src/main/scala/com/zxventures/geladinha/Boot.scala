package com.zxventures.geladinha

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.zxventures.geladinha.components.pointOfSale.PointOfSaleRepository
import com.zxventures.geladinha.infrastructure.config.AppConfig._
import com.zxventures.geladinha.infrastructure.persistence.postgres.DBConnection
import org.slf4j.LoggerFactory

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object Boot extends App with DBConnection {
  implicit val system = ActorSystem("geladinha")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val log = LoggerFactory.getLogger(this.getClass)

  //database connection warm up
  Await.result(PointOfSaleRepository.load(1), 5.seconds)

  //load initial points of sale
  LoadInitialPointsOfSale.load()

  val mainRoute = new MainRoute()

  Http().bindAndHandle(mainRoute.routes, config.getString("http.interface"), config.getInt("http.port")).onComplete {
    case Success(s) => log.info("Application Started")
    case Failure(f) => log.error("Could not start the server", f)
  }
}
