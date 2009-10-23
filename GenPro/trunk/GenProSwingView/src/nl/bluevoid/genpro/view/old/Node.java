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
import java.util.Vector;

import nl.bluevoid.genpro.cell.CellInterface;
import nl.bluevoid.genpro.util.Calc;
import nl.bluevoid.genpro.util.Debug;

/*
 * Created on 4-mei-2006
 */

public class Node
{
  static boolean debug = false;
  int locX;
  int locY;
  
  Vector<NodeLink> linkedWith = new Vector<NodeLink>();
  int nodeNr;
  
  GridField landscape;
  int movesDone = 0;
  private static int nodeNrCounter = 0;

  final boolean canMove;

  /**
   * Field indicates that this node is only made for checking new positions
   */
  boolean isPhantom = false;
  // this field is needed to not count the crossing with the original node
  Node phantomForNode;
  final CellInterface cell;
  

  public Node(int x, int y, GridField landscape, boolean canMove, CellInterface cell)
  {
    locX = x;
    locY = y;
    this.canMove = canMove;
    this.cell = cell;
    Debug.checkNotNull(landscape, "landscape");
    this.landscape = landscape;
    nodeNr = ++nodeNrCounter;
  }

  public Node(int x, int y, GridField landscape, Node phantomForNode)
  {
    this(x, y, landscape, false, null);
    isPhantom = true;
    this.phantomForNode = phantomForNode;
  }

  public void linkWith(Node n, int nodelinkType, String naam) throws LinkError
  {
    if (n == this)
      throw new LinkError("can not link with self");

    NodeLink link = new NodeLink(this, n, nodelinkType,naam);
    addLink(link);
    n.addLink(link);
  }

  private void addLink(final NodeLink link)
  {
    //if (!isLinked(link.getOtherNode(this)))
    {
      linkedWith.add(link);
    }// else
     // throw new IllegalStateException("error linking already linked:"
     //     + link.getOtherNode(this).toString() + " this:" + this.toString());
  }

  boolean isLinked(Node n)
  {
    for (NodeLink link : linkedWith)
    {
      if (link.nodeA == n || link.nodeB == n)
        return true;
    }
    return false;
  }

  public boolean isOnLocation(int x, int y)
  {
    return (x == locX && y == locY);
  }

  public float getAverageDistance()
  {
    if (linkedWith.isEmpty())
      return 0;
    float lenTotaal = 0;
    for (NodeLink link : linkedWith)
    {
      lenTotaal += link.getLength();
    }
    return lenTotaal / linkedWith.size();
  }

  public float getTotalLinkDistance()
  {
    float lenTotaal = 0;
    for (NodeLink link : linkedWith)
    {
      lenTotaal += link.getLength();
    }
    return lenTotaal;
  }

  // -1 0 1
  // -1
  // 0 0
  // 1
  // public final static int[][] movements = { { -1, -1 }, { 0, -1 }, { 1, -1 },
  // { -1, 0 }, { 1, 0 }, { -1, 1 }, { 0, 1 }, { 1, 1 } };

  public final static int[][] movements;

  static
  {
    int range = 9;
    int valueNr = (range * 2 + 1) * (range * 2 + 1);
    movements = new int[valueNr][2];
    int arrayPosition = 0;
    for (int x = -range; x <= range; x++)
    {
      for (int y = -range; y <= range; y++)
      {
        movements[arrayPosition][0] = x;
        movements[arrayPosition][1] = y;
        arrayPosition++;
      }
    }
//    System.out.println("movementsArray");
//    for (int i = 0; i < movements.length; i++)
//
//    {
//      System.out.println("" + movements[i][0] + "," + movements[i][1]);
//    }
  }

  public boolean doMove()
  {
    movesDone++;
    // veel opties, welke is beste?
    Node bestOption = null;

    for (int i = 0; i < movements.length; i++)
    {
      int newX = locX + movements[i][0];
      int newY = locY + movements[i][1];
      bestOption = checkoption(bestOption, newX, newY);
    }

    if (bestOption.isHappierThan(this)) // only swap when bestoption is happier!!!
    {
      //System.out.println("Moving: this:" + toString() + "\n Best option:"
      //    + bestOption.toString());
      swapPlaces(bestOption);
      return true;
    } else
    {
      //System.out.println("No move: this:" + toString() + "\n  Best option:"
      //    + bestOption.toString());
      return false;
    }
  }

  private Node checkoption(Node bestOption, int newX, int newY)
  {
    if (landscape.isValidField(newX, newY)
        && landscape.getNode(newX, newY) == null)
    {
      Node d1 = getPhantomCloneWithNewLocation(newX, newY);
      d1.ensureNrOfLinksCrossingIsCalculated();
      if (d1.isHappierThan(bestOption))
        bestOption = d1;
    }
    return bestOption;
  }

  Node lastSwapped;

