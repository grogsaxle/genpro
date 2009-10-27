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

package nl.bluevoid.genpro.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
/** 
 * @author Rob van der Veer
 * @since 1.0
 */
public class FileUtil {
  /**
   * Leest de regels van een bestand in. Throws FileTooLargeException als bestand groter is dan 50000 regels.
   */
  public static String readFile(String fileName) throws IOException {
    return readFile(fileName, 50000);
  }

  /**
   * Leest de regels van een bestand in. Throws FileTooLargeException als bestand groter is dan maxLines.
   */

  public static String readFile(String fileName, int maxLines) throws IOException {
    int curLineNr = 0;
    StringBuffer result = new StringBuffer();

    File file = new File(fileName);
    FileReader reader = new FileReader(file);
    BufferedReader bf = new BufferedReader(reader);

    String in = bf.readLine();
    while (curLineNr < maxLines && in != null) // eof=null
    {
      result.append(in + "\n");
      in = bf.readLine();
      curLineNr++;
    }
    bf.close();
    reader.close();
    if (curLineNr >= maxLines) {
      throw new IllegalArgumentException("File: " + fileName + " is groter dan " + maxLines
          + " regels, inlezen is afgebroken.");
    }
    return result.toString();
  }

  public static void copyBinaryFileTo(String fileName, OutputStream out) throws IOException {
    File file = new File(fileName);
    FileInputStream fis = new FileInputStream(file);
    int c;
    while ((c = fis.read()) != -1) {
      out.write(c);
    }
    fis.close();
    out.close();
  }

  public static byte[] readToBuffer(InputStream is) throws IOException {
    ByteArrayOutputStream configBuffer = new ByteArrayOutputStream();
    BufferedInputStream reader = new BufferedInputStream(is, 1024);

    byte[] ba = new byte[1024];
    int totalyRead = 0;
    int bytesRead; // -1 means eof.
    try {
      do {
        bytesRead = reader.read(ba, 0, ba.length);
        if (bytesRead != -1) {
          configBuffer.write(ba, 0, bytesRead);
          totalyRead += bytesRead;
        }
      } while (bytesRead != -1);
    } finally {
      try {
        reader.close();
      } catch (IOException e) {
        // ignore
      }
    }
    return configBuffer.toByteArray();
  }

  public static void storeBufferInFile(ByteArrayOutputStream buffer, File tosave) throws IOException {
    storeBufferInFile(buffer.toByteArray(), tosave);
  }

  public static void storeBufferInFile(final byte[] data, File tosave) throws IOException {
    FileOutputStream fw = null;
    try {
      fw = new FileOutputStream(tosave);
      fw.write(data, 0, data.length);
    } finally {
      if (fw != null) {
        fw.close();
      }
    }
  }

  public static void storeBufferInFile(final String data, File tosave) throws IOException {
    FileOutputStream fw = null;
    OutputStreamWriter osw = null;
    try {
      fw = new FileOutputStream(tosave);
      osw = new OutputStreamWriter(fw);
      osw.write(data, 0, data.length());
    } finally {
      if (osw != null) {
        osw.close();
      }
      if (fw != null) {
        fw.close();
      }
    }
  }

  /** maakt van namen een geldigebestandsnaam */
  public static String replaceNonLetterOrDigitWithUnderScores(String naam) {
    StringBuffer sb = new StringBuffer("");
    for (int i = 0; i < naam.length(); i++) {
      char letter = naam.charAt(i);
      if (Character.isLetterOrDigit(letter)) {
        sb.append(letter);
      } else {
        sb.append('_');
      }
    }
    return sb.toString();
  }

  public static List<String> readTextFromJar(String s) {
    InputStream is = null;
    BufferedReader br = null;
    String line;
    ArrayList<String> list = new ArrayList<String>();

    try {
      is = FileUtil.class.getResourceAsStream(s);
      br = new BufferedReader(new InputStreamReader(is));
      while (null != (line = br.readLine())) {
        list.add(line);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (br != null)
          br.close();
        if (is != null)
          is.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return list;
  }

}
