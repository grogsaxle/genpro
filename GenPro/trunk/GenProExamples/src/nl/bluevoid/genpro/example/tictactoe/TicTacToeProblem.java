package nl.bluevoid.genpro.example.tictactoe;

import nl.bluevoid.genpro.Setup;
import nl.bluevoid.genpro.cell.ConstantCell;
import nl.bluevoid.genpro.cell.LibraryCell;
import nl.bluevoid.genpro.operations.NumberOperations;

public class TicTacToeProblem {
  Setup setup = new Setup(this);

  public TicTacToeProblem() {

    
    setup.addInputCell("board", TicTacToeBoard.class);
    setup.addInputCell("player", Player.class);
    setup.addOutputCell("x", Integer.class);
    setup.addOutputCell("y", Integer.class);

    ConstantCell cc = new ConstantCell("cc", Integer.class, 0f, 2f);
    ConstantCell cc1 = new ConstantCell("cc1", Integer.class, 0f, 2f);
    ConstantCell cc2 = new ConstantCell("cc2", Integer.class, 0f, 2f);
    ConstantCell cc3 = new ConstantCell("cc3", Integer.class, 0f, 2f);
    ConstantCell cc4 = new ConstantCell("cc4", Integer.class, 0f, 2f);
    ConstantCell cc5 = new ConstantCell("cc5", Integer.class, 0f, 2f);
    ConstantCell cc6 = new ConstantCell("cc6", Integer.class, 0f, 2f);

    setup.setEvaluateMultiThreaded(false);
    setup.setGenerationSize(1000);
    setup.setLibraryCells(new LibraryCell[]{NumberOperations.NUM_OPS});
    setup.setConstantCells(new ConstantCell[] { cc, cc1, cc2, cc3, cc4, cc5, cc6 });
    //setup.addIfCell(3);
    setup.setCallCells(15, "cc", new Class[] { Integer.class, boolean.class});
    
    //TournamentTestSet testSet=new TournamentTestSet();

     
      
    
  }
}
