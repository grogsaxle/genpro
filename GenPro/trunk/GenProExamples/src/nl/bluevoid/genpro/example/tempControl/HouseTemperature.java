package nl.bluevoid.genpro.example.tempControl;

import javax.swing.JFrame;

import nl.bluevoid.genpro.Grid;
import nl.bluevoid.genpro.NumberFeedback;
import nl.bluevoid.genpro.Setup;
import nl.bluevoid.genpro.TestSet;
import nl.bluevoid.genpro.cell.ConstantCell;
import nl.bluevoid.genpro.cell.LibraryCell;
import nl.bluevoid.genpro.cell.ReferenceCell;
import nl.bluevoid.genpro.cell.ValueCell;
import nl.bluevoid.genpro.operations.BooleanOperations;
import nl.bluevoid.genpro.operations.NumberOperations;
import nl.bluevoid.genpro.util.Calc;
import nl.bluevoid.genpro.util.InfoException;
import nl.bluevoid.genpro.view.SwingUtil;
import nl.bluevoid.genpro.view.TrainerVisual;

public class HouseTemperature extends TrainerVisual {

  public static final float MIN_TEMP = 12;
  public static final float MAX_TEMP = 24;

  public static void main(String[] args) throws Exception {
    HouseTemperature tp = new HouseTemperature();

    TempResultPanel tempResultPanel = new TempResultPanel(tp);
    tp.addResultListener(tempResultPanel);

    JFrame f = SwingUtil.showPanelInFrame(tempResultPanel);
    f.setSize(800, 600);
    f.validate();
    tp.startTraining();
  }

  @Override
  public Setup createSetup() {
    Setup setup = new Setup(this);
    setup.addInputCell("timeInside", Integer.class);
    setup.addInputCell("minuteOfDay", Integer.class);
    setup.addInputCell("inside", Boolean.class);

    // setup.addInputCell("isInside", Boolean.class);
    setup.addOutputCell("t", Double.class);
    setup.setCallCells(19, "c", Double.class, Boolean.class);
    setup.setMaxSwitchCellNr(4, Boolean.class, Double.class);

    ConstantCell cCell = new ConstantCell("minTemp", Double.class, MIN_TEMP);
    ConstantCell cCell2 = new ConstantCell("maxTemp", Double.class, MAX_TEMP);
    ConstantCell cCell3 = new ConstantCell("const3", Double.class, -100, 100);
    ConstantCell cCell4 = new ConstantCell("const3", Integer.class, 0);
    setup.setConstantCells(cCell, cCell2, cCell3, cCell4);

    setup.setLibraryCells(new LibraryCell[] { NumberOperations.NUM_OPS, NumberOperations.MATH_CLASS,
        BooleanOperations.BOOL_OPS });

    setup.setGenerationSize(2000);
    setup.setMutatePercentage(20);
    setup.setMaxIndividualsWithSameScore(30);

    return setup;
  }

