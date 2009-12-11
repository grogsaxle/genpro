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

package nl.bluevoid.genpro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

import nl.bluevoid.genpro.Grid;
import nl.bluevoid.genpro.cell.CallCell;
import nl.bluevoid.genpro.cell.CellInterface;
import nl.bluevoid.genpro.cell.ConstantCell;
import nl.bluevoid.genpro.cell.InputCell;
import nl.bluevoid.genpro.cell.LibraryCell;
import nl.bluevoid.genpro.cell.ReferenceCell;
import nl.bluevoid.genpro.cell.ValueCell;
import nl.bluevoid.genpro.util.Debug;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
/**
 * @author Rob van der Veer
 * @since 1.0
 */
public class JungGraph {
  private final Grid grid;
  private Graph<CellInterface, Link> g2;
  private SpringLayout<CellInterface, Link> layout;
  private InputCell[] inCells;
  private ReferenceCell[] outCells;

  //private ArrayList<CellInterface> all = new ArrayList<CellInterface>();

  // private static int count = 0;
  private final boolean showJunkDna;
  private CellInterface[] legendCells;

  public JungGraph(Grid grid, boolean showJunkDna) {
    this.showJunkDna = showJunkDna;
    // count++;
    // System.out.println("creating JungGraph " + count);
    this.grid = grid;
    g2 = new SparseMultigraph<CellInterface, Link>();
    layout = new SpringLayout<CellInterface, Link>(g2);
    layout.setSize(new Dimension(800, 600)); // sets the initial size of the space

    createNodes();
    createLinks();
    addLegend();
    lockInputsAndOutputs();

    // System.out.println("The graph g2 = " + g2.toString());
  }

  private void lockInputsAndOutputs() {
    double deltaY = (layout.getSize().getHeight() / (inCells.length + 1));
    double x = layout.getSize().getWidth() / 8;
    for (int i = 0; i < inCells.length; i++) {
      layout.setLocation(inCells[i], new Point2D.Double(x, deltaY * i + deltaY));
      layout.lock(inCells[i], true);
    }

    double deltaYoutputs = (layout.getSize().getHeight() / (outCells.length + 1));
    double xOut = layout.getSize().getWidth() - x;
    for (int i = 0; i < outCells.length; i++) {
      layout.setLocation(outCells[i], new Point2D.Double(xOut, deltaYoutputs * i + deltaYoutputs));
      layout.lock(outCells[i], true);
    }
    // set constants in upper part
    double deltaYcc = (layout.getSize().getHeight() / 2 / (outCells.length + 1));
    double xOutcc = layout.getSize().getWidth() / 2;
    ConstantCell[] cc = grid.getConstantCells();
    for (int i = 0; i < cc.length; i++) {
      layout.setLocation(cc[i], new Point2D.Double(xOutcc, deltaYcc * i + deltaYcc));
    }
    // set libs in lower part
    double deltaYlc = (layout.getSize().getHeight() / 2 / (outCells.length + 1));
    double xOutlc = layout.getSize().getWidth() / 2;
    LibraryCell[] lc = grid.getLibraryCells();
    for (int i = 0; i < lc.length; i++) {
      layout.setLocation(lc[i], new Point2D.Double(xOutlc, deltaYlc * i + deltaYlc
          + layout.getSize().getHeight() / 2));
    }

    // set legend at bottom
    double Ydeltaledgend = 25;
    double xledgend = layout.getSize().getWidth() - 150;
    for (int i = 0; i < legendCells.length; i++) {
      layout.setLocation(legendCells[i], new Point2D.Double(xledgend, layout.getSize().getHeight() - i
          * Ydeltaledgend - 30));
      layout.lock(legendCells[i], true);
    }

  }

  public void createNodes() {
    inCells = grid.getInputCells();
    addCells(inCells, true);

    outCells = grid.getOutputCells();
    addCells(outCells, true);

    addCells(grid.getConstantCells(), false);

    addCells(grid.getCallCells(), false);
    addCells(grid.getLibraryCells(), false);
  }

