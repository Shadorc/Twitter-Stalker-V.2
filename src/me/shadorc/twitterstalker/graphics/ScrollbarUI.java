package me.shadorc.twitterstalker.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.metal.MetalScrollBarUI;

public class ScrollbarUI extends MetalScrollBarUI {

	private Image imageThumb;
	private Position pos;

	public enum Position {
		VERTICAL, HORIZONTAL;
	}

	public ScrollbarUI(Position pos) {
		this.pos = pos;
		this.imageThumb = new ImageIcon(this.getClass().getResource("/res/barre" + (pos == Position.VERTICAL ? "V" : "H") + ".png")).getImage();
	}

	//Bar painting
	@Override
	protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
		//shift the bar to the right/top
		int shiftRight = (pos == Position.VERTICAL ? 7 : 0);
		int shiftTop = (pos == Position.HORIZONTAL ? 3 : 0);
		//reduce bar's width/height
		double widthReduce = (pos == Position.VERTICAL ? 1.5 : 0);
		double heightReduce = (pos == Position.HORIZONTAL ? 1.5 : 0);

		g.translate(thumbBounds.x + shiftRight, thumbBounds.y + shiftTop);
		AffineTransform transform = AffineTransform.getScaleInstance((double) thumbBounds.width/imageThumb.getWidth(null) - widthReduce, (double) thumbBounds.height/imageThumb.getHeight(null) - heightReduce);
		((Graphics2D) g).drawImage(imageThumb, transform, null);
		g.translate(-thumbBounds.x, -thumbBounds.y);
	}

	//Scroll background painting (same color as the background)
	@Override
	protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
		c.setBackground(new Color(179, 229, 252));
	}

	/*Delete top and bottom buttons*/
	@Override
	protected JButton createDecreaseButton(int orientation) {
		return createInvisibleButton();
	}

	@Override    
	protected JButton createIncreaseButton(int orientation) {
		return createInvisibleButton();
	}

	private JButton createInvisibleButton() {
		JButton button = new JButton();
		button.setPreferredSize(new Dimension(0, 0));
		button.setMinimumSize(new Dimension(0, 0));
		button.setMaximumSize(new Dimension(0, 0));
		return button;
	}
}