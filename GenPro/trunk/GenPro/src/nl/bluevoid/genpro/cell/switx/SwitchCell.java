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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import nl.bluevoid.genpro.CallTarget;
import nl.bluevoid.genpro.CellMap;
import nl.bluevoid.genpro.GridExecutionError;
import nl.bluevoid.genpro.Util;
import nl.bluevoid.genpro.cell.Calculable;
import nl.bluevoid.genpro.cell.CellTypeEnum;
import nl.bluevoid.genpro.cell.NoCellFoundException;
import nl.bluevoid.genpro.cell.ValueCell;
import nl.bluevoid.genpro.util.Debug;
import nl.bluevoid.genpro.util.InfoException;
import nl.bluevoid.genpro.util.Sneak;
import nl.bluevoid.genpro.util.XMLBuilder;
/**
 * @author Rob van der Veer
 * @since 1.0
 */
public abstract class SwitchCell extends ValueCell implements Calculable {

  protected final Class<?> switchType;
  protected ValueCell switchValueCell;
  protected ArrayList<SwitchOption> options = new ArrayList<SwitchOption>();

  ArrayList<String> mutations = new ArrayList<String>();

  protected Object[] optionValues;
  protected boolean optionsSorted = false;
  public static final int MIN_OPTIONS = 2;

  protected final int maxOptions;
  protected final int startNumOptions;

  protected int optionCounter = 0;
  protected int calced;
  protected int errored;

  /*
   * Calling calc on a SwitchCell always results in the value of the cell being set. A switch has a valuetype
   * and a switchtype. SwitchType can be boolean, Enum, or a Number The switch has a list of switch options:
   * these are the calls that will be executed depending on the resulting switchvalue.
   * 
   * @param name
   * 
   * @param valueType
   */
  public SwitchCell(String name, Class<?> valueType, Class<?> switchType, int maxOptions, int numOptions,
      CellTypeEnum cte) {
    super(name, valueType, false, cte);
    this.switchType = switchType;
    this.maxOptions = maxOptions;
    this.startNumOptions = numOptions;
    Debug.checkRange(maxOptions, MIN_OPTIONS, Integer.MAX_VALUE);
    Debug.checkRange(numOptions, MIN_OPTIONS, maxOptions);
    optionValues = new Object[this.maxOptions];
  }

  public void sortOptions() {
    if (!optionsSorted) {
      optionValues = new Object[options.size()];
      optionsSorted = true;
      Collections.sort(options);
      // create index for fast choosing
      for (int i = 0; i < options.size(); i++) {
        optionValues[i] = options.get(i).switchCaseValue;
      }
    }
  }

  public boolean canMutate() {
    return true;
  }

  protected abstract void setOptionValues();

  protected abstract void mutateRandomOptionValue();

  public void getXML(XMLBuilder x) {
    x.add(getClass().getName(), "not implemented yet");
  }

  public void calc() throws IllegalAccessException, InvocationTargetException, GridExecutionError {
    // clear value!!!!
    value = null;

    Object switchValue = switchValueCell.getValue();
    if (switchValue == null) {
      // the switchValue resulted in a null value=> has errored or otherwise failed
    } else {
      getOptionToCall(switchValue).calc();
    }
  }

  public SwitchOption getOptionToCall(Object value) {
    sortOptions();
    try {
      int index = Arrays.binarySearch(optionValues, ((Number) value).doubleValue());
      /**
       * Arrays.binarySearch returns: index of the search key, if it is contained in the array within the
       * specified range; otherwise, (-(insertion point) - 1). The insertion point is defined as the point at
       * which the key would be inserted into the array
       */
      if (index < 0)
        return options.get(-(index + 2));
      return options.get(index);
    } catch (ArrayIndexOutOfBoundsException e) {
      InfoException t = new InfoException(e);
      t.addInfo("switch value:" + value);
      t.addInfo(mutations);
      t.addInfoSeperator();
      Sneak.sneakyThrow(t);
    }
    // unreachable by sneakythrow
    return null;
  }

