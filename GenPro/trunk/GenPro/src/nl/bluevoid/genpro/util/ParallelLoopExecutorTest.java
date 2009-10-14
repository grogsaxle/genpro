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

import java.util.Arrays;

import junit.framework.TestCase;

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
