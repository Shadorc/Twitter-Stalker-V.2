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
	
	private String name;
	private Size size;

	public enum Size {
		SMALL, MEDIUM, NORMAL;
	}

	public Button(String name, int[] border, Size size, ActionListener al) {
		super();
		
		this.name = name;
		this.size = size;

		final ImageIcon icon1 = this.getIcon("1");
		final ImageIcon icon2 = this.getIcon("2");
		final ImageIcon icon3 = this.getIcon("3");

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

	private ImageIcon getIcon(String number) {
		ImageIcon icon = new ImageIcon(this.getClass().getResource("/res/Bouton " + name + number + ".png"));
		if(size == Size.MEDIUM) {
			return new ImageIcon(icon.getImage().getScaledInstance(57,57,Image.SCALE_SMOOTH));
		} else if(size == Size.SMALL) {
			return new ImageIcon(icon.getImage().getScaledInstance(25,25,Image.SCALE_SMOOTH));
		}
		return icon;
	}
}