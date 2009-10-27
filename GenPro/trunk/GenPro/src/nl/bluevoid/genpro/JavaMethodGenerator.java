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
import nl.bluevoid.genpro.operations.NumberOperations;
import nl.bluevoid.genpro.util.Debug;
import nl.bluevoid.genpro.util.StringUtil;
/**
 * @author Rob van der Veer
 * @since 1.0
 */
public class JavaMethodGenerator {
  HashMap<String, String> imports = new HashMap<String, String>();

  ArrayList<String> globalAttributes = new ArrayList<String>();

  // ArrayList<String> methods = new ArrayList<String>();

  ArrayList<String> program = new ArrayList<String>();

  ArrayList<String> inputs = new ArrayList<String>();

  private boolean debug;

  private ReferenceCell outputCell;

  public static void printJavaProgram(Grid grid, String className, String packageName,
      Class<?> implInterface, String remark, boolean debug) {
    String java = getJavaProgram(grid, className, packageName, implInterface, remark, debug);
    System.out.println(java);
  }

  public static String getJavaProgram(Grid grid2, String className, String packageName,
      Class<?> implInterface, String remark, boolean debug) {
    Debug.checkNotNull(grid2, "grid2");
    JavaMethodGenerator gen = new JavaMethodGenerator();
    return gen.getProgram(className, grid2, packageName, implInterface, remark, debug);
  }

  public void addImport(String packageOrClassName) {
    imports.put(packageOrClassName, packageOrClassName);
  }

  public void addGlobalAttribute(ValueCell cell, Object value) {
    String v = value == null ? "" : " = " + value.toString();
    String f = value == null ? "" : "final ";
    globalAttributes.add(f + cell.getValueType().getSimpleName() + " " + cell.getName() + getDebugInfo(cell)
        + v + ";" + (cell.isUsedForOutput() ? "  //to output" : ""));
  }

  // add more debug info
  private String getDebugInfo(CellInterface cell) {
    return (debug ? " (" + cell.getSerialNr() + ") " : "");
  }

  public void addCell(Calculable cell) {
    if (cell.isUsedForOutput()) {
      switch (cell.getCellType()) {
      case CallCell:
        // addGlobalAttribute((CallCell) cell, null);
        addCall((CallCell) cell, 1);
        break;
//      case IfCell:
//        addCall((IfCell) cell);
//        break;
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

  public void addCell(InputCell cell) {
    inputs.add(cell.getValueType().getSimpleName() + " " + cell.getName());
  }

  private void addCell(ConstantCell cell) {
    addGlobalAttribute(cell, cell.getValue());
  }

  public void addCell(ReferenceCell cell) {
    if (outputCell == null) {
      this.outputCell = cell;
    } else {
      throw new IllegalArgumentException(
          "Cannot make a method with more than one output!, use program instead");
    }
  }

  public String getProgram(String className, Grid grid, String packageName, Class<?> implInterface,
      String remark, boolean debug) {
    this.debug = debug;
    processCells(grid);

    String methodName = StringUtil.decapitalize(className);
    StringBuffer b = new StringBuffer();
    b.append("package " + packageName + ";");

    for (String s : imports.keySet()) {
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

    // method signature
    b.append("\n\npublic " + outputCell.getValueType().getSimpleName() + " " + methodName + "(");
    b.append(StringUtil.join(", ", inputs.toArray(new String[0])));
    b.append("){\n");

    b.append("\t//constants");
    for (String s : globalAttributes) {
      b.append("\n\t");
      b.append(s);
    }

    b.append("\n\n\t//callcells");

    for (String s : program) {
      b.append("\n");
      b.append(s);
    }
    b.append("\n\n\t");

    b.append("return " + outputCell.getReferedCell().getName());

    b.append("\n}}");
    return b.toString();
  }

  private void processCells(Grid grid) {
    for (InputCell c : grid.getInputCells()) {
      addCell(c);
    }

    for (ConstantCell c : grid.getConstantCells()) {
      addCell(c);
    }

    // TODO more cells
    for (Calculable c : grid.getCallCells()) {
      addCell(c);
    }

    for (ReferenceCell c : grid.getOutputCells()) {
      addCell(c);
    }
  }

  private void addCall(CallCell cell, int indent) {
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
  private void addCall(ReferenceCell referenceCell, CallCell callCell, int indent) {
    String call = tab(indent);
    call += referenceCell.getName() + getDebugInfo(referenceCell) + " = ";
    call += getCallMethod(callCell);
    program.add(call);
  }

  private String tab(int indent) {
    String call = "";
    for (int i = 0; i < indent; i++) {
      call += "\t";
    }
    return call;
  }

  private String getCallMethod(CallCell cell) {
    String call = "";
    if (cell.getTargetCell().getValueType().equals(NumberOperations.class)) {
      // we have a +,-,*,/,%, pow
      String op = NumberOperations.getJavaSyntax(cell.getTargetMethod().getName());

      String operation = joinParams(cell.getParams(), " " + op + " ");
      if (cell.getParams().length == 1)// solve single param operator:postfix
        operation += op;
      call += operation + ";";
    } else if (cell.getTargetCell().getValueType().equals(BooleanOperations.class)) {
      // we have a <,>,==,!=,&&,||, etc
      String op = BooleanOperations.getJavaSyntax(cell.getTargetMethod().getName());

      String operation = cell.hasParams() ? joinParams(cell.getParams(), " " + op + " ") : op;
      call += operation + ";";
    } else {
      // we have a method call
      String paramsStr = joinParams(cell.getParams(), ",");
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

//  private void addCall(IfCell cell) {
//    program.add(tab(1) + "if ( " + cell.getBooleanExpression().getName() + " ){");
//    for (int i = 0; i < cell.getOnTrueList().size(); i++) {
//      addCall(cell.getValueCells().get(i), cell.getOnTrueList().get(i), 2);
//    }
//    program.add(tab(1) + "} else {");
//    for (int i = 0; i < cell.getOnFalseList().size(); i++) {
//      addCall(cell.getValueCells().get(i), cell.getOnFalseList().get(i), 2);
//    }
//    program.add(tab(1) + "}");
//  }

  private void addCall(BooleanSwitchCell cell) {
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
    String[] names = new String[cells.length];
    for (int i = 0; i < names.length; i++) {
      names[i] = cells[i].getName() + getDebugInfo(cells[i]);
    }
    return StringUtil.join(seperator, names);
  }
}
