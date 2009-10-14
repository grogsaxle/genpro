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

package nl.bluevoid.genpro.compile;

import java.util.Arrays;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

import nl.bluevoid.genpro.Grid;
import nl.bluevoid.genpro.JavaGenerator;

public class GridCompiler<T> {

  private CharSequenceCompiler<T> compiler;

  private static int classCounter = 0;

  public GridCompiler() {
    compiler = new CharSequenceCompiler<T>(getClass().getClassLoader(), Arrays.asList(new String[] {
        "-target", "1.6" }));
  }
  
  public synchronized T compile(Grid grid, Class<T> interfaceType) throws ClassCastException, CharSequenceCompilerException, InstantiationException, IllegalAccessException {
    classCounter++;

    final String packageName = "nl.bluevoid.genpro.compiled";
    final String className = grid.getSetup().getName() + "_" + classCounter;
    final String qName = packageName + '.' + className;
    String program = JavaGenerator.getJavaProgram(grid, className, packageName, false);

    // compile the generated Java source
    final DiagnosticCollector<JavaFileObject> errs = new DiagnosticCollector<JavaFileObject>();
    Class<T> compiledProgram = (Class<T>) compiler.compile(qName, program, errs, new Class<?>[] { Object.class });
    System.out.println(errs);

    return compiledProgram.newInstance();
  }

  // http://www.ibm.com/developerworks/java/library/j-jcomp/index.html
  // http://www.velocityreviews.com/forums/t388835-javacompiler-example-trouble-with-finding-class-definitions
  // .html
  // http://groups.google.com/group/comp.lang.java.programmer/msg/0243d70fbce78e52?dmode=source&hl=en

  // private static JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
  // private static StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
  // Iterable<? extends JavaFileObject> compilationUnits1 = fileManager.getJavaFileObjects(program);
  // compiler.getTask(null, fileManager, null, null, null, compilationUnits1).call();
}
