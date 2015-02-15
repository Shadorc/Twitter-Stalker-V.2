package me.shadorc.twitterstalker.graphics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

public class Storage {

	private static File file = new File("./data");

	public enum Data {
		/*Installation*/
		INSTALL,
		/*Connection*/
		TOKEN,
		TOKEN_SECRET,
		/*Options*/
		LIST_LENGHT,
		TWEETS_NUMBER,
		MENTIONS_NUMBER,
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
			JOptionPane.showMessageDialog(null, "Erreur lors de la sauvegarde, " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);

		} finally {
			try {
				writer.flush();
				writer.close();
			} catch (IOException | NullPointerException e) {
				JOptionPane.showMessageDialog(null, "Erreur lors de la sauvegarde, " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
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
}