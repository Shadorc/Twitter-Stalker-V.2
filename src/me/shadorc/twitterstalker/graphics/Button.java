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

	private ButtonType buttonTy;
	private Size size;

	public enum Size {
		SMALL, MEDIUM, NORMAL;
	}

	public enum ButtonType {
		BACK, OPTIONS, UPLOAD, VALIDATE;
	}

	public Button(ButtonType buttonTy, int[] border, Size size, ActionListener listener) {
		super();

		this.buttonTy = buttonTy;
		this.size = size;

		ImageIcon defaultIcon = this.getIcon("default");
		ImageIcon pressedIcon = this.getIcon("pressed");
		ImageIcon mouseOverIcon = this.getIcon("mouseOver");

		this.setIcon(defaultIcon);
		this.setPressedIcon(pressedIcon);
		this.addActionListener(listener);
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				JButton bu = (JButton) e.getSource();
				bu.setIcon(mouseOverIcon);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				JButton bu = (JButton) e.getSource();
				bu.setIcon(defaultIcon);
			}
		});

		this.setBorder(BorderFactory.createEmptyBorder(border[0], border[1], border[2], border[3]));
		this.setFocusable(false);
		this.setContentAreaFilled(false);
		this.setOpaque(false);
		this.setBackground(null);
	}

	private ImageIcon getIcon(String state) {
		ImageIcon icon = new ImageIcon(this.getClass().getResource("/res/button_" + buttonTy.toString().toLowerCase() + "_" + state + ".png"));
		if(size == Size.MEDIUM) {
			return new ImageIcon(icon.getImage().getScaledInstance(57,57,Image.SCALE_SMOOTH));
		} else if(size == Size.SMALL) {
			return new ImageIcon(icon.getImage().getScaledInstance(25,25,Image.SCALE_SMOOTH));
		}
		return icon;
	}
}