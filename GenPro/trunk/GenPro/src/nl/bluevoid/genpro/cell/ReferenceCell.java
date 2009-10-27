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

package nl.bluevoid.genpro.cell;

import nl.bluevoid.genpro.CellMap;
import nl.bluevoid.genpro.util.XMLBuilder;
/**
 * @author Rob van der Veer
 * @since 1.0
 */
public class ReferenceCell extends ValueCell {

  protected ValueCell referredCell;

  /**
   * This cell points to another cell from which it gets its value. This is handy for creating output-cells
   * which can point to any other cell for its value. Output cells used to extend callcells, but should then
   * also mutate cross etc. Just referring is a much better way. Referring cells always point to an object
   * (the value), so they are never static.
   * 
   * @param name
   * @param valueType
   * 
   */
  public ReferenceCell(String name, Class<?> valueType) {
    super(name, valueType, false, CellTypeEnum.ReferenceCell);
    super.setUsedForOutput(true);
  }

  @Override
  public void setValue(Object value) {
    throw new IllegalArgumentException("refered cells can not be written! Only read");
  }

  /**
   * returns the value of the referred cell
   */
  @Override
  public Object getValue() {
    if (referredCell == null)
      return null;
    return referredCell.getValue();
  }

  public ValueCell getReferedCell() {
    return referredCell;
  }

  public void setReferedCell(final ValueCell referedCell) {
    // Assert.assertSame(referedCell.getValueType(), valueType);
    if (!referedCell.getValueType().equals(valueType)) {
      throw new IllegalArgumentException("wrong type, expected:" + valueType + " but got:"
          + referedCell.getValueType() + " on cell" + this);
    }
    this.referredCell = referedCell;
  }

  public void restoreConnections(final CellMap map) throws NoCellFoundException {
    setReferedCell(map.getByNameOrValueType(referredCell));
    // System.out.println( referredCell.getName()+" found "+getReferedCell().getSerialNr());
  }

  @Override
  public String toString() {
    String clazz = (valueType == null) ? "null" : valueType.getSimpleName() + ".class";
    String value = "";
    if (referredCell == null)
      value = "null";
    else
      value = referredCell.getName() + ", value:"
          + ((referredCell.getValue() == null) ? "null" : referredCell.getValue());

    return getClass().getSimpleName() + " " + getName() + " - content/obj: " + clazz + "/ " + "refered cell:"
        + value;
  }

  public boolean canMutate() {
    return true;
  }

  public void getXML(XMLBuilder x) {
    x.startTag(getClass().getName());
    x.add("name", getName());
    x.add("type", getValueType().getName());
    x.add("referenced", referredCell.getName());
    x.endTag();
  }

  public void validateLeadsToInputCell() {
    setLeadsToInputCell(referredCell.isLeadsToInputCell());
  }

  public void setCascadeUsedForOutput() {
    super.setUsedForOutput(true);
    if (referredCell instanceof Calculable) {
      ((Calculable) referredCell).setCascadeUsedForOutput();
    } else {
      referredCell.setUsedForOutput(true);
    }
  }
}
