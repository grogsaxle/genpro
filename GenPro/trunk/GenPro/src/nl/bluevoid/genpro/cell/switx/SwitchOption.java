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

import nl.bluevoid.genpro.cell.CallCell;
import nl.bluevoid.genpro.util.Debug;

public class SwitchOption extends CallCell implements Comparable<SwitchOption> {
  final SwitchCell switchCell;

  // The "case" value
  @SuppressWarnings("unchecked")
  protected Comparable switchCaseValue; 

  public SwitchOption(String name, SwitchCell switchCell) {
    super(name, switchCell.getValueType());
    this.switchCell = switchCell;
    Debug.checkNotNull(switchCell, "switchCell");
  }

  public Comparable<?> getSwitchCaseValue() {
    return switchCaseValue;
  }

  // calc calls setvalue, the value should be forwarded to the switches value
  @Override
  public void setValue(Object value) {
    switchCell.setValue(value);
  }

  // for sorting
  @SuppressWarnings("unchecked")
  public int compareTo(SwitchOption o) {
    return switchCaseValue.compareTo(o.switchCaseValue);
  }

  public String toString() {
    return "switch on case:" + switchCaseValue + " " + super.toString();
  }
  
  @Override
  public SwitchOption clone() {
    SwitchOption opt=(SwitchOption)super.clone();
    return opt;
    //opt.switchCaseValue=(Comparable)((Object)switchCaseValue).clone();
  }
}
