package me.shadorc.twitterstalker.initialization;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import me.shadorc.twitterstalker.graphics.Ressources;
import me.shadorc.twitterstalker.storage.Data;
import me.shadorc.twitterstalker.storage.Storage;

import org.apache.commons.io.FileUtils;

import twitter4j.JSONArray;
import twitter4j.JSONObject;
import twitter4j.JSONTokener;

public class UpdateChecker {

	private static JLabel info;

	public static void check() {

		try {
			URL url = new URL("https://api.github.com/repos/Shadorc/Twitter-Stalker-V.2/releases");
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

			JSONArray releasesArray = (JSONArray) new JSONTokener(reader).nextValue();
			JSONObject lastRelease = (JSONObject) releasesArray.get(0);

			Version lastVersion = new Version(lastRelease.getString("tag_name"));

			if(lastVersion.isNewerThan(Ressources.getVersion()) && Storage.getData(Data.UPDATE) == null) {

				int reply = JOptionPane.showOptionDialog(null,
						Storage.tra("updateAvailable") + " (" + lastVersion + ") !",
						Storage.tra("update"),
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE,
						Ressources.getBigIcon(),
						new String[] {Storage.tra("download"), Storage.tra("doNotDownload"), Storage.tra("doNotRemindMe")},
						"default");

				if(reply == JOptionPane.YES_OPTION) {
					//Assets are downloadable files for the release
					JSONObject asset = (JSONObject) lastRelease.getJSONArray("assets").get(0);

					URL downloadUrl = new URL(asset.get("browser_download_url").toString());

					//zip file which will be downloaded
					File zipFile = new File(Paths.get(".").toAbsolutePath().normalize().toString() + "/" + asset.getString("name"));

					showUpdateFrame();

					info.setText("Downloading new version (" + String.format("%.2f", Integer.parseInt(asset.getString("size"))/1000000.0f) + " mo)...");
					FileUtils.copyURLToFile(downloadUrl, zipFile);

					//Extract the zip outside the current directory
					File extractingDir = zipFile.getParentFile().getParentFile();

					info.setText("Extracting files...");
					UnzipUtility.unzip(zipFile, extractingDir);
					info.setText("Finished.");

					zipFile.delete();

					File data = Storage.getFile();
					//Copy-Paste data to the new directory
					File copyData = new File(extractingDir + "/" + lastRelease.getString("name") +"/" + data.getName());

					if(data.exists()) {
						Files.copy(data.toPath(), copyData.toPath());
					}
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

	private static void showUpdateFrame() {
		JFrame frame = new JFrame(Ressources.getName() + " - Update");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JLabel icon = new JLabel(Ressources.getBigIcon());
		panel.add(icon, BorderLayout.WEST);

		info = new JLabel("", JLabel.CENTER);
		panel.add(info, BorderLayout.CENTER);

		frame.setContentPane(panel);

		frame.setIconImage(Ressources.getSmallIcon().getImage());
		frame.setSize(400, 200);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);;
	}
}
