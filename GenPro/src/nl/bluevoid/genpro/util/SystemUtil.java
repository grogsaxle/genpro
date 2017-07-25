package nl.bluevoid.genpro.util;

import java.util.ArrayList;

public class SystemUtil {

  public static boolean isJava1_5() {
    // java.specification.version : 1.5
    String prop = System.getProperty("java.specification.version");
    return "1.5".equals(prop);
  }

  public static void printVmProperties() {
    Debug.println("System.getProperties() contains:\n");
    ArrayList<String> settings = CollectionsUtil.toStringEntries(System.getProperties());
    for (String string : settings) {
      System.out.println("   " + string);
    }
  }
  
  public static void main(String[] strings){
    System.out.println(StringUtil.join(" | ", strings));
    printVmProperties();
  }
  
}
