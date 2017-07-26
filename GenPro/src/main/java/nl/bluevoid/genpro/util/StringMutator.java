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

import java.util.Arrays;
/**
 * @author Rob van der Veer
 * @since 1.0
 */
public class StringMutator {

  final byte[] letters;

  int usedLenght = 1;

  private static final byte start = 48; // 48 met cijfers 65 zonder

  private static final byte end = 122; // 122 - 13 chars

  private static final int options = end - start - 13 + 1;

  final long nrMutations;

  public StringMutator(int length) {
    letters = new byte[length];
    nrMutations = (long) Math.pow(options, length);
    System.out.println("At length:" + length + " with " + options + " options " + nrMutations / 1000000000.0
        + " miljard mutations (" + options + "^" + length + ") will be calculated (singleprocessor) in "
        + nrMutations / (74236000 * 60.0) + " hours");
    Arrays.fill(letters, start);
  }

  public final String getString() {
    return new String(letters, 0, usedLenght);
  }

  public final long getCount() {
    // tel in 62 tallig stelsel
    long total = 0;
    for (int i = 0; i < usedLenght; i++) {
      byte b = letters[i];
      int done = (b - start);
      if (b >= 97)
        done -= 13;
      else if (b >= 65)
        done -= 8;
      long add = (long) (done * Math.pow(options, i));
      total += add;
      // System.out.println("letter"+letters[i]+"add="+add);
    }
    return total;
  }

  public final void raise(int i) {
    switch (++letters[i]) {
    case end + 1:
      letters[i] = start;
      raise(++i);
      if (i == usedLenght)
        usedLenght = i + 1;
      break;
    case 91:
      letters[i] = 97;
      break;
    case 58:
      letters[i] = 65;
      break;
    }
  }
}
