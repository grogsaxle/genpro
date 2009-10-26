package nl.bluevoid.genpro.example.eggweight;

import nl.bluevoid.genpro.Grid;
import nl.bluevoid.genpro.ScoringType;
import nl.bluevoid.genpro.Setup;
import nl.bluevoid.genpro.TestSet;
import nl.bluevoid.genpro.cell.ConstantCell;
import nl.bluevoid.genpro.cell.LibraryCell;
import nl.bluevoid.genpro.cell.ReferenceCell;
import nl.bluevoid.genpro.operations.NumberOperations;
import nl.bluevoid.genpro.view.TrainerVisual;

public class EggWeightProblem extends TrainerVisual {

  public static void main(String[] args) throws Exception {
    EggWeightProblem ep = new EggWeightProblem();
    ep.startTraining();
  }

  @Override
  public Setup createSetup() {
    Setup setup = new Setup("EggWeight");

    // create all cells
    setup.addInputCell("height", Double.class);
    setup.addInputCell("width", Double.class);
    setup.addOutputCell("weight", Double.class);

    setup.setCallCells(5, "c",  Double.class );

    ConstantCell cCell1 = new ConstantCell("const1", Double.class, -100, 100);
    ConstantCell cCell2 = new ConstantCell("const2", Double.class, -100, 100);
    ConstantCell cCell3 = new ConstantCell("const3", Double.class, -100, 100);
    setup.setConstantCells( cCell1, cCell2, cCell3 );
    setup.setLibraryCells( NumberOperations.NUM_OPS, //NumberOperations.MATH_CLASS,
        GonioOperations.GONIO_OPS, new LibraryCell(Egg.class) );

    setup.setGenerationSize(2000);
    setup.setMutatePercentage(30);
    setup.setMaxIndividualsWithSameScore(30);
    setup.setMinimumScoreForSaving(3);
    //setup.setSolutionInterface(EggWeightSolution.class);
    return setup;
  }

  @Override
  public TestSet createTestSet() {
    TestSet testSet = new TestSet(setup,  "height", "width", "weight" ) {
      public double scoreOutput(ReferenceCell cell, Object calculated, Object expected) {
        return getAbsoluteNumberDifferencePercentage((Number)calculated, (Number)expected);
      }

      @Override
      public double scoreGrid(Grid g) {
        return 0;//g.getNrOfUsedCallCells()*0.1; // each cell may cost a 0.1 gram deviation
      }
    };
    testSet.setScoringType(ScoringType.SCORING_AVARAGE_PERCENTAGE_PER_TESTCASE);
    testSet.addCellValuesFromFile("eggData.txt");
    return testSet;
  }
}