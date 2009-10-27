/*
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.bluevoid.genpro.example.eggweight;

import nl.bluevoid.genpro.cell.LibraryCell;
/**
 * @author Rob van der Veer
 * @since 1.0
 */
public class GonioOperations {
  public static final LibraryCell GONIO_OPS = new LibraryCell(GonioOperations.class);

  public static double surfaceFromRadius(double r) {
    return Math.PI * r * r;
  }

  public static double surfaceFromDiameter(double d) {
    return surfaceFromRadius(d / 2);
  }

  public static double surfaceElipseFromDiameter(double w, double h) {
    return Math.PI * (w / 2) * (h / 2);
  }

  public static double volumeElipseFromDiameter(double w, double h, double z) {
    return  Math.PI * w * h * z* (4.0 / 3.0);
  }

}
