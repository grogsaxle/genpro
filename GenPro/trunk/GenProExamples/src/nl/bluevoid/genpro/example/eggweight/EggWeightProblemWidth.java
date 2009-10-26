package nl.bluevoid.genpro.example.eggweight;

import nl.bluevoid.genpro.Grid;
import nl.bluevoid.genpro.ScoringType;
import nl.bluevoid.genpro.Setup;
import nl.bluevoid.genpro.TestSet;
import nl.bluevoid.genpro.Trainer;
import nl.bluevoid.genpro.cell.ConstantCell;
import nl.bluevoid.genpro.cell.LibraryCell;
import nl.bluevoid.genpro.cell.ReferenceCell;
import nl.bluevoid.genpro.operations.NumberOperations;
import nl.bluevoid.genpro.view.TrainerVisual;

public class EggWeightProblemWidth extends Trainer{//Visual {

  public static void main(String[] args) throws Exception {
    EggWeightProblemWidth ep = new EggWeightProblemWidth();
    ep.startTraining();
  }

  @Override
  public Setup createSetup() {
    Setup setup = new Setup(this);

    // create all cells
    setup.addInputCell("width", Double.class);
    setup.addOutputCell("weight", Double.class);

    setup.setCallCells(7, "c",  Double.class );

    ConstantCell cCell1 = new ConstantCell("const1", Double.class, -100, 100);
    ConstantCell cCell2 = new ConstantCell("const2", Double.class, -100, 100);
    ConstantCell cCell3 = new ConstantCell("const3", Double.class, -100, 100);
    setup.setConstantCells( cCell1, cCell2, cCell3 );
    setup.setLibraryCells( NumberOperations.NUM_OPS, //NumberOperations.MATH_CLASS,
        GonioOperations.GONIO_OPS, new LibraryCell(Egg.class) );

    setup.setGenerationSize(2000);
    setup.setMutatePercentage(90);
    setup.setCrossingPercentage(30);
    setup.setMaxIndividualsWithSameScore(30);
    setup.setMinimumScoreForSaving(10);
    setup.setEvaluateMultiThreaded(false);
    //setup.setSolutionInterface(EggWeightSolution.class);
    return setup;
  }

  @Override
  public TestSet createTestSet() {
    TestSet testSet = new TestSet(setup, "width", "weight" ) {
      public double scoreOutput(ReferenceCell cell, Object calculated, Object expected) {
        return getAbsoluteNumberDifferencePercentage((Number)calculated, (Number)expected);
      }

      @Override
      public double scoreGrid(Grid g) {
        return (g.getNrOfUsedCallCells()-2)*0.1; // each cell may cost a 0.1 gram deviation
      }
    };
    testSet.setScoringType(ScoringType.SCORING_HIGHEST_PERCENTAGE_OF_TESTCASES);
    testSet.addCellValuesFromFile("eggData.txt", TestSet.SKIP_DATA_COLUMN, "width", "weight");
    return testSet;
  }
}