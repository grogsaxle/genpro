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

import nl.bluevoid.genpro.Util;
import nl.bluevoid.genpro.util.Debug;
import nl.bluevoid.genpro.util.XMLBuilder;

public class ConstantCell extends ValueCell {

  private final double range;
  private final double min;
  private final double max;
  private final boolean canMutate;

  public ConstantCell(String name, Class<?> constantClass, double min, double max) {
    super(name, constantClass, false, CellTypeEnum.ConstantCell);
    Debug.errorOnTrue(max < min, "max is smaller than min!", max, " ", min);
    this.min = min;
    this.max = max;
    range = max - min;
    canMutate = true;
    setRandomValue();
  }

  public ConstantCell(String name, Class<?> constantClass, double value) {
    super(name, constantClass, false, CellTypeEnum.ConstantCell);
    this.min = value;
    this.max = value;
    this.range = 0;
    canMutate = false;
    if (valueType.equals(Double.class)) {
      setValue(min);
    } else if (valueType.equals(Integer.class)) {
      setValue((int) min);
    } else {
      throw new IllegalArgumentException("class not supported:" + valueType.getName());
    }
  }

  public ConstantCell(ConstantCell c) {
    this(c.getName(), c.getValueType(), c.min, c.max);
  }

  public ConstantCell(String name, Class<String> constantClass, String value) {
    super(name, constantClass, false, CellTypeEnum.ConstantCell);
    canMutate=false;
    setValue(value);
    this.range=0;
    this.min = 0;
    this.max = 0;
  }
  
  public void setRandomValue() {
    if (valueType.equals(Double.class)) {
      setValue(random.nextDouble() * range + min);
    } else if (valueType.equals(Integer.class)) {
      setValue((int)(random.nextInt((int) range) + min));
    } else {
      throw new IllegalArgumentException("class not supported:" + valueType.getName());
    }
  }

  public void mutate() {
    if (canMutate) {
      if (super.valueType.equals(Double.class)) {
        setValue(Util.mutateperc((Double) getValue(), 20, min, max));
      } else if (super.valueType.equals(Integer.class)) {
        final int val = Util.mutateperc((Integer) getValue(), 100, min, max).intValue();
        setValue(val);
      } else {
        throw new IllegalArgumentException("class not supported:" + valueType.getName());
      }
    } else {
      throw new IllegalStateException("Can not mutate!:" + this);
    }
  }

  public ConstantCell clone() {
    ConstantCell c = (ConstantCell) super.clone();
    // new ConstantCell(getName(),getValueType(), min, max);
    // c.setValue(getValue());
    return c;
  }

  public double getRange() {
    return range;
  }

  public double getMin() {
    return min;
  }

  public double getMax() {
    return max;
  }

  public boolean canMutate() {
    return canMutate;
  }

  public void getXML(XMLBuilder x) {
    x.startTag(getClass().getName());
    x.add("name", getName());
    x.add("type", getValueType().getName());
    x.add("canMutate", "" + canMutate);
    if (canMutate) {
      x.add("min", "" + min);
      x.add("max", "" + max);
    }
    x.add("value", "" + getValue());
    x.endTag();
  }
}
