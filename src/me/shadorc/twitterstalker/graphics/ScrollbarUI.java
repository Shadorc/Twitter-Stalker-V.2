package me.shadorc.twitterstalker.graphics;

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
	private Image imageThumb, imageTrack;

	public ScrollbarUI() {
		imageThumb = new ImageIcon(this.getClass().getResource("/res/barre.png")).getImage();
		imageTrack = new ImageIcon(this.getClass().getResource("/res/scroll.png")).getImage();
	}

	//Bar painting
	@Override
	protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {  
		//7 : shift the bar to the right
		g.translate(thumbBounds.x + 7, thumbBounds.y);
		//-0.75 : reduce bar's width
		AffineTransform transform = AffineTransform.getScaleInstance((double) (thumbBounds.width/imageThumb.getWidth(null)) - 0.75 , (double) thumbBounds.height/imageThumb.getHeight(null));
		((Graphics2D)g).drawImage(imageThumb, transform, null);
		g.translate(-thumbBounds.x, -thumbBounds.y );
	}

	//Scroll background painting
	@Override
	protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {        
		g.translate(trackBounds.x, trackBounds.y);
		((Graphics2D) g).drawImage(imageTrack, AffineTransform.getScaleInstance(1, (double) trackBounds.height/imageTrack.getHeight(null)), null);
		g.translate(-trackBounds.x, -trackBounds.y);
	}

	/*Delete top and bottom buttons*/
	@Override
	protected JButton createDecreaseButton(int orientation) {
		return createZeroButton();
	}

	@Override    
	protected JButton createIncreaseButton(int orientation) {
		return createZeroButton();
	}

	private JButton createZeroButton() {
		JButton button = new JButton();
		button.setPreferredSize(new Dimension(0, 0));
		button.setMinimumSize(new Dimension(0, 0));
		button.setMaximumSize(new Dimension(0, 0));
		return button;
	}
}