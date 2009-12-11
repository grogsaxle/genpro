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

import java.util.Random;

import junit.framework.Assert;

/**
 * @author Rob van der Veer
 * @since 1.0
 */
public final class Calc {
  static long seed = System.currentTimeMillis() % 1000;
  private static final Random random = new Random(seed);
  static {
    Debug.info(true, "Random seed=", seed);
  }

  public static final int getRandomInt(final int min, final int max) {
    Debug.checkParam(min > max, "min>max");
    // add 1 cause in nextInt(n) n is exclusive,
    return random.nextInt(max - min + 1) + min;
  }

  public static final double getRandomDouble(final double min, final double max) {
    Debug.checkParam(min > max, "min>max");
    double range = max - min + 1;
    return range * random.nextDouble() + min;
  }

  public static boolean isNaNorInfinite(double d) {
    if (Double.isInfinite(d))
      return true;
    return Double.isNaN(d);
  }

  /**
   * @param min
   * @param max
   * @param pow
   *          value between 2 and 9 where 2 returns values according to a line of 45 degrees (x=y) and 9 gives
   *          a very strong curved hyperbolic line with very high chance of getting min value and almost no
   *          chance of getting the max value.
   * @return
   */

  public static final int getRandomIntWithHigherChanceTowardsMin(final int min, final int max,
      final double power) {
    Debug.checkParam(min > max, "min>max");
    Debug.checkParam(power < 1 || power > 9, "power<2 || power>9:", power);

    // With power 2 the procedure is like: sqrt(a random from range*range)

    // range runs from 0 to range.
    int range = max - min;
    // stretch range in a predictable way
    double strechedRange = Math.pow(range + .5, power);
    // somehow the +.5 is needed to get a good distribution

    // check for overflow
    Debug.errorOnTrue(strechedRange < 0, "overflow error at range:", range, " & power:", power);
    // get evenly spread random
    double randomDoubleFromStrechedRange = getRandomDouble(0, strechedRange);
    // Shrink range stronger at the max-end than at the min-end
    // to get more hits at the max-end.
    // Getting the power of 1/2 is the same as sqrt()
    int rangeValue = (int) (Math.pow(randomDoubleFromStrechedRange, 1.0d / power) + .5);
    // Invert value to get more hits at min-end than at the max-end
    rangeValue = Math.abs(rangeValue - range);
    // change value to fit the min-max range that is needed
    return rangeValue + min;
  }

  public static final float getRandomFloat(final float min, final float max) {
    Debug.checkParam(min > max, "min>max");
    // add 1 cause in nextInt(n) n is exclusive,
    return random.nextFloat() * (max - min) + min;
  }

  public static final int naarVeelvoud(int amount, int veelvoud) {
    if (veelvoud == 1) {
      return amount;
    }
    return amount - (amount % veelvoud);
  }

  /**
   * Truncate a float to a two-decimal float, as used fro currency.
   * 
   * @param f
   *          The float to truncate.
   * @return The truncated float.
   */
  public static float trunc2Decimals(final float f) {
    return ((int) (f * 100.0f)) / 100.0f;
  }

  public static float truncDecimals(final float f, int decimals) {
    int multiplier = (int) Math.pow(10, decimals);
    return ((int) (f * multiplier)) / (float) multiplier;
  }

  public static double truncDecimals(final double f, int decimals) {
    int multiplier = (int) Math.pow(10, decimals);
    return ((int) (f * multiplier)) / (double) multiplier;
  }

  public static void main(String[] args) {
    Assert.assertEquals(truncDecimals(3.141595, 2), 3.14, 0.0001);
    Assert.assertEquals(truncDecimals(3.141595, 3), 3.141, 0.0001);
    Assert.assertEquals(truncDecimals(3.141595, 0), 3, 0.0001);
    Assert.assertEquals(truncDecimals(3.141595, 1), 3.1, 0.0001);
    test(0);
    for (int i = 0; i < 10000; i++) {
      int r = random.nextInt();
      test(r);
    }

    test(10);
    test(-10);

  }

  private static void test(int r) {
    double val = intRangeTo0_1(r);
    Debug.checkRange(val, 0, 1, "val");
    System.out.println("R=" + r + " val=" + val);
  }

  final static double MAX = signedLog(Integer.MAX_VALUE);
  final static double MIN = signedLog(Integer.MIN_VALUE + 1);
  final static double RANGE = MAX - MIN;

  final static double INTRANGE = Integer.MAX_VALUE - Integer.MIN_VALUE;

  public static double intRangeTo0_1(double val){
    return (val- Integer.MIN_VALUE) / INTRANGE;
  }
  
  public static double toLogRange(final int min, final int range, final int value) {
    final double log = value == 0 ? 0 : signedLog(value);
    // to 0-1
    final double pos = (log - MIN) / RANGE;
    return pos * range + min;
  }

  public static double signedLog(int value) {
    return Math.signum(value) * Math.log(Math.abs(value));
  }
}
