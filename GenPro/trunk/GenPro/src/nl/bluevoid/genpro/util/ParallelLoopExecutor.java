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

public abstract class ParallelLoopExecutor implements Runnable {

  public enum Scheduling {
    /**
     * assigns every thread an equal part of the work
     */
    STATIC_SCHEDULING,

    /**
     * assigns every thread a part of the work defined by threadWorkLoadSize,
     */
    SELF_SCHEDULING,

    /**
     * assigns every thread a smaller getting part of the work with a minimum of 5 %
     */
    DYNAMIC_SCHEDULING;
  }

  protected final Thread lookupThreads[];
  protected final int startLoop, endLoop, numThreads;
  protected int curLoop;
  protected int groupSize;

  protected Scheduling schedulingMode = Scheduling.STATIC_SCHEDULING;
  protected static boolean continuRunning = true;

  public ParallelLoopExecutor(int start, int end, int threads) {
    if (threads < 1)
      throw new IllegalArgumentException("Thread number to small:" + threads);
    if (end <= start)
      throw new IllegalArgumentException("End (" + end + ") cannot be smaller or equal to start(" + start
          + ")");
    startLoop = curLoop = start;
    endLoop = end;
    numThreads = threads;
    lookupThreads = new Thread[numThreads];
  }

  public ParallelLoopExecutor(int start, int end, int threads, Scheduling schedulingMode) {
    this(start, end, threads);
    this.schedulingMode = schedulingMode;
  }

  public ParallelLoopExecutor(int start, int end, int threads, int threadWorkLoadSize) {
    this(start, end, threads);
    if (threadWorkLoadSize < 1)
      throw new IllegalArgumentException("ThreadWorkLoadSize to small:" + threadWorkLoadSize);
    schedulingMode = Scheduling.SELF_SCHEDULING;
    groupSize = threadWorkLoadSize;
  }

  public void run() {
    LoopRange str;
    while (continuRunning && (str = loopGetRange()) != null) {
      loopDoRange(str.start, str.end);
    }
  }

  /**
   * implement this method for your job, check for continuRunning in your loop to
   * 
   * @param start
   * @param end
   */
  public abstract void loopDoRange(int start, int end);

  /**
   * calling this will stop assiging new work to threads
   */
  protected void stopAllThreads() {
    continuRunning = false;
  }

  protected synchronized LoopRange loopGetRange() {
    if (curLoop >= endLoop)
      return null;

    final int start = curLoop;

    switch (schedulingMode) {
    case STATIC_SCHEDULING:
      curLoop += (endLoop - startLoop) / numThreads + 1;
      break;
    case SELF_SCHEDULING:
      curLoop += groupSize;
      break;
    case DYNAMIC_SCHEDULING:
      final int minimum = (endLoop - startLoop) / 25;
      int step = (endLoop - curLoop) / 9;
      step = Math.max(step, minimum);// take biggest, assures minimum
      curLoop += step;
      break;
    default:
      throw new IllegalStateException("Unsupported mode:" + schedulingMode);
    }
    final int end = (curLoop < endLoop) ? curLoop : endLoop;

    return new LoopRange(start, end);
  }

  /**
   * Call this method to start the threads doing the job defined in loopDoRange
   * 
   * @throws InterruptedException
   */
  public void excuteParallelLoops() throws InterruptedException {
    for (int i = 0; i < numThreads; i++) {
      lookupThreads[i] = new Thread(this, "parallelThread " + i);
      lookupThreads[i].start();
    }
    for (int i = 0; i < numThreads; i++) {
      lookupThreads[i].join();
    }
  }
}

class LoopRange {
  public final int start, end;

  public LoopRange(int start, int end) {
    this.start = start;
    this.end = end;
  }
}
