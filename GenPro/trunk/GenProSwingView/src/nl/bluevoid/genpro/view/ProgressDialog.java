package nl.bluevoid.genpro.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

@SuppressWarnings("serial")
public class ProgressDialog extends JDialog {

  private JLabel jLabelTitle = null;

  private JPanel jPanelContent = null;

  private JProgressBar jProgressBar = null;

  private final boolean showText;

  /**
   * This method initializes
   * 
   * @param showText
   * 
   */

  public ProgressDialog(Container owner, String label, boolean showText) {
    super(SwingUtil.findParentFrame(owner), false);
    this.showText = showText;
    initialize();
    jLabelTitle.setText(label);
    int w = 300, h = 70;
    setSize(new Dimension(w, h));
    setLocationRelativeTo(null);
    setLocation((owner.getWidth() - w) / 2, (owner.getHeight() - h) / 2);
    setVisible(true);
    jProgressBar.setStringPainted(showText);
  }

  public void setText(String text) {
    jLabelTitle.setText(text);
    SwingUtil.paintNow(jLabelTitle);
  }

  /**
   * This method initializes this
   * 
   */
  private void initialize() {
    this.setContentPane(getJPanelContent());
    //super.setAlwaysOnTop(true);
    super.setUndecorated(true);
  }

  private JLabel getJLableTitle() {
    if (jLabelTitle == null) {
      jLabelTitle = new JLabel();
    }
    return jLabelTitle;
  }

  public void setProgress(int max, int current) {
    jProgressBar.setMaximum(max);
    jProgressBar.setValue(current);
    if (showText)
      jProgressBar.setString("" + current);
    //SwingUtil.paintNow(jPanelContent);
  }

  public void setMaximum(int maximum) {
    jProgressBar.setMaximum(maximum);
    SwingUtil.paintNow(jPanelContent);
  }

  public void advanceProgress() {
    jProgressBar.setValue(jProgressBar.getValue() + 1);
    SwingUtil.paintNow(jPanelContent);
  }

  /**
   * 
   * @param max
   *          , the maximum range of the progress
   * @param start
   *          the startPoint of faking progress (> 0)
   * @param end
   *          the endPoint of faking progress (< max)
   * @param timePerStep
   *          , the time in ms to wait between each progress between start en end
   */

  private boolean stopFake;

  public void startfakeProgress(final int steps, final long timePerStep) {
    stopFake = false;
    jProgressBar.setStringPainted(false);
    Thread t = new Thread("ProgressDialog fakeProgress") {
      @Override
      public void run() {
        long timePerStep2 = timePerStep;
        for (int i = 0; i < steps && stopFake == false; i++) {
          if (!stopFake)
            advanceProgress();
          SwingUtil.paintNow(jPanelContent);
          try {
            Thread.sleep(timePerStep2);
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          // SwingUtil.interruptSafeSleep(timePerStep2);
          timePerStep2 = (int) (timePerStep2 * 1.4);
        }
      }
    };
    t.start();
  }

  /**
   * This method initializes jPanelContent
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJPanelContent() {
    if (jPanelContent == null) {
      jPanelContent = new JPanel();
      jPanelContent.setLayout(new BorderLayout());
      CompoundBorder lines = new CompoundBorder(new LineBorder(Color.WHITE, 1), new LineBorder(Color.BLUE, 1));
      jPanelContent.setBorder(new CompoundBorder(lines, new EmptyBorder(8, 8, 8, 8)));
      jPanelContent.setBackground(Color.WHITE);
      jPanelContent.add(getJLableTitle(), BorderLayout.NORTH);
      jPanelContent.add(getJProgressBar(), BorderLayout.SOUTH);
    }
    return jPanelContent;
  }

  /**
   * This method initializes jProgressBar
   * 
   * @return javax.swing.JProgressBar
   */
  private JProgressBar getJProgressBar() {
    if (jProgressBar == null) {
      jProgressBar = new JProgressBar();

    }
    return jProgressBar;
  }

  /**
   * set to max, sleep for 100 ms for showing, then remove from screen
   * 
   */
  public void setToMaxAndClose() {
    try {
      setProgress(100, 100);
      SwingUtil.paintNow(jPanelContent);
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      // SwingUtil.interruptSafeSleep(100);
    } finally {
      setVisible(false);
    }
  }
}
