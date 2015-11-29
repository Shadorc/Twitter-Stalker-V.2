package me.shadorc.twitterstalker.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import me.shadorc.twitterstalker.graphics.panel.OptionsPanel;
import twitter4j.JSONException;
import twitter4j.JSONObject;

public class Storage {

	private static File saveFile = new File("./data.json");

	private static void createFile() {
		FileWriter writer = null;

		try {
			saveFile.createNewFile();

			writer = new FileWriter(saveFile);
			writer.write(new JSONObject().toString());

		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, Storage.tra("saveError"), Storage.tra("error"), JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();

		} finally {
			try {
				if(writer != null)	writer.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, Storage.tra("saveError"), Storage.tra("error"), JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
	}
	public static String getData(Enum<?> data) {

		if(!saveFile.exists() || saveFile.length() == 0) {
			createFile();
		}

		try {
			String text = new String(Files.readAllBytes(Paths.get(saveFile.getPath())), StandardCharsets.UTF_8);
			JSONObject obj = new JSONObject(text);
			if(obj.has(data.toString())) {
				return obj.getString(data.toString());
			}
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void saveData(Object data, Object value) {
		FileWriter writer = null;
		try {
			String text = new String(Files.readAllBytes(Paths.get(saveFile.getPath())), StandardCharsets.UTF_8);

			JSONObject jsonObject = new JSONObject(text);
			jsonObject.put(data.toString(), value);

			writer = new FileWriter(saveFile);
			writer.write(jsonObject.toString(2));

		} catch (IOException | JSONException e) {
			JOptionPane.showMessageDialog(null, Storage.tra("saveError"), Storage.tra("error"), JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();

		} finally {
			try {
				if(writer != null)	writer.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, Storage.tra("saveError"), Storage.tra("error"), JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
	}

	public static String tra(Object key) {
		String lang = OptionsPanel.getLocaleLang().getLanguage().substring(0, 2).toLowerCase();
		try {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(Storage.class.getResourceAsStream("/lang/Translation." + lang + ".resx"));
			document.getDocumentElement().normalize();

			NodeList dataList = document.getElementsByTagName("data");
			for (int i = 0; i < dataList.getLength(); i++) {
				Node node = dataList.item(i);

				if(node.getAttributes().getNamedItem("name").getTextContent().equals(key.toString())) {
					return ((Element) node).getElementsByTagName("value").item(0).getTextContent();
				}
			}
		} catch (Exception ignore) { }

		System.err.println("[WARNING] Translation not found [lang: " + lang + ", key: " + key + "]");
		return key.toString();
	}

	public static File getSaveFile() {
		return saveFile;
	}
}