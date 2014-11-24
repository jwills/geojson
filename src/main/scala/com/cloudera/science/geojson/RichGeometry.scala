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

import com.esri.core.geometry.{GeometryEngine, SpatialReference, Geometry}

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