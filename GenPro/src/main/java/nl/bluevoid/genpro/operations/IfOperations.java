package nl.bluevoid.genpro.operations;

import nl.bluevoid.genpro.NoCallTarget;
import nl.bluevoid.genpro.cell.LibraryCell;

public class IfOperations {

  public static final LibraryCell IF_OPS=new LibraryCell(IfOperations.class);
  
  /**
   * beware: order of params maters!! See getJavaSyntax
   * 
   * @param in
   * @param optionTrue
   * @param optionFalse
   * @return
   */
  public static Integer intIf(boolean in, int optionTrue, int optionFalse) {
    return in ? optionTrue : optionFalse;
  }

  /**
   * beware: order of params maters!! See getJavaSyntax
   * 
   * @param in
   * @param optionTrue
   * @param optionFalse
   * @return
   */
  public static Double doubleIf(boolean in, double optionTrue, double optionFalse) {
    return in ? optionTrue : optionFalse;
  }

  /**
   * beware: order of params maters!! See getJavaSyntax
   * 
   * @param in
   * @param optionTrue
   * @param optionFalse
   * @return
   */
  public static String stringIf(boolean in, String optionTrue, String optionFalse) {
    return in ? optionTrue : optionFalse;
  }

  @NoCallTarget
  public static String getJavaSyntax(String methodName) {
    if (methodName.endsWith("If")) {
      return "${0} ? ${1} : ${2}";
    }
    throw new IllegalArgumentException("No javaSyntax for:" + methodName);
  }
}