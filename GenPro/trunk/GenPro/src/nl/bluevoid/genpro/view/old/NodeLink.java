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

import java.awt.geom.Line2D;

import nl.bluevoid.genpro.util.Debug;

/*
 * Created on 4-mei-2006
 */

public class NodeLink {
  public final static int RIGHT_AND_ABOVE = 16734;
  public final static int LEFT_AND_UNDER = 287;
  public final static int ON_LINE = 38787;

  public final static int PARAM = 87;
  public final static int TARGET = 879;
  public final int type;
  public final Node nodeA;
  public final Node nodeB;
  private boolean isCrossing = false;
  private int nrOfCrossings;
  // dit moet worden: aantal kruisingen!!!
  private boolean crossingCalculated = false;
  final String naam;

  public NodeLink(Node a, Node b, int type, String naam) {
    this.type = type;
    this.naam = naam;
    Debug.checkNotNull(a, "node a");
    Debug.checkNotNull(b, "node a");
    Debug.checkParamInList(type, new int[] { PARAM, TARGET });
    if (a == b)
      throw new IllegalArgumentException("a==b");
    nodeA = a;
    nodeB = b;
  }
  
  public Node getOtherNode(Node node) {
    if (nodeA == node)
      return nodeB;
    if (nodeB == node)
      return nodeA;
    return null;
  }

  public String hashKey() {
    int a = nodeA.nodeNr;
    int b = nodeB.nodeNr;
    if (a < b)
      return "" + a + "|" + b;

    return "" + b + "|" + a;
  }

  public float getLength() {
    return nodeA.getDistance(nodeB);
  }

  public NodeLink cloneAndReplace(Node oldNode, Node newNode) {
    return new NodeLink(getOtherNode(oldNode), newNode, type, naam);
  }

  public void resetCrossing() {
    isCrossing = false;
    nrOfCrossings = 0;
    crossingCalculated = false;
  }

  public boolean isCrossing() {
    return isCrossing;
  }

  public int nrOfCrossings() {
    return nrOfCrossings;
  }

  public boolean callculateCrossing(NodeLink b) {
    if (b == null)
      throw new IllegalArgumentException("b==null");

    crossingCalculated = true;

    if (b == this)
      return false;

    // filter out the lines from our original node if we are a link from a
    // phantom node
    if (this.isPhantomLink()) {
      Node phantom = getPhantomNode();
      Node original = phantom.phantomForNode;
      if (b.nodeA == original || b.nodeB == original) {
        return false;
      }
    }

    boolean lineCrossing = doIntersect(b);

    if (lineCrossing) {
      isCrossing = true;
      nrOfCrossings++;

    }
    return lineCrossing;
  }

  private boolean isPhantomLink() {
    return nodeA.isPhantom || nodeB.isPhantom;
  }

  private Node getPhantomNode() {
    if (nodeA.isPhantom)
      return nodeA;
    if (nodeB.isPhantom)
      return nodeB;
    return null;
  }

  public boolean doIntersect(NodeLink b) {
    double dyline1, dxline1;
    double dyline2, dxline2;
    int x1line1, y1line1, x2line1, y2line1;
    int x1line2, y1line2, x2line2, y2line2;

    if (!Line2D.linesIntersect(nodeA.locX, nodeA.locY, nodeB.locX, nodeB.locY, b.nodeA.locX, b.nodeA.locY,
        b.nodeB.locX, b.nodeB.locY))
      return false;

    x1line1 = nodeA.locX;
    y1line1 = nodeA.locY;
    x2line1 = nodeB.locX;
    y2line1 = nodeB.locY;

    x1line2 = b.nodeA.locX;
    y1line2 = b.nodeA.locY;
    x2line2 = b.nodeB.locX;
    y2line2 = b.nodeB.locY;

    dyline1 = -(y2line1 - y1line1);
    dxline1 = x2line1 - x1line1;

    dyline2 = -(y2line2 - y1line2);
    dxline2 = x2line2 - x1line2;

    // multiple points overlap!
    if ((dyline1 * dxline2 - dyline2 * dxline1) == 0)
      return true;

    /*
     * check to see if the segments have any endpoints in common. If they do,the intersection is on the
     * endpoint and does not count then return the endpoints as the intersection point
     */
    if ((x1line1 == x1line2) && (y1line1 == y1line2) || (x1line1 == x2line2) && (y1line1 == y2line2)
        || (x2line1 == x1line2) && (y2line1 == y1line2) || (x2line1 == x2line2) && (y2line1 == y2line2)) {
      return false;
    }
    return true;
  }