  @Override
  public TestSet createTestSet() {
    TestSet testSet = new TestSet(setup, new String[] { "minuteOfDay", "timeInside", "inside", "t" }) {
      public double scoreOutput(ReferenceCell cell, Object calculated, Object expected) {
        double temp = (Double) calculated;
        NumberFeedback td = (NumberFeedback) expected;

        if (Calc.isNaNorInfinite(temp))
          return temp;

        return getAbsoluteNumberDifference(temp, td);
      }

      public double scoreGrid(Grid g) {
        final int sampleNr = 29;
        final int minutesInDay = 24 * 60;
        float[][] dataout = getTempDayDataOuthouse(g, minutesInDay / sampleNr);
        float[][] datain = getTempDayDataInhouse(g, minutesInDay / sampleNr);

        // 1 any temperature above MAX_TEMP degrees?
        double aboveMAX_TEMP = 0;
        // 2 any temperature below MIN_TEMP degrees?
        double belowMIN_TEMP = 0;
        // 3 average temperature above MIN_TEMP?
        double avarageAboveMIN_TEMP = 0;

        for (float[] fs : dataout) {
          float temp = fs[1];
          if (temp > MIN_TEMP) {
            avarageAboveMIN_TEMP += temp - MIN_TEMP;
            if (temp > MAX_TEMP)
              aboveMAX_TEMP += temp - MAX_TEMP;
          } else {
            belowMIN_TEMP += (MIN_TEMP - temp);
          }
        }

        for (float[] fs : datain) {
          float temp = fs[1];
          if (temp > MIN_TEMP) {
            avarageAboveMIN_TEMP += temp - MIN_TEMP;
            if (temp > MAX_TEMP)
              aboveMAX_TEMP += temp - MAX_TEMP;
          } else {
            belowMIN_TEMP += (MIN_TEMP - temp);
          }
        }
        // create avarages
        aboveMAX_TEMP /= sampleNr * 2;// 2 datasets
        belowMIN_TEMP /= sampleNr * 2;
        avarageAboveMIN_TEMP /= sampleNr * 2;

        double score = aboveMAX_TEMP * 100 + belowMIN_TEMP * 100 + avarageAboveMIN_TEMP/3;

        return score;
      }
    };

    testSet.addCellValues(um(8, 00), 15, true, new NumberFeedback(NumberFeedback.HIGHER, 16));
    testSet.addCellValues(um(8, 30), 20, true, new NumberFeedback(NumberFeedback.LOWER, 20));
    testSet.addCellValues(um(9, 00), 26, true, new NumberFeedback(NumberFeedback.HIGHER, 18));
    testSet.addCellValues(um(10, 30), 700, true, new NumberFeedback(NumberFeedback.HIGHER, 22));
    testSet.addCellValues(um(11, 30), 200, true, new NumberFeedback(NumberFeedback.HIGHER, 20));
    testSet.addCellValues(um(12, 30), 100, true, new NumberFeedback(NumberFeedback.HIGHER, 19));
    testSet.addCellValues(um(15, 00), 18, true, new NumberFeedback(NumberFeedback.HIGHER, 18));
    testSet.addCellValues(um(16, 00), 36, true, new NumberFeedback(NumberFeedback.HIGHER, 19));
    testSet.addCellValues(um(16, 30), 120, true, new NumberFeedback(NumberFeedback.LOWER, 21));
    testSet.addCellValues(um(20, 00), 60, true, new NumberFeedback(NumberFeedback.HIGHER, 19));
    return testSet;
  }

  private int um(int u, int m) {
    return u * 60 + m;
  }

  public synchronized float[][] getTempDayDataOuthouse(Grid ind, final int sampleIntervalMinutes) {
    return getTempDayData(ind, sampleIntervalMinutes, 0);
  }

  public synchronized float[][] getTempDayDataInhouse(Grid ind, final int sampleIntervalMinutes) {
    return getTempDayData(ind, sampleIntervalMinutes, 100);
  }

