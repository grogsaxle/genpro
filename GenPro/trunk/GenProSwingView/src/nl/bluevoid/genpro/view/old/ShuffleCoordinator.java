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

package nl.bluevoid.genpro.view.old;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import nl.bluevoid.genpro.util.Debug;

/*
 * Created on 4-mei-2006
 */

public class ShuffleCoordinator extends Thread {
  private GridField gridField;
  private GridPanel view;

  private int cyclesToGo = 0;
  private long totalCycles = 0;
  private int noMoreMovesCounter = 0;
  private int leastCrossings = Integer.MAX_VALUE;

  private boolean endless = true;
  private boolean running = false;
  private int waitMillis = 0;
  private Thread thread = null;

  public ShuffleCoordinator(GridField gridfield, GridPanel view) {
    this.gridField = gridfield;
    this.view = view;
    Debug.checkNotNull(gridfield, "landscape");
    Debug.checkNotNull(view, "view");
  }

  public void run() {
    gridField.calculateAllCrossings();
    boolean finished = false;
    int nrNodes = gridField.getNrOfNodes();
    Vector<Node> exclude = new Vector<Node>();
    Queue<Node> waitList = new LinkedList<Node>();
    running = true;
    int currentnode = 0;
    Node[] nodes = gridField.getAllNodes();
    long lastdrawtime = 0;
    while (!finished) {
      // moveUnhappiestNode(nrNodes, exclude, waitList);
      currentnode++;
      if (currentnode >= nrNodes)
        currentnode = 0;
      if (nodes[currentnode].canMove) {
        nodes[currentnode].doMove();
      }

      int nr = gridField.calculateAllCrossings();
      if (nr < leastCrossings || System.currentTimeMillis() - lastdrawtime > 1000) {
        leastCrossings = nr;
        lastdrawtime = System.currentTimeMillis();
        view.forcePaint();
        System.out.println("leastCrossings " + leastCrossings);
      }
      if (nr == 0) {
        System.out.println("Finishing");
        // Node.debug = true;
        reposition();
        finished = true;
      }

      try {
        Thread.sleep(waitMillis);
      } catch (InterruptedException ex) {
        // TODO Auto-generated catch block
        ex.printStackTrace();
      }
      totalCycles++;
    }
    running = false;
    // extra whileloop catches the splitsecond that running is true &
    // cycles is was running to null and cycles was set to i;
    view.forcePaint(); // paint at end
    System.out.println("nr of calculation cycles " + totalCycles);
    System.out.println("noMoreMovesCounter " + noMoreMovesCounter);
  }

  private void moveUnhappiestNode(int nrNodes, Vector<Node> exclude, Queue<Node> waitList) {
    exclude.clear();
    if (waitList.size() > nrNodes * .65)
      waitList.poll();
    // System.out.println("waitList size:" + waitList.size());
    exclude.addAll(waitList);
    Node n = null;

    do {
      if (n != null)
        exclude.add(n);
      n = findunHappiestNodeExlude(exclude);
      if (n == null) {
        // System.out.println("game over? no more moves, exclude=" +
        // exclude.size());
        noMoreMovesCounter++;
        break;
      }
    } while (!n.doMove());
    waitList.add(n);
    // System.out.println("waitList size:" + waitList.size());
  }

  private void reposition() {
    int crossings = gridField.calculateAllCrossings();
    System.out.println("reposition crossings start=" + crossings);
    for (int i = 0; i < 18; i++) {
      boolean anyMovement = false;
      for (Node node : gridField.getAllNodes()) {
        if (node.canMove) {
          // crossings=landscape.calculateAllCrossings();
          if (crossings > 0) {
            System.out.println("reposition crossings end=" + crossings);

            return;
          }
          // System.out.println(node.toString());
          boolean moved = node.doMove();
          if (moved) {
            view.forcePaint();
            anyMovement = true;
          }
          // crossings=landscape.calculateAllCrossings();
          // System.out.println(node.toString());

          try {
            Thread.sleep(100);
          } catch (InterruptedException e) {

          }
        }
      }
      if (!anyMovement) {
        System.out.println("Ending: No movement detected");
        break;
      }
    }
    System.out.println("repositioning ended with:" + crossings + " crossings");
  }

  private void makeEvenDistance() {

  }

  private void doMakeSpace(int clearing) {
    int[] xyCenter = gridField.getCentreLocation();
    int x = xyCenter[0];
    int y = xyCenter[1];
    boolean allClear = false;
    int counter = 0;
    do {
      counter++;
      allClear = true;
      for (Node node : gridField.getAllNodes()) {
        if (!node.moveAwayWithClearing(x, y, 2)) {
          allClear = false;
        }
        gridField.calculateAllCrossings();
        view.forcePaint();
        try {
          Thread.sleep(waitMillis);
        } catch (InterruptedException ex) {
          // TODO Auto-generated catch block
          ex.printStackTrace();
        }
      }
    } while (allClear == false && counter < 3);
  }

  private Node findunHappiestNodeExlude(Vector<Node> exclude) {
    Node unhappiest = null;

    for (Node n : gridField.getAllNodes()) {
      if (n.canMove && !exclude.contains(n) && (unhappiest == null || unhappiest.isHappierThan(n))) {
        unhappiest = n;
      }
    }
    return unhappiest;
  }

  public void setUpdateMillis(int millis) {
    waitMillis = millis;
  }

  public void setCyclesToGo(int i) {
    cyclesToGo = i;
    if (!running && cyclesToGo != 0) {
      startRunning();
    }
  }

  public void setEndlessLoop(boolean b) {
    endless = b;
    if (endless) {
      cyclesToGo = 0;
      if (!running)
        startRunning();
    }
  }

  private void startRunning() {
    thread = new Thread(this, "Landscape cycler");
    thread.start();
  }
}
