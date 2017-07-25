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
/**
 * @author Rob van der Veer
 * @since 1.0
 */
public class Egg {
  // The resulting formula for egg volume, V, was V =
  // (0.6057 - 0.0018B)LB2 in which L is the egg length in
  // millimeters, and B is the egg maximum breadth in millimeters.
  public static double volume(double l, double b) {
    return (0.6057 - 0.0018 * b) * l * b * b;
  }
}
