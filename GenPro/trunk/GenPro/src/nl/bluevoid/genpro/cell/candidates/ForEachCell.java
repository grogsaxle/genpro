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

package nl.bluevoid.genpro.cell.candidates;

/**
 * @author Rob van der Veer
 * @since 1.0
 */
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
