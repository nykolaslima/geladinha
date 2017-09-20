package com.zxventures.geladinha.infrastructure.serialization

import com.trueaccord.scalapb.json.{JsonFormat, Parser, Printer}
import com.zxventures.geladinha.infrastructure.generators.pointOfSale.PointOfSaleGenerator
import com.zxventures.geladinha.infrastructure.test.UnitSpec
import com.zxventures.geladinha.resources.geometry.{MultiPolygon, Point}

class GeoJsonMarshallingSpec extends UnitSpec with PointOfSaleGenerator {
  "toJson" when {
    "Point message" must {
      "generate JSON in GeoJSON format" in {
        val point = pointOfSaleResourceGen.sample.get.address.get

        val registry = JsonFormat.DefaultRegistry
          .registerWriter[Point](GeoJsonMarshalling.pointWriter, GeoJsonMarshalling.pointParser)

        new Printer(formatRegistry = registry).print(point) shouldEqual
          s"""{"type":"Point","coordinates":[${point.longitude},${point.latitude}]}"""
      }
    }

    "MultiPolygon message" must {
      "generate JSON in GeoJSON format" in {
        val multiPolygon = pointOfSaleResourceGen.sample.get.coverageArea.get

        val registry = JsonFormat.DefaultRegistry
          .registerWriter[MultiPolygon](GeoJsonMarshalling.multiPolygonWriter, GeoJsonMarshalling.multiPolygonParser)

        new Printer(formatRegistry = registry).print(multiPolygon) shouldEqual
          s"""{"type":"MultiPolygon","coordinates":[[[[${polygonPoints(0).longitude},${polygonPoints(0).latitude}],[${polygonPoints(1).longitude},${polygonPoints(1).latitude}],[${polygonPoints(2).longitude},${polygonPoints(2).latitude}],[${polygonPoints(3).longitude},${polygonPoints(3).latitude}],[${polygonPoints(4).longitude},${polygonPoints(4).latitude}],[${polygonPoints(5).longitude},${polygonPoints(5).latitude}]]]]}"""
      }
    }
  }

  "fromJson" when {
    "Point GeoJSON" must {
      "generate Point message" in {
        val point = pointOfSaleResourceGen.sample.get.address.get

        val registry = JsonFormat.DefaultRegistry
          .registerWriter[Point](GeoJsonMarshalling.pointWriter, GeoJsonMarshalling.pointParser)

        new Parser(formatRegistry = registry).fromJsonString[Point](s"""{"type":"Point","coordinates":[${point.longitude},${point.latitude}]}""") shouldEqual point
      }
    }

    "MultiPolygon GeoJSON" must {
      "generate MultiPolygon message" in {
        val multiPolygon = pointOfSaleResourceGen.sample.get.coverageArea.get

        val registry = JsonFormat.DefaultRegistry
          .registerWriter[MultiPolygon](GeoJsonMarshalling.multiPolygonWriter, GeoJsonMarshalling.multiPolygonParser)

        new Parser(formatRegistry = registry).fromJsonString[MultiPolygon](s"""{"type":"MultiPolygon","coordinates":[[[[-46.7936897277832,-23.53560255279179],[-46.78783178329468,-23.54185830797095],[-46.77781105041503,-23.54059931197617],[-46.77856206893921,-23.537530458746964],[-46.78401231765747,-23.52997605345957],[-46.7936897277832,-23.53560255279179]]]]}""") shouldEqual multiPolygon
      }
    }
  }
}
