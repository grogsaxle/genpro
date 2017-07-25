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
/** 
 * @author Rob van der Veer
 * @since 1.0
 */
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
