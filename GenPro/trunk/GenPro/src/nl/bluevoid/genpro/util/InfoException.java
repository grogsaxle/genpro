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

import java.util.ArrayList;
/**
 * Exception that allows extra info to be stored in it, handy for debugging
 * 
 * @author Rob van der Veer
 * @since 1.0
 */
@SuppressWarnings("serial")
public class InfoException extends Exception {

  private ArrayList<String> info = new ArrayList<String>();

  public InfoException(String message, Throwable cause) {
    super(message, cause);
  }

  public InfoException(Throwable cause) {
    super(cause);
  }

  public InfoException() {
  }

  public void addInfo(String info) {
    this.info.add(info);
  }

  public void addInfo(String[] info) {
    for (String string : info) {
      addInfo(string);
    }
  }
  
  public void addInfo(ArrayList<String> info) {
    for (String string : info) {
      addInfo(string);
    }
  }

  public void addInfoSeperator() {
    addInfo("-----------------------------------------------");
  }

  public InfoException(String string) {
    super(string);
    addInfo(super.getMessage());
  }

  @Override
  public String getMessage() {
    return StringUtil.join("\n", info.toArray());
  }
}
