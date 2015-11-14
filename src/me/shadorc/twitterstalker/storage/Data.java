package me.shadorc.twitterstalker.storage;

public class Data {

	public enum Installation {
		INSTALLED,
		UPDATE
	}

	public enum Connection {
		TOKEN,
		TOKEN_SECRET
	}

	public enum Options {
		LETTERS_PER_WORD_MIN,
		INTERFACE_LANG,
		MENTIONS_TO_ANALYZE,
		TWEETS_TO_ANALYZE,
		LIST_LENGHT,
		SHOW_NUMBER
	}

	public enum Statistics {
		TWEETS, 			//Category
		WORDS_PER_TWEET,
		LETTERS_PER_TWEET,
		LETTERS_PER_WORD,

		TIMELINE,			//Category
		PURETWEETS_COUNT,
		MENTIONS_COUNT,
		RETWEET_BY_ME,

		REPUTE,				//Category
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

		TWEETS_PER_DAY,
		FIRST_TALK
	}
}