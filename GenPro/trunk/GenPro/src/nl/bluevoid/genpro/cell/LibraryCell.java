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

public class LibraryCell extends ValueCell {

  public LibraryCell(String name, Class<?> contentClass) {
    super(name, contentClass, true, CellTypeEnum.LibraryCell);
  }

  public LibraryCell(String name, Class<?> contentClass, Object contentObject) {
    super(name, contentClass, contentObject, CellTypeEnum.LibraryCell);
  }

  public LibraryCell(Class<?> contentClass) {
    super(contentClass.getSimpleName(), contentClass, true, CellTypeEnum.LibraryCell);
  }

  public boolean canMutate() {
    return false;
  }

  public void getXML(XMLBuilder x) {
    x.add(getClass().getName(), super.valueType.getName());
  }
}
