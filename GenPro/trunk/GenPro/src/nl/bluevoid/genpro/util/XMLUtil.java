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

import java.io.StringReader;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.xml.sax.InputSource;

import junit.framework.TestCase;

public class XMLUtil extends TestCase {

  private final static XPath xp = XPathFactory.newInstance().newXPath();

  public static synchronized String evaluateXpath(String expression, String XML)
      throws XPathExpressionException {
    InputSource is = new InputSource(new StringReader(XML));
    return xp.evaluate(expression, is);
  }

  public void testEvaluate() throws XPathExpressionException {
    String xml = "<a><b>content</b></a>";
    String c = evaluateXpath("/a/b/text()", xml);
    assertEquals("content", c);
  }
}
