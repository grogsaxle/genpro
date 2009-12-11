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

import java.util.HashMap;
import java.util.Random;

import nl.bluevoid.genpro.cell.ConstantCell;
import nl.bluevoid.genpro.cell.LibraryCell;
import nl.bluevoid.genpro.operations.BlackList;
import nl.bluevoid.genpro.util.Debug;

/**
 * @author Rob van der Veer
 * @since 1.0
 */
public class Setup {

  private static final Random random = new Random();

  final HashMap<String, Class<?>> outputCellDataMap = new HashMap<String, Class<?>>();
  final HashMap<String, Class<?>> inputCellDataMap = new HashMap<String, Class<?>>();

  private String callCellNamePrefix = "c";
  private int callCellNumber = 0;
  private int switchCellNr = 0;
  private Class<?>[] callCellValueTypes = new Class[0];
  private Class<?>[] switchCellValueTypes = new Class[0];

  private ConstantCell[] constantCells = new ConstantCell[0];
  private LibraryCell[] libraryCells = new LibraryCell[0];

  private int generationSize = 1000;

  private int mutatePercentage = 20;
  private int crossingPercentage = 80;

  private boolean evaluateMultiThreaded = false;

  private boolean hasMaxperScore = true;
  private int maxPerScore = 30;

  private String name = "unnamed";

  // disable auto storing
  private double minimumScoreForSaving = Double.NEGATIVE_INFINITY;

  private int stopAtGeneration = -1;
  private double stopAtScore = -1;

  private Class<?> solutionInterface;

  private boolean gridHistoryTracking = false;

  private boolean junkDnaShown = false;

  private boolean debugInfoVisible = false;

  public boolean isGridHistoryTrackingOn() {
    return gridHistoryTracking;
  }

  public Class<?> getSolutionInterface() {
    return solutionInterface;
  }

  public int getStopAtGeneration() {
    return stopAtGeneration;
  }

  public double getStopAtScore() {
    return stopAtScore;
  }

  public Setup(String name) {
    this.setName(name);

  }

  public Setup() {
  }

  // public Setup(Object creator) {
  // this(creator.getClass().getSimpleName());
  // }

  public void setCallCells(int number, String namePrefix, Class<?>... classes) {
    // Debug.errorOnFalse(number > 0, "callcell number must be 1 or more!");
    Debug.checkNotNull(namePrefix, "namePrefix");
    Debug.checkNotNull(classes, "classes");

    // we store the info to make them later
    this.callCellNumber = number;
    this.callCellNamePrefix = namePrefix;
    this.callCellValueTypes = classes;
  }

  public Class<?> getRandomCallCellType() {
    return callCellValueTypes[random.nextInt(callCellValueTypes.length)];
  }

  public Class<?> getRandomCallCellTypeForSwitch() {
    return switchCellValueTypes[random.nextInt(switchCellValueTypes.length)];
  }

  public void addOutputCell(String name, Class<?> class1) {
    Object o = outputCellDataMap.put(name, class1);
    if (o != null)
      throw new IllegalArgumentException("Output with name " + name + " was added already");
  }

  public void addInputCell(String name, Class<?> class1) {
    Object o = inputCellDataMap.put(name, class1);
    if (o != null)
      throw new IllegalArgumentException("Input with name " + name + " was added already");
  }

  public Class<?> getInOrOutPutCellType(String name) {
    Class<?> type = outputCellDataMap.get(name);
    if (type != null)
      return type;

    type = inputCellDataMap.get(name);
    if (type != null)
      return type;

    throw new IllegalArgumentException("type not found for in/out cell with name:" + name);
  }

  public boolean isInputCell(String name) {
    return inputCellDataMap.get(name) != null;
  }

  public int getCallCellNumber() {
    return callCellNumber;
  }

  public String getCallCellNamePrefix() {
    return callCellNamePrefix;
  }

  public Class<?>[] getCallCellValueTypes() {
    return callCellValueTypes;
  }

