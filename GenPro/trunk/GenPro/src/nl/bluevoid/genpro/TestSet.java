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

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import nl.bluevoid.genpro.cell.ReferenceCell;
import nl.bluevoid.genpro.util.Calc;
import nl.bluevoid.genpro.util.Debug;
import nl.bluevoid.genpro.util.FileUtil;
import nl.bluevoid.genpro.util.Sneak;
import nl.bluevoid.genpro.util.StringUtil;
/**
 * @author Rob van der Veer
 * @since 1.0
 */
public abstract class TestSet implements Cloneable {

  public static final String SKIP_DATA_COLUMN = "SKIP_DATA_COLUMN";

  private String[] cellNames;
  private String[] inputCellNames;

  public String[] getInputCellNames() {
    return inputCellNames;
  }

  public String[] getOutputCellNames() {
    return outputCellNames;
  }

  private String[] outputCellNames;

  private ConcurrentMap<String, ArrayList<Object>> values = new ConcurrentHashMap<String, ArrayList<Object>>();

  private int numValues = 0;
  private final Setup setup;
  private static final int letterWrongpenalty = 1;

  private int evalTillTestSetNr = -1;
  private ScoringType scoringType = ScoringType.SCORING_AVARAGE_PER_TESTCASE;

  public boolean increaseIncrementalInvolvement() {
    if (evalTillTestSetNr >= numValues) {
      return false;
    } else {
      evalTillTestSetNr++;
      return true;
    }
  }

  public TestSet(Setup setup, String... cellNames) {
    Debug.checkNotNull(setup, "setup");
    this.setup = setup;
    this.cellNames = cellNames;
    ArrayList<String> in = new ArrayList<String>();
    ArrayList<String> out = new ArrayList<String>();
    // split in and outputs
    for (String celName : cellNames) {
      values.put(celName, new ArrayList<Object>());
      if (setup.isInputCell(celName)) {
        in.add(celName);
      } else {
        out.add(celName);
      }
    }
    inputCellNames = in.toArray(new String[in.size()]);
    outputCellNames = out.toArray(new String[out.size()]);
  }

  public void addCellValues(final Object... objects) {
    System.out.println("adding values to testset: " + StringUtil.join(", ", cellNames) + " : "
        + StringUtil.join(", ", objects));
    Debug.errorOnFalse(cellNames.length == objects.length, "expected " + cellNames.length
        + " objects, but received " + objects.length);
    for (int i = 0; i < cellNames.length; i++) {
      // final Class<?> cellType = setup.getInOrOutPutCellType(cellNames[i]);

      ArrayList<Object> arr = values.get(cellNames[i]);
      arr.add(objects[i]);
    }
    numValues++;
    evalTillTestSetNr = numValues;
  }
  
  public ArrayList<Object> getCellValues(String name){
    return values.get(name);
  }

  public void addCellValues(final Object[] objects, String[] columns) {
    System.out.println("adding values to testset: " + StringUtil.join(", ", cellNames) + " : "
        + StringUtil.join(", ", objects));
    Debug.errorOnFalse(cellNames.length == objects.length, "expected " + cellNames.length
        + " objects, but received " + objects.length);
    for (int i = 0; i < columns.length; i++) {
      // final Class<?> cellType = setup.getInOrOutPutCellType(cellNames[i]);
      ArrayList<Object> arr = values.get(columns[i]);
      arr.add(objects[i]);
    }
    numValues++;
    evalTillTestSetNr = numValues;
  }

  public TestSetStatistics getDeviations(final Grid grid) {
    TestSetStatistics tss = new TestSetStatistics(this, grid);
    grid.resetGridExecutionErrors();
    grid.resetCellCallCounters();

    for (int valueNr = 0; valueNr < numValues; valueNr++) {
      // set inputs
      for (final String name : inputCellNames) {
        grid.getInputCell(name).setValue(getValue(name, valueNr));
      }
      grid.calc();

      // read outputs && store values in "ACTUAL"
      for (final String name : outputCellNames) {
        final ReferenceCell ocell = grid.getOutputCell(name);
        tss.setActualOutputValue(name, valueNr, ocell.getValue());
      }

    }
    return tss;
  }

