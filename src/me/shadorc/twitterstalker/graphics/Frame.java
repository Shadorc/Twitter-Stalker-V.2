package me.shadorc.twitterstalker.graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import me.shadorc.infonet.Infonet;
import me.shadorc.twitterstalker.graphics.TextField.Text;
import me.shadorc.twitterstalker.graphics.panel.ConnectionPanel;
import me.shadorc.twitterstalker.graphics.panel.MenuPanel;
import me.shadorc.twitterstalker.graphics.panel.OptionsPanel;
import me.shadorc.twitterstalker.initialization.Shortcut;
import me.shadorc.twitterstalker.initialization.UpdateChecker;
import me.shadorc.twitterstalker.storage.Data.Connection;
import me.shadorc.twitterstalker.storage.Storage;

import org.apache.commons.io.FileUtils;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class Frame extends JFrame {

	private static final long serialVersionUID = 1L;

	private static Frame frame;
	private static JPanel currentPanel;

	private static Twitter twitter;
	private static RequestToken requestToken;
	private static AccessToken accessToken;

	private static int[] code = {KeyEvent.VK_UP, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_B, KeyEvent.VK_A};
	private static int keyTyped = 0;

	public static void main(String[] args) {

		//If this is a recent update, delete the old folder
		if(args.length > 0) {
			try {
				FileUtils.deleteDirectory(new File(args[0]));
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, Storage.tra("updataDeletingError") + " : " + e, Storage.tra("error"), JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}		
		}

		//Load options
		new OptionsPanel();

		Shortcut.create();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame = new Frame();
			}
		});

		try {
			Frame.checkID();
		} catch (TwitterException e) {
			JPanel error = new JPanel(new BorderLayout());
			error.setBackground(new Color(2, 136, 209));

			JLabel icon = new JLabel(Ressources.getBigIcon());
			icon.setBorder(BorderFactory.createEmptyBorder(100, 0, 0, 0));
			error.add(icon, BorderLayout.PAGE_START);

			JLabel info = new JLabel(Storage.tra("internetError"), JLabel.CENTER);
			info.setBorder(BorderFactory.createEmptyBorder(0, 0, 250, 0));
			info.setFont(Ressources.getFont("RobotoCondensed-Regular.ttf", 50));
			info.setForeground(Color.WHITE);
			error.add(info, BorderLayout.CENTER);

			currentPanel = error;
		}

		frame.setVisible(true);

		UpdateChecker.check();
	}

	Frame() {
		super(Ressources.getName() + " " + Ressources.getVersion());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new MyDispatcher());

		this.setIconImage(Ressources.getSmallIcon().getImage());
		this.setContentPane(new JPanel());
		this.pack();
		this.setMinimumSize(new Dimension(1024, 768));
		this.setPreferredSize(new Dimension(1280, 720));
		this.setSize(1280, 720);
		this.setLocationRelativeTo(null);
	}

	//Easter Egg : Konami Kode YEAH
	private class MyDispatcher implements KeyEventDispatcher {
		@Override
		public boolean dispatchKeyEvent(KeyEvent e) {
			if(e.getID() == KeyEvent.KEY_PRESSED) {
				if(e.getKeyCode() == code[keyTyped]) {
					keyTyped++;
					if(keyTyped == code.length) {
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

	private static void checkID() throws TwitterException {
		twitter = TwitterFactory.getSingleton();
		twitter.setOAuthConsumer("", "");

		requestToken = twitter.getOAuthRequestToken();
		if(Storage.getData(Connection.TOKEN) == null) {
			Frame.setPanel(new ConnectionPanel(Storage.tra(Text.PIN)));
			new Thread() {
				@Override
				public void run() {
					if(!Infonet.open(requestToken.getAuthorizationURL(), true)) {
						JOptionPane.showMessageDialog(null, Storage.tra("urlError"), Storage.tra("error"), JOptionPane.ERROR_MESSAGE);
					}
				}
			}.start();

		} else {
			accessToken = new AccessToken(Storage.getData(Connection.TOKEN), Storage.getData(Connection.TOKEN_SECRET));
			Frame.connect(null);
		}
	}

	public static void connect(String pin) {
		try {
			//True when pin has never been configured
			if(accessToken == null && pin != null) {
				accessToken = twitter.getOAuthAccessToken(requestToken, pin);
				Storage.saveData(Connection.TOKEN_SECRET, accessToken.getTokenSecret());
				Storage.saveData(Connection.TOKEN, accessToken.getToken());
			}
			twitter.setOAuthAccessToken(accessToken);

			Frame.setPanel(new MenuPanel());
		} catch (TwitterException e) {
			((ConnectionPanel) frame.getContentPane()).invalidPin();
		}
	}

	public static void upload(String message) {
		new Share(message, frame.getContentPane());
	}

	public static Twitter getTwitter() {
		return twitter;
	}

	public static void setPanel(JPanel panel) {
		currentPanel = panel;
		frame.remove(frame.getContentPane());
		frame.setContentPane(panel);
		frame.getContentPane().revalidate(); 
		frame.getContentPane().repaint();
	}

	public static void reset() {
		frame.setContentPane(currentPanel);
		frame.revalidate();
	}
}