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
			if((Storage.getData(Data.INSTALL) == null) && System.getProperty("os.name").startsWith("Windows")) {
				int reply = JOptionPane.showOptionDialog(null,
						Storage.tra("Bonjour et merci d'avoir téléchargé Twitter Stalker !"
								+ "\nPlacez le dossier téléchargé où vous voulez puis relancer le pour créer un raccourci sur le bureau (optionnel)."
								+ "\nVoulez-vous créer un raccourci sur le bureau maintenant ?"),
								Storage.tra("Installation"),
								JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.PLAIN_MESSAGE,
								new ImageIcon(Frame.class.getResource("/res/IconeAppli.png")),
								new String[] {Storage.tra("Oui"), Storage.tra("Pas maintenant"), Storage.tra("Jamais")},
						"default");

				//"Yes"
				if(reply == JOptionPane.YES_OPTION) {
					JShellLink link = new JShellLink();
					link.setFolder(JShellLink.getDirectory("desktop"));
					link.setName("Twitter Stalker");
					link.setIconLocation(new File(Frame.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent() + "\\IconeAppli.ico");
					link.setPath(Frame.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().replaceFirst("/", ""));
					link.save();

					Storage.saveData(Data.INSTALL, "true");

				}
				//"Never"
				else if (reply == JOptionPane.CANCEL_OPTION) {
					Storage.saveData(Data.INSTALL, "true");
				}
			}
		} catch(URISyntaxException e) {
			e.printStackTrace();
		}
	}


	public static void checkForUpdate(String version) {
		//Check the last version and prevent user if an update is available
		try {
			String lastVersion = Infonet.parseHTML(new URL("http://shadorc.webnode.fr/twitter-stalker/"), "Version actuelle ", "(", ")", true, true);

			if(!version.equals(lastVersion) && Storage.getData(Data.UPDATE) == null) {

				int reply = JOptionPane.showOptionDialog(null,
						Storage.tra("Une nouvelle mise à jour est disponible") + " (" + lastVersion + ") !",
						Storage.tra("Mise à jour"),
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE,
						new ImageIcon(Frame.class.getResource("/res/IconeAppli.png")),
						new String[] {Storage.tra("Télécharger"), Storage.tra("Ne pas télécharger"), Storage.tra("Ne plus me le rappeler")},
						"default");

				//"Yes"
				if(reply == JOptionPane.YES_OPTION) {
					if(!Infonet.open("http://shadorc.webnode.fr/twitter-stalker/", true)) {
						JOptionPane.showMessageDialog(null, Storage.tra("Erreur lors de l'ouverture, l'URL a été copiée dans le presse-papier."), Storage.tra("Erreur"), JOptionPane.PLAIN_MESSAGE);
					}
				} 
				//"Never"
				else if(reply == JOptionPane.CANCEL_OPTION) {
					Storage.saveData(Data.UPDATE, "false");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
