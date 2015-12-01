package me.shadorc.twitterstalker.storage;

public class Data {

	public enum Installation {
		INSTALLED,
		UPDATE
	}

	public enum Connection {
		TOKEN_SECRET,
		TOKEN
	}

	public enum Options {
		LETTERS_PER_WORD_MIN,
		MENTIONS_TO_ANALYZE,
		TWEETS_TO_ANALYZE,
		INTERFACE_LANG,
		LIST_LENGHT
	}

	public enum Category {
		SUMMARY,
		TWEETS,
		TIMELINE,
		REPUTE
	}

	public enum UsersEnum {
		MENTIONS_RECEIVED,
		MENTIONS_SENT,
		FIRST_TALK
	}

	public enum WordsEnum {
		SOURCE,
		DAYS,
		HOURS,
		WORDS,
		HASHTAG,
		LANG,
		POPULAR
	}

	public enum NumbersEnum {
		LETTERS_PER_TWEET,
		LETTERS_PER_WORD,
		PURETWEETS_COUNT,
		WORDS_PER_TWEET,
		MENTIONS_COUNT,
		TWEETS_PER_DAY,
		RETWEET_BY_ME,
		HASHTAG_COUNT,
		WORDS_COUNT,
		LETTERS,
		RETWEET,
		MEDIA,
		LIKE,
		URL
	}
}