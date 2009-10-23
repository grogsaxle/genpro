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

package nl.bluevoid.genpro.test;

import nl.bluevoid.genpro.Grid;
import nl.bluevoid.genpro.Setup;
import nl.bluevoid.genpro.TestSet;
import nl.bluevoid.genpro.Trainer;
import nl.bluevoid.genpro.cell.ConstantCell;
import nl.bluevoid.genpro.cell.LibraryCell;
import nl.bluevoid.genpro.cell.ReferenceCell;
import nl.bluevoid.genpro.operations.NumberOperations;

public class SwitchTest extends Trainer {

  public static void main(String[] args) {
    SwitchTest st = new SwitchTest();
    st.startTraining();
  }

 
  @Override
  public Setup createSetup() {
    Setup s = new Setup(this.getClass().getSimpleName());
    s.addInputCell("length", Double.class);
    s.addOutputCell("Category", Double.class);
    s.setCallCells(0, "c", Double.class);
    s.setLibraryCells(NumberOperations.NUM_OPS);
    ConstantCell cCell1 = new ConstantCell("const1", Double.class, -100, 100);
    ConstantCell cCell2 = new ConstantCell("const2", Double.class, -100, 100);
    ConstantCell cCell3 = new ConstantCell("const3", Double.class, -100, 100);
    s.setConstantCells(cCell1, cCell2, cCell3);

    s.setMaxSwitchCellNr(1, Double.class);
    return s;
  }

  @Override
  public TestSet createTestSet() {
    TestSet ts = new TestSet(getSetup(), "length", "Category") {
      @Override
      public double scoreGrid(Grid g) {
        return 0;
      }

      @Override
      public double scoreOutput(ReferenceCell outputCell, Object calculated, Object expected) {
        return getAbsoluteNumberDifference((Number)calculated, (Number)expected);
      }
    };
    ts.addCellValues(300d, 3);
    ts.addCellValues(323d, 3);
    ts.addCellValues(223d, 2);
    ts.addCellValues(123d, 1);
    ts.addCellValues(323d, 3);
    ts.addCellValues(623d, 6);
    ts.addCellValues(323d, 3);
    ts.addCellValues(316d, 3);

    return ts;
  }

}

enum Category {
  TOO_SHORT, JUST_RIGHT, TOO_LONG;
}