  public void connectCell(HashMap<Class<?>, ArrayList<CallTarget>> callTargetsByReturnType,
      ArrayList<ValueCell> allParamCells) throws NoCellFoundException {
    switchValueCell = Util.getRandomCell(switchType, allParamCells);
    for (int i = 0; i < startNumOptions; i++) {
      SwitchOption opt = createAndAddOption();
      opt.connectCell(callTargetsByReturnType, allParamCells);
    }
    setOptionValues();
    sortOptions();
    validateLeadsToInputCell();
  }

  public void validateLeadsToInputCell() { // TODO also call after mutate??
    setLeadsToInputCell(false);
    if (switchValueCell.isLeadsToInputCell()) {
      setLeadsToInputCell(true);
      return;
    }
    for (SwitchOption option : getOptions()) {
      option.validateLeadsToInputCell();
      if (option.isLeadsToInputCell()) {
        setLeadsToInputCell(true);
        return;
      }
    }
  }

  protected void setNewInput(ArrayList<ValueCell> allParamCells) {
    try {
      // set new input
      switchValueCell = Util.getRandomCell(switchType, allParamCells);
      mutations.add("changed switchValueCell");
    } catch (NoCellFoundException e) {
      // bad luck
    }
  }

  protected SwitchOption createAndAddOption() {
    SwitchOption opt = new SwitchOption("opt " + optionCounter, this);
    // setOptionValue(opt); do not call here!! ruins the boolean type
    options.add(opt);
    optionCounter++;
    optionsSorted = false;
    return opt;
  }

  protected abstract void setOptionValue(SwitchOption opt);

  public abstract void mutate(HashMap<Class<?>, ArrayList<CallTarget>> callTargetsByReturnType,
      ArrayList<ValueCell> allParamCells); 
  
  protected void deleteOption() {
    if (options.size() > MIN_OPTIONS) {
      options.remove(random.nextInt(options.size() - 1) + 1); // option 0 may not be deleted!!
      optionsSorted = false;
    }
  }

  protected void mutateRandomOption(HashMap<Class<?>, ArrayList<CallTarget>> callTargetsByReturnType,
      ArrayList<ValueCell> allParamCells) {
    options.get(random.nextInt(options.size())).mutate(callTargetsByReturnType, allParamCells);
    optionsSorted = false;
    mutations.add("mutate random option");
  }
  
  public void restoreConnections(CellMap map) throws NoCellFoundException {
    switchValueCell = map.getByNameOrValueType(switchValueCell);
    for (SwitchOption option : options) {
      option.restoreConnections(map);
    }
  }

  public Class<?> getSwitchType() {
    return switchType;
  }

  public ArrayList<SwitchOption> getOptions() {
    return options;
  }

  public ValueCell getSwitchValueCell() {
    return switchValueCell;
  }

  public int getNumOptions() {
    return options.size();
  }

  @Override
  public String toString() {
    StringBuffer st = new StringBuffer();
    for (SwitchOption switchOption : options) {
      st.append("\n     " + switchOption.toString());
    }
    return super.toString() + st.toString() + "\n";
  }

  protected void setSwitchValueCell(ValueCell switchValueCell) {
    this.switchValueCell = switchValueCell;
  }

  //@Override
  public void setCascadeUsedForOutput() {
    if (!isUsedForOutput()) {
      super.setUsedForOutput(true);
      ValueCell.setCascadeUsedForOutput(switchValueCell);
      for (SwitchOption switchOption : options) {
        ValueCell.setCascadeUsedForOutput(switchOption);
      }
    }
  }

  //@Override
  public int getCalced() {
    return calced;
  }

  //@Override
  public int getErrored() {
    return errored;
  }

  //@Override
  public void resetCallAndErrorCounter() {
    calced = 0;
    errored = 0;
    for (SwitchOption switchOption : options) {
      switchOption.resetCallAndErrorCounter();
    }
  }

  @Override
  public SwitchCell clone() {
    final SwitchCell clone = (SwitchCell) super.clone();
    clone.calced = 0;
    clone.errored = 0;
    clone.options = new ArrayList<SwitchOption>();
    for (SwitchOption opt : options) {
      clone.options.add(opt.clone());
    }
    return clone;
  }
}
