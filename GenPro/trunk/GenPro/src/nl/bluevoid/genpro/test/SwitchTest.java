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

package nl.bluevoid.genpro.test;

import nl.bluevoid.genpro.Grid;
import nl.bluevoid.genpro.GridSolutionEvaluator;
import nl.bluevoid.genpro.Setup;
import nl.bluevoid.genpro.TestSet;
import nl.bluevoid.genpro.TestSetSolutionEvaluator;
import nl.bluevoid.genpro.Trainer;
import nl.bluevoid.genpro.cell.ConstantCell;
import nl.bluevoid.genpro.cell.ReferenceCell;
import nl.bluevoid.genpro.operations.NumberOperations;

/**
 * @author Rob van der Veer
 * @since 1.0
 */
public class SwitchTest extends Trainer {

  public static void main(String[] args) {
    SwitchTest st = new SwitchTest();
    st.startTraining();
  }

  @SuppressWarnings("unchecked")
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
  public TestSetSolutionEvaluator createEvaluator() {
    return new GridSolutionEvaluator() {

      @Override
      public double scoreOutput(ReferenceCell outputCell, Object calculated, Object expected) {
        return getAbsoluteNumberDifference((Number) calculated, (Number) expected);
      }

      @Override
      public TestSet createTestSet() {
        TestSet ts = new TestSet(getSetup(), "length", "Category");
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

      @Override
      public double scoreGrid(Grid g) {
        return 0;
      }

      @Override
      public double scoreGridException(Throwable t) {
        return 0;
      }
    };
  }
}

enum Category {
  TOO_SHORT, JUST_RIGHT, TOO_LONG;
}