  public double evaluate(final Grid grid) {
    // System.out.println("Evaluating:"+grid.toString());
    grid.resetGridExecutionErrors();
    double score = 0;

    for (int valueNr = 0; valueNr < evalTillTestSetNr; valueNr++) {
      // set inputs
      for (final String name : inputCellNames) {
        grid.getInputCell(name).setValue(getValue(name, valueNr));
        // Debug.println("input "+name+" set to:"+grid.getInputCell(name));
      }

      // TODO why do we need this????
      // het zorgt ervoor dat calc maar op 1 thread tegelijk wordt aangeroepen
      // als calc wordt uitgevoerd samen met een andere thread die iets in evaluate doet, dan gaat het mis.
      // Wat wordt er dan in calc gedeeld met andere grids???
      // niet inputs en outputs
      // wel static library objecten
      // niet constanten

      // synchronized (this) {
      // do calc

      grid.calc();

      double testcaseScore = 0;
      // read outputs && count delta
      for (final String name : outputCellNames) {
        final ReferenceCell ocell = grid.getOutputCell(name);
        if (ocell.getValue() == null) {
          testcaseScore += 200; // TODO beter way of scoring
        } else {
          testcaseScore += scoreOutput(ocell, ocell.getValue(), getValue(name, valueNr));
        }
      }
      switch (scoringType) {
      case SCORING_HIGHEST_OF_TESTCASES:
      case SCORING_HIGHEST_PERCENTAGE_OF_TESTCASES:
        score = Math.max(testcaseScore, score);
        break;
      case SCORING_AVARAGE_PER_TESTCASE:
      case SCORING_AVARAGE_PERCENTAGE_PER_TESTCASE:
        score += testcaseScore;
        break;
      default:
        throw new IllegalArgumentException("scoring type not supported:" + scoringType);
      }

      if (Calc.isNaNorInfinite(score)) {
        return score;
      }
    }
    score += grid.getGridExecutionErrors().size() * 5000;// TODO 5000???
    switch (scoringType) {
    case SCORING_HIGHEST_OF_TESTCASES:
    case SCORING_HIGHEST_PERCENTAGE_OF_TESTCASES:
      return score + scoreGrid(grid);
    case SCORING_AVARAGE_PER_TESTCASE:
    case SCORING_AVARAGE_PERCENTAGE_PER_TESTCASE:
      return score / evalTillTestSetNr + scoreGrid(grid);
    default:
      throw new IllegalArgumentException("scoring type not supported:" + scoringType);
    }
  }

  public abstract double scoreOutput(ReferenceCell outputCell, Object calculated, Object expected);

  public abstract double scoreGrid(Grid g);

  public double getAbsoluteNumberDifference(final Number calculated, final Number expected) {
    if (calculated == null)
      return Math.abs( expected.doubleValue());
    return Math.abs(calculated.doubleValue() - expected.doubleValue());
  }

  public double getAbsoluteNumberDifference(final Number calculated, final NumberFeedback expected) {

    double calced = calculated.doubleValue();
    double expect = expected.value;
    if (expected.directive == NumberFeedback.HIGHER && calced < expect) {
      // System.out.println("HIGHER "+td.temperature+" calced "+temp);
      return getAbsoluteNumberDifference(calced, expect);
    } else if (expected.directive == NumberFeedback.LOWER && calced > expect) {
      // System.out.println("LOWER "+td.temperature+" calced "+temp);
      return getAbsoluteNumberDifference(calced, expect);
    } else
      // System.out.println(temp + " " + td);
      return 0;
  }

  public double getAbsoluteNumberDifferencePercentage(final Number calculated, final Number expected) {
    if (calculated == null)
      return 100;
    final double diff = Math.abs(calculated.doubleValue() - expected.doubleValue());
    return (diff / expected.doubleValue()) * 100;
  }

