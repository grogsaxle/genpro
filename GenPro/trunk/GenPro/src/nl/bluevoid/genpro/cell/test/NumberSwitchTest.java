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

package nl.bluevoid.genpro.cell.test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import junit.framework.TestCase;
import nl.bluevoid.genpro.CallTarget;
import nl.bluevoid.genpro.GridExecutionError;
import nl.bluevoid.genpro.cell.ConstantCell;
import nl.bluevoid.genpro.cell.NoCellFoundException;
import nl.bluevoid.genpro.cell.ValueCell;
import nl.bluevoid.genpro.cell.switx.NumberSwitchCell;
import nl.bluevoid.genpro.cell.switx.SwitchOption;
/**
 * @author Rob van der Veer
 * @since 1.0
 */
public class NumberSwitchTest extends TestCase {

  public void test1() throws IllegalAccessException, InvocationTargetException, GridExecutionError {
    final int NR_OPTIONS = 400;
    final int MIN = -10000;
    final int MAX = 10000;

    ValueCell switchvalueCell = new ConstantCell("in", Double.class, MIN, MAX);
    TestNumberSwitchCell b = new TestNumberSwitchCell("b", Double.class, NR_OPTIONS, MIN, MAX,
        switchvalueCell);

    b.createOptions();
    int optNumber = b.getNumOptions();

    ArrayList<SwitchOption> options = b.getOptions();
    assertEquals(optNumber, b.getNumOptions());
    assertEquals(optNumber, options.size());
    assertTrue("" + options.size() + " / " + NR_OPTIONS, options.size() <= NR_OPTIONS);
    System.out.println(b);

    checkSortOrder(switchvalueCell, b);

    b.mutateRandomOptionValue();

    checkSortOrder(switchvalueCell, b);
  }

  private void checkSortOrder(ValueCell switchvalueCell, TestNumberSwitchCell b) {
    // check sortorder, should be lowest first
    double lowest = Double.NEGATIVE_INFINITY;// lowest, NOT Double.MIN_VALUE!!!
    for (int i = 0; i < b.getOptions().size(); i++) {
      SwitchOption switchOption = b.getOptions().get(i);
      System.out.println("testing=" + i + ": " + switchOption);
      double casevalue = (Double) switchOption.getSwitchCaseValue();
      System.out.println("casevalue:" + casevalue);
      // check sorting
      assertTrue(casevalue >= lowest);
      lowest = casevalue;

      // check select option on edge
      switchvalueCell.setValue(casevalue);
      SwitchOption nr = b.getOptionToCall(switchvalueCell.getValue());
      assertEquals(switchOption, nr);

      // check select option on edge + a bit
      casevalue += 0.00000000000001;
      switchvalueCell.setValue(casevalue);
      SwitchOption nr2 = b.getOptionToCall(switchvalueCell.getValue());
      System.out.println("casevalue + a bit:" + casevalue + " IndexToCall:" + nr2);
      assertEquals(switchOption, nr2);// fails if two cases are just 0.00000000000001 away from eachother
    }
  }

  public void testClone() throws NoCellFoundException {

    NumberSwitchCell nsc = new NumberSwitchCell("c23", Integer.class, 12, -10000, +10000);

    ConstantCell c1 = new ConstantCell("const1", Integer.class, 50);
    ArrayList<ValueCell> cm = new ArrayList<ValueCell>();
    cm.add(c1);
    HashMap<Class<?>, ArrayList<CallTarget>> callTargetsByReturnType = new HashMap<Class<?>, ArrayList<CallTarget>>();
    c1.addCallTarget2(callTargetsByReturnType);
    nsc.connectCell(callTargetsByReturnType, cm);

    NumberSwitchCell clone = (NumberSwitchCell) nsc.clone();
    assertEquals(clone.getCalced(), 0);
    assertEquals(clone.getErrored(), 0);
    assertEquals(clone.getNumOptions(), nsc.getNumOptions());
    assertNotSame(clone.getOptions(), nsc.getOptions());
    for (int i = 0; i < clone.getOptions().size(); i++) {
      SwitchOption s1 = nsc.getOptions().get(i);
      SwitchOption s2 = clone.getOptions().get(i);
      assertNotSame(s1, s2);
    }

  }
}

class TestNumberSwitchCell extends NumberSwitchCell {
  public TestNumberSwitchCell(String name, Class<?> valueType, int maxOptionsNr, double minOptionValue,
      double maxOptionValue, ValueCell input) {
    super(name, valueType, maxOptionsNr, minOptionValue, maxOptionValue);
    setSwitchValueCell(input);
  }

  public void createOptions() {
    for (int i = 0; i < startNumOptions; i++) {
      // SwitchOption option =
      createAndAddOption();
    }
    setOptionValues();
  }
}
