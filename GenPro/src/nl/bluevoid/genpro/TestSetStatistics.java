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
import java.util.Arrays;
import java.util.HashMap;

import nl.bluevoid.genpro.util.CollectionsUtil;
import nl.bluevoid.genpro.util.Debug;
import nl.bluevoid.genpro.util.SD;
import nl.bluevoid.genpro.util.StringUtil;

/**
 * @author Rob van der Veer
 * @since 1.0
 */
public class TestSetStatistics {

  public final static String ACTUAL = "_actual";
  public final static String EXPECTED = "_expected";
  public static final String DIFF = "_diff";
  public static final String DIFF_PERCENTAGE = "_diff%";
  private static final String TEST_CASE_SCORE = "score";

  private final TestSet testSet;
  private final HashMap<String, Object[]> results = new HashMap<String, Object[]>();
  private final Grid grid;
  private final TestSetSolutionEvaluator evaluator;

  public TestSetStatistics(final TestSet testSet, TestSetSolutionEvaluator evaluator, Grid grid) {
    this.evaluator = evaluator;
    this.grid = grid;
    Debug.checkNotNull(testSet, "testSet");
    Debug.checkNotNull(evaluator, "evaluator");
    this.testSet = testSet;
    // create arrays for output result: actual & expected
    for (final String name : testSet.getOutputCellNames()) {
      Object[] vals = testSet.getValues().get(name).toArray(new Object[0]);
      results.put(name + EXPECTED, vals);
      results.put(name + ACTUAL, new Object[vals.length]);
      results.put(name + DIFF, new Object[vals.length]);
      results.put(name + DIFF_PERCENTAGE, new Object[vals.length]);
      results.put(TEST_CASE_SCORE, new Double[vals.length]);
    }

    // create Arrays for inputs
    for (final String name : testSet.getInputCellNames()) {
      Object[] vals = testSet.getValues().get(name).toArray(new Object[0]);
      results.put(name, vals);
    }
  }

  public void setActualOutputValue(final String outputName, final int valueNr, final Object value) {
    results.get(outputName + ACTUAL)[valueNr] = value;
    Object[] diffs = results.get(outputName + DIFF);
    Object[] diffsPercentage = results.get(outputName + DIFF_PERCENTAGE);
    Object resultExpected = results.get(outputName + EXPECTED)[valueNr];
    if (value == null) {
      diffs[valueNr] = 0.0;
      diffsPercentage[valueNr] = 100.0;
    } else if (Number.class.isAssignableFrom(value.getClass())) {
      double actualValue = ((Number) value).doubleValue();
      if (resultExpected instanceof Number) {
        double expected = ((Number) resultExpected).doubleValue();
        double diff = actualValue - expected;
        diffs[valueNr] = diff;
        double diffPercentage = (diff / expected) * 100;
        diffsPercentage[valueNr] = diffPercentage;
      } else if (resultExpected instanceof NumberFeedback) {

        NumberFeedback feedback = (NumberFeedback) resultExpected;
        double expected = feedback.value;
        double diff = 0;
        switch (feedback.directive) {
        case NumberFeedback.HIGHER:
          if (actualValue < expected)
            diff = expected - actualValue;
          break;
        case NumberFeedback.LOWER:
          if (actualValue > expected)
            diff = actualValue - expected;
          break;
        default:
          throw new IllegalArgumentException("no support for: " + feedback.directive);
        }
        diffs[valueNr] = diff;
        double diffPercentage = (diff / expected) * 100;
        diffsPercentage[valueNr] = diffPercentage;
      } else {
        throw new IllegalArgumentException(" no support for: " + resultExpected.getClass().getName());
      }
    } else {
      diffs[valueNr] = "?";
      diffsPercentage[valueNr] = "?";
    }
  }

  public void setTestCaseScore(double testCaseScore, int valueNr) {
    results.get(TEST_CASE_SCORE)[valueNr] = testCaseScore;
  }

  public String getResults() {
    StringBuffer b = new StringBuffer();

    // collect all names
    ArrayList<String> names = new ArrayList<String>();
    CollectionsUtil.addAll(names, testSet.getInputCellNames());
    for (final String name : testSet.getOutputCellNames()) {
      names.add(name + EXPECTED);
      names.add(name + ACTUAL);
      names.add(name + DIFF);
      names.add(name + DIFF_PERCENTAGE);
    }
    names.add(TEST_CASE_SCORE);
    // print names
    for (String name : names) {
      b.append(StringUtil.assureLength(name, 15));
    }
    b.append("\n");
    // print values for each name
    for (int i = 0; i < testSet.getNumberOfTestCases(); i++) {
      for (String name : names) {
        Object o = results.get(name)[i];
        try {
          b.append(StringUtil.assureLength("" + o, 15));
        } catch (NullPointerException e) {
          System.out.println("error at " + name + " " + i);
          throw e;
        }
      }
      b.append("\n");
    }
    // print statistical data
    for (final String name : testSet.getOutputCellNames()) {
      Class<?> type = testSet.getSetup().getInOrOutPutCellType(name);
      if (Number.class.isAssignableFrom(type)) {
        Number[] diffs = getSortedNumberArray(name + DIFF);
        // get min/max diff
        double min = diffs[0].doubleValue();
        double max = diffs[diffs.length - 1].doubleValue();

        Number[] diffs_percentage = getSortedNumberArray(name + DIFF_PERCENTAGE);
        // get min/max diff
        double minPerc = diffs_percentage[0].doubleValue();
        double maxPerc = diffs_percentage[diffs.length - 1].doubleValue();
        // get stats
        final double mean = SD.mean(diffs);
        final double standardDeviation = SD.sdKnuth(diffs);
        b.append("\nstats of " + name + ":");
        b.append("\nMin diff%:" + minPerc + " max diff:" + maxPerc);
        b.append("\nMin diff:" + min + " max diff:" + max);
        b.append("\ndiff Mean:" + mean);
        b.append("  diff StandardDeviation:" + standardDeviation);
      }
    }
    b.append("\nGridscore:" + evaluator.scoreGrid(grid));
    return b.toString();
  }

  private Number[] getSortedNumberArray(final String diff) {
    Object[] objs = results.get(diff);
    //remove null values TODO fix later gives false stats
    ArrayList<Object> a = new ArrayList<Object>();
    for (Object object : objs) {
      if (object != null) {
        a.add(object);
      }
    }
    Object[] objectsNoNulls = a.toArray(new Object[0]);
    // convert to number array
    Number[] diffs = new Number[objectsNoNulls.length];
    try {
      System.arraycopy(objectsNoNulls, 0, diffs, 0, objectsNoNulls.length);
    } catch (ArrayStoreException e) {
      e.printStackTrace();
      System.err.println("content of objs");
      System.err.println(StringUtil.join("\n", objs));
    }
    Arrays.sort(diffs);
    return diffs;
  }

}
