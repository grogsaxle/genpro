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

package nl.bluevoid.genpro;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import nl.bluevoid.genpro.cell.Cell;
import nl.bluevoid.genpro.cell.NoCellFoundException;
import nl.bluevoid.genpro.cell.NoCellOfTypeFoundException;
import nl.bluevoid.genpro.cell.ValueCell;
/**
 * @author Rob van der Veer
 * @since 1.0
 */
public class CellMap {
  private HashMap<String, Cell> map = new HashMap<String, Cell>();
  private HashMap<Class<?>, ArrayList<ValueCell>> mapByValuetype = new HashMap<Class<?>, ArrayList<ValueCell>>();

  public CellMap(ArrayList<? extends Cell> cells) {
    for (Cell c : cells) {
      putByName(c);
    }
  }

  public CellMap() {
  }

  public void putByName(Cell c) {
    map.put(c.getName(), c);
    if (c instanceof ValueCell) {
      ArrayList<ValueCell> cells = mapByValuetype.get(((ValueCell) c).getValueType());
      if (cells == null) {
        cells = new ArrayList<ValueCell>();
        mapByValuetype.put(((ValueCell) c).getValueType(), cells);
      }
      cells.add((ValueCell) c);
    }
  }

  public void putByName(Cell[] c) {
    for (Cell cell : c) {
      putByName(cell);
    }
  }

  public boolean containsCell(Cell cell) {
    return map.containsValue(cell);
  }

  public Cell getByName(Cell c) throws NoCellFoundException {
    Cell found = map.get(c.getName());
    if (found == null) {
      NoCellFoundException ncfe = new NoCellFoundException("No cell found with name " + c.getName());
      ncfe.addInfo(toString());
      ncfe.addInfoSeperator();
      throw ncfe;
    }
    return found;
  }

  /*
   * Tries to find a cell with the same name and type, if not found will return a random cell with the same
   * valuetype as c has. throws CellNotFoundException if no cell found.
   */

  public ValueCell getByNameOrValueType(ValueCell c) throws NoCellFoundException {
    ValueCell vc = (ValueCell) getByName(c);
    Class<?> type = c.getValueType();
    if (vc.getValueType().equals(type))
      return vc;
    else {
      return getRandomValueCell(type);
    }
  }

  public ValueCell getRandomValueCell(Class<?> type) throws NoCellOfTypeFoundException {
    ArrayList<ValueCell> cells = mapByValuetype.get(type);
    if (cells == null)
      throw new NoCellOfTypeFoundException("No cell found of type:" + type);
    try {
      return Util.getRandomCell(cells);
    } catch (NoCellFoundException e) {
      throw new NoCellOfTypeFoundException("No cell found of type:" + type, e);
    }
  }

  public Collection<Cell> getCells() {
    return map.values();
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("Cells in map:\n");
    for (String key : map.keySet()) {
      sb.append(key + ":");
      sb.append(map.get(key) + "\n");
    }
    return sb.toString();
  }
}
