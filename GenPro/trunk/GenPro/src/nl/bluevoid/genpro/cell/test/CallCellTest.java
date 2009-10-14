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

package nl.bluevoid.genpro.cell.test;

import junit.framework.Assert;
import junit.framework.TestCase;
import nl.bluevoid.genpro.CellMap;
import nl.bluevoid.genpro.cell.CallCell;
import nl.bluevoid.genpro.cell.ValueCell;


public class CallCellTest extends TestCase{

  public void testRestore() throws Exception {
    CallCell c1=new CallCell("c1", Double.class);
    CallCell c2=new CallCell("c2", Double.class);
    CallCell c3=new CallCell("c3", Double.class);
    
    CallCell c4=new CallCell("c3", Double.class);
    c4.setParams(new ValueCell[]{c1,c2});
    c4.setTargetCell(c3);
    
    CallCell c1a=new CallCell("c1", Double.class);
    CallCell c2a=new CallCell("c2", Double.class);
    CallCell c3a=new CallCell("c3", Double.class);
    CellMap map=new CellMap();
    map.putByName(c1a);
    map.putByName(c2a);
    map.putByName(c3a);
    c4.restoreConnections(map);
    Assert.assertSame(c4.getParams()[0], c1a);
    Assert.assertSame(c4.getParams()[1], c2a);
    Assert.assertSame(c4.getTargetCell(), c3a);
  }
  
  public void testCloning(){
    CallCell c1=new CallCell("c1", Double.class);
    CallCell c2=new CallCell("c2", Double.class);
    CallCell c3=new CallCell("c3", Double.class);
    
    CallCell c4=new CallCell("c3", Double.class);
    c4.setParams(new ValueCell[]{c1,c2});
    c4.setTargetCell(c3);
    CallCell clone=(CallCell) c4.clone();
    
    Assert.assertNotSame(clone.getParams(), c4.getParams());
  }
  
  public void testIsNumber(){
    CallCell c1=new CallCell("c1", Double.class);
    Assert.assertTrue(c1.isValueA_Number());
  }
}
