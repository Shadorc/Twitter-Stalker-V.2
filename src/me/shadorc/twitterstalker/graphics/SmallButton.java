package me.shadorc.twitterstalker.graphics;

import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.Border;

public class SmallButton extends JButton {

	private static final long serialVersionUID = 1L;

	public SmallButton(String name, Border border) {
		super();

		final ImageIcon icon1 = new ImageIcon(new ImageIcon(this.getClass().getResource("/res/Bouton " + name + ".png")).getImage().getScaledInstance(57,57,Image.SCALE_SMOOTH));
		final ImageIcon icon2 = new ImageIcon(new ImageIcon(this.getClass().getResource("/res/Bouton " + name + "2.png")).getImage().getScaledInstance(57,57,Image.SCALE_SMOOTH));
		final ImageIcon icon3 = new ImageIcon(new ImageIcon(this.getClass().getResource("/res/Bouton " + name + "3.png")).getImage().getScaledInstance(57,57,Image.SCALE_SMOOTH));

		this.setIcon(icon1);
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				JButton bu = (JButton) e.getSource();
				bu.setIcon(icon3);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				JButton bu = (JButton) e.getSource();
				bu.setIcon(icon1);
			}
		});
		this.setPressedIcon(icon2);
		this.setBorder(border);
		this.setFocusable(false);
		this.setContentAreaFilled(false);
		this.setOpaque(false);
		this.setBackground(null);
	}
}