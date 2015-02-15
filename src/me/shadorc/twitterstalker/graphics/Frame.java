package me.shadorc.twitterstalker.graphics;

import java.awt.*;
import java.awt.Desktop.Action;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import me.shadorc.twitterstalker.graphics.Storage.Data;
import me.shadorc.twitterstalker.graphics.panel.ConnectionPanel;
import me.shadorc.twitterstalker.graphics.panel.ConnectionPanel.Text;
import me.shadorc.twitterstalker.graphics.panel.MenuPanel;
import me.shadorc.twitterstalker.graphics.panel.OptionsPanel;
import net.jimmc.jshortcut.JShellLink;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class Frame extends JFrame {

	private static final long serialVersionUID = 1L;

	private static Frame frame;
	private static JPanel _panel;

	private static Twitter twitter;
	private static RequestToken requestToken;
	private static AccessToken accessToken;

	private static int[] code = {KeyEvent.VK_UP, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_B};
	private static int keyTyped = 0;

	public static void main(String[] args) {
		try {
			if((Storage.getData(Data.INSTALL) == "false" || Storage.getData(Data.INSTALL) == null) && System.getProperty("os.name").startsWith("Windows")) {
				int reply = JOptionPane.showOptionDialog(null,
						"Bonjour et merci d'avoir téléchargé Twitter Stalker !"
								+ "\nPlacez le dossier téléchargé où vous voulez puis relancer le pour créer un raccourci sur le bureau (optionnel)."
								+ "\nVoulez-vous créer un raccourci sur le bureau maintenant ?",
								"Installation",
								JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.PLAIN_MESSAGE,
								new ImageIcon(Frame.class.getResource("/res/IconeAppli.png")),
								new String[] {"Oui", "Non", "Jamais"},
						"default");

				if (reply == JOptionPane.YES_OPTION) {
					JShellLink link = new JShellLink();
					link.setFolder(JShellLink.getDirectory("desktop"));
					link.setName("Twitter Stalker 2.0");
					link.setIconLocation(new File(Frame.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent() + "\\IconeAppli.ico");
					link.setPath(Frame.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().replaceFirst("/", ""));
					link.save();

					Storage.saveData(Data.INSTALL, "true");

				} else if (reply == JOptionPane.CANCEL_OPTION) {
					Storage.saveData(Data.INSTALL, "true");

				} else {
					System.exit(0);
				}
			}
		} catch(URISyntaxException e) {
			e.printStackTrace();
		}

		//Load options
		new OptionsPanel();
		frame = new Frame();
	}

	//Easter Egg : Konami Kode YEAH
	private class MyDispatcher implements KeyEventDispatcher {
		@Override
		public boolean dispatchKeyEvent(KeyEvent e) {
			if (e.getID() == KeyEvent.KEY_PRESSED) {
				if(e.getKeyCode() == code[keyTyped]) {
					keyTyped++;
					if(keyTyped == code.length) {
						try {
							Desktop.getDesktop().browse(new URI("https://www.youtube.com/watch?v=V6rJo6tsYvU"));
						} catch (IOException | URISyntaxException e1) {
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

	Frame() {
		super("Twitter Stalker 2.0");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new MyDispatcher());

		try {
			this.connexion();
		} catch (TwitterException e) {
			JPanel error = new JPanel(new BorderLayout());
			error.setBackground(new Color(2, 136, 209));

			JLabel icon = new JLabel(new ImageIcon(this.getClass().getResource("/res/IconeAppli.png")));
			icon.setBorder(BorderFactory.createEmptyBorder(100, 0, 0, 0));
			error.add(icon, BorderLayout.PAGE_START);

			JLabel info = new JLabel("Erreur. Vérifiez votre connexion Internet.", JLabel.CENTER);
			info.setBorder(BorderFactory.createEmptyBorder(0, 0, 250, 0));
			info.setFont(Frame.getFont("RobotoCondensed-Regular.ttf", 50));
			info.setForeground(Color.WHITE);
			error.add(info, BorderLayout.CENTER);

			this.setContentPane(error);
		}

		this.setIconImage(new ImageIcon(this.getClass().getResource("/res/IconeAppli.png")).getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));
		this.pack();
		this.setMinimumSize(new Dimension(1280, 720));
		this.setPreferredSize(new Dimension(1280, 720));
		this.setSize(1280, 720);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	private void connexion() throws TwitterException {
		twitter = TwitterFactory.getSingleton();
		twitter.setOAuthConsumer("48eR2GMLRHgjL1awz5E5BjflE", "KWAP5Fz4rZleBa2NkMWsYjTbf83D2tPLZHEQZT3mRu79RjVyZu");

		requestToken = twitter.getOAuthRequestToken();

		if(Storage.getData(Data.TOKEN) == null) {
			this.setContentPane(new ConnectionPanel(Text.PIN));
			this.openUrl();
		} else {
			accessToken = new AccessToken(Storage.getData(Data.TOKEN), Storage.getData(Data.TOKEN_SECRET));
			Frame.connect();
		}
	}

	private void openUrl() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String url = requestToken.getAuthorizationURL();

				if(!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Action.BROWSE)) {
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(url), null);
					JOptionPane.showMessageDialog(null, "Erreur lors de l'ouverture, l'URL a été copiée dans le presse-papier.", "Erreur", JOptionPane.ERROR_MESSAGE);
				} else {
					try {
						Desktop.getDesktop().browse(new URI(url));
					} catch (IOException | URISyntaxException e) {
						Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(url), null);
						JOptionPane.showMessageDialog(null, "Erreur lors de l'ouverture, l'URL a été copiée dans le presse-papier.", "Erreur", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}).start();
	}

	//We don't always need PIN so, String... allows to don't create 2 methods one with PIN and the other one without.
	public static void connect(String... pin) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if(accessToken == null) {
						accessToken = twitter.getOAuthAccessToken(requestToken, pin[0]);
						Storage.saveData(Data.TOKEN_SECRET, accessToken.getTokenSecret());
						Storage.saveData(Data.TOKEN, accessToken.getToken());
					}
					twitter.setOAuthAccessToken(accessToken);
					Frame.setJPanel(new MenuPanel());
				} catch (TwitterException e) {
					((ConnectionPanel) frame.getContentPane()).setError(Text.INVALID_PIN);
				}
			}
		}).start();
	}

	public static void upload(String message) {
		new Upload(message, frame.getContentPane());
	}

	public static Twitter getTwitter() {
		return twitter;
	}

	public static Font getFont(String name, float size) {
		Font font;
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, Frame.class.getResourceAsStream("/res/" + name)).deriveFont(Font.PLAIN, size);
		} catch (FontFormatException | IOException e) {
			font = new Font("Consolas", Font.PLAIN, (int) size);
		}
		return font;
	}

	public static void setJPanel(JPanel panel) {
		_panel = panel;
		frame.remove(frame.getContentPane());
		frame.setContentPane(panel);
		frame.getContentPane().revalidate(); 
		frame.getContentPane().repaint();
	}

	public static void reset() {
		frame.setContentPane(_panel);
		frame.revalidate();
	}
}
