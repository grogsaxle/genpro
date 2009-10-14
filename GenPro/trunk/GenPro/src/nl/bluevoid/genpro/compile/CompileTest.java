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

import junit.framework.TestCase;

public class CompileTest extends TestCase {

  String code = "package nl.bluevoid.genpro.compiled;" + "\nimport static java.lang.Math.*;"

  + "\npublic class Function2 implements nl.bluevoid.genpro.compile.Function {"
      + "\n   public double f(double x) {" + "\nreturn (Math.cos(x)) ; \n} \n}";

  public void test1() throws Throwable {
    final CharSequenceCompiler<Function> compiler = new CharSequenceCompiler<Function>(new Object().getClass()
        .getClassLoader(), Arrays.asList(new String[] { "-target", "1.6" }));

    Function f = newFunction(compiler, code);

    assertEquals(Math.cos(3), f.f(3), 0.0001);
    assertEquals(Math.cos(2), f.f(2), 0.0001);
    assertEquals(Math.cos(1), f.f(1), 0.0001);
  }

  Function newFunction(CharSequenceCompiler<Function> compiler, String code) throws ClassCastException,
      CharSequenceCompilerException, InstantiationException, IllegalAccessException {
    // generate semi-secure unique package and class names
    final String packageName = "nl.bluevoid.genpro.compiled";
    final String className = "Function2";
    final String qName = packageName + '.' + className;
    // compile the generated Java source
    final DiagnosticCollector<JavaFileObject> errs = new DiagnosticCollector<JavaFileObject>();
    Class<Function> compiledFunction = compiler.compile(qName, code, errs, new Class<?>[] { Function.class });
    System.out.println(errs);
    return compiledFunction.newInstance();
  }
}
