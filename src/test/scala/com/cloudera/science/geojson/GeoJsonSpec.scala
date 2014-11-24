package com.cloudera.science.geojson

import com.esri.core.geometry.Point
import org.scalatest.{Matchers, FlatSpec}
import spray.json._
import GeoJsonProtocol._

class GeoJsonSpec extends FlatSpec with Matchers {
  val geojson = scala.io.Source.fromURL(getClass.getResource("/boroughs.geojson")).mkString

  "GeoJson" should "correctly parse the NYC boroughs into usable Geometry objects" in {
    val features = geojson.parseJson.convertTo[FeatureCollection]
    val point = new Point(-73.994499, 40.75066)
    val b = features.filter(f => f.geometry.contains(point))
    b.map(f => f.properties("borough").convertTo[String]) should be(Array("Manhattan"))
  }

  "GeoJson" should "parse GeoJSON into objects and back again" in {
    val features = geojson.parseJson.convertTo[FeatureCollection]
    val geojson2 = features.toJson.compactPrint
    val features2 = geojson2.parseJson.convertTo[FeatureCollection]
    features2.toJson.compactPrint should be(geojson2)
  }
}
