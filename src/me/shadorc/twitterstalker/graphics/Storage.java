package me.shadorc.twitterstalker.graphics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;

import me.shadorc.twitterstalker.graphics.panel.OptionsPanel;

public class Storage {

	private static File file = new File("./data");

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
		PURETWEETS;
	}

	public static String getData(Data data) {
		BufferedReader reader = null;

		try {
			file.createNewFile();
			reader = new BufferedReader(new FileReader(file));

			String line;
			while((line = reader.readLine()) != null) {
				if(line.startsWith(data.toString() + ":")) {
					return line.split(":", 2)[1];
				}
			}

		} catch (IOException e) {
			return null;

		} finally {
			try {
				reader.close();
			} catch (IOException | NullPointerException e) {
				return null;
			}
		}

		return null;
	}

	public static void saveData(Data data, String text) {
		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new FileWriter(file, true));

			if(getData(data) == null) {
				writer.write(data.toString() + ":" + text + "\n");
			} else {
				replaceData(data, text);
			}

		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, Storage.tra("Erreur lors de la sauvegarde, ") + e.getMessage(), Storage.tra("Erreur"), JOptionPane.ERROR_MESSAGE);

		} finally {
			try {
				writer.flush();
				writer.close();
			} catch (IOException | NullPointerException e) {
				JOptionPane.showMessageDialog(null, Storage.tra("Erreur lors de la sauvegarde, ") + e.getMessage(), Storage.tra("Erreur"), JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private static void replaceData(Data data, String text) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));

		String line = "";
		String allData = "";

		while((line = reader.readLine()) != null) {
			allData += line + "\n";
		}

		reader.close();

		for(String li : allData.split("\n")) {
			if(li.startsWith(data.toString() + ":")) {
				allData = allData.replaceAll(li, data.toString() + ":" + text);
				break;
			}
		}

		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(allData);
		writer.close();
	}

	public static String tra(String original) {
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new InputStreamReader(Storage.class.getResourceAsStream("/lang/" + OptionsPanel.getLang() + ".txt"), "UTF-8"));

			String line;
			while((line = reader.readLine()) != null) {
				if(original.equals(line.replaceAll("\"", ""))) {
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
				reader.close();
			} catch (IOException | NullPointerException e) {
				return original;
			}
		}

		System.err.println("Translation not found : " + original);
		return original;
	}
}