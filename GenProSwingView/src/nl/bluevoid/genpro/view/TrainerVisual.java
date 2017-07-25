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

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import nl.bluevoid.genpro.Grid;
import nl.bluevoid.genpro.ResultListener;
import nl.bluevoid.genpro.Trainer;
import nl.bluevoid.genpro.util.Debug;
import nl.bluevoid.genpro.util.StringUtil;
import nl.bluevoid.genpro.util.VMUtil;

import com.mallardsoft.tuple.Pair;
import com.mallardsoft.tuple.Variable;

/**
 * @author Rob van der Veer
 * @since 1.0
 */
public abstract class TrainerVisual extends Trainer implements ResultListener {

  private static final int GRAPH = 0;

  private static final String TITLE_PREFIX = "GenPro - ";
  private JFrame frame;
  private JPanel basePanel = new JPanel();
  private JTabbedPane tab;

  private JTextArea statsText;
  private JTextArea javaText;

  private ProgressDialog progBar;
  private JLabel statusLabel;

  private boolean firstBest = true;
  BlockingQueue<Grid> renderQueue = new LinkedBlockingQueue<Grid>();
  BlockingQueue<Pair<Integer, Long>> statsQueue = new LinkedBlockingQueue<Pair<Integer, Long>>();
  private JTextArea historyText;
  protected Grid lastOrCurrentRenderedGrid;
  private JCheckBox trackHistory;
  private JTextArea errorText;

  public TrainerVisual() {
    super.addResultListener(this);
    init();
    renderThread.start();
    statsRenderThread.start();
  }

  private void init() {
    frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(basePanel);
    frame.setSize(800, 700);
    frame.setVisible(true);
    basePanel.setLayout(new BorderLayout());
    basePanel.add(getControlPanel(), BorderLayout.NORTH);
    basePanel.add(getTabPanel(), BorderLayout.CENTER);
    basePanel.add(getStatusPanel(), BorderLayout.SOUTH);
    frame.setTitle(TITLE_PREFIX + setup.getName());
    tab.setComponentAt(GRAPH, new JLabel(""));
  }

  private JPanel getStatusPanel() {
    JPanel p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
    statusLabel = new JLabel("Please wait for first results...");
    p.add(statusLabel);
    if (!VMUtil.isVMRunningInServerMode()) {
      JLabel warningLabel = new JLabel("Running the VM is server mode will speed up by 50%. Currently: "
          + VMUtil.getJitCompilerName() + ". Add -server to VM arguments.\n");
      p.add(warningLabel);
    }
    return p;
  }

