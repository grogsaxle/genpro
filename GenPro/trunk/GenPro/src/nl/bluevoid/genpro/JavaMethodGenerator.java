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

import nl.bluevoid.genpro.cell.Calculable;
import nl.bluevoid.genpro.cell.CallCell;
import nl.bluevoid.genpro.cell.CellInterface;
import nl.bluevoid.genpro.cell.ConstantCell;
import nl.bluevoid.genpro.cell.InputCell;
import nl.bluevoid.genpro.cell.ReferenceCell;
import nl.bluevoid.genpro.cell.ValueCell;
import nl.bluevoid.genpro.cell.switx.BooleanSwitchCell;
import nl.bluevoid.genpro.cell.switx.NumberSwitchCell;
import nl.bluevoid.genpro.cell.switx.SwitchCell;
import nl.bluevoid.genpro.cell.switx.SwitchOption;
import nl.bluevoid.genpro.operations.BooleanOperations;
import nl.bluevoid.genpro.operations.IfOperations;
import nl.bluevoid.genpro.operations.NumberOperations;
import nl.bluevoid.genpro.util.Debug;
import nl.bluevoid.genpro.util.StringUtil;

/**
 * @author Rob van der Veer
 * @since 1.0
 */
public class JavaMethodGenerator {
  private final HashMap<String, String> imports = new HashMap<String, String>();

  private final ArrayList<String> globalAttributes = new ArrayList<String>();

  private final ArrayList<String> program = new ArrayList<String>();

  private final ArrayList<String> inputs = new ArrayList<String>();

  private boolean debug;

  private boolean showJunkDna;

  private ReferenceCell outputCell;

  public static String getJavaProgram(final Grid grid,final  String className,final String packageName,
      final Class<?> implInterface,final String remark,final boolean debug,final boolean showJunkDNA) {
    Debug.checkNotNull(grid, "grid");
    JavaMethodGenerator gen = new JavaMethodGenerator();
    return gen.getProgram(className, grid, packageName, implInterface, remark, debug, showJunkDNA);
  }

  public void addImport(final String packageOrClassName) {
    imports.put(packageOrClassName, packageOrClassName);
  }

  public void addGlobalAttribute(final ValueCell cell, final Object value) {
    final String v = value == null ? "" : " = " + value.toString();
    final String f = value == null ? "" : "final ";
    globalAttributes.add(f + cell.getValueType().getSimpleName() + " " + cell.getName() + getDebugInfo(cell)
        + v + ";" + (cell.isUsedForOutput() ? "  //to output" : ""));
  }

  // add more debug info
  private String getDebugInfo(final CellInterface cell) {
    return (debug ? " (" + cell.getSerialNr() + ") " : "");
  }

  public void addCell(final Calculable cell) {
    if (showJunkDna || cell.isUsedForOutput()) {
      switch (cell.getCellType()) {
      case CallCell:
        // addGlobalAttribute((CallCell) cell, null);
        addCall((CallCell) cell, 1);
        break;
      case BooleanSwitchCell:
        addCall((BooleanSwitchCell) cell);
        break;
      case NumberSwitchCell:
        addCall((NumberSwitchCell) cell);
        break;
      default:
        throw new IllegalArgumentException("not supported:" + cell.toString());
      }
    }
  }

  public void addCell(final InputCell cell) {
    inputs.add(cell.getValueType().getSimpleName() + " " + cell.getName());
  }

  private void addCell(final ConstantCell cell) {
    addGlobalAttribute(cell, cell.getValue());
  }

  public void addCell(final ReferenceCell cell) {
    if (outputCell == null) {
      this.outputCell = cell;
    } else {
      throw new IllegalArgumentException(
          "Cannot make a method with more than one output!, use program instead");
    }
  }

  public String getProgram(final String className, final Grid grid, final String packageName,
      final Class<?> implInterface, final String remark, final boolean debug, final boolean showJunkDNA) {
    this.debug = debug;
    this.showJunkDna = showJunkDNA;
    processCells(grid);

    final StringBuffer b = new StringBuffer();
    b.append("package " + packageName + ";");

    for (final String s : imports.keySet()) {
      if (!s.startsWith("java.lang.")) {
        b.append("\nimport ");
        b.append(s);
        b.append(";");
      }
    }

    b.append("\n\n//" + remark);
    b.append("\npublic class " + className);
    if (implInterface != null)
      b.append(" implements " + implInterface.getName());
    b.append(" {");
    
    b.append("\n\t//constants");
    for (final String s : globalAttributes) {
      b.append("\n\t");
      b.append(s);
    }

    // method signature
    b.append("\n\npublic " + outputCell.getValueType().getSimpleName() + " calc(");
    b.append(StringUtil.join(", ", inputs.toArray(new String[0])));
    b.append("){\n\t//callcells");

    for (final String s : program) {
      b.append("\n");
      b.append(s);
    }
    b.append("\n\n\treturn " + outputCell.getReferedCell().getName()+";");

    b.append("\n}}");
    return b.toString();
  }

  private void processCells(final Grid grid) {
    for (final InputCell c : grid.getInputCells()) {
      addCell(c);
    }

    for (final ConstantCell c : grid.getConstantCells()) {
      addCell(c);
    }

    // TODO more cells
    for (final Calculable c : grid.getCallCells()) {
      addCell(c);
    }

    for (final ReferenceCell c : grid.getOutputCells()) {
      addCell(c);
    }
  }

