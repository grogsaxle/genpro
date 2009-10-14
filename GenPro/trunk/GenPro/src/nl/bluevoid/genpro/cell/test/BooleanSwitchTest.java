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

package nl.bluevoid.genpro.cell.test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import junit.framework.TestCase;
import nl.bluevoid.genpro.GridExecutionError;
import nl.bluevoid.genpro.cell.InputCell;
import nl.bluevoid.genpro.cell.ValueCell;
import nl.bluevoid.genpro.cell.switx.BooleanSwitchCell;
import nl.bluevoid.genpro.cell.switx.SwitchOption;

public class BooleanSwitchTest extends TestCase {

  public void test1() throws IllegalAccessException, InvocationTargetException, GridExecutionError {
    int NR_OPTIONS = 2;

    ValueCell switchvalueCell = new InputCell("in", Boolean.class);
    TestBooleanSwitchCell b = new TestBooleanSwitchCell("b", Double.class, switchvalueCell);
    

    b.createOptions();
    assertEquals(NR_OPTIONS, b.getNumOptions());
    // check options nr
    ArrayList<SwitchOption> options = b.getOptions();
    assertEquals(NR_OPTIONS, options.size());
    assertTrue("" + options.size() + " / " + NR_OPTIONS, options.size() == NR_OPTIONS);
    System.out.println(b);

    // check false
    SwitchOption switchOptionFalse = b.getOptions().get(0);
    String falseOptName=switchOptionFalse.getName();
    {
      System.out.println("testing=" + 0 + ": " + switchOptionFalse);
      Boolean casevalue = (Boolean) switchOptionFalse.getSwitchCaseValue();
      System.out.println("casevalue:" + casevalue);
      assertTrue(casevalue.equals(Boolean.FALSE));
      switchvalueCell.setValue(Boolean.FALSE);
      SwitchOption nr = b.getOptionToCall(switchvalueCell.getValue());
      assertEquals(switchOptionFalse, nr);
    }

    // check true
    SwitchOption switchOptionTrue = b.getOptions().get(1);
    String trueOptName=switchOptionTrue.getName();
    {
      System.out.println("testing=" + 1 + ": " + switchOptionTrue);
      Boolean casevalue = (Boolean) switchOptionTrue.getSwitchCaseValue();
      System.out.println("casevalue:" + casevalue);
      assertTrue(casevalue.equals(Boolean.TRUE));
      switchvalueCell.setValue(Boolean.TRUE);
      SwitchOption nr = b.getOptionToCall(switchvalueCell.getValue());
      assertEquals(switchOptionTrue, nr);
    }

    b.mutateRandomOptionValue();
    System.out.println(b.toString());
    {
      switchvalueCell.setValue(Boolean.TRUE);
      SwitchOption nr = b.getOptionToCall(switchvalueCell.getValue());
      assertEquals(nr.getName(), falseOptName); // should be reversed
    }
    {
      switchvalueCell.setValue(Boolean.FALSE);
      SwitchOption nr = b.getOptionToCall(switchvalueCell.getValue());
      assertEquals(nr.getName(), trueOptName); // should be reversed
    }
  }
}

class TestBooleanSwitchCell extends BooleanSwitchCell {
  public TestBooleanSwitchCell(String name, Class<?> valueType, ValueCell input) {
    super(name, valueType);
    setSwitchValueCell(input);
  }

  public void createOptions() {
    for (int i = 0; i < maxOptions; i++) {
      SwitchOption option = createAndAddOption();
      System.out.println("created option: " + option);
    }
    setOptionValues();
  }
}
