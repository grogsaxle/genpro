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

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import nl.bluevoid.genpro.util.Debug;
import nl.bluevoid.genpro.util.FileUtil;
import nl.bluevoid.genpro.util.Sneak;
import nl.bluevoid.genpro.util.StringUtil;

/**
 * @author Rob van der Veer
 * @since 1.0
 */
public class TestSet implements Cloneable {

  public static final String SKIP_COLUMN = "SKIP_DATA_COLUMN";

  private String[] cellNames;
  private String[] inputCellNames;

  private String[] outputCellNames;

  private ConcurrentMap<String, ArrayList<Object>> values = new ConcurrentHashMap<String, ArrayList<Object>>();

  private int numValues = 0;
  private final Setup setup;

  public static final int MYSQLDUMP = 234;

  public String[] getInputCellNames() {
    return inputCellNames;
  }

  public String[] getOutputCellNames() {
    return outputCellNames;
  }

  public TestSet(Setup setup, String... cellNames) {
    Debug.checkNotNull(setup, "setup");
    this.setup = setup;
    this.cellNames = cellNames;
    ArrayList<String> in = new ArrayList<String>();
    ArrayList<String> out = new ArrayList<String>();
    // split in and outputs
    for (String celName : cellNames) {
      values.put(celName, new ArrayList<Object>());
      if (setup.isInputCell(celName)) {
        in.add(celName);
      } else {
        out.add(celName);
      }
    }
    inputCellNames = in.toArray(new String[in.size()]);
    outputCellNames = out.toArray(new String[out.size()]);
  }

  public void addCellValues(final Object... objects) {
    System.out.println("adding values to testset: " + StringUtil.join(", ", cellNames) + " : "
        + StringUtil.join(", ", objects));
    Debug.errorOnFalse(cellNames.length == objects.length, "expected " + cellNames.length
        + " objects, but received " + objects.length);
    for (int i = 0; i < cellNames.length; i++) {
      // final Class<?> cellType = setup.getInOrOutPutCellType(cellNames[i]);

      ArrayList<Object> arr = values.get(cellNames[i]);
      arr.add(objects[i]);
    }
    numValues++;
  }

  public ArrayList<Object> getCellValues(String name) {
    return values.get(name);
  }

  public void addCellValues(final Object[] objects, String[] columns) {
    System.out.println("adding values to testset: " + StringUtil.join(", ", cellNames) + " : "
        + StringUtil.join(", ", objects));
    Debug.errorOnFalse(cellNames.length == objects.length, "expected " + cellNames.length
        + " objects, but received " + objects.length);
    for (int i = 0; i < columns.length; i++) {
      // final Class<?> cellType = setup.getInOrOutPutCellType(cellNames[i]);
      ArrayList<Object> arr = values.get(columns[i]);
      if (arr == null)
        throw new IllegalArgumentException("Unknown cellname:" + columns[i]);
      arr.add(objects[i]);
    }
    numValues++;
  }

  public Object getValue(final String name, final int i) {
    return values.get(name).get(i);
  }

  @Override
  public TestSet clone() {
    try {
      final TestSet clone = (TestSet) super.clone();
      clone.cellNames = (String[]) Util.clone(cellNames);
      clone.inputCellNames = (String[]) Util.clone(inputCellNames);
      clone.outputCellNames = (String[]) Util.clone(outputCellNames);

      clone.values = new ConcurrentHashMap<String, ArrayList<Object>>();

      for (final String key : values.keySet()) {
        final ArrayList<Object> obj = values.get(key);
        // ArrayList<Object> objList=new ArrayList<Object>();
        clone.values.put(key, obj);
      }
      return clone;
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
      throw new IllegalStateException(e.getMessage());
    }
  }

  public void addCellValuesFromFile(String fileName) {
    try {
      System.out.println("addCellValuesFromFile");
      String line = FileUtil.readFile(fileName);
      String[] lines = line.split("\n");
      for (String string : lines) {
        String stripped = string.trim();
        if (stripped.startsWith("#") || stripped.startsWith("//") || stripped.length() == 0) {
          // skip
        } else {
          // process
          String[] split = stripped.split(",");
          Double[] ds = new Double[split.length];// TODO typing: strings, int etc
          for (int i = 0; i < split.length; i++) {
            ds[i] = Double.parseDouble(split[i]);
          }
          addCellValues((Object[]) ds);
        }
      }
    } catch (IOException e) {
      Sneak.sneakyThrow(e);
    }
  }

