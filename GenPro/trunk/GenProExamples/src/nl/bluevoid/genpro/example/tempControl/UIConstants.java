package nl.bluevoid.genpro.example.tempControl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.plaf.ColorUIResource;



public class UIConstants {

	private static final int LABEL_FONT_SIZE = 12;

	public static final String LABEL_FONT_NAME = "SansSerif";

	public static final Font FONT_HTMLBUTTON_SELECTED = new Font(LABEL_FONT_NAME, Font.BOLD,
			LABEL_FONT_SIZE + 2);

	public static final Font FONT_HTMLBUTTON_UNSELECTED = new Font("SansSerif", Font.PLAIN,
			LABEL_FONT_SIZE);

	public static final Font FONT_MAIN_TITLE = new Font(LABEL_FONT_NAME, Font.ITALIC | Font.BOLD,
			LABEL_FONT_SIZE + 5);

	public static final Font Font_TAB_DESCRIPTION = new Font(LABEL_FONT_NAME, Font.BOLD,
			LABEL_FONT_SIZE + 5);

	public static final Font FONT_INSTRUCTION_TEXT = new Font(LABEL_FONT_NAME, Font.BOLD,
			LABEL_FONT_SIZE + 3);

	public static final Font FONT_INSTRUCTION_TEXT_SMALL = new Font(LABEL_FONT_NAME, Font.PLAIN,
			LABEL_FONT_SIZE + 1);

	public static final Font FONT_CONTENT_DESCRIPTION = new Font(LABEL_FONT_NAME, Font.BOLD,
			LABEL_FONT_SIZE + 0);

	public static final Font FONT_HELP_TEXT = new Font("SansSerif", Font.PLAIN, LABEL_FONT_SIZE - 1);

	public static final Color COLOR_ACCENT = new Color(17, 121, 147);// 17,g=121,b=147

	public static final Color COLOR_TEXT_HTMLBUTTONS = Color.WHITE;

	public static final Border BORDER_ERROR = new LineBorder(Color.RED, 1);

	public static final Color COLOR_BACKGROUND_PANEL = Color.WHITE;// new

	public static final Color COLOR_BACKGROUND_BORDER = new Color(150, 205,
			218);// 204, 240, 249) 150,205,218

	public static final Color COLOR_TAB_FOREGROUND = COLOR_ACCENT;

	public static final Color COLOR_TAB_BACKGROUND = Color.LIGHT_GRAY;

	public static final Color COLOR_SELECTED = new Color(255, 175, 0);//Color.YELLOW);



	// public static Color KLEUR_REEKS[];
	// static // maakt reeks van aantal kleuren in zelfde tint op basis van
	// kleur
	// // k
	// {
	// Color k = COLOR_ACCENT;
	// int aantal = 11;
	// Color reeks[] = new Color[aantal];
	// float f[] = new float[3];
	// f = Color.RGBtoHSB(k.getRed(), k.getGreen(), k.getBlue(), f);
	// float h = f[0];
	// float offsetb = 0.75f, stepb = ((1 - offsetb) / aantal); // brightness
	// // van
	// // offset
	// // tot 1
	// float offset = 0.01f; // saturation van offset tot 1
	// float step = (1 - offset) / aantal;
	// for (int i = 0; i < reeks.length; i++) {
	// reeks[i] = Color.getHSBColor(h, (offset + step * i), 1 - stepb * i);
	// }
	// KLEUR_REEKS = reeks;
	// }

	public static final String PLAF_WINDOWS = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

	// public static final String PLAF_MOTIF
	// ="com.sun.java.swing.plaf.motif.MotifLookAndFeel";

	public static final String PLAF_JAVA = UIManager.getCrossPlatformLookAndFeelClassName();

	public static final int WORDWRAP_LENGTH = 70;

//	public static final Integer OPTION_OK = 4;
//
//	public static final Integer OPTION_CANCEL = 8;
//	
//	public static final Integer OPTION_YES = 16;
//	
//	public static final Integer OPTION_NO = 32;

	public static final int TIME_MS_DIALOG_INFO = 2000;

	

	public static void main(String[] str) {
//		LookAndFeelInfo[] lafi = UIManager.getInstalledLookAndFeels();
//		for (LookAndFeelInfo info : lafi) {
//			System.out.println(info.getName() + ":" + info.getClassName());
//		}
//		System.out.println(UIManager.getSystemLookAndFeelClassName());
	}

	public static void setLookandFeel() throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(PLAF_WINDOWS);
		// UIManager.setLookAndFeel(PLAF_JAVA);

		// com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel());

		// UIManager.setLookAndFeel(new
		// com.sun.java.swing.plaf.motif.MotifLookAndFeel());

		// UIManager.setLookAndFeel(new
		// javax.swing.plaf.metal.MetalLookAndFeel());

		// UIManager.setLookAndFeel(new
		// it.unitn.ing.swing.plaf.macos.MacOSLookAndFeel());

		// UIManager.setLookAndFeel(new
		// com.sun.java.swing.plaf.gtk.GTKLooktAndFeel());

		// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	//	Debug.println("Look and feel installed:" + UIManager.getLookAndFeel().getName());
		UIManager.put("TabbedPane.selected", UIConstants.COLOR_ACCENT);
		UIManager.put("OptionPane.background", COLOR_BACKGROUND_PANEL);
		// UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 2, 2,
		// 2));
		// UIManager.put("TabbedPane.tabAreaBackground",
		// UIConstants.COLOR_ACCENT);
		// UIManager.put("TabbedPane.shadow", new
		// ColorUIResource(UIConstants.COLOR_ACCENT));
		UIManager.put("TabbedPane.contentAreaColor", new ColorUIResource(UIConstants.COLOR_ACCENT));
		// UIManager.put("ToolTip.foreground", new ColorUIResource(Color.red));
		// UIManager.put("ToolTip.background", new
		// ColorUIResource(Color.yellow));
		// UIManager.put("TabbedPane.background", UIConstants.COLOR_ACCENT);
		// System.out.println("TabbedPane.selected
		// "+UIManager.getColor("TabbedPane.selected").toString());
	}

//	public static ImageIcon sizeIconForFloorplan(ImageIcon icon) {
//		return FileUtil.fitBox(icon, FLOORPLAN_ICON_SIZE, FLOORPLAN_ICON_SIZE);
//	}

	/**
	 * This method creates a compact button, put here for uniformity
	 * 
	 * @param wijzig
	 */
	public static void compactButton(JButton button) {
		if(button.getText().equals("")){
			//we have an icon button
			button.setMargin(new Insets(4, 8, 4, 8));
		}
		else
		button.setMargin(new Insets(1, 8, 1, 8));

	}
}