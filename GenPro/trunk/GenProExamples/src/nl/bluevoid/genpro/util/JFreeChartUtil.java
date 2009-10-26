package nl.bluevoid.genpro.util;

import java.awt.Color;
import java.util.Date;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriod;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;
import org.jfree.chart.renderer.xy.XYItemRenderer;

public class JFreeChartUtil {
  public static JFreeChart generateStepChart(TimePeriodValuesCollection tpvc, Date startDate, Date endDate,
      String x_title, String y_title, String chartname) {
    Debug.checkNotNull(tpvc, "tpvc");
    Debug.checkNotNull(startDate, "startDate");
    Debug.checkNotNull(endDate, "endDate");
    // TimePeriodValuesCollection staat periodes met verschillende lengte toe.
    // TimePeriodValuesCollection tpvc = new TimePeriodValuesCollection();

    JFreeChart jfc;

    jfc = ChartFactory.createTimeSeriesChart(chartname, // title
        x_title, // x-axis label
        y_title, // y-axis label
        tpvc, // data
        true, // create legend?
        true, // generate tooltips?
        false // generate URLs?
        );
    // This method constructs a JFreeChart

    jfc.setBackgroundPaint(new Color(255, 255, 200));
    XYPlot plot = (XYPlot) jfc.getPlot();
    // ValueAxis var = plot.getRangeAxis();
    // log.info("getRangeAxis:" + var.getClass().getName());
    DateAxis vad = (DateAxis) plot.getDomainAxis();
    vad.setRange(startDate, endDate);

    // XYItemRenderer r = plot.getRenderer();

    // XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
    // renderer.setBaseShapesVisible(true);
    // renderer.setBaseShapesFilled(true);
    jfc.setAntiAlias(true);
    jfc.setTextAntiAlias(true);
    return jfc;
  }

  public static TimePeriodValuesCollection addDataSerie(final float[][] data, final String chartname,
      TimePeriodValuesCollection tpvc) {
    if (tpvc == null) {
      tpvc = new TimePeriodValuesCollection();
    }
    TimePeriodValues tpv = new TimePeriodValues(chartname, "domain", "range");

    if (data.length > 0) {
      for (int i = 0; i < data.length; i++) {
        TimePeriod tp = new SimpleTimePeriod((long) data[i][0], (long) data[i][0]);
        tpv.add(tp, data[i][1]);
      }
    }
    tpvc.addSeries(tpv);
    return tpvc;
  }
  
  public static void addOverlayDataSerieToXYChart(JFreeChart jfc, TimePeriodValuesCollection tpvc, XYItemRenderer renderer ){
    XYPlot plot = (XYPlot) jfc.getPlot();
    int max=plot.getDatasetCount();
    plot.setDataset(max, tpvc);
    plot.setRenderer(max, renderer);
   
  }
  
}