  private Object getValue(final String name, final int i) {
    return values.get(name).get(i);
  }

  @Override
  public TestSet clone() {
    try {
      final TestSet clone = (TestSet) super.clone();
      clone.cellNames = (String[]) Util.clone(cellNames);
      clone.inputCellNames = (String[]) Util.clone(inputCellNames);
      clone.outputCellNames = (String[]) Util.clone(outputCellNames);

      clone.values = new ConcurrentHashMap<String, ArrayList<Object>>();

      for (final String key : values.keySet()) {
        final ArrayList<Object> obj = values.get(key);
        // ArrayList<Object> objList=new ArrayList<Object>();
        clone.values.put(key, obj);
      }
      return clone;
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
      throw new IllegalStateException(e.getMessage());
    }
  }

  protected double getStringDifference(String calculated, String expected) {
    // make sure there is a gradual score improvement
    int penalty = 0;
    if (calculated == null) {
      penalty = expected.length() * letterWrongpenalty;
    } else {
      // mogelijkheden:
      // 1 lettervergelijken op elke plek
      // 2
      penalty += Math.abs(calculated.length() - expected.length());
      for (int i = 0; i < expected.length() && i < calculated.length(); i++) {
        if (expected.charAt(i) != calculated.charAt(i)) {
          penalty += letterWrongpenalty;
        }
      }
    }
    return penalty;
  }

  public void addCellValuesFromFile(String fileName) {
    try {
      System.out.println("addCellValuesFromFile");
      String line = FileUtil.readFile(fileName);
      String[] lines = line.split("\n");
      for (String string : lines) {
        String stripped = string.trim();
        if (stripped.startsWith("#") || stripped.startsWith("//") || stripped.length() == 0) {
          // skip
        } else {
          // process
          String[] split = stripped.split(",");
          Double[] ds = new Double[split.length];// TODO typing: strings, int etc
          for (int i = 0; i < split.length; i++) {
            ds[i] = Double.parseDouble(split[i]);
          }
          addCellValues((Object[]) ds);
        }
      }
    } catch (IOException e) {
      Sneak.sneakyThrow(e);
    }
  }

  public void addCellValuesFromFile(String fileName, final String... columns) {

    // create list of valid names (skip SKIP_DATA_COLUMN)
    final ArrayList<String> validColumNames = new ArrayList<String>();
    for (String name : columns) {
      if (!name.equals(SKIP_DATA_COLUMN)) {
        validColumNames.add(name);
      }
    }
    String[] validColumns = validColumNames.toArray(new String[0]);

    try {
      System.out.println("addCellValuesFromFile");
      String line = FileUtil.readFile(fileName);
      String[] lines = line.split("\n");
      for (String string : lines) {
        String stripped = string.trim();
        if (stripped.startsWith("#") || stripped.startsWith("//") || stripped.length() == 0) {
          // skip
        } else {
          // process
          String[] split = stripped.split(",");
          Double[] ds = new Double[validColumns.length];// TODO typing: strings, int etc
          int validCount = 0;
          for (int i = 0; i < split.length; i++) {
            // copy only data which is valid
            if (!columns[i].equals(SKIP_DATA_COLUMN)) {
              ds[validCount] = Double.parseDouble(split[i]);
              validCount++;
            }
          }
          // add valid data with valid columnnames
          addCellValues((Object[]) ds, validColumns);
        }
      }
    } catch (IOException e) {
      Sneak.sneakyThrow(e);
    }
  }

  public ConcurrentMap<String, ArrayList<Object>> getValues() {
    return values;
  }

  public int getNumValues() {
    return numValues;
  }

  public Setup getSetup() {
    return setup;
  }

  public int getEvalTillTestSetNr() {
    return evalTillTestSetNr;
  }

  /**
   * default = SCORING_AVARAGE_PER_TESTCASE
   * 
   * @param scoringType
   */
  public void setScoringType(ScoringType scoringType) {
    this.scoringType = scoringType;
  }

  public ScoringType getScoringType() {
    return scoringType;
  }

}