  private float[][] getTempDayData(Grid ind, final int sampleIntervalMinutes, int timeInside) {
    ind.resetCellCallCounters();
    final int samples = 24 * 60 / sampleIntervalMinutes;
    final float[][] data = new float[samples][2];
    for (int m = 0; m < samples; m++) {
      final int time = m * sampleIntervalMinutes;
      data[m][0] = time * 60 * 1000;
      ind.getInputCell("minuteOfDay").setValue(time);
      ind.getInputCell("timeInside").setValue(timeInside);
      ind.getInputCell("inside").setValue(true);
      ind.resetGridExecutionErrors();

      ind.calc();
      if (ind.getGridExecutionErrors().size() == 0) {
        ValueCell vc = ind.getOutputCell("t");
        Object value = vc.getValue();
        try {
          data[m][1] = ((Double) value).floatValue();
        } catch (NullPointerException e) {
          InfoException ie = new InfoException(e);
          ie.addInfo("m=" + m);
        }
        // System.out.println("time:" + time + " inside:" + timeInside + " desiredTemperature:" + data[m][1]);
      } else
        data[m][1] = 0;
    }
    return data;
  }

}
// public synchronized float[][] getTestCaseErrorData(Grid ind) {
// fitnessFunction.evaluate(ind);
// return fitnessFunction.errorsData;
// }
//
// public static double getErrorFromTC(GPTestCase tc, final double result) {
// double error = 0;
// if (Double.isNaN(result)) {
// return 3000;
// }
//
// // check min max values
// if (result > MAX_TEMP)
// error += filterResult((result - MAX_TEMP) * 100);
// if (result < MIN_TEMP)
// error += filterResult((MIN_TEMP - result) * 100);
//
// // check testcase value
// if (tc.target == GPTestCase.HOGER_DAN && result < tc.output || tc.target == GPTestCase.LAGER_DAN
// && result > tc.output) {
// error += filterResult(Math.abs(result - tc.output));
// }
// return error;
// }
//
// private static double filterResult(double d) {
// if (d < 0)
// throw new IllegalArgumentException("d=" + d);
// return d > 10000 ? 10000 : d;
// }
//
// @Override
// protected synchronized double evaluate(IGPProgram ind) {
// double error = 0.0;
// errorsData = new float[testCases.size()][2];
// double errorTestCasesTotal = 0;
// for (int i = 0; i < testCases.size(); i++) {
// GPTestCase tc = testCases.get(i);
// vMinuutInDag.set(tc.vals[0]);
// vTijdBinnen.set(tc.vals[1]);
// vIsBinnen.set(tc.vals[1] == 0);
//
// try {
// final double result = ind.execute_float(0, noargs);
// float errorTC = (float) getErrorFromTC(tc, result);
// errorsData[i][0] = tc.vals[0] * 60 * 1000;
// errorsData[i][1] = errorTC;
// errorTestCasesTotal += errorTC;
// } catch (ArithmeticException ex) {
// System.out.println(ind);
// ex.printStackTrace();
// throw ex;
// }
// }
// error += ((errorTestCasesTotal / testCases.size()) * 10);
//
// final int interval = 19;
// // calulate temparatuur surface
//
// // check temp oppervlak voor 1 dag en niemand thuis
// vTijdBinnen.set(0f);
// vIsBinnen.set(false);
// float[][] data = getTempDayData(ind, interval);
// double tempavgInhouse = fitnessCalc(data);
// error += filterResult(tempavgInhouse);
//
// // check temp oppervlak voor 1 dag en altijd iemand thuis
// vTijdBinnen.set(500f);
// vIsBinnen.set(true);
// float[][] data2 = getTempDayData(ind, interval);
// double tempavgOuthouse = fitnessCalc(data2);
// error += filterResult(tempavgOuthouse);
//
// if (error < 0) {
// System.err.println("error:" + error);
// }
// if (error < 0.001) {
// error = 0.0d;
// }
//
// if (Double.isInfinite(error)) {
// return Double.MAX_VALUE;
// }
//
// // System.out.println("error:" + error);
// return error;
// }

// private float fitnessCalc(float[][] data) {
// Debug.errorOnTrue(data.length < 1, " data.lengte " + data.length);
//
// float errorPlus = 0;
// float tempTotal = 0;
// int tempCount = 0;
// for (float[] fs : data) {
// final float val = fs[1];
// if (val > MAX_TEMP)
// errorPlus += filterResult((val - MAX_TEMP) * 100);
// else if (val < MIN_TEMP)
// errorPlus += filterResult((MIN_TEMP - val) * 100);
// else if (!Float.isNaN(errorPlus)) {
// tempTotal += filterResult(val - MIN_TEMP);
// tempCount++;
// }
// }
// if (tempCount != 0) {
// errorPlus += filterResult(tempTotal / tempCount);
// }
// if (Float.isNaN(errorPlus)) {
// int a = 0;
// }
// return errorPlus;
// }

// private double getTempAvarageDay(Grid ind) {
// double tempTotal = 0;
// final int interval = 30;
// final int samples = 24 * 60 / interval;
// for (int m = 0; m < samples; m++) {
// final int time = m * interval;
// vMinuutInDag.set((float) time);
// final double result = ind.execute_float(0, noargs);
// tempTotal += result;
// }
// double tempavg = tempTotal / (24 * 60 / interval);
// return tempavg;
// }