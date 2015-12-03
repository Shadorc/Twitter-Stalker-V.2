package me.shadorc.twitterstalker.graphics;

import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import me.shadorc.infonet.Infonet;
import me.shadorc.twitterstalker.utility.Ressources;

public class Frame extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel currentPanel;

	private int[] konamiCode = {KeyEvent.VK_UP, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_B, KeyEvent.VK_A};
	private int keyTyped = 0;

	public Frame() {
		super(Ressources.name + " " + Ressources.version);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new MyDispatcher());

		this.setIconImage(Ressources.smallIcon.getImage());
		this.setContentPane(new JPanel());
		this.pack();
		this.setMinimumSize(new Dimension(1024, 768));
		this.setPreferredSize(new Dimension(1280, 720));
		this.setSize(1280, 720);
		this.setLocationRelativeTo(null);
	}

	//Easter Egg : Konami Code YEAH
	private class MyDispatcher implements KeyEventDispatcher {
		@Override
		public boolean dispatchKeyEvent(KeyEvent e) {
			if(e.getID() == KeyEvent.KEY_PRESSED) {
				if(e.getKeyCode() == konamiCode[keyTyped]) {
					keyTyped++;
					if(keyTyped == konamiCode.length) {
						if(!Infonet.open("https://www.youtube.com/watch?v=V6rJo6tsYvU", false)) {
							JOptionPane.showMessageDialog(null, "That's the Konami Code !", "Konami Code !", JOptionPane.PLAIN_MESSAGE);
						}

						keyTyped = 0;
					}
				} else {
					keyTyped = 0;
				}
			}
			return false;
		}
	}

	public void setPanel(JPanel newPanel) {
		this.currentPanel = newPanel;
		this.remove(this.getContentPane());
		this.setContentPane(newPanel);
		this.getContentPane().revalidate(); 
		this.getContentPane().repaint();
	}

	public void reset() {
		this.setContentPane(currentPanel);
		this.revalidate();
	}
}