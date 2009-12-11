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

package nl.bluevoid.genpro.cell.test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;

import junit.framework.TestCase;
import nl.bluevoid.genpro.NoCallTarget;
import nl.bluevoid.genpro.cell.LibraryCell;
import nl.bluevoid.genpro.operations.NumberOperations;
/**
 * @author Rob van der Veer
 * @since 1.0
 */

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
