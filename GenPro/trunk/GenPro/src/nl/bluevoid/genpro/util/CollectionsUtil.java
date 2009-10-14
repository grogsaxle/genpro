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

import java.util.Collection;
import java.util.Map;

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
}
