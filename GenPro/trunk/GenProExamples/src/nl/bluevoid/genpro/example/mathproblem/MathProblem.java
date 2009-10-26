package nl.bluevoid.genpro.example.mathproblem;

import java.util.Random;

import nl.bluevoid.genpro.Grid;
import nl.bluevoid.genpro.ScoringType;
import nl.bluevoid.genpro.Setup;
import nl.bluevoid.genpro.TestSet;
import nl.bluevoid.genpro.cell.ConstantCell;
import nl.bluevoid.genpro.cell.ReferenceCell;
import nl.bluevoid.genpro.operations.NumberOperations;
import nl.bluevoid.genpro.view.TrainerVisual;

public class MathProblem extends TrainerVisual{

  public static void main(String[] args) throws Exception {
    MathProblem ep = new MathProblem();
    ep.startTraining();
  }
  
  @Override
  public Setup createSetup() {
    Setup setup=new Setup(getClass().getSimpleName());
    // create all cells
    setup.addInputCell("x", Double.class);
    setup.addOutputCell("y", Double.class);
    setup.setCallCells(8, "c",  Double.class );

    ConstantCell cCell = new ConstantCell("const3", Integer.class, 2, 10);
    setup.setConstantCells( cCell );
    setup.setLibraryCells( NumberOperations.NUM_OPS, NumberOperations.MATH_CLASS );
    setup.addAllowedMethodsFilter(Math.class, "cos", "sin", "exp", "pow");
    setup.setGenerationSize(2000);
    setup.setMutatePercentage(30);
    setup.setMaxIndividualsWithSameScore(30);
    setup.setMinimumScoreForSaving(0);
    setup.setStopAtScore(0.02500001);
    return setup;
  }

  @Override
  public TestSet createTestSet() {
    TestSet testSet = new TestSet(setup,  "x", "y") {
      public double scoreOutput(ReferenceCell cell, Object calculated, Object expected) {
        return getAbsoluteNumberDifference((Number)calculated, (Number)expected);
      }

      @Override
      public double scoreGrid(Grid g) {
        return g.getNrOfUsedCallCells()*0.005; // each cell may cost a 0.1 gram deviation
      }
    };
    testSet.setScoringType(ScoringType.SCORING_AVARAGE_PER_TESTCASE);
    
    Random random=new Random();
    for (int i = 0; i < 20; i++) {
      double f = 8.0 * (random.nextDouble() - 0.3);
      double x = f;
      double y = f * f * f * f + f * f * f + f * f - f;
      testSet.addCellValues(x,y);
    }
    return testSet;
  }
}