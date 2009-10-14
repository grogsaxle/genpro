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

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

public class GridMainFrame extends JFrame {
  public GridMainFrame(GridPanel panel) {
    this.addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        dispose();
        System.exit(0);
      }
    });
    this.getContentPane().add(panel, BorderLayout.CENTER);
  }

  public static void main(String[] args) {
    GridMainFrame f = new GridMainFrame(null);
    f.setSize(1000, 800);
    f.validate();
    f.setVisible(true);
  }
}
