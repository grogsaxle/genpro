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

package nl.bluevoid.genpro.cell.switx;

import java.util.ArrayList;
import java.util.HashMap;

import nl.bluevoid.genpro.CallTarget;
import nl.bluevoid.genpro.cell.CellTypeEnum;
import nl.bluevoid.genpro.cell.ValueCell;

public class BooleanSwitchCell extends SwitchCell {

  public BooleanSwitchCell(String name, Class<?> valueType) {
    super(name, valueType, Boolean.class, 2, 2, CellTypeEnum.BooleanSwitchCell);
  }

  @Override
  public void mutateRandomOptionValue() {
    optionsSorted = false;
    getOptions().get(0).switchCaseValue = !((Boolean) getOptions().get(0).switchCaseValue);
    getOptions().get(1).switchCaseValue = !((Boolean) getOptions().get(1).switchCaseValue);
  }

  @Override
  public void setOptionValues() {
    optionsSorted = false;
    getOptions().get(0).switchCaseValue = Boolean.FALSE;
    getOptions().get(1).switchCaseValue = Boolean.TRUE;
  }

  public SwitchOption getOptionToCall(Object value) {
    sortOptions();
    if (((Boolean) value).equals(Boolean.TRUE)) {
      return getOptions().get(1);
    } else {
      return getOptions().get(0);
    }
  }

  @Override
  protected void setOptionValue(SwitchOption opt) {
    throw new IllegalStateException("setOptionValue should never be called");
  }

  @Override
  public void mutate(HashMap<Class<?>, ArrayList<CallTarget>> callTargetsByReturnType,
      ArrayList<ValueCell> allParamCells) {
    final int opt = random.nextInt(10);
    switch (opt) {
    case 0:
    case 1:
    case 2: 
      setNewInput(allParamCells);
      break;
    case 3:
    case 4:
    case 5:
    case 6:// mutate random option
      mutateRandomOption(callTargetsByReturnType, allParamCells);
      break;
    case 7:
    case 8:
    case 9:// mutate values of option
      mutateRandomOptionValue();
      mutations.add("mutate values of option");
      break;
    default:
      throw new IllegalArgumentException("value " + opt + " is not supported");
    }
  }
}