  public void addCellValuesFromFile(String fileName, final String... columns) {
    // create list of valid names (skip SKIP_DATA_COLUMN)
    final ArrayList<String> validColumNames = new ArrayList<String>();
    for (String name : columns) {
      if (!name.equals(SKIP_COLUMN)) {
        validColumNames.add(name);
      }
    }
    String[] validColumns = validColumNames.toArray(new String[0]);

    try {
      System.out.println("addCellValuesFromFile");
      String line = FileUtil.readFile(fileName);
      String[] lines = line.split("\n");
      for (String string : lines) {
        String stripped = string.trim();
        if (stripped.startsWith("#") || stripped.startsWith("//") || stripped.length() == 0) {
          // skip
        } else {
          // process
          String[] split = stripped.split(",");
          Double[] ds = new Double[validColumns.length];// TODO typing: strings, int etc
          int validCount = 0;
          for (int i = 0; i < split.length; i++) {
            // copy only data which is valid
            if (!columns[i].equals(SKIP_COLUMN)) {
              ds[validCount] = Double.parseDouble(split[i]);
              validCount++;
            }
          }
          // add valid data with valid columnnames
          addCellValues((Object[]) ds, validColumns);
        }
      }
    } catch (IOException e) {
      Sneak.sneakyThrow(e);
    }
  }

  public ConcurrentMap<String, ArrayList<Object>> getValues() {
    return values;
  }

  public int getNumberOfTestCases() {
    return numValues;
  }

  public Setup getSetup() {
    return setup;
  }

  SimpleDateFormat mysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private Map<String, DataTransformer> dataTransformers = new HashMap<String, DataTransformer>();

  public void addCellValuesFromMySQLFile(String fileName, final String... columns) {
    try {
      // create list of valid names (skip SKIP_DATA_COLUMN)
      final ArrayList<String> validColumNames = new ArrayList<String>();
      for (String name : columns) {
        if (!name.equals(SKIP_COLUMN)) {
          validColumNames.add(name);
        }
      }
      String[] validColumns = validColumNames.toArray(new String[0]);

      String data = FileUtil.readFile(fileName);
      String[] lines = data.split("\n");

      for (final String line : lines) {
        if (line.startsWith("|")) {
          String lineToSplit = line.substring(1);
          String[] parts = lineToSplit.split("\\|");
          try {
            Object[] ds = new Object[validColumns.length];
            int validCount = 0;
            for (int i = 0; i < columns.length; i++) {
              String cellName = columns[i];
              // copy only data which is valid
              if (!cellName.equals(SKIP_COLUMN)) {
                String cellValue = parts[i].trim();
                DataTransformer dt = dataTransformers.get(cellName);
                if (dt != null) {
                  cellValue = dt.transform(cellValue);
                }
                // Debug.println(cellName + " > " + cellValue);
                Class<? extends Object> type = setup.getInOrOutPutCellType(cellName);

                if (type.equals(Double.class)) {
                  ds[validCount] = Double.parseDouble(cellValue);
                } else if (type.equals(String.class)) {
                  ds[validCount] = cellValue;
                } else if (type.equals(Integer.class)) {
                  ds[validCount] = Integer.parseInt(cellValue);
                } else if (type.equals(InetAddress.class)) {
                  ds[validCount] = InetAddress.getByName(cellValue);
                } else if (type.equals(Date.class)) {
                  ds[validCount] = mysqlDateFormat.parse(cellValue);
                } else if (type.equals(Boolean.class)) {
                  ds[validCount] = Boolean.valueOf(cellValue);
                } else {
                  // try to instantiate (needs constructor with a String argument!!)
                  Constructor<? extends Object> c = type.getDeclaredConstructor(String.class);
                  ds[validCount] = c.newInstance(cellValue);
                }
                validCount++;
              }
            }
            // add valid data with valid columnnames
            addCellValues((Object[]) ds, validColumns);
          } catch (ParseException e) {
            Debug.println("Error parsing: " + line);// TODO fix error logging
            throw e;
          }
        }
      }
    } catch (IOException e) {
      Sneak.sneakyThrow(e);
    } catch (ParseException e) {
      Sneak.sneakyThrow(e);
    } catch (SecurityException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      Sneak.sneakyThrow(e);
    } catch (InstantiationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void addTransformer(String field, DataTransformer dataTransformer) {
    dataTransformers.put(field, dataTransformer);
  }
}