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

package nl.bluevoid.genpro.util.test;

import java.util.Arrays;

import junit.framework.TestCase;
import nl.bluevoid.genpro.util.ParallelLoopExecutor;
/**
 * @author Rob van der Veer
 * @since 1.0
 */
public class ParallelLoopExecutorTest extends TestCase {

  public void testOne() throws InterruptedException {
    System.out.println("testOne");
    final boolean[] b = makeIt();
    ParallelLoopExecutor ple = new ParallelLoopExecutor(0, b.length, 3, 913) {
      @Override
      public void loopDoRange(int start, int end) {
        doIt(b, start, end);
      }
    };
    ple.excuteParallelLoops();
    checkIt(b);
  }

  public void testTwo() throws InterruptedException {
    System.out.println("testTwo");
    final boolean[] b = makeIt();
    ParallelLoopExecutor ple = new ParallelLoopExecutor(0, b.length, 7) {
      @Override
      public void loopDoRange(int start, int end) {
        doIt(b, start, end);
      }
    };
    ple.excuteParallelLoops();
    checkIt(b);
  }

  public void testThree() throws InterruptedException {
    System.out.println("testThree");
    final boolean[] b = makeIt();
    ParallelLoopExecutor ple = new ParallelLoopExecutor(0, b.length, 7,
        ParallelLoopExecutor.Scheduling.DYNAMIC_SCHEDULING) {
      @Override
      public void loopDoRange(int start, int end) {
        doIt(b, start, end);
      }
    };
    ple.excuteParallelLoops();
    checkIt(b);
  }

  private boolean[] makeIt() {
    final boolean[] b = new boolean[999999];
    Arrays.fill(b, false);
    return b;
  }
  
  private void doIt(final boolean[] b, int start, int end) {
    for (int i = start; i < end; i++) {
      //for (int j = 0; j < 40; j++) {
        Math.cos(i);
        Math.exp(i);
        Math.cosh(i);
      //}
      b[i] = !b[i];// flip so with overlaps it turns false again
    }
    System.out.println("start " + start + " end " + end + " : " + (end - start));
  }

  private void checkIt(final boolean[] b) {
    for (int i = 0; i < b.length; i++) {
      // System.out.println("test "+i);
      assertTrue(b[i]);
    }
  }
}
