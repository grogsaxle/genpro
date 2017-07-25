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

import nl.bluevoid.genpro.util.InfoException;
/**
 * @author Rob van der Veer
 * @since 1.0
 */
@SuppressWarnings("serial")
public class NoCellFoundException extends InfoException {

  public NoCellFoundException(String message, Throwable cause) {
    super(message, cause); 
  }

  public NoCellFoundException(String string) {
    super(string);
  }

  public NoCellFoundException(Throwable cause) {
    super(cause);
  }

  public NoCellFoundException() {
  }
}
