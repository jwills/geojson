package com.cloudera.science.geojson

import com.esri.core.geometry._
import org.scalatest.{Matchers, FlatSpec}
import scala.io.Source
import spray.json._
import GeoJsonProtocol._

class GeoJsonSpec extends FlatSpec with Matchers {
  val geojson = Source.fromURL(getClass.getResource("/boroughs.geojson")).mkString

  "GeoJson" should "correctly parse the NYC boroughs" in {
    val fc = geojson.parseJson.convertTo[FeatureCollection]
    val boroughs = fc.features.map(f => f.properties("borough").compactPrint -> f.geometry)
   // boroughs.foreach(println)
    val point = new Point(-73.812, 40.6036)
    boroughs.map { case (name, geo) => GeometryEngine.equals(geo, geo, SpatialReference.create(4326)) }.
      foreach(println)
  }
}
