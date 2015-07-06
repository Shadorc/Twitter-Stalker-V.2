package me.shadorc.twitterstalker.graphics;

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

	public enum Data {
		/*Installation*/
		INSTALLED,
		UPDATE,

		/*Connection*/
		TOKEN,
		TOKEN_SECRET,

		/*Options*/
		LETTERS_PER_WORD_MIN,
		INTERFACE_LANG,
		MENTIONS_TO_ANALYZE,
		TWEETS_TO_ANALYZE,
		LIST_LENGHT,
		SHOW_NUMBER,

		/*Stats*/
		TWEETS,
		WORDS_PER_TWEET,
		LETTERS_PER_TWEET,
		LETTERS_PER_WORD,

		TIMELINE,
		PURETWEETS_COUNT,
		MENTIONS_COUNT,
		RETWEET_BY_ME,

		REPUTE,
		FAVORITE,
		RETWEET,

		SOURCE,
		DAYS,
		HOURS,
		WORDS,
		HASHTAG,
		LANG,

		POPULARE,

		MENTIONS_SENT,
		MENTIONS_RECEIVED,

		WORDS_COUNT,
		HASHTAG_COUNT,
		LETTERS,
		MEDIA,
		URL,

		TWEET_PER_DAYS,
		FIRST_TALK
	}

	public static void init() {
		if(!file.exists() || file.length() == 0) {
			FileWriter writer = null;

			try {
				file.createNewFile();

				writer = new FileWriter(file);
				writer.write(new JSONObject().toString());

			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, Storage.tra("saveError") + e.getMessage(), Storage.tra("error"), JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();

			} finally {
				try {
					if(writer != null)	writer.close();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, Storage.tra("saveError") + e.getMessage(), Storage.tra("error"), JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
		}
	}
	public static String getData(Data data) {
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

	public static void saveData(Data data, String value) {
		FileWriter writer = null;
		try {
			String text = new String(Files.readAllBytes(Paths.get(file.getPath())), StandardCharsets.UTF_8);

			JSONObject jsonObject = new JSONObject(text);
			jsonObject.put(data.toString(), value);

			writer = new FileWriter(file);
			writer.write(jsonObject.toString().replaceAll(",", ",\n").replaceAll("\\{", "\\{\n").replaceAll("\\}", "\n\\}"));

		} catch (IOException | JSONException e) {
			JOptionPane.showMessageDialog(null, Storage.tra("saveError") + e.getMessage(), Storage.tra("error"), JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();

		} finally {
			try {
				if(writer != null)	writer.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, Storage.tra("saveError") + e.getMessage(), Storage.tra("error"), JOptionPane.ERROR_MESSAGE);
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

					return text;
				}
			}
		} catch (Exception ignore) { }

		System.err.println("Translation not found : " + lang + " : " + key);
		return (lang.equals("fr")) ? key : tra(key, "fr");
	}
}