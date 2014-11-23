package com.cloudera.science.geojson

import com.esri.core.geometry._
import spray.json._

case class Feature(val id: JsValue, val properties: Map[String, JsValue], val geometry: Geometry)
case class FeatureCollection(val features: Array[Feature])

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
        "type" -> JsString("Feature"),
        "properties" -> JsObject(f.properties),
        "geometry" -> GeometryJsonFormat.write(f.geometry),
        "id" -> f.id)
    }

    def read(value: JsValue) = {
      val jso = value.asJsObject
      val id = jso.fields.getOrElse("id", JsNull)
      val properties = jso.fields("properties").asJsObject.fields
      val geometry = GeometryJsonFormat.read(jso.fields("geometry"))
      Feature(id, properties, geometry)
    }
  }

  implicit object FeatureCollectionJsonFormat extends RootJsonFormat[FeatureCollection] {
    def write(fc: FeatureCollection) = {
      JsObject(
        "type" -> JsString("FeatureCollection"),
        "features" -> JsArray(fc.features.map(f => FeatureJsonFormat.write(f)): _*)
      )
    }

    def read(value: JsValue) = {
      FeatureCollection(value.asJsObject.fields("features").convertTo[Array[Feature]])
    }
  }
}