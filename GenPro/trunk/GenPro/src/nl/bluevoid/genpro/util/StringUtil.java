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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Rob van der Veer
 * @since 1.0
 */
public class StringUtil {

  public static final Random r = new Random();

  private StringUtil() {
  }

  public static String join(String token, Object[] strings) {
    if (strings.length == 0)
      return "";

    StringBuffer sb = new StringBuffer();
    for (int x = 0; x < (strings.length - 1); x++) {
      sb.append((strings[x] == null) ? "null" : strings[x].toString());
      sb.append(token);
    }
    sb.append(strings[strings.length - 1]);

    return (sb.toString());
  }

  /**
   * Calls the method on all objects and appends the output with tostring(), sepearing it with the separator
   * 
   * @param objs
   * @param methodName
   * @param seperator
   * @return
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws SecurityException
   * @throws NoSuchMethodException
   */
  public static String join(Object[] objs, String methodName, String seperator) {
    if (objs.length == 0)
      return "";
    Class<?> clazz = objs[0].getClass();
    Method m;
    try {
      m = clazz.getMethod(methodName);
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException("method " + methodName + " not found on class: " + clazz);
    }

    String[] strings = new String[objs.length];
    try {
      for (int i = 0; i < objs.length; i++) {
        strings[i] = m.invoke(objs[i]).toString();
      }
    } catch (Exception e) {
      throw new IllegalArgumentException(" calling method " + methodName + " object of class: " + clazz
          + " resulted in a " + e.getClass().getSimpleName());
    }
    return StringUtil.join(seperator, strings);
  }

  public static String[] splitAndTrim(String str, String regexDelim) {
    if (str.length() == 0)
      return new String[0];
    String[] strings = str.split(regexDelim);
    trimAll(strings);
    return strings;
  }

  public static void trimAll(String[] strs) {
    for (int i = 0; i < strs.length; i++) {
      strs[i] = strs[i].trim();
    }
  }

  public static String capitalize(String fieldName) {
    if (fieldName.length() == 0)
      return fieldName;
    return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
  }

  public static String decapitalize(String fieldName) {
    if (fieldName.length() == 0)
      return fieldName;
    return fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
  }

  public static boolean startswithUppercase(String str) {
    return Character.isUpperCase(str.charAt(0));
  }

  /**
   * The text is appended with spaces to make it equaly long to the longest.
   * 
   * @param i18Name
   * @param i18names
   * @return
   */

  public static String stripXMLHeader3(String xml) {
    if (xml.startsWith("<?")) {
      int headStart = xml.indexOf("?>");
      if (headStart > 0)
        xml = xml.substring(headStart + 2);
    }
    return xml;
  }

  public static int longestToString(Object[] objects) {
    int longest = 0;
    for (Object object : objects) {
      longest = Math.max(object.toString().length(), longest);
    }
    return longest;
  }

  public static String fillToLongest(String name, String[] names) {
    int longest = longestToString(names);
    StringBuffer text = appendSpaces(name, longest);
    return text.toString() + ".";
  }

  private static StringBuffer appendSpaces(String name, int maxLenght) {
    StringBuffer text = new StringBuffer(name);
    for (int i = text.length(); i <= maxLenght; i++) {
      text.append(" ");
    }
    return text;
  }

  public static String[] wordWrap(String str, int width) {
    Pattern wrapRE = Pattern.compile(".{0," + (width - 1) + "}([ $|\\s$]|$)");
    List<String> list = new LinkedList<String>();
    Matcher m = wrapRE.matcher(str);
    while (m.find())
      list.add(m.group());
    return list.toArray(new String[list.size()]);
  }

  /**
   * converts a String to length with spaces or cut off with a "..." ending to get to length
   * 
   * @param text
   * @param length
   * @return
   */
  public static String assureLength(String text, int length) {
    if (text.length() > length - 3) {
      return text.substring(0, length - 2) + ".. ";
    } else {
      return appendSpaces(text, length).toString();
    }
  }

  /**
   * add leading zeros if number is smaller than 10 eg: 6,10 -> 06
   * 
   * @param number
   * @param base
   * @return
   */
  public static String addLeadingZero(int number) {
    if (number < 10)
      return "0" + number;
    else
      return "" + number;
  }

  /** maakt van namen een geldigebestandsnaam */
  public static String replaceNonLetterOrDigit(String naam, String replacement) {
    StringBuffer sb = new StringBuffer("");
    for (int i = 0; i < naam.length(); i++) {
      char letter = naam.charAt(i);
      if (Character.isLetterOrDigit(letter)) {
        sb.append(letter);
      } else {
        sb.append(replacement);
      }
    }
    return sb.toString();
  }

  /**
   * MD5 secure but not collision proof
   * 
   * @param text
   * @return
   */
  public static String toMD5Hash(String text) {
    MessageDigest digest;
    try {
      digest = java.security.MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("MD5 not avaialable??", e);
    }
    digest.update(text.getBytes());
    byte[] hash = digest.digest();
    return getHexString(hash);
  }

  public static String toSHAHash(String text) {
    return toSHAHash(text.getBytes());
  }

