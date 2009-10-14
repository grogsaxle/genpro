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

package nl.bluevoid.genpro.cell;

import nl.bluevoid.genpro.util.XMLBuilder;

public class InputCell extends ValueCell {

  public InputCell(String name, Class<?> valueType) {
    super(name, valueType, false, CellTypeEnum.InputCell);
    setLeadsToInputCell(true);
  }

  public boolean canMutate() {
    return false;
  }

  public void getXML(XMLBuilder x) {
    x.startTag(getClass().getName());
    x.add("name", getName());
    x.add("type", getValueType().getName());
    x.endTag();
  }
}
