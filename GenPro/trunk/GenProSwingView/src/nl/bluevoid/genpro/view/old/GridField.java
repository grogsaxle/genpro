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

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2003
 * Company:
 * @author robbio
 * @version 1.0
 */

import java.util.HashMap;
import java.util.Vector;

import nl.bluevoid.genpro.Grid;
import nl.bluevoid.genpro.cell.CallCell;
import nl.bluevoid.genpro.cell.CellInterface;
import nl.bluevoid.genpro.cell.InputCell;
import nl.bluevoid.genpro.cell.ReferenceCell;
import nl.bluevoid.genpro.cell.ValueCell;
import nl.bluevoid.genpro.util.Debug;

public class GridField {
  Vector<Node> allNodes = new Vector<Node>();
  int xSize, ySize;
  private final Grid grid;

  HashMap<String, Node> cellNodeMap = new HashMap<String, Node>();

  public GridField(int vx, int vy, Grid grid) {
    xSize = vx;
    ySize = vy;
    this.grid = grid;
    createNodes();
    createLinks();
  }

  public void createNodes() {
    InputCell[] inCells = grid.getInputCells();
    int spacing = ySize / (inCells.length + 1);
    for (int i = 0; i < inCells.length; i++) {
      Debug.checkNotNull(inCells[i], "inCells:" + i);
      Node n = new Node(0, spacing * i + spacing, this, false, inCells[i]);
      addNode(n);
    }

    ReferenceCell[] outCells = grid.getOutputCells();
    int spacingR = ySize / (outCells.length + 1);
    for (int i = 0; i < outCells.length; i++) {
      Debug.checkNotNull(outCells[i], "outCells:" + i);
      Node n = new Node(xSize - 1, spacingR * i + spacingR, this, false, outCells[i]);
      addNode(n);
    }
    placeRandom(grid.getConstantCells());
    placeRandom(grid.getCallCells());
    placeRandom(grid.getLibraryCells());
  }

  private void placeRandom(CellInterface[] gridCells) {
    for (int i = 0; i < gridCells.length; i++) {
      int x = 0, y = 0;
      do {
        x = (int) (Math.random() * xSize);
        y = (int) (Math.random() * ySize);
      } while (getNode(x, y) != null);
      Node n = new Node(x, y, this, true, gridCells[i]);
      addNode(n);
    }
  }

  private void addNode(Node n) {
    Debug.println("Adding node to map:" + n.cell);
    allNodes.add(n);
    cellNodeMap.put(n.cell.getName(), n);
  }

  private void createLinks() {
    System.out.println("creating links...");
    Node[] nodes = getAllNodes();
    int errors = 0;

    for (Node node : nodes) {
      try {
        if (node.cell instanceof CallCell) {
          // link target
          CellInterface linkedCell = ((CallCell) node.cell).getTargetCell();
          String methodNaam = ((CallCell) node.cell).getTargetMethod().getName();
          Node node2 = cellNodeMap.get(linkedCell.getName());
          Debug.checkNotNull(node2, "not found node for: " + linkedCell);
          node.linkWith(node2, NodeLink.TARGET, methodNaam);
          // link params
          for (ValueCell cell : ((CallCell) node.cell).getParams()) {
            Node nodeParam = cellNodeMap.get(cell.getName());
            Debug.checkNotNull(nodeParam, "not found node for: " + cell);
            node.linkWith(nodeParam, NodeLink.PARAM, "param");
          }
        } else if (node.cell instanceof ReferenceCell) {
          // link target
          CellInterface linkedCell = ((ReferenceCell) node.cell).getReferedCell();
          String methodNaam = "output";
          Node node2 = cellNodeMap.get(linkedCell.getName());
          Debug.checkNotNull(node2, "not found node for: " + linkedCell);
          node.linkWith(node2, NodeLink.TARGET, methodNaam);
        }
      } catch (LinkError e) {
        e.printStackTrace();
      }
    }
  }

  public Node getNode(int x, int y) {
    if (!isValidField(x, y))
      return null;
    for (int i = 0; i < allNodes.size(); i++) {
      if (allNodes.get(i).isOnLocation(x, y)) {
        return allNodes.get(i);
      }
    }
    return null;
  }

  public boolean isValidField(int x, int y) {
    if (x < 0 || x > getX() - 1)
      return false;
    if (y < 0 || y > getY() - 1)
      return false;
    return true;
  }

  public int getX() {
    return xSize;
  }

  public void setX(int x) {
    xSize = x;
  }

  public int getY() {
    return ySize;
  }

  public void setY(int y) {
    ySize = y;
  }

  public Node[] getAllNodes() {
    return allNodes.toArray(new Node[0]);
  }

  public int[] getCentreLocation() {
    int x = 0;
    int y = 0;
    for (Node node : allNodes) {
      x += node.locX;
      y += node.locY;
    }
    return new int[] { x / allNodes.size(), y / allNodes.size() };
  }

  public int calculateAllCrossings() {
    NodeLink[] links = getAllLinks();
    for (NodeLink link : links) {
      link.resetCrossing();
    }
    // System.out.println("******** Start crossing calculation... ");
    int nr = 0;
    for (int i = 0; i < links.length; i++) {
      NodeLink a = links[i];
      for (int j = 0; j < links.length; j++) {
        if (a.callculateCrossing(links[j])) {
          nr++;
          // break;
        }
      }
    }
    // System.out.println("****** nr of crossings:" + nr);
    return nr;
  }

  public void calculateCrossings(NodeLink link) {
    for (NodeLink linkA : getAllLinks()) {
      if (link.callculateCrossing(linkA))
        break;
    }
    // System.out.println("calculatedCrossings for link" + link.toString());

  }

  private NodeLink[] allLinks;

  public NodeLink[] getAllLinks() {
    if (allLinks == null) {
      HashMap h = new HashMap(allNodes.size() * 2);
      for (Node node : allNodes) {
        for (NodeLink link : node.getLinks()) {
          Object obj = h.put(link.hashKey(), link);
        }
      }
      allLinks = (NodeLink[]) h.values().toArray(new NodeLink[0]);
    }
    return allLinks;
  }

  public int getNrOfNodes() {
    return allNodes.size();
  }
}
