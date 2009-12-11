package nl.bluevoid.genpro.util.test;

import junit.framework.TestCase;
import nl.bluevoid.genpro.util.SystemUtil;

public class SystemUtilTest extends TestCase {
  public void testGetProps() {
    SystemUtil.printVmProperties();
  }
}
