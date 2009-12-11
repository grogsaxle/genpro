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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import nl.bluevoid.genpro.util.Debug;
import nl.bluevoid.genpro.util.FileUtil;
import nl.bluevoid.genpro.util.Sneak;

/**
 * @author Rob van der Veer
 * @since 1.0
 */
public abstract class Trainer implements ResultListener {

  //protected final TestSet testSet;
  protected final Setup setup;
  protected GenerationRunner generationRunner;

  protected ArrayList<ResultListener> listeners = new ArrayList<ResultListener>();
  protected TestSetSolutionEvaluator evaluator;

  public Trainer() {
    setup = createSetup();
    evaluator = createEvaluator();
    addResultListener(this);
  }

  public abstract Setup createSetup();

  public abstract TestSetSolutionEvaluator createEvaluator();

  public void startTraining() {
    setup.setName(getClass().getSimpleName());
    generationRunner = new GenerationRunner(setup, evaluator);
    for (ResultListener list : listeners) {
      generationRunner.addResultListener(list);
    }
    generationRunner.runGenerations();
  }

  public void stopTraining() {
    generationRunner.stopRunning();
  }

  public void addResultListener(ResultListener listener) {
    listeners.add(listener);
  }

  public Setup getSetup() {
    return setup;
  }

  // @Override //from interface, does only work in 1.6 and up
  public void newBestResult(Grid bestSolution) {
    printAndStoreToFile(bestSolution, false);
    printBestSolution(bestSolution);
  }

  public void newStats(int generationnr, long millisPerGeneration) {
    // do nothing
  }

  public void startUpProgress(int createdIndividuals) {
    // do nothing
  }

  protected void printBestSolution(Grid bestSolution) {
    String result = getResultsAsString(bestSolution);
    System.out.println(result);
    System.out.println(JavaMethodGenerator.getJavaProgram(bestSolution, setup.getName(), "nl.bluevoid.gp",
        setup.getSolutionInterface(), "best_" + bestSolution.getScore(), setup.isDebugInfoVisible(), setup
            .isJunkDnaShown()));
  }

  public String getResultsAsString(Grid bestSolution) {
    return getStatistics(bestSolution).getResults();
  }

  public TestSetStatistics getStatistics(Grid bestSolution) {
    return evaluator.getDeviations(bestSolution);
  }

  public void printAndStoreToFile(Grid grid, boolean alsoStripped) {
    // do we need to store?
    if (grid.getScore() > setup.getMinimumScoreForSaving())
      return;

    String java = getJava(grid);
    String xml = grid.getXML();
    TestSetStatistics results = getStatistics(grid);
    StringBuffer buffer = new StringBuffer();
    buffer.append("<!--\n");

    buffer.append(results.getResults());
    buffer.append("\n\n");
    buffer.append(java);
    buffer.append("\n\n");
    Grid stripped = grid.clone();
    stripped.stripUnusedCells();
    stripped.setScore(grid.getScore());

    String javaStripped = JavaMethodGenerator.getJavaProgram(stripped, setup.getName(), "nl.bluevoid.gp",
        setup.getSolutionInterface(), "best_stripped " + stripped.getScore(), setup.isDebugInfoVisible(),
        setup.isJunkDnaShown());
    buffer.append(javaStripped);
    buffer.append("\n-->\n\n");

    buffer.append(xml);
    String data = buffer.toString();

    System.out.println(data);
    String dir = setup.getName() + evaluator.getScoringType();
    File dirs = new File(dir);
    File f = new File(dir + "/" + setup.getName() + "_" + grid.getScore() + ".xml");
    Debug.println("Storing solution in " + f.getAbsolutePath());
    try {
      if (!dirs.exists()) {
        boolean succeded = dirs.mkdirs();
        if (!succeded)
          throw new IOException("Unable to create directories for:" + dirs.getAbsolutePath());
      }
      FileUtil.storeBufferInFile(data, f);
    } catch (IOException e) {
      Sneak.sneakyThrow(e);
    }
  }

  public String getJava(Grid grid) {
    String java = JavaMethodGenerator.getJavaProgram(grid, setup.getName(), "nl.bluevoid.genpro", setup
        .getSolutionInterface(), "best " + grid.getScore(), setup.isDebugInfoVisible(), setup
        .isJunkDnaShown());
    return java;
  }
}