  private JPanel getControlPanel() {
    JPanel p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
    showJunkDNA = new JCheckBox("Show junk-DNA");
    showJunkDNA.setSelected(setup.isJunkDnaShown());
    showJunkDNA.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        setup.setJunkDnaShown(showJunkDNA.isSelected());
        rerenderGrid();
      }
    });
    p.add(showJunkDNA);
    trackHistory = new JCheckBox("Track history");
    trackHistory.setSelected(setup.isGridHistoryTrackingOn());
    trackHistory.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        setup.setGridHistoryTracking(trackHistory.isSelected());
        rerenderGrid();
        showOrHideHistoryTab();
      }
    });
    p.add(trackHistory);

    return p;
  }

  private void rerenderGrid() {
    if (renderQueue.isEmpty() && lastOrCurrentRenderedGrid != null) {
      // no upcomming renderevents to do from scratch: rerender last solution
      renderQueue.add(lastOrCurrentRenderedGrid);
    }
  }

  private JTabbedPane getTabPanel() {

    Font f = new Font("Monospaced", Font.PLAIN, 12);

    tab = new JTabbedPane();
    tab.addTab("Graph view", new JPanel());
    JScrollPane javaScrollPane = new JScrollPane();
    tab.addTab("Java view", javaScrollPane);
    JScrollPane statsScrollPane = new JScrollPane();
    tab.addTab("Statistics", statsScrollPane);

    errorScrollPane = new JScrollPane();
    tab.addTab("Errors", errorScrollPane);
    enableErrorTab(false);
    errorText = new JTextArea();
    errorText.setFont(f);
    errorText.setBorder(new EmptyBorder(10, 10, 10, 10));
    errorScrollPane.setViewportView(errorText);

    historyScrollPane = new JScrollPane();
    tab.addTab("Solution History", historyScrollPane);
    historyText = new JTextArea();
    historyText.setFont(f);
    historyText.setBorder(new EmptyBorder(10, 10, 10, 10));
    historyScrollPane.setViewportView(historyText);
    // show if history tracking is on
    showOrHideHistoryTab();

    // Font f = new Font("Monospaced", Font.PLAIN, 12);
    statsText = new JTextArea();
    statsText.setFont(f);
    statsText.setBorder(new EmptyBorder(10, 10, 10, 10));
    statsScrollPane.setViewportView(statsText);

    Font javaFont = new Font("Monospaced", Font.PLAIN, 14);
    javaText = new JTextArea();
    javaText.setFont(javaFont);
    javaText.setBorder(new EmptyBorder(10, 10, 10, 10));
    javaScrollPane.setViewportView(javaText);

    return tab;
  }

  public void startTraining() {
    try {
      super.startTraining();
    } catch (Throwable e) {
      e.printStackTrace();
      showError(e);
    }
  }

  private void showError(Throwable t) {
    enableErrorTab(true);
    tab.setSelectedComponent(errorScrollPane);
    if (errorText.getText() == null || errorText.getText().length() == 0) {
      errorText.setText("An error occured!:\n\n" + t.getMessage() + "\n" + Debug.getFullStackTrace(t));
    }
  }

  private void enableErrorTab(boolean b) {
    tab.setEnabledAt(tab.indexOfComponent(errorScrollPane), b);
  }

  private void showOrHideHistoryTab() {
    tab.setEnabledAt(tab.indexOfComponent(historyScrollPane), setup.isGridHistoryTrackingOn());
  }

  Thread renderThread = new Thread("Trainervisual rendereThread") {
    public void run() {
      while (true) {
        try {
          Grid g = null;
          try {
            g = renderQueue.take();
            lastOrCurrentRenderedGrid = g;
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          Debug.println("\n\nRendering solution:\n" + getJava(g));

          if (progBar != null) {
            // progBar.setToMaxAndClose();
            progBar.setVisible(false);
          }
          final int MAX = 6;
          if (firstBest) {
            progBar = new ProgressDialog(frame, "Rendering...", false);
            progBar.setProgress(MAX, 1);
          }
          frame.setTitle(TITLE_PREFIX + setup.getName() + " - best solution: " + g.getScore());
          // TODO threadleak!!! on every new graph!!!
          JungGraph jg = new JungGraph(g, setup.isJunkDnaShown());
          if (firstBest)
            progBar.setProgress(MAX, 2);

          tab.setComponentAt(GRAPH, jg.getVisualComponent());
          if (firstBest)
            progBar.setProgress(MAX, 3);
          javaText.setText(getJava(g));
          if (firstBest)
            progBar.setProgress(MAX, 4);
          if (setup.isGridHistoryTrackingOn()) {
            historyText.setText(StringUtil.join("\n", g.getHistory().toArray(new String[0])));
          }
          if (firstBest)
            progBar.setProgress(MAX, 5);
          statsText.setText(getResultsAsString(g));
          frame.repaint();
          if (firstBest) {
            progBar.setToMaxAndClose();
            firstBest = false;
          }
          Debug.println(">>>> Finished Rendering solution.");
        } catch (Exception e) {
          e.printStackTrace();
          javaText.setText("Error in rendering!\n\n" + Debug.getFullStackTrace(e));
        }
      }
    }
  };

  Thread statsRenderThread = new Thread("Trainervisual statsRenderThread") {
    public void run() {
      try {
        while (true) {
          Pair<Integer, Long> pair = null;
          try {
            pair = statsQueue.take();
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          Variable<Integer> genNr = new Variable<Integer>();
          Variable<Long> millis = new Variable<Long>();

          pair.extract(genNr).extract(millis);

          String txt = "Evaluating generation: " + (genNr.get() + 1) + ". Average time per generation ("
              + setup.getGenerationSize() + " individuals, " + evaluator.getTestSet().getNumberOfTestCases()
              + " testcases): " + millis.get() + " milliseconds.";
          // System.out.println(txt);
          statusLabel.setText(txt);
        }
      } catch (Throwable e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  };
  private JCheckBox showJunkDNA;
  private JScrollPane historyScrollPane;
  private JScrollPane errorScrollPane;

  public void newBestResult(final Grid g) {
    renderQueue.add(g);
  }

  @Override
  public void newStats(final int generationnr, final long millisPerGeneration) {
    statsQueue.add(Pair.from(generationnr, millisPerGeneration));
  }

  @Override
  public void startUpProgress(int createdIndividuals) {
    if (progBar == null) {
      progBar = new ProgressDialog(frame, "Creating solutions in first generation...", true);
    }
    progBar.setProgress(setup.getGenerationSize(), createdIndividuals);

    if (createdIndividuals == setup.getGenerationSize()) {
      progBar.setText("Evaluating first generation of " + setup.getGenerationSize() + " solutions...");
      progBar.setProgress(30, 0);
      progBar.startfakeProgress(30, 200);
    }
  }
}