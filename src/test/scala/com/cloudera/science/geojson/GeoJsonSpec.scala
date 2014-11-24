/**
 * Copyright (c) 2014, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.science.geojson

import com.esri.core.geometry.Point
import org.scalatest.{Matchers, FlatSpec}
import spray.json._
import GeoJsonProtocol._

class GeoJsonSpec extends FlatSpec with Matchers {
  val geojson = scala.io.Source.fromURL(getClass.getResource("/boroughs.geojson")).mkString

  "GeoJson" should "correctly parse the NYC boroughs into usable RichGeometry objects" in {
    val features = geojson.parseJson.convertTo[FeatureCollection]
    val point = new Point(-73.994499, 40.75066)
    val b = features.filter(f => f.geometry.contains(point))
    b.map(_.properties("borough").convertTo[String]).head should be("Manhattan")
  }

  "GeoJson" should "parse GeoJSON into objects and back again" in {
    val features = geojson.parseJson.convertTo[FeatureCollection]
    val geojson2 = features.toJson.compactPrint
    val features2 = geojson2.parseJson.convertTo[FeatureCollection]
    features2.toJson.compactPrint should be(geojson2)
  }
}
