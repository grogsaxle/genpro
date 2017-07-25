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

package nl.bluevoid.genpro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import nl.bluevoid.genpro.cell.Calculable;
import nl.bluevoid.genpro.cell.CallCell;
import nl.bluevoid.genpro.cell.Cell;
import nl.bluevoid.genpro.cell.CellInterface;
import nl.bluevoid.genpro.cell.CellTypeEnum;
import nl.bluevoid.genpro.cell.NoCellFoundException;
import nl.bluevoid.genpro.cell.ValueCell;
import nl.bluevoid.genpro.cell.switx.BooleanSwitchCell;
import nl.bluevoid.genpro.cell.switx.NumberSwitchCell;
import nl.bluevoid.genpro.cell.switx.SwitchCell;
import nl.bluevoid.genpro.util.Debug;
import nl.bluevoid.genpro.util.ReflectUtil;
import nl.bluevoid.genpro.util.StringUtil;

/**
 * @author Rob van der Veer
 * @since 1.0
 */
public class Util {

  public static final Random random = new Random(System.currentTimeMillis() % 300000);

  public static final CellInterface[] clone(final Cell[] constantCells2) {
    final Cell[] obj = constantCells2.clone();
    for (int i = 0; i < obj.length; i++) {
      obj[i] = (Cell) (obj[i]).clone();
    }
    return obj;
  }

  public static final CellInterface[] clone(final CellInterface[] constantCells2) {
    final CellInterface[] obj = constantCells2.clone();
    for (int i = 0; i < obj.length; i++) {
      obj[i] = (CellInterface) (obj[i]).clone();
    }
    return obj;
  }

  public static final String[] clone(final String[] strArray) {
    final String[] obj = strArray.clone();
    return obj;
  }

  public static final Double mutateperc(final Double value, final double percentage, final double min,
      final double max) {

    // get number form -percentage till +percentage
    final double percentageDelta = (random.nextDouble() * percentage * 2 - percentage);
    // Debug.checkRange(percentageDelta, -percentage, percentage);
    double newVal = value + percentageDelta * value / 100;
    newVal = Math.max(newVal, min);// take biggest > altijd boven min
    return Math.min(newVal, max);// take smallest > altijd onder max
  }

  public static Integer mutateperc(final Integer value, final int percentage, final double min,
      final double max) {
    // get number from 1 till +percentage of range
    final int range = (int) Math.abs(max - min);
    final int delta = random.nextInt((range * percentage / 100) - 1) + 1;
    int newVal = value + (random.nextBoolean() ? delta : -delta);
    newVal = (int) Math.max(newVal, min);// take biggest > altijd boven min
    return (int) Math.min(newVal, max);// take smallest > altijd onder max
  }

  public static final String toStringCells(final CellInterface[] cells) {
    StringBuffer b = new StringBuffer();
    if (cells == null)
      return "No cells!";
    for (final CellInterface cell : cells) {
      b.append("    nr:" + cell.getSerialNr() + " " + cell.toString() + "\n");
    }
    return b.toString();
  }

  public static final ValueCell getRandomCell(final ArrayList<ValueCell> cells) throws NoCellFoundException {
    if (cells.size() == 0)
      throw new NoCellFoundException();
    final ValueCell cell = cells.get(random.nextInt(cells.size()));
    Debug.checkNotNull(cell, "cell");
    return cell;
  }

  public static final ValueCell getRandomCell(final ValueCell[] cells) throws NoCellFoundException {
    if (cells.length == 0)
      throw new NoCellFoundException();
    final ValueCell cell = cells[random.nextInt(cells.length)];
    // Debug.checkNotNull(cell, "cell");
    return cell;
  }

  public static final void addCells(final CellInterface[] cells, final ArrayList arraylist) {
    for (final CellInterface cell : cells) {
      arraylist.add(cell);
    }
  }

  public static final void addCells(final ValueCell[] cells, final ArrayList arraylist) {
    for (final ValueCell cell : cells) {
      arraylist.add(cell);
    }
  }

  public static final ValueCell getRandomCell(final Class<?> contentClass, final ArrayList<ValueCell> cells)
      throws NoCellFoundException {
    final ArrayList<ValueCell> typedCells = new ArrayList<ValueCell>();
    for (final ValueCell cell : cells) {
      final Class<?> content = cell.getValueType();
      if (content.equals(contentClass) || ReflectUtil.canCastNumber(content, contentClass)) {
        typedCells.add(cell);
      }
    }
    try {
      return Util.getRandomCell(typedCells);
    } catch (NoCellFoundException e) {
      e.addInfo("\n nocell found of type:" + contentClass);
      e.addInfo("cells:\n" + StringUtil.join("\n", cells.toArray()));
      e.addInfoSeperator();
      throw e;
    }
  }

  public static final ValueCell getRandomCellFromCalculables(final Class<?> contentClass,
      final Calculable[] cells, final boolean needsToLeadToInput) throws NoCellFoundException {
    final ArrayList<ValueCell> typedCells = new ArrayList<ValueCell>();
    for (final Calculable cell : cells) {
      if(needsToLeadToInput && cell.isLeadsToInputCell())
      switch (cell.getCellType()) {
      case CallCell: {
        addIfMatches(contentClass, typedCells, (CallCell) cell);
      }
        break;
      case BooleanSwitchCell: {
        addIfMatches(contentClass, typedCells, (BooleanSwitchCell) cell);
      }
        break;
      case NumberSwitchCell: {
        addIfMatches(contentClass, typedCells, (NumberSwitchCell) cell);
      }
        break;
      // case IfCell: {
      // for (ReferenceCell referenceCell : ((IfCell) cell).getValueCells()) {
      // addIfMatches(contentClass, typedCells, referenceCell);
      // }
      // }
      // break;
      default:
        throw new IllegalArgumentException("not supported:" + cell.getCellType());
      }
    }
    return Util.getRandomCell(typedCells);
  }

