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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import nl.bluevoid.genpro.Setup;

public class GewogenKansSelector<T>  {//TODO rename
  private Random r = new Random(System.currentTimeMillis());

  HashMap<Double, ArrayList<T>> scoresMap = new HashMap<Double, ArrayList<T>>(300);
  // stores the maxnumber of the object,
  // the range of an object is from the max number of the preceding object to this one
  // When adding a,50 b,60 c,10 d, 50
  // it will construct: 50, 110, 120, 160
  ArrayList<Integer> numbers = new ArrayList<Integer>();
  ArrayList<T> items = new ArrayList<T>();
  ArrayList<Integer> chosen = new ArrayList<Integer>();
  ArrayList<Double> scores = new ArrayList<Double>();

  private int highestNumber = 0;

  private final Setup setup; //TODO delete dependency

  public GewogenKansSelector(Setup setup) {
    this.setup = setup;
  }

  public void add(final T o, final int gewicht, final double score) {
    Debug.errorOnFalse(gewicht > 0, "gewicht=", gewicht);
    if (setup.hasMaxPerScore() && maxOfSameScoreIsReached(o, score)) {
      // Debug.println("maxReached for "+score);
    } else {
      items.add(o);
      highestNumber += gewicht;
      numbers.add(highestNumber);
      chosen.add(0);//
      scores.add(score);
    }
  }

  private boolean maxOfSameScoreIsReached(final T sol, final double score) {
    ArrayList<T> list = scoresMap.get(score);
    if (list == null) {
      list = new ArrayList<T>();//TODO make simpler, just count numbers
      scoresMap.put(score, list);
    }
    list.add(sol);
    return list.size() >= setup.getMaxPerScore();
  }

  public int size() {
    return items.size();
  }

  public T getRandomItem() {
    int choice = r.nextInt(highestNumber);
    int index = Collections.binarySearch(numbers, choice);//TODO make faster with int array
    // System.out.println("choice is:" + choice + " index=" + index);
    int item = index < 0 ? Math.abs(index) - 1 : index;//TODO verify this!!!
    // count
    int val = chosen.get(item);
    chosen.set(item, ++val);
    return items.get(item);
  }

  public void printChooseResult() {
    int last = 0;// end of range of last
    Debug.println("printChooseResult from " + size());

    IntCombi[] sorted = new IntCombi[numbers.size()];
    for (int i = 0; i < numbers.size(); i++) {
      IntCombi ic = new IntCombi();
      sorted[i] = ic;

      ic.weigth = numbers.get(i) - last;
      ic.nr = chosen.get(i);
      ic.score = scores.get(i);
      last = numbers.get(i);
    }
    Arrays.sort(sorted);
    int i = 0;
    for (IntCombi ic : sorted) {
      System.out.print("  w:" + ic.weigth + " #" + ic.nr + " s" + Calc.truncDecimals(ic.score, 3));
      if (i % 100 == 0 && i != 0)// newline every x
        System.out.println();
      i++;
    }
    System.out.println();
  }
}

class IntCombi implements Comparable<IntCombi> {
  int weigth;
  int nr;
  double score;

  public int compareTo(IntCombi o) {
    return o.weigth - weigth;// (o.score - score<=0)?1:-1;
  }
}
