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

package nl.bluevoid.genpro;

import java.util.ArrayList;

import nl.bluevoid.genpro.util.Calc;
import nl.bluevoid.genpro.util.Debug;

/**
 * @author Rob van der Veer
 * @since 1.0
 */
public class GenerationRunner extends Thread {

  private final Setup setup;
  //private final TestSet testSet;

  private double bestScore = Double.MAX_VALUE;
  Grid bestSolution = null;
  private Generation curGen;
  private long startTotal;
  private boolean stopRunning = false;

  ArrayList<ResultListener> resultListeners = new ArrayList<ResultListener>();
  private Generation newGen;
  private long lastStatsTime=0;
  private long statsInterval=10000;
  private final TestSetSolutionEvaluator evaluator;

  public GenerationRunner(Setup setup, TestSetSolutionEvaluator evaluator ) {
    this.setup = setup;
    //this.testSet = testSet;
    this.evaluator = evaluator;
  }

  public void stopRunning() {
    stopRunning = true;
    curGen.stopRunning();
    newGen.stopRunning();
  }

  public void runGenerations() {
    startTotal = System.currentTimeMillis();
    curGen = getCurGen();

    // create generations and evaluate

    final long maxGen = setup.getStopAtGeneration() == -1 ? Long.MAX_VALUE : setup.getStopAtGeneration();

    while (curGen.getNr() < maxGen) {
      newGen = curGen.next();
      evaluateGeneration(newGen);
      if (setup.getStopAtScore() != -1 && newGen.getBestSolution().getScore() < setup.getStopAtScore())
        break;
      curGen = newGen;
      if (stopRunning)
        break;
    }
  }

  public void evaluateGeneration(Generation newGen) {
    newGen.evaluate(evaluator);

    // process result of this generation
    if (newGen.getBestSolution().getScore() < bestScore) {
      bestSolution = newGen.getBestSolution();
      bestScore = bestSolution.getScore();
      notifyResultListenersOnNewBest();
    }

    final int genNr = newGen.getNr();
    final long avgMillisPerGeneration = (System.currentTimeMillis() - startTotal) / genNr;

    notifyResultListenersOnStats((int) genNr, avgMillisPerGeneration);
    final long timeNow=System.currentTimeMillis();
    // print statistics
    if ((timeNow-lastStatsTime)>statsInterval && genNr!=1) {
      lastStatsTime=timeNow;
      
      curGen.printChooseResult();
      Debug.println("gen:" + genNr + " time per gen:" + avgMillisPerGeneration
          + " millis  avg. score of top 80%:" + newGen.getAverageScore(0.8));
      // request garbagecollect
      System.gc();
      // give time to start gc
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      // not android compliant:
      // MemoryMXBean mxb = ManagementFactory.getMemoryMXBean();
      // long bytes = mxb.getHeapMemoryUsage().getUsed();
      // long maxBytes=mxb.getHeapMemoryUsage().getMax();
      long maxBytes = Runtime.getRuntime().totalMemory();
      long bytes = maxBytes - Runtime.getRuntime().freeMemory();
      float mbMax = Calc.truncDecimals(maxBytes / (1024f * 1024), 1);

      float mb = Calc.truncDecimals(bytes / (1024f * 1024), 1);
      Debug.println("Memory used: " + mb + " Mb" + " max:" + mbMax + " Mb");
    }
  }

  protected Generation createStartGeneration() {
    Generation gen = new Generation(setup);
    for (int i = 0; i < setup.getGenerationSize(); i++) {
      Grid grid = setup.generateSolution();

      gen.addSolution(grid);
      if(setup.isGridHistoryTrackingOn()){
        grid.addToHistory("Created as random solution in generation "+gen.getNr());
      }
      if (i % 10 == 0) {
        notifyResultListenersOnStartUp(i);
      }
    }
    notifyResultListenersOnStartUp(setup.getGenerationSize());
    return gen;
  }

  public Generation getCurGen() {
    if (curGen == null) {
      curGen = createStartGeneration();
      evaluateGeneration(curGen);
    }
    return curGen;
  }

  public void addResultListener(ResultListener r) {
    resultListeners.add(r);
  }

  private void notifyResultListenersOnNewBest() {
    for (ResultListener listener : resultListeners) {
      listener.newBestResult(bestSolution);
    }
  }

  private void notifyResultListenersOnStats(final int gen, final long millisAvarage) {
    for (ResultListener listener : resultListeners) {
      listener.newStats(gen, millisAvarage);
    }
  }

  private void notifyResultListenersOnStartUp(int individualsCreated) {
    for (ResultListener listener : resultListeners) {
      listener.startUpProgress(individualsCreated);
    }
  }
}