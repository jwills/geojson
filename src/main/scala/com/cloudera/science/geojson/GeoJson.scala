package com.cloudera.science.geojson

import com.esri.core.geometry.{Geometry, GeometryEngine, SpatialReference}
import spray.json._

case class Feature(val id: Option[JsValue], val properties: Map[String, JsValue], val geometry: Geometry)
case class FeatureCollection(val features: Array[Feature]) extends Iterable[Feature] {
  def iterator = features.iterator
}

object GeoJsonProtocol extends DefaultJsonProtocol {
  implicit object GeometryJsonFormat extends RootJsonFormat[Geometry] {
    def write(g: Geometry) = {
      GeometryEngine.geometryToGeoJson(g).parseJson
    }
    def read(value: JsValue) = {
      GeometryEngine.geometryFromGeoJson(value.compactPrint,
        0, Geometry.Type.Unknown).getGeometry()
    }
  }

  implicit object FeatureJsonFormat extends RootJsonFormat[Feature] {
    def write(f: Feature) = {
      val buf = scala.collection.mutable.ArrayBuffer[(String, JsValue)]()
      buf ++= Seq("type" -> JsString("Feature"), "properties" -> JsObject(f.properties),
        "geometry" -> GeometryJsonFormat.write(f.geometry))
      f.id.foreach(v => { buf += "id" -> v})
      JsObject(buf.toMap)
    }

    def read(value: JsValue) = {
      val jso = value.asJsObject
      val id = jso.fields.get("id")
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

/**
 * A wrapper that provides convenience methods for using the spatial relations in the ESRI
 * GeometryEngine with a particular instance of the Geometry interface and an associated
 * SpatialReference.
 *
 * @param geometry the geometry object
 * @param spatialReference optional spatial reference; if not specified, uses WKID 4326 a.k.a. WGS84, the
 *                         standard coordinate frame for Earth.
 */
class RichGeometry(val geometry: Geometry, val spatialReference: SpatialReference = SpatialReference.create(4326)) {

  def contains(other: Geometry): Boolean = {
    GeometryEngine.contains(geometry, other, spatialReference)
  }

  def within(other: Geometry): Boolean = {
    GeometryEngine.within(geometry, other, spatialReference)
  }

  def overlaps(other: Geometry): Boolean = {
    GeometryEngine.overlaps(geometry, other, spatialReference)
  }

  def touches(other: Geometry): Boolean = {
    GeometryEngine.touches(geometry, other, spatialReference)
  }

  def crosses(other: Geometry): Boolean = {
    GeometryEngine.crosses(geometry, other, spatialReference)
  }

  def disjoint(other: Geometry): Boolean = {
    GeometryEngine.disjoint(geometry, other, spatialReference)
  }

  def distance(other: Geometry): Double = {
    GeometryEngine.distance(geometry, other, spatialReference)
  }
}

/**
 * Helper object for implicitly creating RichGeometry wrappers
 * for a given Geometry instance.
 */
object RichGeometry {
  implicit def createRichGeometry(g: Geometry) = new RichGeometry(g)
}