  // private void printCross(boolean b, NodeLink node)
  // {
  // System.out.print(b ? "Cross -" : "NoCross -");
  // System.out.println("" + toString() + "\n -" + node.toString());
  // }

  private boolean calculateCrossingAngles(Node nodeA2, Node B2) {
    // bereken rico van huidige lijn
    int w = nodeB.locX - nodeA.locX;
    int h = nodeB.locY - nodeA.locY;
    float ricoLijn = getAngle(w, h);
    if (Math.abs(ricoLijn) <= 0.001)
      ricoLijn = (float) Math.PI;
    // bereken rico van node a tov zelfde beginpunt
    int w2 = nodeA2.locX - nodeA.locX;
    int h2 = nodeA2.locY - nodeA.locY;
    float rico2 = getAngle(w2, h2);

    // bereken rico van node b tov zelfde beginpunt
    int w3 = B2.locX - nodeA.locX;
    int h3 = B2.locY - nodeA.locY;
    float rico3 = getAngle(w3, h3);

    boolean ret = true;

    if (ricoLijn < rico2 && ricoLijn < rico3) {
      // draai ricoLijn naar 0
      rico2 -= ricoLijn;
      rico3 -= ricoLijn;
      // als hoeken nu onder en boven 0 lijn (0-3.14) ligt dan kruist de lijn
      // wel

      if (rico2 >= Math.PI && rico3 >= Math.PI)
        ret = false;
      else if (rico2 <= Math.PI && rico3 <= Math.PI)
        ret = false;
    } else if (ricoLijn > rico2 && ricoLijn > rico3) {// draai ricoLijn naar 2*PI
      rico2 += (Math.PI * 2) - ricoLijn;
      rico3 += (Math.PI * 2) - ricoLijn;
      // als hoeken nu onder en boven 0 lijn (0-3.14) ligt dan kruist de lijn
      // wel
      if (rico2 >= Math.PI && rico3 >= Math.PI)
        ret = false;
      else if (rico2 <= Math.PI && rico3 <= Math.PI)
        ret = false;
    }

    // System.out.println("calc:" + nodeA2.nodeNr + " , " + B2.nodeNr + "
    // towards line "
    // + nodeA.nodeNr + " and " + nodeB.nodeNr + " : " + ret + " rLijn " +
    // ricoLijn + " r2 "
    // + rico2 + " r3 " + rico3);
    return ret;
  }

  float getAngle(float h, float w) {
    float wDivH = 0;
    if (Math.abs(w) < 0.0001) {
      if (h < 0)
        wDivH = -100;
      else
        wDivH = 100;
    } else
      wDivH = h / w;
    float angle = (float) Math.atan(wDivH);
    // System.out.println("w,h,wDivH " + w + "," + h + "," + wDivH + " angle " +
    // angle);
    if (w < 0)
      return (float) (Math.PI + angle);
    if (h < 0)
      return (float) (Math.PI * 2 + angle);
    return angle;
  }

  public String toString() {
    return "link from " + nodeA.nodeNr + " (" + nodeA.locX + "," + nodeA.locY + ") to " + nodeB.nodeNr + " ("
        + nodeB.locX + "," + nodeB.locY + ") isCrossing:" + isCrossing();
  }

  public boolean isCrossingCalculated() {
    return this.crossingCalculated;
  }
}
