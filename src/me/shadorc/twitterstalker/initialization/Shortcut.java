package me.shadorc.twitterstalker.initialization;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JOptionPane;

import me.shadorc.twitterstalker.graphics.Ressources;
import me.shadorc.twitterstalker.storage.Data.Installation;
import me.shadorc.twitterstalker.storage.Storage;
import net.jimmc.jshortcut.JShellLink;

import org.apache.commons.io.FileUtils;

public class Shortcut {

	public static void create() {
		//If this wasn't done before and if he's using Windows, ask the user to create a shortcut on desktop
		try {
			if((Storage.getData(Installation.INSTALLED) == null) && System.getProperty("os.name").startsWith("Windows")) {

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

					File jar = new File(Shortcut.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().replaceFirst("/", ""));

					JShellLink link = new JShellLink();
					link.setFolder(JShellLink.getDirectory("desktop"));
					link.setName(Ressources.getName());
					link.setPath(jar.getPath());

					//Copy icon outside the jar to be able to use it
					URL inputUrl = Shortcut.class.getResource("/res/TS_icon.ico");
					File dest = new File(jar.getParent() + "/TS_icon.ico");
					FileUtils.copyURLToFile(inputUrl, dest);

					link.setIconLocation(dest.getPath());
					link.save();

					Storage.saveData(Installation.INSTALLED, true);
				}
				//"Never"
				else if (reply == JOptionPane.CANCEL_OPTION) {
					Storage.saveData(Installation.INSTALLED, true);
				}
			}
		} catch(URISyntaxException | IOException e) {
			e.printStackTrace();
		}
	}
}
