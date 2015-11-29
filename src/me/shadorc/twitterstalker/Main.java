package me.shadorc.twitterstalker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.io.FileUtils;

import me.shadorc.infonet.Infonet;
import me.shadorc.twitterstalker.graphics.SearchField.Text;
import me.shadorc.twitterstalker.graphics.panel.ConnectionPanel;
import me.shadorc.twitterstalker.graphics.panel.MenuPanel;
import me.shadorc.twitterstalker.graphics.panel.OptionsPanel;
import me.shadorc.twitterstalker.storage.Data.Connection;
import me.shadorc.twitterstalker.storage.Storage;
import me.shadorc.twitterstalker.utility.Ressources;
import me.shadorc.twitterstalker.utility.Shortcut;
import me.shadorc.twitterstalker.utility.UpdateUtility;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class Main {

	private static Twitter twitter;
	private static RequestToken requestToken;
	private static AccessToken accessToken;

	public static void main(String[] args) {

		//If this is a recent update, delete the old folder
		if(args.length > 0) {
			try {
				FileUtils.deleteDirectory(new File(args[0]));
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, Storage.tra("updataDeletingError") + " : " + e.getMessage(), Storage.tra("error"), JOptionPane.ERROR_MESSAGE);
			}		
		}

		OptionsPanel.init();
		Shortcut.create();
		UpdateUtility.check();

		try {
			Main.checkID();
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

			Ressources.getFrame().setPanel(error);
		}

		Ressources.getFrame().setVisible(true);
	}

	private static void checkID() throws TwitterException {
		twitter = TwitterFactory.getSingleton();
		twitter.setOAuthConsumer("", "");

		requestToken = twitter.getOAuthRequestToken();
		if(Storage.getData(Connection.TOKEN) == null) {
			Ressources.getFrame().setPanel(new ConnectionPanel(Text.PIN));
			//Open web browser to access PIN code
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
			Main.connect();
		}
	}

	public static void connect(String... pin) {
		try {
			//True when pin has never been configured
			if(accessToken == null && pin.length != 0) {
				accessToken = twitter.getOAuthAccessToken(requestToken, pin[0]);
				Storage.saveData(Connection.TOKEN_SECRET, accessToken.getTokenSecret());
				Storage.saveData(Connection.TOKEN, accessToken.getToken());
			}
			twitter.setOAuthAccessToken(accessToken);

			Ressources.getFrame().setPanel(new MenuPanel());
		} catch (TwitterException e) {
			((ConnectionPanel) Ressources.getFrame().getContentPane()).invalidPin();
		}
	}

	public static Twitter getTwitter() {
		return twitter;
	}
}
