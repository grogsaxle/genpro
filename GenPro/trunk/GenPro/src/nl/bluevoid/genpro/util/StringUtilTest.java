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

import junit.framework.Assert;
import junit.framework.TestCase;

public class StringUtilTest extends TestCase {

  public void testReplaceFirstIgnoreCase() {
    Assert.assertEquals("aaBBccddeeFF", StringUtil.replaceFirstIgnoreCase("aabbccddeeFF", "bb", "BB", 0));
    Assert.assertEquals("aabbccddeeff", StringUtil.replaceFirstIgnoreCase("aabbccddeeff", "bb", "BB", 6));
    Assert.assertEquals("aaBCBccddbbff", StringUtil.replaceFirstIgnoreCase("aabbccddbbff", "BB", "BCB", 0));
    Assert.assertEquals("aaBBcbbcddbbff", StringUtil.replaceFirstIgnoreCase("aabbcbbcddbbff", "bb", "BB", 0));
    Assert.assertEquals("aaBBgembb", StringUtil.replaceFirstIgnoreCase("aagemgembb", "gem", "BB", 0));
    Assert.assertEquals("aabbbbccddbbff", StringUtil.replaceFirstIgnoreCase("aabbccddbbff", "bb", "bbbb", 0));

  }
}
