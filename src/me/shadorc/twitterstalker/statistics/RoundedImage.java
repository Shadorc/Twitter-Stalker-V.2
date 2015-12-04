package me.shadorc.twitterstalker.statistics;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class RoundedImage {

	public enum Scaling {
		NORMAL(140),
		THUMB(64);

		public final int size;
		Scaling(int size) {
			this.size = size;
		}
	}

	public static ImageIcon create(String imageUrl, Scaling scale) {
		Image image = null;
		try {
			/*Get Image from URL*/
			image = ImageIO.read(new URL(imageUrl));

			/*Resize Image*/
			ImageIcon icon =  new ImageIcon(image.getScaledInstance(scale.size, scale.size, Image.SCALE_SMOOTH));

			/* Make Rounded Icon*/
			int cornerRadius = icon.getIconHeight();
			int w = icon.getIconWidth();
			int h = icon.getIconHeight();

			BufferedImage rounded = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

			Graphics2D g2 = rounded.createGraphics();
			g2.setComposite(AlphaComposite.Src);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));
			g2.setComposite(AlphaComposite.SrcAtop);
			g2.drawImage(icon.getImage(), 0, 0, null);
			g2.dispose();

			image = rounded;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return new ImageIcon(image);
	}
}
