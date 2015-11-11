package me.shadorc.twitterstalker.initialization;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Paths;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import me.shadorc.twitterstalker.graphics.Frame;
import me.shadorc.twitterstalker.storage.Data;
import me.shadorc.twitterstalker.storage.Storage;

import org.apache.commons.io.FileUtils;

import twitter4j.JSONArray;
import twitter4j.JSONObject;
import twitter4j.JSONTokener;

public class UpdateChecker {

	public static void check(Version currentVersion) {

		try {
			URL url = new URL("https://api.github.com/repos/Shadorc/Twitter-Stalker-V.2/releases");
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

			JSONArray releasesArray = (JSONArray) new JSONTokener(reader).nextValue();
			JSONObject lastRelease = (JSONObject) releasesArray.get(0);

			Version lastVersion = new Version(lastRelease.getString("tag_name"));

			if(lastVersion.isNewerThan(Frame.version) && Storage.getData(Data.UPDATE) == null) {

				int reply = JOptionPane.showOptionDialog(null,
						Storage.tra("updateAvailable") + " (" + lastVersion + ") !",
						Storage.tra("update"),
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE,
						new ImageIcon(Frame.class.getResource("/res/IconeAppli.png")),
						new String[] {Storage.tra("download"), Storage.tra("doNotDownload"), Storage.tra("doNotRemindMe")},
						"default");

				if(reply == JOptionPane.YES_OPTION) {
					JSONArray assets = lastRelease.getJSONArray("assets");
					JSONObject firstAsset = (JSONObject) assets.get(0);

					URL downloadUrl = new URL(firstAsset.get("browser_download_url").toString());

					File jarFile = new File(Paths.get(".").toAbsolutePath().normalize().toString() + "/" + firstAsset.getString("name")) ;

					System.out.println("Downloading new version...");
					FileUtils.copyURLToFile(downloadUrl, jarFile);
					System.out.println("Finished.\n");

					System.out.println("Extracting files...");
					UnzipUtility.unzip(jarFile, jarFile.getParentFile().getParentFile());
					System.out.println("Finished.");

					jarFile.delete();
				} 

				else if(reply == JOptionPane.CANCEL_OPTION) {
					Storage.saveData(Data.UPDATE, "false");
				}
			}
		} catch (Exception e) {
			System.err.println("New updates haven't been verified");
			e.printStackTrace();
		}
	}
}
