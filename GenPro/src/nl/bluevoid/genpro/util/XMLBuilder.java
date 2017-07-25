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

package nl.bluevoid.genpro.util;

import java.util.LinkedList;
/**
 * @author Rob van der Veer
 * @since 1.0
 */
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
