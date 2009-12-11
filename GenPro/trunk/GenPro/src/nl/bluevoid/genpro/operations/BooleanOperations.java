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

package nl.bluevoid.genpro.operations;

import nl.bluevoid.genpro.NoCallTarget;
import nl.bluevoid.genpro.cell.LibraryCell;
/**
 * @author Rob van der Veer
 * @since 1.0
 */
public class BooleanOperations {
  
  public static final LibraryCell BOOL_OPS=new LibraryCell(BooleanOperations.class);
  
  public static boolean greaterThan(int a, int b) {
    return a > b;
  }

  public static boolean greaterThanOrEquals(int a, int b) {
    return a >= b;
  }
  
  public static boolean smallerThan(int a, int b) {
    return a < b;
  }

  public static boolean smallerThanOrEquals(int a, int b) {
    return a <= b;
  }

  public static boolean greaterThan(double a, double b) {
    return a > b;
  }
  
  public static boolean smallerThan(double a, double b) {
    return a < b;
  }
  
  public static boolean equals(int a, int b) {
    return a == b;
  }

  public static boolean unequals(int a, int b) {
    return a != b;
  }
  
  public static boolean and(boolean a, boolean b) {
    return a && b;
  }

  public static boolean or(boolean a, boolean b) {
    return a || b;
  }

  public static boolean not(boolean a) {
    return !a;
  }
  
  @NoCallTarget
  public static String getJavaSyntax(String methodName) {
    if("greaterThan".equals(methodName)){
      return ">";
    }
    if("greaterThanOrEquals".equals(methodName)){
      return ">=";
    }
    if("smallerThan".equals(methodName)){
      return "<";
    }
    if("smallerThanOrEquals".equals(methodName)){
      return "<=";
    }
    if("equals".equals(methodName)){
      return "==";
    }
    if("unequals".equals(methodName)){
      return "!=";
    }
    if("and".equals(methodName)){
      return "&&";
    }
    if("or".equals(methodName)){
      return "||";
    }
    if("not".equals(methodName)){
      return "!";
    }
    throw new IllegalArgumentException("No javaSyntax for:"+methodName);
  }
}
