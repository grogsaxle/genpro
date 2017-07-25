package nl.bluevoid.genpro;

import nl.bluevoid.genpro.cell.ReferenceCell;
import nl.bluevoid.genpro.util.Calc;

public abstract class GridSolutionEvaluator extends TestSetSolutionEvaluator {

  private GridExecutionError lastException;
  private int evalTillTestSetNr = -1;

  public GridSolutionEvaluator() {
    super();
    evalTillTestSetNr = getTestSet().getNumberOfTestCases();
  }

  public TestSetStatistics getDeviations(final Grid grid) {
    TestSetStatistics tss = new TestSetStatistics(getTestSet(), this, grid);
    grid.resetGridExecutionErrors();
    grid.resetCellCallCounters();

    for (int valueNr = 0; valueNr < getTestSet().getNumberOfTestCases(); valueNr++) {
      // set inputs
      for (final String name : getTestSet().getInputCellNames()) {
        grid.getInputCell(name).setValue(getTestSet().getValue(name, valueNr));
      }

      // TODO reset outputs without reseting constants
      // set outputs (their refered cells) to null
      // for (final String name : outputCellNames) {
      // grid.getOutputCell(name).getReferedCell().setValue(null);
      // }

      lastException = null;
      double testcasescore = executeAndScoreTestCase(grid, valueNr);

      // read outputs && store values in "ACTUAL"
      for (final String name : getTestSet().getOutputCellNames()) {
        final ReferenceCell ocell = grid.getOutputCell(name);

        if (lastException == null) {
          tss.setActualOutputValue(name, valueNr, ocell.getValue());
        } else {
          tss.setActualOutputValue(name, valueNr, lastException);
        }
      }
      tss.setTestCaseScore(testcasescore, valueNr);
    }
    return tss;
  }

  public double evaluate(final Grid grid) {
    // System.out.println("Evaluating:"+grid.toString());
    grid.resetGridExecutionErrors();
    double score = 0;

    for (int valueNr = 0; valueNr < evalTillTestSetNr; valueNr++) {
      // set inputs
      for (final String name : getTestSet().getInputCellNames()) {
        grid.getInputCell(name).setValue(getTestSet().getValue(name, valueNr));
        //Debug.println("input " + name + " set to:" + grid.getInputCell(name));
      }

      // set outputs (their refered cells) to null
      // for (final String name : outputCellNames) {
      // grid.getOutputCell(name).getReferedCell().setValue(null);
      // }
      // TODO reset outputs without reseting constants

      // TODO why do we need this????
      // het zorgt ervoor dat calc maar op 1 thread tegelijk wordt aangeroepen
      // als calc wordt uitgevoerd samen met een andere thread die iets in evaluate doet, dan gaat het mis.
      // Wat wordt er dan in calc gedeeld met andere grids???
      // niet inputs en outputs
      // wel static library objecten
      // niet constanten

      // synchronized (this) {
      // do calc

      double testcaseScore = executeAndScoreTestCase(grid, valueNr);

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

  private double executeAndScoreTestCase(final Grid grid, int valueNr) {
    double testcaseScore = 0;
    try {
      grid.calc();
    } catch (GridExecutionError e) {
      lastException = e;
      testcaseScore += scoreGridException(e);
    }

    // read outputs && count delta
    for (final String name : getTestSet().getOutputCellNames()) {
      final ReferenceCell ocell = grid.getOutputCell(name);
      // final Object result = ocell.getValue();
      testcaseScore += scoreOutput(ocell, ocell.getValue(), getTestSet().getValue(name, valueNr));
    }
    return testcaseScore;
  }

  public boolean increaseIncrementalInvolvement() {
    if (evalTillTestSetNr >= getTestSet().getNumberOfTestCases()) {
      return false;
    } else {
      evalTillTestSetNr++;
      return true;
    }
  }

  /**
   * 
   * @param outputCell
   * @param calculated
   *          Can be null!!!
   * @param expected
   * @return
   */
  public abstract double scoreOutput(ReferenceCell outputCell, Object calculated, Object expected);

}
