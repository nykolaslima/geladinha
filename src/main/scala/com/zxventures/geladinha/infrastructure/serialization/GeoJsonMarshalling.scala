package com.zxventures.geladinha.infrastructure.serialization

import com.zxventures.geladinha.resources.geometry.{LinearString, MultiPolygon, Point, Polygon}
import org.json4s.JValue
import org.json4s.JsonAST._

object GeoJsonMarshalling {
  def pointWriter: (Point => JValue) = {
    (point) => JObject(
      List(
        "type" -> JString("Point"),
        "coordinates" -> JArray(List(JDecimal(BigDecimal(point.longitude.toString)), JDecimal(BigDecimal(point.latitude.toString))))
      )
    )
  }

  def pointParser: (JValue => Point) = {
    import org.json4s._
    (jv) => {
      val longitude: Float = (jv \\ "coordinates").asInstanceOf[JArray].values(0).asInstanceOf[Double].toFloat
      val latitude: Float = (jv \\ "coordinates").asInstanceOf[JArray].values(1).asInstanceOf[Double].toFloat
      Point(latitude, longitude)
    }
  }

  def multiPolygonWriter: (MultiPolygon => JValue) = {
    (multiPolygon) => JObject(
      List(
        "type" -> JString("MultiPolygon"),
        "coordinates" -> encodeJson(multiPolygonAsListOfLists(multiPolygon))
      )
    )
  }

  def multiPolygonParser: (JValue => MultiPolygon) = {
    import org.json4s._
    (jv) => {
      val polygons = (jv \\ "coordinates").asInstanceOf[JArray].arr.map { valuePolygon =>
        val linearStrings: List[LinearString] = valuePolygon.asInstanceOf[JArray].arr.map { valueLinerString =>
          val points: List[Point] = valueLinerString.asInstanceOf[JArray].arr.map { valuePositions =>
            val longitude = valuePositions.asInstanceOf[JArray].values(0).asInstanceOf[Double].toFloat
            val latitude = valuePositions.asInstanceOf[JArray].values(1).asInstanceOf[Double].toFloat

            Point(latitude, longitude)
          }

          LinearString(points)
        }

        Polygon(linearStrings)
      }

      MultiPolygon(polygons)
    }
  }

  private def multiPolygonAsListOfLists(mp: MultiPolygon): Seq[Seq[Seq[Seq[BigDecimal]]]] = {
    mp.polygons.map { p =>
      val linerStringsCoordinates = p.linearStrings.map { lr =>
        lr.points.map { p =>
          Seq(BigDecimal(p.longitude.toString), BigDecimal(p.latitude.toString))
        }
      }

      if(linerStringsCoordinates.size > 1) throw new UnsupportedOperationException("Polygon with exterior rings are not supported because we are using Earth as our exterior ring (https://tools.ietf.org/html/rfc7946#appendix-A.3)")

      linerStringsCoordinates
    }
  }

  private def encodeJson(src: AnyRef): JValue = {
    import org.json4s.jackson.Serialization
    import org.json4s.{Extraction, NoTypeHints}
    implicit val formats = Serialization.formats(NoTypeHints)

    Extraction.decompose(src)
  }
}
