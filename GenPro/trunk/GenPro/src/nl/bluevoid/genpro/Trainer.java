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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import nl.bluevoid.genpro.util.Debug;
import nl.bluevoid.genpro.util.FileUtil;
import nl.bluevoid.genpro.util.Sneak;

public abstract class Trainer implements ResultListener {

  protected final TestSet testSet;
  protected final Setup setup;
  protected GenerationRunner generationRunner;

  ArrayList<ResultListener> listeners = new ArrayList<ResultListener>();

  public Trainer() {
    setup = createSetup();
    testSet = createTestSet();
    addResultListener(this);
  }

  public abstract Setup createSetup();

  public abstract TestSet createTestSet();

  public void startTraining() {
    generationRunner = new GenerationRunner(setup, testSet);
    for (ResultListener list : listeners) {
      generationRunner.addResultListener(list);
    }
    generationRunner.runGenerations();
  }

  public void addResultListener(ResultListener listener) {
    listeners.add(listener);
  }

  public TestSet getTestSet() {
    return testSet;
  }

  public Setup getSetup() {
    return setup;
  }

  @Override
  public void newBestResult(Grid bestSolution) {
    printAndStoreToFile(bestSolution, false);
    printBestSolution(bestSolution);
  }

  protected void printBestSolution(Grid bestSolution) {
    String result = getResultsAsString(bestSolution);
    System.out.println(result);
    JavaMethodGenerator.printJavaProgram(bestSolution, setup.getName(), "nl.bluevoid.gp", setup
        .getSolutionInterface(), "best_" + bestSolution.getScore(), false);
  }

  public String getResultsAsString(Grid bestSolution) {
    return getStatistics(bestSolution).getResults();
  }

  public TestSetStatistics getStatistics(Grid bestSolution) {
    return testSet.getDeviations(bestSolution);
  }
  
  protected void printAndStoreToFile(Grid grid, boolean alsoStripped) {
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
        setup.getSolutionInterface(), "best_stripped " + stripped.getScore(), false);
    buffer.append(javaStripped);
    buffer.append("\n-->\n\n");

    buffer.append(xml);
    String data = buffer.toString();

    System.out.println(data);
    String dir = setup.getName() + testSet.getScoringType();
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

  protected String getJava(Grid grid) {
    String java = JavaMethodGenerator.getJavaProgram(grid, setup.getName(), "nl.bluevoid.gp", setup
        .getSolutionInterface(), "best " + grid.getScore(), false);
    return java;
  }
}
