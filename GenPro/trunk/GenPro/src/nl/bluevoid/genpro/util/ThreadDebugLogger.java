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

import java.util.ArrayList;
import java.util.List;

/**
 * Using ThreadLocal to simplify debug logging Other applications for ThreadLocal in which pooling would not
 * be a useful alternative include storing or accumulating per-thread context information for later retrieval.
 * For example, suppose you wanted to create a facility for managing debugging information in a multithreaded
 * application. You could accumulate debugging information in a thread-local container using this class. At
 * the beginning of a unit of work, you empty the container, and when an error occurs, you query the container
 * to retrieve all the debugging information that has been generated so far by this unit of work.
 * 
 * Throughout your code, you can call ThreadDebugLogger.put(), saving information about what your program is doing,
 * and you can easily retrieve the debugging information relevant to a particular thread later when necessary
 * (such as when an error has occurred). This technique is a lot more convenient and efficient than simply
 * dumping everything to a log file and then trying to sort out which log records come from which thread (and
 * worrying about contention for the logging object between threads.)
 * 
 * @author Brian Goetz
 * 
 */
public class ThreadDebugLogger {
  private static class ThreadLocalList extends ThreadLocal<ArrayList<String>> {
    public ArrayList<String> initialValue() {
      return new ArrayList<String>();
    }

    public List<String> getList() {
      return (List<String>) super.get();
    }
  }

  private ThreadLocalList list = new ThreadLocalList();
  private static String[] stringArray = new String[0];

  public void clear() {
    list.getList().clear();
  }

  public void put(String text) {
    list.getList().add(text);
  }

  public String[] get() {
    return list.getList().toArray(stringArray);
  }
}
