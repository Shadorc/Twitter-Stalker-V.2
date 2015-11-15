package me.shadorc.twitterstalker.initialization;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Paths;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import me.shadorc.twitterstalker.graphics.Frame;
import me.shadorc.twitterstalker.graphics.Ressources;
import me.shadorc.twitterstalker.storage.Data.Installation;
import me.shadorc.twitterstalker.storage.Storage;

import org.apache.commons.io.FileUtils;

import twitter4j.JSONArray;
import twitter4j.JSONException;
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

			if(lastVersion.isNewerThan(Ressources.getVersion()) && Storage.getData(Installation.UPDATE) == null) {

				int reply = JOptionPane.showOptionDialog(null,
						Storage.tra("updateAvailable") + " (" + lastVersion + ") !",
						Storage.tra("update"),
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE,
						Ressources.getBigIcon(),
						new String[] {Storage.tra("download"), Storage.tra("doNotDownload"), Storage.tra("doNotRemindMe")},
						"default");

				if(reply == JOptionPane.YES_OPTION) {
					UpdateChecker.update(lastRelease);
				} 

				else if(reply == JOptionPane.CANCEL_OPTION) {
					Storage.saveData(Installation.UPDATE, false);
				}
			}
		} catch (Exception e) {
			System.err.println("New updates haven't been verified");
			e.printStackTrace();
		}
	}

	private static void update(JSONObject release) {
		try {

			File actualFolder = new File(Paths.get(".").toAbsolutePath().normalize().toString());

			//Assets are downloadable files for the release
			JSONObject asset = (JSONObject) release.getJSONArray("assets").get(0);

			URL downloadUrl = new URL(asset.get("browser_download_url").toString());

			//zip file which will be downloaded
			File zipFile = new File(actualFolder + "/" + asset.getString("name"));

			UpdateChecker.showUpdateFrame();

			info.setText("Downloading new version (" + String.format("%.2f", Integer.parseInt(asset.getString("size"))/1000000.0f) + " mo)...");
			FileUtils.copyURLToFile(downloadUrl, zipFile);

			//Extract the zip outside the current directory
			File extractingDir = actualFolder.getParentFile();

			File newFolder = new File(extractingDir + "/" + release.getString("name"));

			if(newFolder.exists()) {
				info.setText("A folder with the same name already exists : " + newFolder);
				zipFile.delete();
				return;
			}

			info.setText("Extracting files...");
			UnzipUtility.unzip(zipFile, extractingDir);

			zipFile.delete();

			info.setText("Copying data...");
			File data = Storage.getSaveFile();
			//Copy-Paste data to the new folder
			File copyData = new File(newFolder + "/" + data.getName());

			if(data.exists()) {
				FileUtils.copyFile(data, copyData);
			}

			info.setText("Opening new version...");
			ProcessBuilder pb = new ProcessBuilder("java", "-classpath", newFolder + "/TwitterStalker.jar", Frame.class.getName(), actualFolder.getPath());
			pb.start();

			System.exit(0);

		} catch (JSONException | IOException e) {
			JOptionPane.showMessageDialog(null, Storage.tra("updateError") + e.getMessage(), Storage.tra("error"), JOptionPane.ERROR_MESSAGE);
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
		info.setFont(new JOptionPane().getFont());
		info.setOpaque(false);
		panel.add(info, BorderLayout.CENTER);

		frame.setContentPane(panel);

		frame.setIconImage(Ressources.getSmallIcon().getImage());
		frame.setSize(500, 200);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);;
	}
}
