package com.cloudera.science.geojson

import com.esri.core.geometry._
import spray.json._

case class Feature(val id: JsValue, val geometry: Geometry, val properties: Map[String, JsValue])
case class FeatureCollection(val features: List[Feature])

object GeoJsonProtocol extends DefaultJsonProtocol {
  implicit object GeometryJsonFormat extends RootJsonFormat[Geometry] {
    def write(g: Geometry) = {
      JsString(GeometryEngine.geometryToGeoJson(g))
    }
    def read(value: JsValue) = {
      GeometryEngine.geometryFromGeoJson(value.compactPrint, 0, Geometry.Type.Unknown).getGeometry()
    }
  }

  implicit object FeatureJsonFormat extends RootJsonFormat[Feature] {
    def write(f: Feature) = {
      JsObject(
        "type" -> JsString("feature"),
        "properties" -> JsObject(f.properties),
        "geometry" -> GeometryJsonFormat.write(f.geometry),
        "id" -> f.id)
    }

    def read(value: JsValue) = {
      val jso = value.asJsObject
      val id = jso.fields.getOrElse("id", JsNull)
      val geometry = GeometryJsonFormat.read(jso.fields("geometry"))
      val properties = jso.fields("properties").asJsObject.fields
      Feature(id, geometry, properties)
    }
  }

  implicit val featureCollectionFormat = jsonFormat1(FeatureCollection)
}