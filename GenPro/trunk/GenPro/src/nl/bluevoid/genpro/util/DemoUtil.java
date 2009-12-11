package nl.bluevoid.genpro.util;

public class DemoUtil {
  public static void waitForEnterPressed() {
    try {
      System.out.println("Press to continue...");
      System.in.read();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
