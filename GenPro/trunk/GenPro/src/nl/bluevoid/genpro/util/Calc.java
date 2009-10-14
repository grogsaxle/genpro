/*
 * This file is part of GenPro, Reflective Object Oriented Genetic Programming.
 *
 * GenPro offers a dual license model containing the GPL (GNU General Public License) version 2  
 * as well as a commercial license.
 *
 * For licensing information please see the file license.txt included with GenPro
 * or have a look at the top of class nl.bluevoid.genpro.cell.Cell which representatively
 * includes the GenPro license policy applicable for any file delivered with GenPro.
 */

package nl.bluevoid.genpro.util;

import java.util.Random;

import junit.framework.Assert;

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
  /**
   * 
   * @param chancePercentage the percentage of the boolean hitting true
   * @return
   */
  public static final boolean getRandomBoolean(int chancePercentage){
    return getRandomInt(0, 100)<=chancePercentage;
  }

  public static final double getRandomDouble(final double min, final double max) {
    Debug.checkParam(min > max, "min>max");
    double range = max - min + 1;
    return range * random.nextDouble() + min;
  }

  public static boolean isNaNorInfinite(double d){
    if(Double.isInfinite(d)) return true;
    return Double.isNaN(d);
  }
  
  /**
   * @param min
   * @param max
   * @param pow
   *            value between 2 and 9 where 2 returns values according to a line of 45 degrees (x=y) and 9
   *            gives a very strong curved hyperbolic line with very high chance of getting min value and
   *            almost no chance of getting the max value.
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
   *            The float to truncate.
   * @return The truncated float.
   */
  public static float trunc2Decimals(final float f) {
    return ((int) (f * 100.0f)) / 100.0f;
  }

  public static float truncDecimals(final float f, int decimals) {
    int multiplier = (int)Math.pow(10, decimals);
    return ((int) (f * multiplier)) / (float) multiplier;
  }
  
  public static double truncDecimals(final double f, int decimals) {
    int multiplier = (int)Math.pow(10, decimals);
    return ((int) (f * multiplier)) / (double) multiplier;
  }
  
  public static void main(String[] args) {
    Assert.assertEquals(truncDecimals(3.141595, 2), 3.14, 0.0001);
    Assert.assertEquals(truncDecimals(3.141595, 3), 3.141, 0.0001);
    Assert.assertEquals(truncDecimals(3.141595, 0), 3, 0.0001);
    Assert.assertEquals(truncDecimals(3.141595, 1), 3.1, 0.0001);
  }
}
