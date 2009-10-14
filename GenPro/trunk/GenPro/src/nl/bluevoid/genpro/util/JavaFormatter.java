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

/*
 Copyright (C) 2005 Eric Marion

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import java.util.Stack;

public class JavaFormatter extends BaseFormatter {
  public JavaFormatter() {
  }

  public String getName() {
    return "Standard";
  }

  public String format(String code) {
    String result = "";
    boolean addbefore = false, addafter = false;
    String toaddbefore = "", toaddafter = "";
    Stack beforeparens = new Stack();
    Stack sindent = new Stack();
    int indent = 0;

    tokenize(code);

    for (tokennum = 0; tokennum < tokens.size(); tokennum++) {
      addbefore = addafter;
      toaddbefore = toaddafter;
      addafter = false;
      toaddafter = " ";
      String token = (String) tokens.get(tokennum);

      addafter = true;
      toaddafter = " ";

      if (token.startsWith("//")) {
        addbefore = true;
        addafter = true;

        toaddbefore = newline(indent);
        toaddafter = newline(indent);
      } else if (token.startsWith("/*")) {
        addbefore = true;
        addafter = true;

        toaddbefore = "\n" + newline(indent);
        toaddafter = newline(indent);
      } else if (curIs(";")) {
        if (!isTop(beforeparens, "for")) {
          addbefore = false;
          addafter = true;

          boolean should = isTop(sindent, "1");

          while (isTop(sindent, "1")) {
            sindent.pop();
            indent--;
          }

          toaddafter = newline(indent);

          if (should)
            toaddafter = "\n" + toaddafter;

        } else {
          if (!prevIs("(")) { // )
            addbefore = false;
            addafter = true;
            toaddafter = " ";
          } else {
            addbefore = true;
            addafter = true;
            toaddbefore = " ";
            toaddafter = " ";
          }
        }
      } else if (curIs(".")) {
        addbefore = false;
        addafter = false;
      } else if (curIs("{") && !prevIs("=")) {
        addbefore = true;
        addafter = true;
        toaddbefore = " ";

        sindent.push("{"); // }
        indent++;
        toaddafter = newline(indent);
      } else if (curIs("}") && !nextIs(";") && !prevIs("{")) {
        addbefore = true;
        addafter = true;

        if (!sindent.empty() && sindent.peek().equals("c")) {
          sindent.pop();
          indent--;
        }

        sindent.pop();
        indent--;

        toaddbefore = newline(indent);
        toaddafter = newline(indent);

        if (!(nextIs("else") || nextIs("catch"))) {
          while (isTop(sindent, "1")) {
            sindent.pop();
            indent--;
          }

          toaddafter = newline(indent);

          if (!nextIs("}")) // {
            toaddafter = "\n" + toaddafter;

        } else {
          toaddafter = " ";
        }
      } else if (curIs("}") && prevIs("{")) {
        addbefore = true;
        addafter = true;

        sindent.pop();
        indent--;

        toaddbefore = " ";
        toaddafter = newline(indent);

        if (!nextIs("}"))
          toaddafter = "\n" + toaddafter;

        if (nextIs("else") || nextIs("catch")) {
          toaddbefore = newline(indent);
          toaddafter = " ";
        }

      } else if (curIs("[") && !nextIs("]")) {
        addbefore = false;
        addafter = true;
        toaddafter = " ";
      } else if (curIs("[") && nextIs("]")) {
        addbefore = false;
        addafter = false;
      } else if (curIs("]") && !prevIs("[")) {
        addbefore = true;
        addafter = true;
        toaddbefore = " ";
        toaddafter = " ";
      } else if (curIs("]") && prevIs("[")) {
        addbefore = false;
        addafter = true;
        toaddafter = " ";
      } else if (curIs("(") && !nextIs(")")) {
        addbefore = false;
        addafter = true;
        toaddafter = " ";

        if (!prevIs("!") && tokennum - 1 < tokens.size()) {
          String p = (String) tokens.get(tokennum - 1);
          char q = p.charAt(0);

          if (!((q >= 'a' && q <= 'z') || (q >= 'A' && q <= 'Z'))) {
            addbefore = true;
            toaddbefore = " ";
          }
        }

        if (prevIs(";") || prevIs("}")) {
          addbefore = true;

          toaddbefore = newline(indent);
        }

        beforeparens.push(tokens.get(tokennum - 1));
      } else if (curIs("(") && nextIs(")")) {
        addbefore = false;
        addafter = false;
      } else if (curIs(")") && !prevIs("(")) {
        addbefore = true;
        addafter = true;
        toaddbefore = " ";
        toaddafter = " ";

        if ((isTop(beforeparens, "for") || isTop(beforeparens, "while") || isTop(beforeparens, "if"))
            && !nextIs("{")) {
          // )))
          indent++;
          sindent.push("1");

          toaddafter = newline(indent);
        }

        beforeparens.pop();
      } else if (curIs(")") && prevIs("(")) {
        addbefore = false;
        addafter = true;
        toaddafter = " ";
      } else if (curIs("++")) {
        addbefore = false;
        addafter = true;

        toaddafter = " ";
      } else if (curIs("--")) {
        addbefore = false;
        addafter = true;

        toaddafter = " ";
      } else if (curIs(",")) {
        addbefore = false;
        addafter = true;
        toaddafter = " ";
      } else if (curIs("!")) {
        addbefore = true;
        addafter = false;
        toaddbefore = " ";
      } else if (curIs(":")) {
        addbefore = false;
        addafter = true;
        toaddafter = newline(indent);
      } else if (curIs("else") && !nextIs("if") && !nextIs("{")) { // }
        addbefore = true;
        addafter = true;
        toaddbefore = newline(indent);
        indent++;
        sindent.push("1");
        toaddafter = newline(indent);
      } else if (curIs("case") || curIs("default")) {
        if (!(!sindent.empty() && sindent.peek().equals("c"))) {
          sindent.push("c");

          indent++;
        }

        addbefore = true;
        addafter = true;
        toaddbefore = newline(indent - 1);
        toaddafter = " ";
      } else if (curIs("for") || curIs("while") || curIs("do")) {
        addbefore = true;
        addafter = false;

        toaddbefore = "\n" + newline(indent);
      } else if (curIs("return") && !prevIs("{") && !prevIs("}")) {
        addbefore = true;
        toaddbefore = "\n" + newline(indent);
      }

      if (addbefore)
        result += toaddbefore + token;
      else
        result += token;
    }

    return result;
  }

  private String newline(int indent) {
    String result = "\n";

    for (int count = 0; count < indent; count++)
      result += "   ";

    return result;
  }

  private boolean isTop(Stack s, String str) {
    if (s.empty())
      return false;

    return s.peek().equals(str);
  }
}
