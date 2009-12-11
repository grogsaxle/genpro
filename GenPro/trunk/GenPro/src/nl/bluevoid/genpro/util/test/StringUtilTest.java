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

package nl.bluevoid.genpro.util.test;

import junit.framework.Assert;
import junit.framework.TestCase;
import nl.bluevoid.genpro.util.StringUtil;
/** 
 * @author Rob van der Veer
 * @since 1.0
 */
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
