package me.shadorc.twitterstalker.graphics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JOptionPane;

import me.shadorc.twitterstalker.graphics.panel.OptionsPanel;
import twitter4j.JSONException;
import twitter4j.JSONObject;

public class Storage {

	private static File file = new File("./data.json");

	public enum Data {
		/*Installation*/
		INSTALL,
		UPDATE,
		/*Connection*/
		TOKEN,
		TOKEN_SECRET,
		/*Options*/
		LIST_LENGHT,
		TWEETS_NUMBER,
		MENTIONS_NUMBER,
		INTERFACE_LANG,
		/*Stats*/
		LETTERS_PER_WORD,
		HASHTAG,
		TIMELINE,
		TWEETS,
		REPUTE,
		MENTIONS,
		RETWEET_BY_ME,
		RETWEET,
		FAVORITE,
		WORDS_COUNT,
		LETTERS,
		MENTIONS_RECEIVED,
		MENTIONS_SENT,
		POPULARE,
		LANG,
		WORDS,
		DAYS,
		HOURS,
		SOURCE,
		TWEET_PER_DAYS,
		WORDS_PER_TWEET,
		LETTERS_PER_TWEET,
		PURETWEETS,
		MEDIA,
		URL,
		FIRST_TALK;
	}

	public static String getData(Data data) {
		try {
			if(!file.exists()) {
				file.createNewFile();
			} else {
				String text = new String(Files.readAllBytes(Paths.get(file.getPath())), StandardCharsets.UTF_8);
				JSONObject obj = new JSONObject(text);
				if(obj.has(data.toString())) {
					return obj.getString(data.toString());
				}
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

			JSONObject jsonObject;
			if(text.length() != 0) {
				jsonObject = new JSONObject(text);
			} else {
				jsonObject = new JSONObject();
			}
			jsonObject.put(data.toString(), value);

			writer = new FileWriter(file);
			writer.write(jsonObject.toString().replaceAll(",", ",\n"));

		} catch (IOException | JSONException e) {
			JOptionPane.showMessageDialog(null, Storage.tra("Erreur lors de la sauvegarde, ") + e.getMessage(), Storage.tra("Erreur"), JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();

		} finally {
			try {
				if(writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, Storage.tra("Erreur lors de la sauvegarde, ") + e.getMessage(), Storage.tra("Erreur"), JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
	}

	public static String tra(String original) {
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new InputStreamReader(Storage.class.getResourceAsStream("/lang/" + OptionsPanel.getLang() + ".txt"), "UTF-8"));

			String line;
			while((line = reader.readLine()) != null) {
				if(original.equals(line.replaceAll("\"", ""))) {
					//Check if the line is not out of the text file
					if((line = reader.readLine()) != null) {
						return line.replaceAll("\"", "");
					} else {
						return original;
					}
				}
			}

		} catch (IOException e) {
			return original;

		} finally {
			try {
				if(reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				return original;
			}
		}

		System.err.println("Translation not found : " + original);
		return original;
	}
}