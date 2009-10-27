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

package nl.bluevoid.genpro.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import nl.bluevoid.genpro.NoCallTarget;
/**
 * @author Rob van der Veer
 * @since 1.0
 */
public class ReflectUtil {
  private static Random random = new Random(System.currentTimeMillis());

  public static ArrayList<Method> getAllMethods(final Class<?> clazz, final boolean staticOnly) {
    // Debug.println("getAllMethods called for:" + clazz + " staticOnly:" + staticOnly);
    final ArrayList<Method> ms = new ArrayList<Method>();
    final Method[] all = clazz.getMethods();
    for (final Method method : all) {
      final int mod = method.getModifiers();
      if (Modifier.isPublic(mod) && !Modifier.isAbstract(mod)) {
        if (staticOnly == Modifier.isStatic(mod)) {//select only static or only instance
          if (!method.isAnnotationPresent(NoCallTarget.class))
            ms.add(method);
        }
      }
    }
    return ms;
  }

  public static Method getRandomMethod(final ArrayList<Method> methods) {
    return methods.get(random.nextInt(methods.size()));
  }

  private static HashMap<Class<?>, Class<?>> map = new HashMap<Class<?>, Class<?>>();
  static {
    map.put(byte.class, Byte.class);
    map.put(short.class, Short.class);
    map.put(int.class, Integer.class);
    map.put(long.class, Long.class);
    map.put(float.class, Float.class);
    map.put(double.class, Double.class);
    map.put(char.class, Character.class);
    map.put(boolean.class, Boolean.class);
  }

  public static Class<?> getClassForPrimitive(final Class<?> clazz) {
    if (clazz.isPrimitive())
      return map.get(clazz);
    return clazz;
  }

  private static HashMap<Class<? extends Number>, Integer> typesMap = new HashMap<Class<? extends Number>, Integer>();
  static {
    
    typesMap.put(Byte.class, 2);
    typesMap.put(Short.class, 3);
    typesMap.put(Integer.class, 4);
    typesMap.put(Long.class, 5);
    typesMap.put(Float.class, 6);
    typesMap.put(Double.class, 7);
    typesMap.put(Number.class, 8);
  };

  public static Number castNumber(final Number number, final Class<? extends Number> numberClass) {
    if (numberClass.equals(Double.class) || numberClass.equals(Float.class)) {
      return numberClass.cast(number.doubleValue());
    } else {
      return numberClass.cast(number.longValue());
    }
  }

  public static boolean canCastNumber(final Class<?> from, final Class<?> to) {
    if (from.equals(to))
      return true;
    final Integer indexTo = typesMap.get(to);
    if (indexTo == null) {
      return false;
      // throw new IllegalArgumentException("Number not supported:" + to.getName());
    }
    final Integer indexFrom = typesMap.get(from);
    if (indexFrom == null)
      return false;

    return indexFrom <= indexTo;
    // if (indexFrom <= indexTo) {
    // // Debug.println(from.getSimpleName()+" can be cast (widened) to "+to.getSimpleName());
    // return true;
    // } else {
    // // Debug.println(from.getSimpleName()+" can NOT be cast (widened) to "+to.getSimpleName());
    // return false;
    // }
  }
}
