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

	public static String name = "Twitter Stalker";

	public static final boolean forceUpdate = false;
	public static final boolean isBeta = true;
	public static final Version version = new Version("2.1.3", isBeta);

	public static boolean stop = false;
	public static boolean showLogs = false;

	public static final Frame frame = new Frame();

	public static final ImageIcon bigIcon = new ImageIcon(Ressources.class.getResource("/res/TS_icon.png"));
	public static final ImageIcon smallIcon = new ImageIcon(bigIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));

	private static final DecimalFormat df = new DecimalFormat("#.#");

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

	public static String format(double value) {
		return df.format(value);
	}
}
