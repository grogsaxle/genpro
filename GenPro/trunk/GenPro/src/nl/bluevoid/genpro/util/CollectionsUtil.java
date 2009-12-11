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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author Rob van der Veer
 * @since 1.0
 */
public class CollectionsUtil {

  public static <T> void addAll(Collection<T> c, T[] objs) {
    for (T object : objs) {
      c.add(object);
    }
  }

  public static <K, V> V getOrCreate(Map<K, V> map, K key, Class<V> clazz) throws InstantiationException,
      IllegalAccessException {
    V value = map.get(key);
    if (value == null) {
      value = clazz.newInstance();
      map.put(key, value);
    }
    return value;
  }

  public static ArrayList<String> toStringEntries(Map<?, ?> map) {
    ArrayList<String> str = new ArrayList<String>();
    for (Entry<?, ?> e : map.entrySet()) {
      str.add(e.getKey().toString() + " : " + e.getValue().toString());
    }
    return str;
  }

}