  private void addLegend() {
    CallCell ccUsed = new CallCell("CallCell used", Double.class);
    ccUsed.setCascadeUsedForOutput();
    CallCell ccUnUsed = new CallCell("CallCell unused", Double.class);

    legendCells = new CellInterface[] { new ConstantCell("ConstantCell", Double.class, 0), ccUnUsed, ccUsed,
        new LibraryCell("LibraryCell", Math.class), new InputCell("Input/OutputCell", Double.class) };
    addCells(legendCells, true);
  }

  private void addCells(CellInterface[] inCells, boolean forceAdd) {
    for (int i = 0; i < inCells.length; i++) {
      Debug.checkNotNull(inCells[i], "inCells:" + i);
      if (forceAdd || showJunkDna || inCells[i].isUsedForOutput())
        g2.addVertex(inCells[i]);
    }
  }

  private void createLinks() {
    // System.out.println("creating links...");

    for (CellInterface cell : g2.getVertices().toArray(new CellInterface[0])) {
      if (cell instanceof CallCell) {
        // link target
        CellInterface linkedCell = ((CallCell) cell).getTargetCell();
        String methodNaam = ((CallCell) cell).getTargetMethod().getName();
        g2.addEdge(new Link(null, methodNaam), cell, linkedCell, EdgeType.DIRECTED);
        // link params
        ValueCell[] params = ((CallCell) cell).getParams();
        for (int i = 0; i < params.length; i++) {
          String pName = "param" + (params.length == 1 ? "" : " " + (i + 1));
          g2.addEdge(new Link(null, pName), cell, params[i], EdgeType.DIRECTED);
        }
      } else if (cell instanceof ReferenceCell) {
        // link target
        CellInterface linkedCell = ((ReferenceCell) cell).getReferedCell();
        String methodNaam = "output";
        g2.addEdge(new Link(null, methodNaam), cell, linkedCell, EdgeType.DIRECTED);
      }
    }
  }

  @SuppressWarnings("unchecked")
  public JPanel getVisualComponent() {
    // SimpleGraphDraw sgv = new SimpleGraphDraw(); // We create our graph in here

    // The BasicVisualizationServer<V,E> is parameterized by the edge types
    BasicVisualizationServer<CellInterface, Link> vv = new BasicVisualizationServer<CellInterface, Link>(
        layout);

    // Dimension d=new Dimension(layout.getSize());
    vv.setPreferredSize(layout.getSize()); // Sets the viewing area size

    vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<CellInterface>() {
      public String transform(CellInterface v) {
        return v.getName();
      }
    });
    vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());

    vv.getRenderer().getVertexLabelRenderer().setPosition(Position.E);

    // Setup up a new vertex to paint transformer...
    Transformer<CellInterface, Paint> vertexPaint = new Transformer<CellInterface, Paint>() {
      public Paint transform(CellInterface i) {
        switch (i.getCellType()) {
        case CallCell:
          if (i.isUsedForOutput())
            return Color.BLUE;
          else
            return Color.BLUE.darker().darker();
        case LibraryCell:
          return Color.YELLOW;
        case ConstantCell:
          return Color.LIGHT_GRAY;
        }
        return Color.WHITE;
      }
    };

    // Set up a new stroke Transformer for the edges
    float dash[] = { 10.0f };
    final Stroke edgeStrokeParam = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
        dash, 0.0f);
    final Stroke edgeStrokeCall = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);

    Transformer<Link, Stroke> edgeStrokeTransformer = new Transformer<Link, Stroke>() {
      public Stroke transform(Link s) {
        if (s.t.startsWith("param"))
          return edgeStrokeParam;
        else
          return edgeStrokeCall;
      }
    };
    vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
    vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);

    layout.setForceMultiplier(0.1);
    layout.setRepulsionRange(200);

    // for (int i = 0; i < 50; i++) {
    // layout.step();
    // }

    return vv;
  }
}

class Link {
  public Link(CellInterface ci, String t) {
    super();
    this.ci = ci;
    this.t = t;
  }

  final CellInterface ci;
  final String t;

  public String toString() {
    return t;
  }
}
