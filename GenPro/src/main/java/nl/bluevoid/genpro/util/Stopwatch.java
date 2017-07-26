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

package nl.bluevoid.genpro.util;
/**
 * @author Rob van der Veer
 * @since 1.0
 */
public class Stopwatch {
  private long startTime;
  private final String action;

  public Stopwatch(String action) {
    this.action = action;
    reset();
  }

  public void reset() {
    startTime = System.currentTimeMillis();
  }

  public double ellapsedSeconds() // return seconden als double
  {
    return (System.currentTimeMillis() - startTime) / 1000.0;
  }

  public void printEllapsedTime() {
    System.out.println(action + " took " + ellapsedSeconds() +" seconds.");
  }

}
