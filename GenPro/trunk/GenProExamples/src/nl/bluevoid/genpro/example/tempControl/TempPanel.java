package nl.bluevoid.genpro.example.tempControl;

import javax.swing.JPanel;

import nl.bluevoid.genpro.view.SwingUtil;

@SuppressWarnings("serial")
public class TempPanel extends FeedbackGUIPanel {
  private static final String[] feedbackbuttontext = new String[] { "Too Warm", "Too Cold" };
  private static final String title = "GP Temperature Algorithm Simulation and Control Panel";

  public TempPanel() {
    super(title, feedbackbuttontext, getResultPanel());
    setBackground(UIConstants.COLOR_BACKGROUND_PANEL);
  }

  private static JPanel getResultPanel() {
    return null;// new JPanel();
  }

  @Override
  protected void proceedTime(int factor) {
    // TODO Auto-generated method stub

  }

  @Override
  protected void feedBackProvided(String text) {
    // TODO Auto-generated method stub

  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    SwingUtil.setWindowsLookAndFeel();
    SwingUtil.showPanelInFrame(new TempPanel());
  }
}
