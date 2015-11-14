package me.shadorc.twitterstalker.graphics;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.io.IOException;

import javax.swing.ImageIcon;

import me.shadorc.twitterstalker.initialization.Version;

public class Ressources {

	private static ImageIcon bigIcon = new ImageIcon(Ressources.class.getResource("/res/TS_icon.png"));
	private static ImageIcon smallIcon = new ImageIcon(bigIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));

	private static String name = "Twitter Stalker";

	private static boolean forceUpdate = false;
	private static boolean isBeta = true;
	private static Version version = new Version("2.1.3", isBeta);

	public static Font getFont(String name, int size) {
		try {
			return Font.createFont(Font.TRUETYPE_FONT, Frame.class.getResourceAsStream("/res/Font/" + name)).deriveFont(Font.PLAIN, size);
		} catch (FontFormatException | IOException e) {
			return new Font("Consolas", Font.PLAIN, size);
		}
	}

	public static ImageIcon getBigIcon() {
		return bigIcon;
	}

	public static ImageIcon getSmallIcon() {
		return smallIcon;
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
}
