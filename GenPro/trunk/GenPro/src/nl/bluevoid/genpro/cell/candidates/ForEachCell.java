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

package nl.bluevoid.genpro.cell.candidates;


public class ForEachCell extends SubroutineCell {

  public ForEachCell(String name) {
    super(name);
  }

  /*
   * (non-javadoc)
   */
  private Object[] objectList;

  public void calc() {
    // TODO Auto-generated method stub
    throw new IllegalStateException("calc is not implemented yet");
  }

  /**
   * Getter of the property <tt>objectList</tt>
   * 
   * @return Returns the objectList.
   * 
   */

  public Object[] getObjectList() {
    return objectList;
  }

  /**
   * Setter of the property <tt>objectList</tt>
   * 
   * @param objectList
   *            The objectList to set.
   * 
   */
  public void setObjectList(Object[] objectList) {
    this.objectList = objectList;
  }

}
