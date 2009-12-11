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
import java.util.Collections;
import java.util.Random;

import nl.bluevoid.genpro.cell.NoCellFoundException;
import nl.bluevoid.genpro.util.Calc;
import nl.bluevoid.genpro.util.Debug;
import nl.bluevoid.genpro.util.GewogenKansSelector;
import nl.bluevoid.genpro.util.ParallelLoopExecutor;
import nl.bluevoid.genpro.util.Sneak;

/**
 * @author Rob van der Veer
 * @since 1.0
 */
public class Generation {
  final ArrayList<Grid> solutions = new ArrayList<Grid>();
  final ArrayList<Grid> sortedSolutions = new ArrayList<Grid>();

  private final int nr;
  private Grid bestSolution;
  private double bestScore = Double.MAX_VALUE;
  private boolean stopRunning = false;
  public int nanAndInfinateCounter = 0;

  private final Setup setup;
  private GewogenKansSelector<Grid> gws;

  private boolean sorted = false;
  private boolean evaluated = false;

  private static Random random = new Random();

  public Generation(Setup setup) {
    this.setup = setup;
    nr = 1;
  }

  private Generation(int nr, Setup setup) {
    this.nr = nr;
    this.setup = setup;
  }

  public void addSolution(Grid s) {
    solutions.add(s);
  }

  public Generation next() {
    // printProgramsWithSameScore(4);
    gws = getSelector();
    // create next gen
    final Generation next = new Generation(nr + 1, setup);
    final String histPrefix = "Generation " + next.getNr() + ": ";
    // Debug.println("Adding best solution:" + bestSolution.getScore()+ " "+bestSolution.toString());
    // add best solution + mutated
    bestSolution.resetCellCallCounters();
    bestSolution.resetGridExecutionErrors();
    next.addSolution(bestSolution);

    // TODO beste herberekenen gaat fout in multithreading

    final Grid b2 = bestSolution.clone();
    b2.resetCellCallCounters();
    b2.resetGridExecutionErrors();
    try {
      b2.mutate(histPrefix);
      next.addSolution(b2);
    } catch (NoCellFoundException e) {
      // mutation failed, so skip
    }

    final int maxIndividuals = setup.getGenerationSize() - next.getSize();

    if (gws.size() < setup.getGenerationSize() / 2) {
      Debug.printErrln("gws.size():" + gws.size());
    }

    final String historyString = histPrefix + "Created by crossing";

    int added = 0;
    while (added < maxIndividuals) {
      final Grid s1 = gws.getRandomItem();
      final Grid s2 = gws.getRandomItem();

      final Grid g1 = s1.clone();
      final Grid g2 = s2.clone();
      final boolean cross = random.nextInt(100) < setup.getCrossingPercentage();
      final Grid[] s34 = cross ? g1.cross(g2, getNr()) : new Grid[] { g1, g2 };

      // grids might be null!!!!
      for (final Grid grid : s34) {
        if (grid != null) {
          try {
            if (cross && setup.isGridHistoryTrackingOn()) {
              grid.addToHistory(historyString);
            }
            if (!cross) {// TODO fix this: cross has a overhand on mutate!!
              final boolean mutate = random.nextInt(100) < setup.getMutatePercentage();
              if (mutate) {
                grid.mutate(histPrefix);
              }
            }
            next.addSolution(grid);

            added++;
          } catch (NoCellFoundException e) {
            // mutation failed, not added to generation next, so skip
          }
        }
      }
    }
    return next;
  }

  public void evaluate( TestSetSolutionEvaluator evaluator) {
    if (setup.evaluateMultiThreaded()) {
      evaluateMultiThreaded(evaluator);
      evaluated = true;// TODO not correct find end of threadruns
    } else {
      evaluateSingleThreaded(evaluator);
      evaluated = true;
    }
  }

  private void evaluateSingleThreaded(TestSetSolutionEvaluator evaluator) {
    for (Grid sol : getSolutions()) {
      try {
        evaluate(evaluator, sol);
        if (stopRunning)
          break;
      } catch (Throwable e) {
        Debug.printFullStackTrace(e);
        System.exit(0);
      }
    }
  }

