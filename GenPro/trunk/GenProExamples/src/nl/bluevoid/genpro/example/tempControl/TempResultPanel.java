package nl.bluevoid.genpro.example.tempControl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import nl.bluevoid.genpro.Grid;
import nl.bluevoid.genpro.NumberFeedback;
import nl.bluevoid.genpro.ResultListener;
import nl.bluevoid.genpro.util.JFreeChartUtil;
import nl.bluevoid.genpro.view.SwingUtil;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.TimePeriodValuesCollection;

@SuppressWarnings("serial")
public class TempResultPanel extends JPanel implements ResultListener {
  private final HouseTemperature tempProblem;
  private JFreeChart chart;
  private Thread t;
  private float[][] ups;
  private float[][] downs;

  public TempResultPanel(HouseTemperature tempProblem) {
    this.tempProblem = tempProblem;
    init();
    createFeedbackGraphData();
  }

  private void createFeedbackGraphData() {

    ArrayList<Object> feedbacks = tempProblem.getTestSet().getCellValues("t");
    ArrayList<Object> times = tempProblem.getTestSet().getCellValues("minuteOfDay");
    ups = new float[feedbacks.size()][2];
    downs = new float[feedbacks.size()][2];
    int upCounter = 0;
    int downCounter = 0;
    for (int i = 0; i < feedbacks.size(); i++) {
      Number num = (Number) times.get(i);
      NumberFeedback nuf = (NumberFeedback) feedbacks.get(i);
      if (nuf.directive == NumberFeedback.HIGHER) {
        ups[upCounter][0] = num.floatValue()*60*1000;
        ups[upCounter][1] = (float) nuf.value;
        System.out.println("adding up:"+ups[upCounter][1]);
        upCounter++;
      } else {
        downs[downCounter][0] = num.floatValue()*60*1000;
        downs[downCounter][1] = (float) nuf.value;
        System.out.println("adding down:"+downs[downCounter][1]);
        downCounter++;
      }
    }
    for (int i = upCounter; i < ups.length; i++) {
      ups[i][0]=ups[i-1][1]+1000;
      ups[i][1]=Float.NaN;
    }
    for (int i = downCounter; i < downs.length; i++) {
      //downs[i][0]=downs[i-1][1]+1000;
      downs[i][1]=Float.NaN;
    }
  }

  private void init() {
    setBorder(new LineBorder(UIConstants.COLOR_ACCENT, 2));
    setMinimumSize(new Dimension(100, 300));
  }

  public void paint(Graphics g) {
    if (chart != null) {
      BufferedImage image = chart.createBufferedImage(getWidth(), getHeight());
      g.drawImage(image, 0, 0, null);
    }
  }

  public synchronized void newBestResult(final Grid g) {
    if (t != null) {
      t.interrupt();
    }
    final JPanel panel = this;
    t = new Thread() {
      public void run() {
        try {
          float[][] data = tempProblem.getTempDayDataInhouse(g, 10);
          // float[][] data2 = tempProblem.getTestCaseErrorData(g);
          float[][] dataOut = tempProblem.getTempDayDataOuthouse(g, 10);

          // tempProblem.
          // cutOffHighValues(data2);
          cutOffHighValues(data);
          cutOffHighValues(dataOut);
          System.out.println("inhouse");
          printValues(data);
          System.out.println("outhouse");
          printValues(dataOut);
          // System.out.println("error");
          // printValues(data2);
          //          

          TimePeriodValuesCollection tpvc = JFreeChartUtil.addDataSerie(data, "person inhouse", null);
          JFreeChartUtil.addDataSerie(dataOut, "person outhouse", tpvc);
          // JFreeChartUtil.addDataSerie(data2, "testcase error", tpvc);

          chart = JFreeChartUtil.generateStepChart(tpvc, new Date(0), new Date(
              (long) data[data.length - 1][0]), "time on day", "temperature", "Temperature inhouse");

          Polygon triangleUp = new Polygon();
          triangleUp.addPoint(-3, 3);
          triangleUp.addPoint(3, 3);
          triangleUp.addPoint(0, -3);
          Polygon triangleDown = new Polygon();
          triangleDown.addPoint(-3, -3);
          triangleDown.addPoint(3, -3);
          triangleDown.addPoint(0, 3);
     
          XYLineAndShapeRenderer rendererhoger = new XYLineAndShapeRenderer();
          rendererhoger.setLinesVisible(false);  
          rendererhoger.setBaseStroke(new BasicStroke(2));
          rendererhoger.setSeriesShape(0, triangleUp);
          rendererhoger.setSeriesShape(1, triangleDown);
          rendererhoger.setBasePaint(Color.BLUE);

          TimePeriodValuesCollection hoger = JFreeChartUtil.addDataSerie(ups, "hoger", null);
          JFreeChartUtil.addDataSerie(downs, "lager", hoger);
          JFreeChartUtil.addOverlayDataSerieToXYChart(chart, hoger, rendererhoger);

        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        SwingUtil.paintNow(panel);
      }
    };
    t.start();

  }

  private void printValues(float[][] data2) {
    for (float[] fs : data2) {
      System.out.print(" " + ((int) (fs[1] * 10)) / 10.0);
    }
    System.out.println();
  }

  private void cutOffHighValues(float[][] data) {
    for (float[] fs : data) {
      if (fs[1] < 100 && fs[1] > -100) {
        // do nothing
      } else if (fs[1] > 100)
        fs[1] = 100;
      else if (fs[1] < -100)
        fs[1] = -100;
      else if (Float.isNaN(fs[1]))
        fs[1] = 0;
      else if (Float.isInfinite(fs[1]))
        fs[1] = 100;
    }
  }
}