  public void setConstantCells(ConstantCell... constantCells) {
    this.constantCells = constantCells;
  }

  public void setLibraryCells(LibraryCell... libraryCells) {
    if (this.libraryCells.length == 0) {
      this.libraryCells = libraryCells;
    }else{
      throw new IllegalStateException("Librarycell are set already!");
    }
  }

  public Grid generateSolution() {
    Grid grid = new Grid(this, (ConstantCell[]) Util.clone(constantCells), libraryCells);
    grid.createSolution();
    return grid;
  }

  public void setGenerationSize(int generationSize) {
    this.generationSize = generationSize;
  }

  public int getGenerationSize() {
    return generationSize;
  }

  public int getMutatePercentage() {
    return mutatePercentage;
  }

  public int getCrossingPercentage() {
    return crossingPercentage;
  }

  public void setCrossingPercentage(int percentage) {
    this.crossingPercentage = percentage;
  }

  /**
   * 
   * @param mutatePercentage
   *          between 0 and 100, default=15
   */
  public void setMutatePercentage(int mutatePercentage) {
    Debug.checkRange(mutatePercentage, 0, 100);
    this.mutatePercentage = mutatePercentage;
  }

  public boolean evaluateMultiThreaded() {
    return evaluateMultiThreaded;
  }

  public void setEvaluateMultiThreaded(boolean b) {
    evaluateMultiThreaded = b;
  }

  public boolean hasMaxPerScore() {
    return hasMaxperScore;
  }

  public int getMaxPerScore() {
    return maxPerScore;
  }

  public void setMaxIndividualsWithSameScore(int maxPerScore) {
    this.maxPerScore = maxPerScore;
    hasMaxperScore = true;
  }

  public String getName() {
    return name;
  }

  public void setMinimumScoreForSaving(double saveScore) {
    this.minimumScoreForSaving = saveScore;
  }

  public double getMinimumScoreForSaving() {
    return minimumScoreForSaving;
  }

  public void setStopAtGeneration(int stopAtGeneration) {
    this.stopAtGeneration = stopAtGeneration;

  }

  public void setStopAtScore(double stopAtScore) {
    this.stopAtScore = stopAtScore;
  }

  public void setSolutionInterface(Class<?> solutionInterface) {
    this.solutionInterface = solutionInterface;
  }

  /**
   * 
   * @param switchCellNr
   * @param switchTypes
   *          supported are Boolean and any Number subclass
   */
  public void setMaxSwitchCellNr(int switchCellNr, Class<? extends Comparable<?>>... switchTypes) {
    this.switchCellNr = switchCellNr;
    Debug.errorOnTrue(switchTypes.length == 0, "need minimum one switchtype");
    for (Class<? extends Comparable<?>> switchType : switchTypes) {
      if (Number.class.isAssignableFrom(switchType) || switchType.equals(Boolean.class)) {
        // ok
      } else {
        throw new IllegalArgumentException("type is not supported for switch: " + switchType.getName());
      }
    }
    this.switchCellValueTypes = switchTypes;
  }

  public int getMaxSwitchCellNr() {
    return switchCellNr;
  }

  public void addAllowedMethodsFilter(Class<?> targetClass, String... methodNames) {
    BlackList.addAllowedMethodsFilter(targetClass, methodNames);
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * allows to track the history (create mutate etc) of each solution
   * 
   * @param b
   *          default false
   */
  public void setGridHistoryTracking(boolean b) {
    gridHistoryTracking = b;
  }

  /**
   * 
   * @param junkDnaShown
   *          default false
   */
  public void setJunkDnaShown(boolean junkDnaShown) {
    this.junkDnaShown = junkDnaShown;
  }

  public boolean isJunkDnaShown() {
    return junkDnaShown;
  }

  /**
   * 
   * @param debugInfoVisible
   *          default false
   */
  public void setDebugInfoVisible(boolean debugInfoVisible) {
    this.debugInfoVisible = debugInfoVisible;
  }

  public boolean isDebugInfoVisible() {
    return debugInfoVisible;
  }
}
