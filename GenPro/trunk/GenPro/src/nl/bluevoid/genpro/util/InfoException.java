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

package nl.bluevoid.genpro.util;

import java.util.ArrayList;

import nl.bluevoid.genpro.util.StringUtil;

@SuppressWarnings("serial")
public class InfoException extends Exception {

  private ArrayList<String> info = new ArrayList<String>();

  public InfoException(String message, Throwable cause) {
    super(message, cause);
  }

  public InfoException(Throwable cause) {
    super(cause);
  }

  public InfoException() {
  }

  public void addInfo(String info) {
    this.info.add(info);
  }

  public void addInfo(String[] info) {
    for (String string : info) {
      addInfo(string);
    }
  }
  
  public void addInfo(ArrayList<String> info) {
    for (String string : info) {
      addInfo(string);
    }
  }

  public void addInfoSeperator() {
    addInfo("-----------------------------------------------");
  }

  public InfoException(String string) {
    super(string);
    addInfo(super.getMessage());
  }

  @Override
  public String getMessage() {
    return StringUtil.join("\n", info.toArray());
  }
}
