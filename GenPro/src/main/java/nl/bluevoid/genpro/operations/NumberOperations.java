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
public class NumberOperations {

  public static final LibraryCell NUM_OPS=new LibraryCell(NumberOperations.class);
  public static final LibraryCell MATH_CLASS=new LibraryCell(Math.class);
  
  @NoCallTarget
  public static String getJavaSyntax(String methodName){
    if("multiply".equals(methodName)){
      return "*";
    }
    if("divide".equals(methodName)){
      return "/";
    }
    if("plus".equals(methodName)){
      return "+";
    }
    if("minus".equals(methodName)){
      return "-";
    }
    if("mod".equals(methodName)){
      return "%";
    }
    if("pow".equals(methodName)){
      return "${0} * ${0}";
    }
    throw new IllegalArgumentException("No javaSyntax for:"+methodName);
  }
  
  public static int multiply(int a, int b) {
    return a * b;
  }
  
  public static int divide(int a, int b) {
    if(b==0) return Integer.MAX_VALUE;
    return a / b;
  }
  
  public static int plus(int a, int b) {
    return a + b;
  }
  
  public static int minus(int a, int b) {
    return a - b;
  }
    
//  public static int mod(int a, int b) {
//    //if(b==0)return a;
//    return a % b;
//  }
  
  public static double pow(double a) {
    return a * a;
  }
  
  public static double multiply(double a, double b) {
    return a * b;
  }
  
  public static double divide(double a, double b) {
    return a / b;
  }
  
  public static double plus(double a, double b) {
    return a + b;
  }
  
  public static double minus(double a, double b) {
    return a - b;
  }

//  public static double mod(double a, double b) {
//    return a % b;
//  }
  
}
