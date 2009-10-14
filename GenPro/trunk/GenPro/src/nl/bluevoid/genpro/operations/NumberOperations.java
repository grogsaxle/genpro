/*
 * This file is part of GenPro, Reflective Object Oriented Genetic Programming.
 *
 * GenPro offers a dual license model containing the GPL (GNU General Public License) version 2  
 * as well as a commercial license.
 *
 * For licensing information please see the file license.txt included with GenPro
 * or have a look at the top of class nl.bluevoid.genpro.cell.Cell which representatively
 * includes the GenPro license policy applicable for any file delivered with GenPro.
 */

package nl.bluevoid.genpro.operations;

import nl.bluevoid.genpro.NoCallTarget;
import nl.bluevoid.genpro.cell.LibraryCell;

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
      return "^2";
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
