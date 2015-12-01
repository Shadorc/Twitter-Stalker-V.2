package me.shadorc.twitterstalker.utility;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.swing.ImageIcon;

import me.shadorc.twitterstalker.graphics.Frame;
import twitter4j.Status;

public class Ressources {

	private static String name = "Twitter Stalker";

	private static boolean forceUpdate = false;
	private static boolean isBeta = true;
	private static Version version = new Version("2.1.3", isBeta);

	private static ImageIcon bigIcon = new ImageIcon(Ressources.class.getResource("/res/TS_icon.png"));
	private static ImageIcon smallIcon = new ImageIcon(bigIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));

	private static DecimalFormat df = new DecimalFormat("#.#");

	private static Frame frame = new Frame();

	//'Back' button has been pressed
	public static boolean stop = false;

	public static Font getFont(String name, int size) {
		try {
			return Font.createFont(Font.TRUETYPE_FONT, Ressources.class.getResourceAsStream("/res/Font/" + name)).deriveFont(Font.PLAIN, size);
		} catch (FontFormatException | IOException e) {
			return new Font("Consolas", Font.PLAIN, size);
		}
	}

	public static String getStatusURL(Status status) {
		return "http://twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId();
	}

	public static String removeHTML(String text) {
		return text.replaceAll("<[^>]*>", "");
	}

	public static String getName() {
		return name;
	}

	public static boolean forceUpdate() {
		return forceUpdate;
	}

	public static Version getVersion() {
		return version;
	}

	public static ImageIcon getBigIcon() {
		return bigIcon;
	}

	public static ImageIcon getSmallIcon() {
		return smallIcon;
	}

	public static String format(double value) {
		return df.format(value);
	}

	public static Frame getFrame() {
		return frame;
	}
}
