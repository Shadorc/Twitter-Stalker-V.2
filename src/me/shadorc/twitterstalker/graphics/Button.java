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

	private ButtonType type;
	private Size size;

	public enum ButtonType {
		BACK, OPTIONS, UPLOAD, VALIDATE;
	}

	public enum Size {
		SMALL, MEDIUM, NORMAL;
	}

	public Button(ButtonType buttonTy, Size size, int[] border,  ActionListener listener) {
		super();

		this.type = buttonTy;
		this.size = size;

		ImageIcon defaultIcon = this.getIcon("default");
		ImageIcon pressedIcon = this.getIcon("pressed");
		ImageIcon mouseOverIcon = this.getIcon("mouseOver");

		this.setIcon(defaultIcon);
		this.setPressedIcon(pressedIcon);
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

		this.addActionListener(listener);
		this.setBorder(BorderFactory.createEmptyBorder(border[0], border[1], border[2], border[3]));
		this.setFocusable(false);
		this.setContentAreaFilled(false);
		this.setOpaque(false);
		this.setBackground(null);
	}

	private ImageIcon getIcon(String state) {
		ImageIcon icon = new ImageIcon(this.getClass().getResource("/res/Buttons/" + type.toString() + "_" + state + ".png"));
		if(size == Size.MEDIUM) {
			return new ImageIcon(icon.getImage().getScaledInstance(57,57,Image.SCALE_SMOOTH));
		} else if(size == Size.SMALL) {
			return new ImageIcon(icon.getImage().getScaledInstance(25,25,Image.SCALE_SMOOTH));
		}
		return icon;
	}
}