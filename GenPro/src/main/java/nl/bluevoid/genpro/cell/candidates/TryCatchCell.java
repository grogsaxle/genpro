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

package nl.bluevoid.genpro.cell.candidates;

import nl.bluevoid.genpro.cell.Cell;
import nl.bluevoid.genpro.cell.CellTypeEnum;
import nl.bluevoid.genpro.util.XMLBuilder;
/**
 * @author Rob van der Veer
 * @since 1.0
 */

public class TryCatchCell extends Cell {
  //private int nrOfSteps;
  //private Class<?> exceptionType;//????
 // private Calculable[] onError; 
  
  public TryCatchCell(String name) {
    super(name, CellTypeEnum.TryCatchCell);
  }

  public boolean canMutate() {
    return true;
  }

  public void getXML(XMLBuilder x) {
    // TODO Auto-generated method stub
    throw new IllegalStateException("getXML is not implemented yet");
  }

  public boolean isUsedForOutput() {
    // TODO Auto-generated method stub
    throw new IllegalStateException("isUsedForOutput is not implemented yet");
  }
}
