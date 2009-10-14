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

package nl.bluevoid.genpro.cell.test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;

import nl.bluevoid.genpro.NoCallTarget;
import nl.bluevoid.genpro.cell.LibraryCell;
import nl.bluevoid.genpro.operations.NumberOperations;
import junit.framework.TestCase;


public class LibAndAnnotationTest extends TestCase{
  
  public void testAnnotation() {
    LibraryCell num=NumberOperations.NUM_OPS;
    ArrayList<Method> meths=num.getAllMethods();
    for (Method method : meths) {
      System.out.println(""+method);
      assertFalse(method.isAnnotationPresent(NoCallTarget.class));
      for(Annotation a:method.getAnnotations()){
        System.out.println("  "+a);
      }
    }
  }

}
