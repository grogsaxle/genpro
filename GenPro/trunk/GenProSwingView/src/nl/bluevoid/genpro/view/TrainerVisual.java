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

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import nl.bluevoid.genpro.Grid;
import nl.bluevoid.genpro.ResultListener;
import nl.bluevoid.genpro.Trainer;
/**
 * @author Rob van der Veer
 * @since 1.0
 */
public abstract class TrainerVisual extends Trainer implements ResultListener {

  private static final int GRAPH = 0;
  private static final int JAVA = 1;
  private static final int STATS = 2;

  private JFrame frame;
  private JPanel basePanel = new JPanel();
  private JTabbedPane tab;

  private JTextArea statsText;
  private JTextArea javaText;

  public TrainerVisual() {
    super.addResultListener(this);
    init();
  }

  private void init() {
    frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(basePanel);
    frame.setSize(800, 700);
    frame.setVisible(true);
    basePanel.setLayout(new BorderLayout());
    basePanel.add(getTabPanel(), BorderLayout.CENTER);
    frame.setTitle(setup.getName());
  }

  private JTabbedPane getTabPanel() {
    tab = new JTabbedPane();
    tab.addTab("Graph", new JPanel());
    JScrollPane javaScrollPane = new JScrollPane();
    tab.addTab("Java", javaScrollPane);
    JScrollPane statsScrollPane = new JScrollPane();
    tab.addTab("Statistics", statsScrollPane);

    Font f = new Font("Monospaced", Font.PLAIN, 12);
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

  protected void showGraphPanel(JPanel panel, String name) {
    frame.setTitle("Best solution for " + name);
  }

  public void newBestResult(Grid g) {
    frame.setTitle("Best solution for " + setup.getName() + ", score: " + g.getScore());

    JungGraph jg = new JungGraph(g, true);
    tab.setComponentAt(GRAPH, jg.getVisualComponent());

    //javaText.setText(formatter.format(getJava(g)));
    javaText.setText(getJava(g));
    statsText.setText(getResultsAsString(g));
  }
}
