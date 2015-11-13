package me.shadorc.twitterstalker.initialization;

import java.io.File;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

import me.shadorc.twitterstalker.graphics.Ressources;
import me.shadorc.twitterstalker.storage.Data;
import me.shadorc.twitterstalker.storage.Storage;
import net.jimmc.jshortcut.JShellLink;

public class Shortcut {

	public static void create() {
		//If this wasn't done before and if he's using Windows, ask the user to create a shortcut on desktop
		try {
			if((Storage.getData(Data.INSTALLED) == null) && System.getProperty("os.name").startsWith("Windows")) {
				int reply = JOptionPane.showOptionDialog(null,
						Storage.tra("createShortcut"),
						Storage.tra("installation"),
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE,
						Ressources.getBigIcon(),
						new String[] {Storage.tra("yes"), Storage.tra("notNow"), Storage.tra("never")},
						"default");

				//"Yes"
				if(reply == JOptionPane.YES_OPTION) {
					JShellLink link = new JShellLink();
					link.setFolder(JShellLink.getDirectory("desktop"));
					link.setName(Ressources.getName());
					link.setIconLocation(new File(Shortcut.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent() + "\\IconeAppli.ico");
					link.setPath(Shortcut.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().replaceFirst("/", ""));
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
}