  /**
   * clearing: distance to neighbours
   * 
   * @param x
   * @param y
   * @param clearing
   */
  public boolean moveAwayWithClearing(int x, int y, int clearing)
  {
    System.out.println("clearMove for " + toString());
    if (getClearing() > clearing)
    {
      System.out.println("clear");
      return true;
    }
    Node bestOption = this;
    for (int i = 0; i < movements.length; i++)
    {
      int newX = locX + movements[i][0];
      int newY = locY + movements[i][1];
      if (landscape.isValidField(newX, newY)
          && landscape.getNode(newX, newY) == null)
      {
        Node d1 = getPhantomCloneWithNewLocation(newX, newY);
        // System.out.println("checking option: " + d1.toString());
        if (d1.isBeterPlaceForClearing(x, y, bestOption, clearing))
          bestOption = d1;
      }
    }
    swapPlaces(bestOption);
    return (getClearing() > clearing);
  }

  private boolean isBeterPlaceForClearing(int x, int y, Node node, int clearing)
  {
    // is er plek vrijachter me?
    if (node.getClearing() > clearing)
      return false;

    if (getClearing() > node.getClearing())
      return true;
    return false;
    // return getDistance(x, y)>node.getDistance(x, y);
  }

  private float getClearing()
  {
    float smallest = Float.MAX_VALUE;
    for (Node node : landscape.getAllNodes())
    {
      float dist = getDistance(node);
      if (dist < smallest)
        smallest = dist;
    }
    return smallest;
  }

  public float getDistance(Node nodeB)
  {
    return getDistance(nodeB.locX, nodeB.locY);
  }

  public float getDistance(int x, int y)
  {
    int dx = Math.abs(locX - x);
    int dy = Math.abs(locY - y);
    return (float) Math.sqrt(dy * dy + dx * dx);
  }

  private void swapPlaces(Node node)
  {
    if (lastSwapped == node)
    {
      System.out.println("no swap for:" + node.nodeNr + " and " + this.nodeNr);
      return;// no back and forth swapping between nodes
    }
    lastSwapped = node;
    int newX = node.locX;
    int newY = node.locY;
    // System.out.println("swapping: " + locX + ", " + locY + " for " + newX
    // + ", " + newY);
    node.locX = locX;
    node.locY = locY;
    this.locX = newX;
    this.locY = newY;
  }

  public synchronized boolean isHappierThan(Node node)
  {
    if (node == null)
      return true;
    int crossingsDiff = getNrOfLinksCrossing() - node.getNrOfLinksCrossing();
    float distanceGain = node.getTotalLinkDistance() - getTotalLinkDistance();
    float biggestLengthGain = node.getBiggestDistance() - getBiggestDistance();
    boolean happier = false;
    String trigger = "none";

    if (crossingsDiff != 0)
    {
      happier = crossingsDiff < 0;
      trigger = "crossing";
    } else if (Math.abs(biggestLengthGain) > 0.05)
    {
      happier = biggestLengthGain > 0.05;
      trigger = "biggestLength";
    } else if (Math.abs(distanceGain) > 0.05)
    {
      happier = distanceGain > 0.05;
      trigger = "totalDistance";
    } else
    {
      happier = false;
    }

    if (debug)
    {
      String mark = happier ? " TRUE" : "";
      System.out.println(toString() + "\n   isHappier:" + happier + " trigger:"
          + trigger + mark);
      System.out.println("   " + node.toString() + " : ");
      System.out
          .print("   CrossingsDiff:" + Calc.trunc2Decimals(crossingsDiff));
      System.out.print(" biggestLengthGain:" + biggestLengthGain);
      System.out.println(" distanceGain:" + distanceGain);
    }

    // System.out.println("" + nodeNr + " isHappierThan " + node.nodeNr + " : "
    // + ret + " "
    // + distanceDiff);
    return happier;
  }

  public int getNrOfLinksCrossing()
  {
    int nrOfLinksCrossing = 0;
    for (NodeLink link : linkedWith)
    {
      // if (link.isCrossing())
      // nrOfLinksCrossing++;
      nrOfLinksCrossing += link.nrOfCrossings();
    }
    return nrOfLinksCrossing;
  }

  public void ensureNrOfLinksCrossingIsCalculated()
  {
    for (NodeLink link : linkedWith)
    {
      if (!link.isCrossingCalculated())
      {
        landscape.calculateCrossings(link);
      }
    }
  }

  public int getNrOfLinks()
  {
    return linkedWith.size();
  }

  private float getBiggestDistance()
  {
    float length = 0;
    for (NodeLink link : linkedWith)
    {
      if (length < link.getLength())
      {
        length = link.getLength();
      }
    }
    return length;
  }

  private Node getPhantomCloneWithNewLocation(int x, int y)
  {
    Node d = new Node(x, y, landscape, this);

    d.movesDone = movesDone;
    for (NodeLink link : linkedWith)
    {
      NodeLink newLink = link.cloneAndReplace(this, d);
      d.addLink(newLink);
    }
    return d;
  }

  public String toString()
  {
    return "nr " + nodeNr + " | x,y " + locX + ", " + locY + " | links "
        + linkedWith.size() + " | crossing:" + getNrOfLinksCrossing()
        + " | av dist:" + getAverageDistance() + " | biggest dist:"
        + getBiggestDistance() + " | tot link dist" + getTotalLinkDistance();
  }

  public NodeLink[] getLinks()
  {
    return linkedWith.toArray(new NodeLink[0]);
  }
}
