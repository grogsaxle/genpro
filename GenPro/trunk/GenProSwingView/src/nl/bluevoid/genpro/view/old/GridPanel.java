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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

import nl.bluevoid.genpro.cell.CallCell;
import nl.bluevoid.genpro.cell.CellInterface;
import nl.bluevoid.genpro.cell.ConstantCell;
import nl.bluevoid.genpro.cell.InputCell;
import nl.bluevoid.genpro.cell.LibraryCell;
import nl.bluevoid.genpro.cell.ReferenceCell;
import nl.bluevoid.genpro.cell.ValueCell;
import nl.bluevoid.genpro.view.SwingUtil;

public class GridPanel extends JPanel {
  int pixelSizePerUnitX = 10, pixelSizePerUnitY;
  int startX = 140;
  int startY = 30;
  Point2D.Double[] hoeken = new Point2D.Double[4];
  GridField gridfield;
  int sizeX, sizeY;

  boolean autoScale = true;
  boolean drawMountainPoints = true;
  public int MUISVAK = 10;

  public GridPanel(GridField landscape) {
    this.gridfield = landscape;
    setPreferredSize(new Dimension(1000, 800));
    setParamsAfterAdjustment();
  }

  public void setDrawScaleX(int x) {
    pixelSizePerUnitX = x;
    setParamsAfterAdjustment();
  }

  public int getDrawScaleX() {
    return pixelSizePerUnitX;
  }

  public boolean isAutoScale() {
    return autoScale;
  }

  public void setAutoScale(boolean b) {
    autoScale = b;
    setParamsAfterAdjustment();
  }

  public void setParamsAfterAdjustment() {
    sizeX = gridfield.getX();
    sizeY = gridfield.getY();
    if (autoScale)
      pixelSizePerUnitX = (getWidth() - startX - 120) / sizeX;// TODO convert to
    // percentages
    pixelSizePerUnitY = (int) (pixelSizePerUnitX);// * .7);
  }

  public void forcePaint() {
    // int w = startX + sizeX * pixelSizePerUnitX;
    // int h = startY + sizeY * pixelSizePerUnitY;
    paintImmediately(0, 0, getWidth(), getHeight());
    // output.append("\n>> forcedpaint Gview w "+w+" h "+h+">> ");
  }

  public void paint(Graphics g) {
    super.paint(g);
    SwingUtil.setAntiAlias(g);
    // output.append("\nIn gview paint");
    // long start = System.currentTimeMillis();

    setParamsAfterAdjustment();
    if (true) {
      g.setColor(Color.blue);
      for (int yL = 0; yL < sizeY; yL++) {
        for (int xL = 0; xL < sizeX; xL++) {
          int x = (int) getScreenX(xL, yL);
          int y = (int) getScreenY(yL);

          if ((xL + 1) % 5 == 0 || (yL + 1) % 5 == 0) {
            g.fillOval(x, y, 2, 2);
          } else {
            g.drawLine(x, y, x + 1, y);
          }
          if (yL == 0)
            g.drawString("" + xL, x, y - 15);
          if (xL == 0)
            g.drawString("" + yL, x - 35, y);
        }
      }
    }
    final int ovalW = (int) (pixelSizePerUnitX * 0.8);
    final int ovalH = (int) (ovalW);// * 0.8);
    // draw nodes
    Node[] nodes = gridfield.getAllNodes();

    for (Node node : nodes) {
      for (NodeLink link : node.linkedWith) {
        if (link.nodeA == node) {
          int x = (int) getScreenX(node.locX, node.locY);
          int y = (int) getScreenY(node.locY);
          Node node2 = link.getOtherNode(node);
          int x2 = (int) getScreenX(node2.locX, node2.locY);
          int y2 = (int) getScreenY(node2.locY);
          if (link.type == NodeLink.TARGET)
            g.setColor(Color.BLUE);
          else
            // param
            g.setColor(Color.LIGHT_GRAY);
          g.drawLine(x, y, x2, y2);

          SwingUtil.drawStringCentered(g, link.naam, (x + x2) / 2, (y + y2) / 2);
          int xPijl = x + (int) ((x2 - x) * 0.7);
          int yPijl = y + (int) ((y2 - y) * 0.7);

          g.fillOval(xPijl - 2, yPijl - 2, 5, 5);
          // ((Graphics2D)g).
        }
      }
    }

    for (Node node : nodes) {
      int x = (int) getScreenX(node.locX, node.locY);
      int y = (int) getScreenY(node.locY);
      int ovalX = x - ovalW / 2;
      int OvalY = y - ovalH / 2;

      CellInterface cell = node.cell;
      String extraInfo = "";
      if (cell instanceof ValueCell) {
        extraInfo += ((ValueCell) cell).getValueType().getSimpleName();
      }
      if (cell instanceof InputCell) {
        g.setColor(Color.WHITE);
      } else if (cell instanceof ReferenceCell) {
        g.setColor(Color.WHITE);

      } else if (cell instanceof LibraryCell) {
        g.setColor(Color.WHITE);
      } else if (cell instanceof CallCell) {
        g.setColor(Color.LIGHT_GRAY);
      } else if (cell instanceof ConstantCell) {
        g.setColor(Color.YELLOW);
        //extraInfo += ((ConstantCell) cell).getValue().toString();
      }

      g.fillOval(ovalX, OvalY, ovalW, ovalH);
      g.setColor(Color.BLACK);
      g.drawOval(ovalX, OvalY, ovalW, ovalH);
      SwingUtil.drawStringCentered(g, cell.getName(), x, y);
      SwingUtil.drawStringCentered(g, extraInfo, x, y + 10);
    }
  }

  private float getScreenX(int x, int y) {
    float xFactor = (pixelSizePerUnitX + ((y * (float) pixelSizePerUnitX) / 40f));
    int offset = startX - y * 6;
    return (x * xFactor + offset);
  }

  private float getScreenY(int y) {
    float lineSize = (pixelSizePerUnitY + y / 5f);
    return y * lineSize + startY;
  }
}