  /**
   * may have security issues, but collision proof
   * 
   * @param text
   * @return
   */
  public static String toSHAHash(byte[] text) {
    MessageDigest digest;
    try {
      digest = java.security.MessageDigest.getInstance("SHA-1");

    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA not avaialable??", e);
    }
    digest.update(text);
    byte[] hash = digest.digest();
    return getHexString(hash);
  }

  /**
   * no security issues and collision proof
   * 
   * @param text
   * @return
   */
  public static String toSHA256Hash(byte[] text) {
    MessageDigest digest;
    try {
      digest = java.security.MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA not available??", e);
    }
    digest.update(text);
    byte[] hash = digest.digest();
    return getHexString(hash);
  }

  public static String getTextFromTo(String text, String startTag, String endTag, boolean includeTags) {
    int indexStart = text.indexOf(startTag);
    if (indexStart == -1)
      return "";
    int indexEnd = text.indexOf(endTag, indexStart + startTag.length());
    if (indexEnd == -1)
      return "";
    if (includeTags) {
      indexEnd += endTag.length();
    } else {
      indexStart += startTag.length();
    }
    return text.substring(indexStart, indexEnd);
  }

  public static void main(String[] str) {
    System.out.println(getTextFromTo("abcdefghijklmnopqrstuvwxyz", "de", "jk", true));
    System.out.println(getTextFromTo("abcdefghijklmnopqrstuvwxyz", "de", "jk", false));
    // Debug.println("SHA 256 hash: welkom=" + toSHA256Hash("welkom".getBytes()));
    // crackHash("371620aa75830b1388b63305b0d42f06");
    // welkom=371620aa75830b1388b63305b0d42f06
    // 0000=4a7d1ed414474e4033ac29ccb8653d9b
    // 2222=6cb4aca7ff4e34570695a28d08d0e9ef or
    // 934b535800b1cba8f96a5d72f72f1611
    String v=getRandomString(6);
    for (int i = 0; i < 300; i++) {
      System.out.println(v);
      v=mutateString(v, 6);
    }
  }

  public static final String letters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

  public static String getRandomMime64String(int length) {
    StringBuilder b = new StringBuilder();
    for (int i = 0; i < length; i++) {
      int index = r.nextInt(letters.length());
      b.append(letters.charAt(index));
    }
    return b.toString();
  }

  public static String replaceAccentChars(String text) {
    text = text.replaceAll("[ÀÁÂÄÃ]", "A");
    text = text.replaceAll("[àáâãäå]", "a");
    text = text.replaceAll("[ÌÍÎÏ]", "I");
    text = text.replaceAll("[ìíîï]", "i");
    text = text.replaceAll("[ÒÓÔÕÖ]", "O");
    text = text.replaceAll("[òóôõö]", "o");
    text = text.replaceAll("[ÙÚÛÜ]", "U");
    text = text.replaceAll("[ùúûü]", "u");
    text = text.replaceAll("[ÈÉÊË]", "E");
    text = text.replaceAll("[èéêë]", "e");
    text = text.replaceAll("[Ñ]", "N");
    text = text.replaceAll("[ñ]", "n");
    text = text.replaceAll("[ç]", "c");
    return text;
  }

  public static String getHexString(byte[] b) {
    String result = "";
    for (int i = 0; i < b.length; i++) {
      result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
    }
    return result;
  }

  public static byte[] toBytes(String hashString) {
    byte[] targetHash = new byte[hashString.length() / 2];
    for (int i = 0; i < targetHash.length; i++) {
      targetHash[i] = (byte) Integer.parseInt(hashString.substring(2 * i, 2 * i + 2), 16);
    }
    return targetHash;
  }

  public static String replaceAllIgnoreCase(final String text, final String oldString,
      final String newString, int fromIndex) {
    throw new IllegalArgumentException("niy, to bad");
  }

  public static String replaceFirstIgnoreCase(final String text, final String oldString,
      final String newString, int fromIndex) {
    String zoek = oldString.toLowerCase();
    String textToSearch = text.toLowerCase();
    int start = textToSearch.indexOf(zoek, fromIndex);
    if (start > -1) {
      return text.substring(0, start) + newString + text.substring(start + zoek.length());
    } else
      return text;
  }

  public static String getRandomString(int length) {
    StringBuilder b = new StringBuilder();
    for (int i = 0; i < length; i++) {
      b.append(getRandomLetter());
    }
    return b.toString();
  }

  public static char getRandomLetter() {
    return letters.charAt(r.nextInt(letters.length()));
  }

  public static String mutateString(String value, int maxLength) {
    switch (r.nextInt(3)) {
    // add letter
    case 0:
      //System.out.println("add");
      if (value.length() < maxLength) {
        int cut = r.nextInt(value.length());
        return value.substring(0,cut) + getRandomLetter() + value.substring(cut, value.length());
      }
    case 1:
      //System.out.println("delete");
      if (value.length() > 1) {
        int cut = r.nextInt(value.length() - 1);
        return value.substring(0, cut) + value.substring(cut + 1, value.length());
      }
      // delete letter
    case 2:
     // System.out.println("change");
      char[] chars = value.toCharArray();
      chars[r.nextInt(chars.length)] = getRandomLetter();
      return new String(chars);
      // change letter
    default:
      throw new IllegalStateException("wrong case");
    }
  }
}