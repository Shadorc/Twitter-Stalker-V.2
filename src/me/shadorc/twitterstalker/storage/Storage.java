package me.shadorc.twitterstalker.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilderFactory;

import me.shadorc.twitterstalker.graphics.panel.OptionsPanel;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import twitter4j.JSONException;
import twitter4j.JSONObject;

public class Storage {

	private static File file = new File("./data.json");

	public static void init() {
		if(!file.exists() || file.length() == 0) {
			FileWriter writer = null;

			try {
				file.createNewFile();

				writer = new FileWriter(file);
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
	}
	public static String getData(Enum<?> data) {
		try {
			String text = new String(Files.readAllBytes(Paths.get(file.getPath())), StandardCharsets.UTF_8);
			JSONObject obj = new JSONObject(text);
			if(obj.has(data.toString())) {
				return obj.getString(data.toString());
			}
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void saveData(Enum<?> data, String value) {
		FileWriter writer = null;
		try {
			String text = new String(Files.readAllBytes(Paths.get(file.getPath())), StandardCharsets.UTF_8);

			JSONObject jsonObject = new JSONObject(text);
			jsonObject.put(data.toString(), value);

			writer = new FileWriter(file);
			writer.write(jsonObject.toString().replaceAll(",", ",\n").replaceAll("\\{", "\\{\n").replaceAll("\\}", "\n\\}"));

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

	public static String tra(String key) {
		return tra(key, OptionsPanel.getLocaleLang().getLanguage().substring(0, 2).toLowerCase());
	}

	private static String tra(String key, String lang) {
		try {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(Storage.class.getResourceAsStream("/lang/Translation." + lang + ".resx"));

			NodeList nodeList = document.getElementsByTagName("data");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);

				if(node.getAttributes().getNamedItem("name").getTextContent().equals(key)) {

					String text = node.getTextContent();
					text = text.replaceAll("\n", ""); 			//Remove line break added by XML
					text = text.substring(4, text.length()-2); 	//Remove whitespace at the start and at the end added by XML
					text = text.replaceAll("\\\\n", "\n");
					text = text.replaceAll("\\[", "\\<");
					text = text.replaceAll("\\]", "\\>");

					return text;
				}
			}
		} catch (Exception ignore) { }

		System.err.println("Translation not found, language : " + lang + ", key : " + key);
		return (lang.equals("fr")) ? key : tra(key, "fr");
	}

	public static File getFile() {
		return file;
	}
}