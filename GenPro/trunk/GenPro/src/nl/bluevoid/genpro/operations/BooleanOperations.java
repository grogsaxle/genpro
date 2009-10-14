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

  public static boolean setTrue() {
    return true;
  }
  
  public static boolean setFalse() {
    return false;
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
    if("setTrue".equals(methodName)){
      return "true";
    }
    if("setFalse".equals(methodName)){
      return "false";
    }
    throw new IllegalArgumentException("No javaSyntax for:"+methodName);
  }
}
