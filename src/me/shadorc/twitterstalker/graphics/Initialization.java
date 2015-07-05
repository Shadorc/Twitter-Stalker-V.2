package me.shadorc.twitterstalker.graphics;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import me.shadorc.infonet.Infonet;
import me.shadorc.twitterstalker.graphics.Storage.Data;
import net.jimmc.jshortcut.JShellLink;

public class Initialization {

	public static void createShortcut() {
		//If this wasn't done before and if he's using Windows, ask the user to create a shortcut on desktop
		try {
			if((Storage.getData(Data.INSTALLED) == null) && System.getProperty("os.name").startsWith("Windows")) {
				int reply = JOptionPane.showOptionDialog(null,
						Storage.tra("createShortcut"),
								Storage.tra("installation"),
								JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.PLAIN_MESSAGE,
								new ImageIcon(Frame.class.getResource("/res/IconeAppli.png")),
								new String[] {Storage.tra("yes"), Storage.tra("notNow"), Storage.tra("never")},
						"default");

				//"Yes"
				if(reply == JOptionPane.YES_OPTION) {
					JShellLink link = new JShellLink();
					link.setFolder(JShellLink.getDirectory("desktop"));
					link.setName("Twitter Stalker");
					link.setIconLocation(new File(Frame.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent() + "\\IconeAppli.ico");
					link.setPath(Frame.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().replaceFirst("/", ""));
					link.save();

					Storage.saveData(Data.INSTALLED, "true");
				}
				//"Never"
				else if (reply == JOptionPane.CANCEL_OPTION) {
					Storage.saveData(Data.INSTALLED, "true");
				}
			}
		} catch(URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public static void checkForUpdate(String version) {
		//Check the last version and prevent user if an update is available
		try {
			String lastVersion = Infonet.parseHTML(new URL("http://shadorc.webnode.fr/twitter-stalker/"), "Version actuelle ", "(", ")");

			if(!version.equals(lastVersion) && Storage.getData(Data.UPDATE) == null) {

				int reply = JOptionPane.showOptionDialog(null,
						Storage.tra("updateAvailable") + " (" + lastVersion + ") !",
						Storage.tra("update"),
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE,
						new ImageIcon(Frame.class.getResource("/res/IconeAppli.png")),
						new String[] {Storage.tra("download"), Storage.tra("doNotDownload"), Storage.tra("doNotRemindMe")},
						"default");

				//"Yes"
				if(reply == JOptionPane.YES_OPTION) {
					if(!Infonet.open("http://shadorc.webnode.fr/twitter-stalker/", true)) {
						JOptionPane.showMessageDialog(null, Storage.tra("urlError"), Storage.tra("Erreur"), JOptionPane.PLAIN_MESSAGE);
					}
				} 
				//"Never"
				else if(reply == JOptionPane.CANCEL_OPTION) {
					Storage.saveData(Data.UPDATE, "false");
				}
			}
		} catch (IOException e) {
			System.err.println("Les nouvelles mises à jour n'ont pas pu être vérifiées : " + e.getMessage());
		}
	}
}
