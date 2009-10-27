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

import nl.bluevoid.genpro.cell.CallCell;
import nl.bluevoid.genpro.util.Debug;
/**
 * @author Rob van der Veer
 * @since 1.0
 */
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
