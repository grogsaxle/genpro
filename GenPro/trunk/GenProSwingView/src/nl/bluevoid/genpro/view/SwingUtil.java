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
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Label;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import nl.bluevoid.genpro.util.Debug;
import nl.bluevoid.genpro.util.FileUtil;
/**
 * @author Rob van der Veer
 * @since 1.0
 */
public class SwingUtil {

  public static final String PLAF_WINDOWS = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
  public static final String PLAF_JAVA = UIManager.getCrossPlatformLookAndFeelClassName();

  public static Frame findParentFrame(Container c) {
    while (c != null) {
      if (c instanceof Frame)
        return (Frame) c;
      c = c.getParent();
    }
    Debug.println("No parent frame found starting up with null as parent");
    return null;
  }

  public static void paintNow(JComponent comp) {
    comp.paintImmediately(0, 0, comp.getWidth(), comp.getHeight());
  }

  public static int getLongestPixelSize(Object[] objects, Font f) {
    FontMetrics fm = new Label().getFontMetrics(f);
    int longest = 0;
    for (Object object : objects) {
      longest = Math.max(fm.stringWidth(object.toString()), longest);
    }
    return longest;
  }

  public static int findLongestDrawingLength(String[] texts, Font font) {
    JLabel label = new JLabel();
    FontMetrics m = label.getFontMetrics(font);
    int longest = 0;
    for (String string : texts) {
      longest = Math.max(longest, m.stringWidth(string));
    }
    return longest;
  }

