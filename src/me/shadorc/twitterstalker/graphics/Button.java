package me.shadorc.twitterstalker.graphics;

import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Button extends JButton {

	private static final long serialVersionUID = 1L;

	public Button(String name, int[] border, boolean small, ActionListener al) {
		super();

		final ImageIcon icon1 = this.getIcon(name + "1", small);
		final ImageIcon icon2 = this.getIcon(name+ "2", small);
		final ImageIcon icon3 = this.getIcon(name + "3", small);

		this.setIcon(icon1);
		this.addActionListener(al);
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
		this.setBorder(BorderFactory.createEmptyBorder(border[0], border[1], border[2], border[3]));
		this.setFocusable(false);
		this.setContentAreaFilled(false);
		this.setOpaque(false);
		this.setBackground(null);
	}

	private ImageIcon getIcon(String name, boolean small) {
		ImageIcon icon = new ImageIcon(this.getClass().getResource("/res/Bouton " + name + ".png"));
		if(small) {
			return new ImageIcon(icon.getImage().getScaledInstance(57,57,Image.SCALE_SMOOTH));
		}
		return icon;
	}
}