  private void evaluateMultiThreaded(final TestSetSolutionEvaluator evaluator) {
    // create ThreadLocal so every thread gets a clone of testset, initialValue is called when get() is called
    final ThreadLocal<TestSetSolutionEvaluator> tl = new ThreadLocal<TestSetSolutionEvaluator>() {
      @Override
      public TestSetSolutionEvaluator initialValue() {
        return evaluator; //TODO do we need a clone here? .clone()
      }
    };

    // final ArrayList<Grid> grids = new ArrayList<Grid>();

    ParallelLoopExecutor ple = new ParallelLoopExecutor(0, getSolutions().size(), 2,
        ParallelLoopExecutor.Scheduling.DYNAMIC_SCHEDULING) {
      @Override
      public void loopDoRange(final int start, final int end) {
        // Debug.println("Starting execution from "+start+" to "+end);
        int i = start;
        Grid gr = null;
        try {
          final TestSetSolutionEvaluator localTestSet = tl.get();
          for (; i < end; i++) {
            // System.out.println("eval:" + i + " " + Thread.currentThread().getName());
            // System.out.flush();
            gr = solutions.get(i);
            // grids.add(gr);
            evaluate(localTestSet, gr);
            if (stopRunning)
              break;
            if (!continuRunning) {
              JavaGenerator.printJavaProgram(gr, "last " + i + " from " + Thread.currentThread().getName(),
                  "nl.bluevoid.gp", true);
              System.out.flush();
              break;
            }
          }
        } catch (Throwable e) {
          stopAllThreads();
          Debug.printFullStackTrace(e);
          JavaGenerator.printJavaProgram(gr, "fail " + i + " " + Thread.currentThread().getName(),
              "nl.bluevoid.gp", true);
          System.out.flush();
          System.exit(1);
        }
      }
    };

    try {
      ple.excuteParallelLoops();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void evaluate(final TestSetSolutionEvaluator evaluator, final Grid sol) {
    // Debug.println("score:" + score);
    try {
      final double score = evaluator.evaluate(sol);
      sol.setScore(score);

      if (Calc.isNaNorInfinite(score)) {
        nanAndInfinateCounter++;
      } else {
        synchronized (this) {
          if (score < bestScore) {
            bestScore = score;
            bestSolution = sol;
          }
        }
      }
    } catch (Throwable t) {
      System.err.println(JavaMethodGenerator.getJavaProgram(sol, "errorClass", "nl.bluevoid.gp", null, t
          .getMessage(), setup.isDebugInfoVisible(), setup.isJunkDnaShown()));
      Sneak.sneakyThrow(t);
    }
  }

  public void printChooseResult() {
    gws.printChooseResult();
  }

  private GewogenKansSelector<Grid> getSelector() {
    sortSolutions();
    // schaal naar 0-1 (fitness delen door max)
    final GewogenKansSelector<Grid> gws = new GewogenKansSelector<Grid>(setup);
    final int size = sortedSolutions.size();
    final int selectmax = size - Math.max(size / 100, 1);
    final double min = sortedSolutions.get(0).getScore();
    final double max = sortedSolutions.get(selectmax - 1).getScore();
    for (int i = 0; i < selectmax; i++) {
      final Grid s = sortedSolutions.get(i);
      final double score = s.getScore();
      final int gewicht = Math.max(1, scoreWeigth(score, min, max));
      gws.add(s, gewicht, score);
      // } else {
      // Debug.println(getNr()+" throwing away solution with score "+score +" & weight " +
      // gewicht+" min="+min+" max="+max);
      // }
    }
    return gws;
  }

  /**
   * 
   * @param score
   * @param min
   * @param max
   * @return 0 if score = max, TODO fix this!
   */
  private int scoreWeigth(final double score, final double min, final double max) {
    final double zeroto1 = (score - min) / (max - min);
    Debug.checkRange(zeroto1, 0, 1);
    return (int) Math.pow((100 - (zeroto1 * 100)), 2);
  }

  /**
   * sortedSolutions does NOT contain solutions with NAN AND INFINITE scores!!!!
   */
  private void sortSolutions() {
    if (!sorted) {
      if (!evaluated) {
        throw new IllegalStateException("generation is not evaluated yet, sorting is not possible");
      }
      sortedSolutions.clear();
      // remove bad ones for sorting
      for (Grid g : solutions) {
        if (Calc.isNaNorInfinite(g.getScore())) {
          // throw away result
        } else
          sortedSolutions.add(g);
      }
      Collections.sort(sortedSolutions);
      sorted = true;
    }
  }

  /**
   * Returns the avarage score from a part of the solutions, this takes out the varies high scores at the end
   * of the population and gives a general feel of how the generations are evolving
   * 
   * @param topPart
   *          number from 0 to 1 indicating which part you want to avarage, 0.8 is a good number to start with
   * @return
   */
  public double getAverageScore(double topPart) {
    Debug.checkRange(topPart, 0, 1);
    sortSolutions();
    // get maximum
    int max = (int) Math.min(topPart * setup.getGenerationSize(), sortedSolutions.size());
    double total = 0;
    for (int i = 0; i < max; i++) {
      total += sortedSolutions.get(i).getScore();
    }
    return total / max;
  }

  public ArrayList<Grid> getSolutions() {
    return solutions;
  }

  public int getNr() {
    return nr;
  }

  public Grid getBestSolution() {
    return bestSolution;
  }

  public int getSize() {
    return solutions.size();
  }

  public void stopRunning() {
    stopRunning = true;
  }
}