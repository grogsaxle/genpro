package nl.bluevoid.genpro.example.tempControl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import nl.bluevoid.genpro.Grid;
import nl.bluevoid.genpro.JavaGenerator;
import nl.bluevoid.genpro.view.SwingUtil;

@SuppressWarnings("serial")
public abstract class FeedbackGUIPanel extends JPanel {
  private JPanel headerPanel;
  private JPanel timeProceedPanel;
  private JPanel feedbackPanel;
  private final String[] feedbackbuttontext;
  private JPanel algoritmResultPanel;
  private final JPanel algResult;
  private JTextArea algoritme;
  private JLabel fitness;
  private final String title;

  public FeedbackGUIPanel(String title, String[] feedbackbuttontext, /* @NonNull */JPanel algResult) {
    this.title = title;
    this.feedbackbuttontext = feedbackbuttontext;
    this.algResult = algResult;
    init();
  }

  private void init() {
    setOpaque(true);
    setLayout(new BorderLayout());
    add(getInputPanel(), BorderLayout.WEST);
    add(getHeaderPanel(), BorderLayout.NORTH);
    add(getFeedbackPanel(), BorderLayout.EAST);
    add(getAlgoritmResultPanel(), BorderLayout.CENTER);
    add(getTimeProceedPanel(), BorderLayout.SOUTH);
  }

  private Component getAlgoritmResultPanel() {
    algoritmResultPanel = new JPanel();
    algoritmResultPanel.setLayout(new BoxLayout(algoritmResultPanel, BoxLayout.Y_AXIS));
    algoritme = new JTextArea("no algorithm");
    algoritme.setEditable(false);
    algoritmResultPanel.add(algoritme);
    fitness = new JLabel("algorithm fitness");
    algoritmResultPanel.add(fitness);
    algoritmResultPanel.add(algResult);
    return algoritmResultPanel;
  }

  public void setBestResult(Grid program) {
    fitness.setText("algorithm fitness: " + program.getScore());
    String java = JavaGenerator.getJavaProgram(program, "best_" + program.getScore(),"nl.bluevoid.genpro.compiled", false);
    algoritme.setText(java);
  }

  protected abstract void proceedTime(int factor);

  private Component getFeedbackPanel() {
    feedbackPanel = new JPanel();
    feedbackPanel.setOpaque(false);
    feedbackPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
    feedbackPanel.setLayout(new BoxLayout(feedbackPanel, BoxLayout.Y_AXIS));
    for (final String text : feedbackbuttontext) {
      JButton b = new JButton(text);
      b.addActionListener(new ActionListener() {
        
        public void actionPerformed(ActionEvent e) {
          feedBackProvided(text);
        }
      });
      feedbackPanel.add(b);
    }
    return feedbackPanel;
  }

  protected abstract void feedBackProvided(String text);

  private JPanel getTimeProceedPanel() {
    timeProceedPanel = new JPanel();
    timeProceedPanel.setOpaque(false);
    timeProceedPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    for (int i = 0; i < 4; i++) {
      final int factor = (int) Math.pow(10, i);
      JButton b = new JButton(factor + " steps");
      b.addActionListener(new ActionListener() {
        
        public void actionPerformed(ActionEvent e) {
          proceedTime(factor);
        }
      });
      timeProceedPanel.add(b);
    }
    return timeProceedPanel;
  }

  private JPanel getInputPanel() {
    JPanel inputPanel = new JPanel();
    inputPanel.setOpaque(false);
    inputPanel.setLayout(new GridBagLayout());
    return inputPanel;
  }

  private JPanel getHeaderPanel() {
    headerPanel = new JPanel();
    headerPanel.setOpaque(false);
    headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 5));
    headerPanel.setOpaque(false);
    JLabel label = new JLabel(title);

    label.setFont(UIConstants.FONT_MAIN_TITLE);
    label.setForeground(Color.BLUE);
    headerPanel.add(label);
    try {
      ImageIcon bluevoidLogo = SwingUtil.createImageIcon("images/logo_bluevoid.PNG", "logo");
      headerPanel.add(new JLabel(bluevoidLogo));
    } catch (Throwable e) {
      e.printStackTrace();
    }
    headerPanel.setBorder(new CompoundBorder(new EmptyBorder(5, 5, 15, 5), new MatteBorder(0, 0, 1, 0,
        Color.BLACK)));
    return headerPanel;
  }

}
