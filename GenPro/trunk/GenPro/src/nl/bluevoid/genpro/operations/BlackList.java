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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import nl.bluevoid.genpro.util.Debug;
import nl.bluevoid.genpro.util.StringUtil;
/**
 * @author Rob van der Veer
 * @since 1.0
 */
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
      add(Double.class, "hashCode");
      
      add(Integer.class, "intValue");
      add(Integer.class, "valueOf", int.class);
      add(Integer.class, "hashCode");
      
      add(Float.class, "floatValue");
      add(Float.class, "valueOf", float.class);
      add(Float.class, "hashCode");
      
      add(Byte.class, "byteValue");
      add(Byte.class, "valueOf", byte.class);
      add(Byte.class, "hashCode");
      
      add(Long.class, "longValue");
      add(Long.class, "valueOf", long.class);
      add(Long.class, "hashCode");
      
      add(Short.class, "shortValue");
      add(Short.class, "valueOf", short.class);
      add(Short.class, "hashCode");
      
      add(Boolean.class, "booleanValue");
      add(Boolean.class, "valueOf", boolean.class);
      add(Boolean.class, "hashCode");
      
      add(String.class, "toString");
      add(String.class, "intern");
      add(String.class, "hashCode");
      add(String.class, "offsetByCodePoints", int.class, int.class);
      add(String.class, "codePointBefore", int.class);
      add(String.class, "codePointAt", int.class);
      add(String.class, "codePointCount", int.class, int.class);
      
      add(Object.class, "wait");
      add(Object.class, "wait", long.class, int.class);
      add(Object.class, "wait", long.class);
      add(Object.class, "notify");
      add(Object.class, "notifyAll");
      add(Object.class, "hashCode");
      
      add(Math.class, "ulp", float.class);
      add(Math.class, "ulp", double.class);
      add(Math.class, "random");
      add(Math.class, "nextAfter", float.class, double.class);
      add(Math.class, "nextAfter", double.class, double.class);
      add(Math.class, "copySign", float.class, float.class);
      add(Math.class, "copySign", double.class, double.class);
      add(Math.class, "nextUp", double.class);
      add(Math.class, "nextUp", float.class);
      add(Math.class, "IEEEremainder", double.class, double.class);
      
      Debug.println("Finished initializing Blacklist.");
    } catch (Exception e) {
      throw new Error(e);
    }
  }

  public static void add(Class<?> clazz, String method, Class<?>... args) {
    try {
      add(clazz, clazz.getMethod(method, args));
    } catch (Exception e) {
      Debug.printErrln("   Error finding " + method + "(" + StringUtil.join(args, "getSimpleName", ",")
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