  public static void setAntiAlias(Graphics g) {
    ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
        RenderingHints.VALUE_ANTIALIAS_ON);
  }

  public static void drawStringCentered(Graphics g, String str, int x, int y) {
    int w = g.getFontMetrics().stringWidth(str) / 2;
    g.drawString(str, x - w, y);
  }

  /**
   * Returns an ImageIcon, or null if the path was invalid. The path is relative to the base of the project:
   * the directory above the src dir.
   */
  public static ImageIcon createImageIcon(String path, String description) {
    java.net.URL imgURL = FileUtil.class.getClassLoader().getResource(path);
    if (imgURL != null) {
      return new ImageIcon(imgURL, description);
    } else {
      Debug.println(" ****** Couldn't find file: " + path);
      return null;
    }
  }

  public static ImageIcon fitBox(ImageIcon imageIcon, int newWidth, int heigth) {
    Image i = imageIcon.getImage();
    Image resizedImage = null;

    int iWidth = i.getWidth(null);
    int iHeight = i.getHeight(null);

    if (iWidth > iHeight) {
      resizedImage = i.getScaledInstance(newWidth, (newWidth * iHeight) / iWidth, Image.SCALE_SMOOTH);
    } else {
      resizedImage = i.getScaledInstance((newWidth * iWidth) / iHeight, newWidth, Image.SCALE_SMOOTH);
    }
    // This code ensures that all the pixels in the image are loaded.
    Image temp = new ImageIcon(resizedImage).getImage();
    // Create the buffered image.
    BufferedImage bufferedImage = new BufferedImage(temp.getWidth(null), temp.getHeight(null),
        BufferedImage.TYPE_INT_RGB);
    // Copy image to buffered image.
    Graphics g = bufferedImage.createGraphics();
    // Clear background and paint the image.
    g.setColor(Color.white);
    g.fillRect(0, 0, temp.getWidth(null), temp.getHeight(null));
    g.drawImage(temp, 0, 0, null);
    g.dispose();
    // Soften.
    float softenFactor = 0.05f;
    float[] softenArray = { 0, softenFactor, 0, softenFactor, 1 - (softenFactor * 4), softenFactor, 0,
        softenFactor, 0 };
    Kernel kernel = new Kernel(3, 3, softenArray);
    ConvolveOp cOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
    bufferedImage = cOp.filter(bufferedImage, null);
    ImageIcon ii2 = new ImageIcon(bufferedImage);
    return ii2;
  }

  /**
   * adds a label and a component in a two column gridbaglayout to create a form
   * 
   * @param content
   * @param rowNr
   * @param label
   *          may be null
   * @param component
   *          may be null
   */
  public static void placeInGridbagForm(JPanel content, int rowNr, JLabel label, JComponent component) {
    if (!(content.getLayout() instanceof GridBagLayout)) {
      throw new IllegalArgumentException("Panel does not have GridbagLayout!!");
    }
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(2, 2, 2, 2);
    c.ipadx = 5; // increases component width by 10 pixels
    c.ipady = 5; // increases component height by 10 pixels
    c.anchor = GridBagConstraints.NORTHWEST;
    c.gridx = 0; // column 0
    c.gridy = rowNr; // row 1
    if (label != null) {
      content.add(label, c);
    }
    c.gridx = 1; // column 1
    c.gridy = rowNr; // row 1
    if (component != null) {
      content.add(component, c);
    }
  }

  /**
   * call this before any components are created!!!
   */
  public static void setWindowsLookAndFeel() {
    try {
      UIManager.setLookAndFeel(PLAF_WINDOWS);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static JFrame showPanelInFrame(JPanel panel) {
    JFrame frame = new JFrame();

    JFrame.setDefaultLookAndFeelDecorated(true);
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    frame.setLayout(new BorderLayout());
    frame.add(panel, BorderLayout.CENTER);
    frame.pack();
    frame.setVisible(true);

    return frame;
  }

 
  //	
  // /**
  // * @param titel
  // * @param message
  // * may have "\n" for nextlines
  // * @param messageType
  // * @param optionValues
  // * @param optionNames
  // * @param modal
  // * setting this to true will makes it put the dialog in foregound
  // * and not leave till button is pressed
  // * @return the selected optionValue or -1 if closed (no selection)
  // */
  // public static int createMultiButtonDialog(String titel, String message, int messageType,
  // Integer[] optionValues, String[] optionNames, Color bgColor, boolean modal) {
  // Debug.checkParamInList(messageType, new int[] { JOptionPane.PLAIN_MESSAGE,
  // JOptionPane.QUESTION_MESSAGE, JOptionPane.INFORMATION_MESSAGE,
  // JOptionPane.ERROR_MESSAGE, JOptionPane.WARNING_MESSAGE });
  // String wrappedMessage = StringUtil.join("\n", StringUtil.wordWrap(message,
  // UIConstants.WORDWRAP_LENGTH));
  //
  // Object[] options = ComboOption.getComboChoices(optionNames, optionValues).toArray(
  // new ComboOption[0]);
  // JOptionPane jop = new JOptionPane(wrappedMessage, messageType, JOptionPane.DEFAULT_OPTION,
  // null, options, options[0]);
  // JDialog dialog = jop.createDialog(null, titel);
  // dialog.setModal(modal);
  // setPanelBackground(dialog, bgColor);
  // dialog.pack();
  // dialog.setVisible(true);// blocking call: only if modal ==true!!
  // while (jop.getValue() instanceof String) {
  // SwingUtil.interruptSafeSleep(50);
  // }
  //
  // dialog.dispose();// throw away after returning
  // if (jop.getValue() == null)
  // return -1;
  // return ((Integer) ((ComboOption) jop.getValue()).value);
  // }
  //
  // public static void createTimedDialog(final int showTimeMs, String titel, String message,
  // int messageType, Color bgColor) {
  // String wrappedMessage = StringUtil.join("\n", StringUtil.wordWrap(message,
  // UIConstants.WORDWRAP_LENGTH));
  //
  // Object[] options = new Object[0];
  // JOptionPane jop = new JOptionPane(wrappedMessage, messageType, JOptionPane.DEFAULT_OPTION,
  // null, options, null);
  // final JDialog dialog = jop.createDialog(null, titel);
  // setPanelBackground(dialog, bgColor);
  // dialog.pack();
  // Thread closeThread = new Thread() {
  // public void run() {
  // SwingUtil.interruptSafeSleep(showTimeMs);
  //
  // dialog.dispose();
  // }
  // };
  // closeThread.start();
  // dialog.setVisible(true);// blocking call
  // }
  //
  // public static void setPanelBackground(JDialog dialog, Color color) {
  // recolor(dialog.getContentPane(), color);
  // }
  //
  /**
   * You could take the JDialog manipulated there and pass its contentPane to the following method...
   * 
   * By the way, I though of changing the UIDefault for "OptionPane.background", (see reply 2
   * [url=http://forum.java.sun.com/thread .jsp?forum=54&thread=461840&tstart=15&trange=15]here[/url]) but
   * that only changes the border area color of the dialog, not the panel on which the button and label
   * appear, etc...
   * 
   * @param component
   * @param color
   */
  public static void recolor(Component component, Color color) {
    component.setBackground(color);
    if (component instanceof Container) {
      Container container = (Container) component;
      for (int i = 0, ub = container.getComponentCount(); i < ub; ++i)
        recolor(container.getComponent(i), color);
    }
  }
}
