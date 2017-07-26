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

package nl.bluevoid.genpro.cell.switx;

import java.util.ArrayList;
import java.util.HashMap;

import nl.bluevoid.genpro.CallTarget;
import nl.bluevoid.genpro.cell.CellTypeEnum;
import nl.bluevoid.genpro.cell.ValueCell;

/**
 * @author Rob van der Veer
 * @since 1.0
 */
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