  private static void addIfMatches(final Class<?> contentClass, final ArrayList<ValueCell> typedCells,
      final ValueCell cell) {
    final Class<?> content = cell.getValueType();
    if (content.equals(contentClass) || ReflectUtil.canCastNumber(content, contentClass)) {
      typedCells.add(cell);
    }
  }

  public static final ValueCell getRandomCell(final Class<?> contentClass, final ValueCell[] cells)
      throws NoCellFoundException {
    final ArrayList<ValueCell> typedCells = new ArrayList<ValueCell>();
    for (final ValueCell cell : cells) {
      final Class<?> content = cell.getValueType();
      if (content.equals(contentClass) || ReflectUtil.canCastNumber(content, contentClass)) {
        typedCells.add(cell);
      }
    }
    return Util.getRandomCell(typedCells);
  }

  public static final void printCallTargets(
      final HashMap<Class<?>, ArrayList<CallTarget>> callTargetsByReturnType) {
    for (final Class<?> ret : callTargetsByReturnType.keySet()) {
      System.out.println("Methods for returnType:" + ret.getName());
      for (CallTarget ct : callTargetsByReturnType.get(ret)) {
        System.out.println("   " + ct);
      }
    }
  }

  public static final void addCallTargets(final ValueCell[] cells,
      final HashMap<Class<?>, ArrayList<CallTarget>> callTargetsByReturnType) {
    for (final ValueCell valueCell : cells) {
      valueCell.addCallTarget2(callTargetsByReturnType);
      // Util.addCallTarget2(valueCell, callTargetsByReturnType);
    }
  }

  public static final void addCallTargets(final ArrayList<? extends ValueCell> cells,
      final HashMap<Class<?>, ArrayList<CallTarget>> callTargetsByReturnType) {
    for (final ValueCell valueCell : cells) {
      valueCell.addCallTarget2(callTargetsByReturnType);
      // Util.addCallTarget2(valueCell, callTargetsByReturnType);
    }
  }

  // private static final void addCallTarget2(ValueCell valueCell,
  // HashMap<Class, ArrayList<CallTarget>> callTargetsByReturnType) {
  // for (final Method m : valueCell.getAllMethods()) {
  // CallTarget c = new CallTarget(valueCell, m);
  // Class ret = ReflectUtil.getClassForPrimitive(m.getReturnType());
  // ArrayList<CallTarget> arr = callTargetsByReturnType.get(ret);
  // if (arr == null) {
  // arr = new ArrayList<CallTarget>();
  // callTargetsByReturnType.put(ret, arr);
  // }
  // arr.add(c);
  // }
  // }

  public static final void addCallTarget(final Calculable valueCell,
      final HashMap<Class<?>, ArrayList<CallTarget>> callTargetsByReturnType) {
    if (valueCell instanceof ValueCell) {
      ((ValueCell) valueCell).addCallTarget2(callTargetsByReturnType);
      // addCallTarget2((ValueCell) valueCell, callTargetsByReturnType);
    } else if (valueCell.getCellType() == CellTypeEnum.IfCell) {
      throw new IllegalStateException("not implemented");
    }
  }

  public static final void addUsedCellsRecursivly(final CellMap used, ValueCell cc) {
    switch (cc.getCellType()) {
    case CallCell:
      addUsedCellsRecursivly(used, (CallCell) cc);
      break;
    case NumberSwitchCell:
    case BooleanSwitchCell:
      addUsedCellsRecursivly(used, (SwitchCell) cc);
      break;
    case ConstantCell:
      used.putByName(cc);
      break;
    case IfCell:
    default:
      throw new IllegalArgumentException("not supported:" + cc.getCellType());
    }
  }

  private static final void addUsedCellsRecursivly(final CellMap used, final CallCell cc) {
    used.putByName(cc);
    used.putByName(cc.getTargetCell());
    used.putByName(cc.getParams());

    if (cc.getTargetCell().getCellType() == CellTypeEnum.CallCell) {
      addUsedCellsRecursivly(used, (CallCell) cc.getTargetCell());
    }
    for (final ValueCell pcell : cc.getParams()) {
      if (pcell.getCellType() == CellTypeEnum.CallCell) {
        addUsedCellsRecursivly(used, (CallCell) pcell);
      }
    }
  }

  private static final void addUsedCellsRecursivly(final CellMap used, final SwitchCell cc) {
    used.putByName(cc);
    used.putByName(cc.getSwitchValueCell());
  }

  private static final ValueCell[] emptyValueCell = new ValueCell[0];

  public static ValueCell[] getRandomParamsCells(final CallTarget ct, final ArrayList<ValueCell> allParamCells)
      throws NoCellFoundException {
    final Class<?>[] paramTypesNeeded = ct.method.getParameterTypes();
    if (paramTypesNeeded.length == 0) {
      return emptyValueCell;
    } else {
      final ValueCell[] paramCells = new ValueCell[paramTypesNeeded.length];
      for (int i = 0; i < paramTypesNeeded.length; i++) {
        paramCells[i] = Util.getRandomCell(ReflectUtil.getClassForPrimitive(paramTypesNeeded[i]),
            allParamCells);
      }
      return paramCells;
    }
  }

}
