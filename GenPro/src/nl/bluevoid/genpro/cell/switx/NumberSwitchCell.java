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
import nl.bluevoid.genpro.Util;
import nl.bluevoid.genpro.cell.CellTypeEnum;
import nl.bluevoid.genpro.cell.NoCellFoundException;
import nl.bluevoid.genpro.cell.ValueCell;
import nl.bluevoid.genpro.util.Debug;

/**
 * @author Rob van der Veer
 * @since 1.0
 */

public class NumberSwitchCell extends SwitchCell {

  private final double minOptionValue;
  private final double maxOptionValue;
  private final double range;
  
  /**
   * Minimum number of options is 2, option 1 always has value: Double.NEGATIVE_INFINITY;
   * 
   */

  public NumberSwitchCell(String name, Class<?> valueType, int maxOptionsNr, double minOptionValue,
      double maxOptionValue) {
    super(name, valueType, Number.class, maxOptionsNr, random.nextInt(maxOptionsNr - 1) + 2,
        CellTypeEnum.NumberSwitchCell);
    Debug.errorOnTrue(maxOptionValue < minOptionValue, "max is bigger than min!", maxOptionValue, " ",
        minOptionValue);
    this.minOptionValue = minOptionValue;
    this.maxOptionValue = maxOptionValue;
    range = maxOptionValue - minOptionValue;
  }

  @Override
  public void mutate(HashMap<Class<?>, ArrayList<CallTarget>> callTargetsByReturnType,
      ArrayList<ValueCell> allParamCells) {

    final int opt = random.nextInt(10);
    switch (opt) {
    case 0:
    case 1:
      setNewInput(allParamCells);
      break;
    case 2: // add option
      if (options.size() <= maxOptions) {
        SwitchOption option = createAndAddOption();
        setOptionValue(option);
        try {
          option.connectCell(callTargetsByReturnType, allParamCells);
        } catch (NoCellFoundException e) {
          Debug.printErrln("Mutation failed "+e.getMessage());
        }
        optionsSorted = false;
        mutations.add("added option");
      }
      break;
    case 3: // delete option
      deleteOption();
      break;
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

  @Override
  public void mutateRandomOptionValue() {
    optionsSorted = false;
    // select option
    int option = random.nextInt(getNumOptions() - 1) + 1;
    Debug.checkRange(option, 1, getNumOptions(), "option may not be 0 or higher than numoptions:", option,
        " ", getNumOptions());
    Double value = (Double) getOptions().get(option).switchCaseValue;
    getOptions().get(option).switchCaseValue = Util.mutateperc(value, 10000, minOptionValue, maxOptionValue);
  }

  @Override
  public void setOptionValues() {
    optionsSorted = false;
    getOptions().get(0).switchCaseValue = Double.NEGATIVE_INFINITY;
    for (int i = 1; i < getNumOptions(); i++) {
      setOptionValue(getOptions().get(i));
    }
  }

  @Override
  protected void setOptionValue(SwitchOption opt) {
    optionsSorted = false;
    opt.switchCaseValue = minOptionValue + random.nextDouble() * range;
  }
}
