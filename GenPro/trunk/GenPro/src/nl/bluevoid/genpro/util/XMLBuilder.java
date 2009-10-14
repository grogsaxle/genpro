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

import java.util.LinkedList;

public class XMLBuilder {
  StringBuilder builder = new StringBuilder();
  private int indent = 0;

  LinkedList<String> l = new LinkedList<String>();

  public void add(String tagname, String content) {
    indent();
    builder.append("<" + tagname + ">");
    builder.append(content);
    builder.append("</" + tagname + ">\n");
  }

  public void startTag(String tagname) {
    indent();
    builder.append("<" + tagname + ">\n");
    indent++;
    l.addLast(tagname);
  }

  public void endTag() {
    indent--;
    indent();
    builder.append("</" + l.removeLast() + ">\n");
  }

  private void indent() {
    for (int i = 0; i < indent; i++) {
      builder.append("  ");
    }
  }

  @Override
  public String toString() {
    return builder.toString();
  }
}
