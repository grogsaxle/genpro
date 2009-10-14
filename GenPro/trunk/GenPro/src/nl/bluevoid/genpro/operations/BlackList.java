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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import nl.bluevoid.genpro.util.Debug;
import nl.bluevoid.genpro.util.StringUtil;

public class BlackList {

  private static HashMap<Class<?>, ArrayList<Method>> blacklist = new HashMap<Class<?>, ArrayList<Method>>();

  static {
    try {
      Debug.println("Initializing Blacklist... ");
      add(Double.class, "doubleValue");
      add(Double.class, "valueOf", double.class);
      add(Double.class, "longBitsToDouble", long.class);
      add(Double.class, "doubleToLongBits", double.class);
      add(Double.class, "doubleToRawLongBits", double.class);

      add(Integer.class, "intValue");
      add(Integer.class, "valueOf", int.class);
      add(Float.class, "floatValue");
      add(Float.class, "valueOf", float.class);
      add(Byte.class, "byteValue");
      add(Byte.class, "valueOf", byte.class);
      add(Long.class, "longValue");
      add(Long.class, "valueOf", long.class);
      add(Short.class, "shortValue");
      add(Short.class, "valueOf", short.class);
      add(Boolean.class, "booleanValue");
      add(Boolean.class, "valueOf", boolean.class);
      add(String.class, "toString");
      add(String.class, "intern");
      add(Math.class, "ulp", float.class);
      add(Math.class, "ulp", double.class);
      add(Math.class, "random");
      add(Object.class, "wait");
      add(Object.class, "wait", long.class, int.class);
      add(Object.class, "wait", long.class);
      add(Object.class, "notify");
      add(Object.class, "notifyAll");

      add(Math.class, "nextAfter", float.class, double.class);
      add(Math.class, "nextAfter", double.class, double.class);
      add(Math.class, "copySign", float.class, float.class);
      add(Math.class, "copySign", double.class, double.class);
      add(Math.class, "nextUp", double.class);
      add(Math.class, "nextUp", float.class);
      Debug.println("Finished initializing Blacklist.");
    } catch (Exception e) {
      throw new Error(e);
    }
  }

  public static void add(Class<?> clazz, String method, Class<?>... args) {
    try {
      add(clazz, clazz.getMethod(method, args));
    } catch (Exception e) {
      Debug.println("   Error finding " + method + "(" + StringUtil.join(args, "getSimpleName", ",")
          + ") on " + clazz.getSimpleName() + " error: " + e.getClass().getSimpleName());
    }
  }

  public static ArrayList<Method> getMethods(Class<?> clazz) {
    ArrayList<Method> list = blacklist.get(clazz);
    return list == null ? new ArrayList<Method>() : list;
  }

  private static void add(Class<?> clazz, Method m) {
    Debug.checkNotNull(m, "method");
    ArrayList<Method> list = blacklist.get(clazz);
    if (list == null) {
      list = new ArrayList<Method>();
      blacklist.put(clazz, list);
    }
    list.add(m);
  }

  public static boolean isListed(Class<?> clazz, Method m) {
    ArrayList<Method> list = blacklist.get(clazz);
    ArrayList<Method> listObj = blacklist.get(Object.class);
    if (listObj.contains(m))
      return true;
    if (list == null)
      return false;
    return list.contains(m);
  }

  public static ArrayList<Method> filterBlackListed(Class<?> valueType, ArrayList<Method> list) {
    ArrayList<Method> meths = new ArrayList<Method>();
    for (Method m : list) {
      if (BlackList.isListed(valueType, m)) {
        Debug.println("Listed " + m);
      } else {
        meths.add(m);
      }
    }
    return meths;
  }

  public static void addAllowedMethodsFilter(Class<?> targetClass, String[] methodNames) {
    Method[] methods = targetClass.getMethods();
    for (Method method : methods) {
      boolean allowed = false;
      for (String name : methodNames) {
        if (method.getName().equals(name)) {
          allowed = true;
          break;
        }
      }
      if (!allowed) {
        // add to blacklist
        add(targetClass, method);
      }
    }
  }

}
