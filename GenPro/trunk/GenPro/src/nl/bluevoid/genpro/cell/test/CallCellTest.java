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

package nl.bluevoid.genpro.cell.test;

import junit.framework.Assert;
import junit.framework.TestCase;
import nl.bluevoid.genpro.CellMap;
import nl.bluevoid.genpro.cell.CallCell;
import nl.bluevoid.genpro.cell.ValueCell;

/**
 * @author Rob van der Veer
 * @since 1.0
 */
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
