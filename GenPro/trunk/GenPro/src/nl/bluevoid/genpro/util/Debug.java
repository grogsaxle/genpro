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

package nl.bluevoid.genpro.util;

import java.util.Map;
import java.util.Properties;

/*
 * Created on 28-jul-2005
 */

public class Debug {
  public static void errorOnTrue(boolean b, Object... messageParts) {
    if (b) {
      throw new IllegalStateException(getString(messageParts));
    }
  }

  public static void errorOnFalse(boolean b, Object... messageParts) {
    errorOnTrue(!b, messageParts);
  }

  public static void checkParam(boolean b, Object... messageParts) {
    if (b) {
      throw new IllegalArgumentException(getString(messageParts));
    }
  }

  public static void info(boolean b, Object... messageParts) {
    if (b) {
      info(getString(messageParts));
    }
  }

  private static void info(String message) {
    System.out.println("## Info from: " + getSourceCodeSource(3) + "\n## Info= " + message);
  }

  public static void warn(boolean b, Object... messageParts) {
    if (b)
      System.out.println("@@ Warning from: " + getSourceCodeSource(2) + "\n@@ Warning= "
          + getString(messageParts));
  }

  public static void printCallTrace(boolean b, Object... messageParts) {
    if (b) {
      Exception e = new Exception(getString(messageParts));
      e.printStackTrace();
      System.out.println("nr of lines in stacktrace:" + e.getStackTrace().length);
    }
  }

  private static int lineCounter = 0;

  public static void println(String message) {
    lineCounter++;
    String prefix = lineCounter + " " + getCallPointPrefix(true, 0);
    System.out.println(prefix + message);
  }

  public static void printErrln(String message) {
    lineCounter++;
    String prefix = lineCounter + " " + getCallPointPrefix(true, 0);
    System.err.println(prefix + message);
  }

  public static void println(String message, int linesBack) {
    lineCounter++;
    String prefix = lineCounter + " " + getCallPointPrefix(true, linesBack);
    // message = addLinebreaks(message);
    System.out.println(prefix + message);
  }

  public static void print(String message) {
    System.out.print(getCallPointPrefix(false, 0) + message);
  }

  private static String lastPrintFromFile;

  /**
   * callpoint prefix is of format <file>[linenumber] setting println to false will leave out the <file> if it
   * is the same as the last file using print and println will then result (eg) in: GeneratedPanel [ 56]
   * smsif_trunc_prefix:BOOLEAN_OPTION [102] CheckedRadioButton
   * 
   * @param println
   * @return
   */
  public static String getCallPointPrefix(boolean println, int linesBack) {

    final Exception e = new Exception();
    StackTraceElement[] traces = e.getStackTrace();
    StackTraceElement callpoint = traces[2 + linesBack];
    // strip filename extension from filename
    String file = callpoint.getFileName().split("\\.")[0];
    // check are we doing a print from the same file as the last
    if (file.equals(lastPrintFromFile))
      file = "";
    // always use at least three places for linenumber
    String lineNumber = String.format(" [%1$3s] ", callpoint.getLineNumber());
    lastPrintFromFile = println ? null : file;
    String callPointString = file + lineNumber;
    return callPointString;
  }

  private static String getString(Object... messageParts) {
    StringBuilder out = new StringBuilder();
    for (Object o : messageParts) {
      out.append(o);// is like toString.
    }
    return out.toString();
  }

  private static String getSourceCodeSource(int distance) {
    Exception e = new Exception();
    e.fillInStackTrace();
    StackTraceElement[] ste = e.getStackTrace();
    return ste[distance].toString();
  }

  public static void checkNotNull(final Object object, final String name) {
    if (object == null) {
      throw new IllegalArgumentException(name + " is null");
    }
  }

  public static void printAllSystemProperties() {
    Properties props = System.getProperties();
    props.list(System.out);

    Map<String, String> environment = System.getenv();
    System.out.println(environment.toString());
  }

  public static void printChanged(String oldXML, String newXML) {
    String[] oldLines = oldXML.split("\n");
    String[] newLines = newXML.split("\n");
    for (int i = 0; i < oldLines.length; i++) {
      if (i < newLines.length) {
        if (!oldLines[i].equals(newLines[i])) {
          System.out.println("Old: " + i + " " + oldLines[i]);
          System.out.println("New: " + i + " " + newLines[i]);
        }
      }
    }
    if (oldLines.length != newLines.length)
      System.out.println("Different number of lines old=: " + oldLines.length + " new=" + newLines.length);
  }

  public static void checkParamInList(int param, int[] list) {
    for (int i : list) {
      if (param == i)
        return;
    }
    throw new IllegalArgumentException("param is not in list:" + param);
  }

  public static void checkRange(final double value, final double min, final double max, Object... text) {
    if (value < min)
      throw new IllegalArgumentException("value " + value + "is below:" + min + " "
          + StringUtil.join(" ", text));
    if (value > max)
      throw new IllegalArgumentException("value " + value + "is above:" + max + " "
          + StringUtil.join(" ", text));
  }

  public static void printFullStackTrace(Throwable t) {
    printErrln(getFullStackTrace(t));
  }

  public static Throwable getRootCause(Throwable t) {
    Throwable rootCause = t;
    while (rootCause.getCause() != null) {
      rootCause = rootCause.getCause();
    }
    return rootCause;
  }

  public static String getFullStackTrace(Throwable t) {
    StringBuffer sb = new StringBuffer(100);
    Throwable printException = t;
    String caption = "\n  Throwable(printed by TextUtil.getStackTrace()): ";
    while (printException != null) {
      sb.append(caption);
      sb.append(printException.getClass().getName() + ": ");
      sb.append(printException.getMessage());

      StackTraceElement[] el = printException.getStackTrace();
      for (int i = 0; i < el.length; i++) {
        sb.append("\n   at ");
        sb.append(el[i]);
      }
      caption = "\n  Caused by: ";
      printException = printException.getCause();
    }
    return sb.toString();
  }

}
