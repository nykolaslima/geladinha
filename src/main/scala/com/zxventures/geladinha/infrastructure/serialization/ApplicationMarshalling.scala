package com.zxventures.geladinha.infrastructure.serialization

import akka.http.scaladsl.marshalling.{Marshaller, _}
import akka.http.scaladsl.model.MediaType.Compressible
import akka.http.scaladsl.model.{ContentType, ContentTypes, HttpEntity, MediaType}
import akka.http.scaladsl.unmarshalling.Unmarshaller.UnsupportedContentTypeException
import akka.http.scaladsl.unmarshalling.{Unmarshaller, _}
import akka.http.scaladsl.util.FastFuture
import com.google.protobuf.CodedInputStream
import com.trueaccord.scalapb.json.{JsonFormat, Parser, Printer}
import com.trueaccord.scalapb.{GeneratedMessage, GeneratedMessageCompanion, Message}
import com.zxventures.geladinha.resources.geometry.{MultiPolygon, Point}

import scala.concurrent.Future

trait ApplicationMarshalling {
  private val protobufContentType = ContentType(MediaType.applicationBinary("x-protobuf", Compressible, "proto"))
  private val applicationJsonContentType = ContentTypes.`application/json`
  private val registry = JsonFormat.DefaultRegistry
    .registerWriter[Point](GeoJsonMarshalling.pointWriter, GeoJsonMarshalling.pointParser)
    .registerWriter[MultiPolygon](GeoJsonMarshalling.multiPolygonWriter, GeoJsonMarshalling.multiPolygonParser)
  private val printer = new Printer(formatRegistry = registry)
  private val parser = new Parser(formatRegistry = registry)

  def scalaPBFromRequestUnmarshaller[O <: GeneratedMessage with Message[O]](companion: GeneratedMessageCompanion[O]): FromEntityUnmarshaller[O] = {
    Unmarshaller.withMaterializer[HttpEntity, O](_ ⇒ implicit mat ⇒ {
      case entity@HttpEntity.Strict(`applicationJsonContentType`, data) ⇒
        val charBuffer = Unmarshaller.bestUnmarshallingCharsetFor(entity)
        FastFuture.successful(parser.fromJsonString(data.decodeString(charBuffer.nioCharset().name()))(companion))
      case entity@HttpEntity.Strict(`protobufContentType`, data) ⇒
        FastFuture.successful(companion.parseFrom(CodedInputStream.newInstance(data.asByteBuffer)))
      case entity ⇒
        Future.failed(UnsupportedContentTypeException(applicationJsonContentType, protobufContentType))
    })
  }

  implicit def scalaPBToEntityMarshaller[U <: GeneratedMessage]: ToEntityMarshaller[U] = {
    def jsonMarshaller(): ToEntityMarshaller[U] = {
      val contentType = applicationJsonContentType
      Marshaller.withFixedContentType(contentType) { value ⇒
        HttpEntity(contentType, printer.print(value))
      }
    }

    def protobufMarshaller(): ToEntityMarshaller[U] = {
      Marshaller.withFixedContentType(protobufContentType) { value ⇒
        HttpEntity(protobufContentType, value.toByteArray)
      }
    }

    Marshaller.oneOf(jsonMarshaller(), protobufMarshaller())
  }

}
