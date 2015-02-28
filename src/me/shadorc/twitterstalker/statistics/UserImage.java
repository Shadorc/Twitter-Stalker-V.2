package me.shadorc.twitterstalker.statistics;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class UserImage extends JLabel {

	private static final long serialVersionUID = 1L;

	public UserImage(Image image_u) {
		ImageIcon icon =  new ImageIcon(image_u.getScaledInstance(140, 140, Image.SCALE_SMOOTH));

		/* Make Rounded Icon*/
		int cornerRadius = icon.getIconHeight();
		int w = icon.getIconWidth();
		int h = icon.getIconHeight();

		BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2 = output.createGraphics();
		g2.setComposite(AlphaComposite.Src);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));
		g2.setComposite(AlphaComposite.SrcAtop);
		g2.drawImage(icon.getImage(), 0, 0, null);
		g2.dispose();

		this.setIcon(new ImageIcon(output));
		this.setHorizontalAlignment(SwingConstants.RIGHT);
	}
}
