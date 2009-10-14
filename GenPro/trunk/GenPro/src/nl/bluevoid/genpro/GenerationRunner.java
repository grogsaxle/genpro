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

package nl.bluevoid.genpro;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.ArrayList;

import nl.bluevoid.genpro.util.Calc;
import nl.bluevoid.genpro.util.Debug;

public class GenerationRunner extends Thread {

  final Setup setup;
  final TestSet testSet;

  private double bestScore = Double.MAX_VALUE;
  Grid bestSolution = null;
  private Generation curGen;
  private long startTotal;
  private long genCounter;

  ArrayList<ResultListener> resultListeners = new ArrayList<ResultListener>();

  public GenerationRunner(Setup setup, TestSet testSet) {
    this.setup = setup;
    this.testSet = testSet;
  }

  public void runGenerations() {
    curGen = getCurGen();
    startTotal = System.currentTimeMillis();
    // create generations and evaluate
   
    final long maxGen = setup.getStopAtGeneration() == -1 ? Long.MAX_VALUE : setup.getStopAtGeneration();
    Generation newGen = null;
    for (genCounter = 0; genCounter < maxGen; genCounter++) {
      newGen = curGen.next();
      evaluateGeneration(newGen);
      if (setup.getStopAtScore() != -1 && newGen.getBestSolution().getScore() < setup.getStopAtScore())
        break;
      curGen = newGen;
    }
    Grid winner = newGen.getBestSolution();
  }

  public void evaluateGeneration(Generation newGen) {
    newGen.evaluate(testSet);

    // process result of this generation
    if (newGen.getBestSolution().getScore() < bestScore) {
      bestSolution = newGen.getBestSolution();
      bestScore = bestSolution.getScore();
      notifyResultListeners();
    }

    //print statistics 
    if (genCounter % 100 == 1) {
      final long end = System.currentTimeMillis();

      curGen.printChooseResult();
      Debug.println("gen:" + newGen.getNr() + " time per gen:" + (end - startTotal) / genCounter
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
      MemoryMXBean mxb = ManagementFactory.getMemoryMXBean();
      long bytes = mxb.getHeapMemoryUsage().getUsed();
      long maxBytes=mxb.getHeapMemoryUsage().getMax();
      float mbMax = Calc.truncDecimals(maxBytes / (1024f * 1024), 1);
      
      float mb = Calc.truncDecimals(bytes / (1024f * 1024), 1);
      Debug.println("Memory used: " + mb + " Mb"+ " max:"+ mbMax+" Mb");
    }
  }

  private Generation createStartGeneration() {
    Generation gen = new Generation(setup);
    for (int i = 0; i < setup.getGenerationSize(); i++) {
      Grid grid = setup.generateSolution();
      gen.addSolution(grid);
    }
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

  private void notifyResultListeners() {
    for (ResultListener listener : resultListeners) {
      listener.newBestResult(bestSolution);
    }
  }
}
