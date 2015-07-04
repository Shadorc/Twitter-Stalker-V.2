package me.shadorc.twitterstalker.graphics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;

import javax.swing.JOptionPane;

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

		/*Stats*/
		TWEETS,
		WORDS_PER_TWEET,
		LETTERS_PER_TWEET,
		LETTERS_PER_WORD,

		TIMELINE,
		PURETWEETS,
		MENTIONS,
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
				JOptionPane.showMessageDialog(null, Storage.tra("Erreur lors de la sauvegarde, ") + e.getMessage(), Storage.tra("Erreur"), JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();

			} finally {
				try {
					if(writer != null)	writer.close();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, Storage.tra("Erreur lors de la sauvegarde, ") + e.getMessage(), Storage.tra("Erreur"), JOptionPane.ERROR_MESSAGE);
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
			writer.write(jsonObject.toString().replaceAll(",", ",\n"));

		} catch (IOException | JSONException e) {
			JOptionPane.showMessageDialog(null, Storage.tra("Erreur lors de la sauvegarde, ") + e.getMessage(), Storage.tra("Erreur"), JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();

		} finally {
			try {
				if(writer != null)	writer.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, Storage.tra("Erreur lors de la sauvegarde, ") + e.getMessage(), Storage.tra("Erreur"), JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
	}

	public static String tra(String original) {
		BufferedReader reader = null;

		try {
			String obj = Storage.getData(Data.INTERFACE_LANG);
			String lang = (obj != null) ? obj : Locale.getDefault().getDisplayLanguage(Locale.ENGLISH);

			reader = new BufferedReader(new InputStreamReader(Storage.class.getResourceAsStream("/lang/" + lang + ".txt"), "UTF-8"));

			String line;
			while((line = reader.readLine()) != null) {
				if(original.equals(line.replaceAll("\"", "").replaceAll("\\\\n", "\n"))) {
					//Check if the line is not out of the text file
					if((line = reader.readLine()) != null) {
						return line.replaceAll("\"", "").replaceAll("\\\\n", "\n");
					}
					break;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			try {
				if(reader != null)	reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.err.println("Translation not found : " + original);
		return original;
	}
}