  private void addCall(final CallCell cell, final int indent) {
    String call = tab(indent);
    call += cell.getValueType().getSimpleName() + " " + cell.getName() + getDebugInfo(cell) + " = ";
    call += getCallMethod(cell);
    program.add(call);
  }

  private void addCall(SwitchOption cell, SwitchCell switchCell, int indent) {
    String call = tab(indent);
    call += switchCell.getName() + getDebugInfo(switchCell) + " = ";
    call += getCallMethod(cell);
    program.add(call);
  }

  /**
   * used to add call from ifcell
   * 
   * @param referenceCell
   * @param callCell
   * @param indent
   */
  // private void addCall(ReferenceCell referenceCell, CallCell callCell, int indent) {
  // String call = tab(indent);
  // call += referenceCell.getName() + getDebugInfo(referenceCell) + " = ";
  // call += getCallMethod(callCell);
  // program.add(call);
  // }
  private String tab(int indent) {
    switch (indent) {
    case 0:
      return "";
    case 1:
      return "\t";
    case 2:
      return "\t\t";
    case 3:
      return "\t\t\t";
    case 4:
      return "\t\t\t\t";
    case 5:
      return "\t\t\t\t\t";
    default:
      String call = "";
      for (int i = 0; i < indent; i++) {
        call += "\t";
      }
      return call;
    }
  }

  private String getCallMethod(final CallCell cell) {
    String call = "";
    final Class<?> valueClassType = cell.getTargetCell().getValueType();
    final String methodName = cell.getTargetMethod().getName();

    if (valueClassType.equals(NumberOperations.class)) {
      // we have a +,-,*,/,%, pow
      final String op = NumberOperations.getJavaSyntax(methodName);
      call = createJavaLine(cell, call, op);
    } else if (valueClassType.equals(BooleanOperations.class)) {
      // we have a <,>,==,!=,&&,||, etc
      final String op = BooleanOperations.getJavaSyntax(methodName);
      call = createJavaLine(cell, call, op);
    } else if (valueClassType.equals(IfOperations.class)) {
      // we have xxxxIf
      final String op = IfOperations.getJavaSyntax(methodName);
      call = createJavaLine(cell, call, op);
    } else {
      // we have a method call
      final String paramsStr = joinParams(cell.getParams(), ",");
      call += cell.getTargetCell().getName() + "." + cell.getTargetMethod().getName() + "(" + paramsStr
          + ");";
      addImport(cell.getTargetCell().getValueType().getName());
    }
    call += "  //" + (cell.isUsedForOutput() ? "to output, " : "");
    call += (cell.isLeadsToInputCell() ? "to input" : "");
    call += ", calls/errors:" + cell.getCalced() + "/" + cell.getErrored();
    addImport(cell.getValueType().getName());
    return call;
  }

  private String createJavaLine(final CallCell cell, final String call, final String op) {
    final boolean isFillInCode = op.indexOf("${") >= 0;
    String operation = null;
    if (isFillInCode) {
      operation = pasteCellNamesInPlaces(op, cell.getParams());
    } else {
      operation = joinParams(cell.getParams(), " " + op + " ");
      if (cell.getParams().length <= 1)// solve single or zero param operator:postfix
        operation += op;
    }
    return operation + ";";
  }

  private static String pasteCellNamesInPlaces(final String text, final ValueCell[] params) {
    final int start = text.indexOf("${");
    // is tag found?
    String result = text;
    if (start >= 0) {
      final int end = text.indexOf("}", start);
      if (end < 0)
        throw new IllegalArgumentException("after opening tag '${' no closing tag '}' found in: " + text);
      final String tag = text.substring(start + 2, end);
      final String replacement = params[Integer.parseInt(tag)].getName();
      // replace tag
      final String textNew = text.substring(0, start) + replacement + text.substring(end + 1);
      // find next tag recursive...
      result = pasteCellNamesInPlaces(textNew, params);
    }
    return result;
  }

  private void addCall(final BooleanSwitchCell cell) {
    program.add(tab(1) + cell.getValueType().getSimpleName() + " " + cell.getName() + " = null;");
    program.add(tab(1) + "if ( " + cell.getSwitchValueCell().getName() + " ){");
    addCall(cell.getOptions().get(1), 2);
    program.add(tab(1) + "} else {");
    addCall(cell.getOptions().get(0), 2);
    program.add(tab(1) + "}");
  }

  private void addCall(NumberSwitchCell cell) {
    program.add(tab(1) + cell.getValueType().getSimpleName() + " " + cell.getName() + " = null;");
    ArrayList<SwitchOption> options = cell.getOptions();
    String inputName = cell.getSwitchValueCell().getName();
    String ifelse = "if ( ";
    for (int i = options.size() - 1; i >= 0; i--) {
      SwitchOption opt = options.get(i);
      if (i == 0) {// last line
        program.add(tab(1) + "} else {");
      } else {
        program.add(tab(1) + ifelse + inputName + " > " + opt.getSwitchCaseValue() + " ){");
      }
      addCall(opt, cell, 2);
      ifelse = "} else if ( ";
    }
    program.add(tab(1) + "}");
  }

  private String joinParams(ValueCell[] cells, String seperator) {
    final String[] names = new String[cells.length];
    for (int i = 0; i < names.length; i++) {
      names[i] = cells[i].getName() + getDebugInfo(cells[i]);
    }
    return StringUtil.join(seperator, names);
  }
}