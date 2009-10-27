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

package nl.bluevoid.genpro.cell;

import java.util.Random;

import nl.bluevoid.genpro.util.Debug;
/**
 * @author Rob van der Veer
 * @since 1.0
 */
public abstract class Cell implements Cloneable, CellInterface{

  private static int serialNrCounter=1;
  
  protected static final Random random=new Random(System.currentTimeMillis());
  private final String name;
  private int serialNr;
  private final CellTypeEnum cellType;
  
  public Thread calcThread;
  
  public Cell(String name, CellTypeEnum cellType) {
    this.cellType = cellType;
    Debug.checkNotNull(name, "name");
    this.name = name;
    serialNr=serialNrCounter++;
  }

  public String getName() {
    return name;
  }
  
  @Override
  public String toString() {
    return "'"+name+"'";
  }
  
  public synchronized Cell clone() {
    try {
      final Cell c=(Cell)super.clone();
      c.serialNr=serialNrCounter++;
      return c;
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
      throw new IllegalStateException();
    }
  }

  /* (non-Javadoc)
   * @see nl.bluevoid.genpro.cell.CellInterface#getSerialNr()
   */
  public int getSerialNr() {
    return serialNr;
  }

  /* (non-Javadoc)
   * @see nl.bluevoid.genpro.cell.CellInterface#getCellType()
   */
  public CellTypeEnum getCellType() {
    return cellType;
  }
}
