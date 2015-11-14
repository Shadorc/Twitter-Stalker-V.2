package me.shadorc.twitterstalker.graphics.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedHashMap;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;

import me.shadorc.twitterstalker.graphics.Button;
import me.shadorc.twitterstalker.graphics.Button.ButtonType;
import me.shadorc.twitterstalker.graphics.Button.Size;
import me.shadorc.twitterstalker.graphics.CheckBoxOption;
import me.shadorc.twitterstalker.graphics.Frame;
import me.shadorc.twitterstalker.graphics.Ressources;
import me.shadorc.twitterstalker.graphics.ScrollbarUI;
import me.shadorc.twitterstalker.graphics.ScrollbarUI.Position;
import me.shadorc.twitterstalker.storage.Data.Options;
import me.shadorc.twitterstalker.storage.Data.Statistics;
import me.shadorc.twitterstalker.storage.Storage;

public class OptionsPanel extends JPanel implements ActionListener, ItemListener {

	private static final long serialVersionUID = 1L;

	private static LinkedHashMap <Enum<?>, JCheckBox> checkBoxMap = new LinkedHashMap <Enum<?>, JCheckBox> ();
	private static JComboBox <String> list_lenght, letters_word, tweets_number, mentions_number, languages;
	private JButton back;

	public OptionsPanel() {
		super(new BorderLayout());
		this.setBackground(new Color(2, 136, 209));

		list_lenght = this.createComboBox(Options.LIST_LENGHT, new String[] {"1","2","3","4","5","6","7","8","9","10"}, "3");
		letters_word = this.createComboBox(Options.LETTERS_PER_WORD_MIN, new String[] {"1","2","3","4","5","6","7","8","9","10"}, "1");
		//Limit : 3200 Tweets
		tweets_number = this.createComboBox(Options.TWEETS_TO_ANALYZE, new String[] {"200","400","600","800","1000","1200","1400","1600","1800","2000","2200","2400","2600","2800","3000"}, "3000");
		//Limit : 800 Mentions
		mentions_number = this.createComboBox(Options.MENTIONS_TO_ANALYZE, new String[] {"200","400","600"}, "600");
		languages = this.createComboBox(Options.INTERFACE_LANG, new String[] {"French","English"}, Locale.getDefault().getDisplayLanguage(Locale.ENGLISH));

		JPanel top = new JPanel(new BorderLayout());
		top.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(183,183,183)), BorderFactory.createEmptyBorder(25, 0, 25, 0)));
		top.setBackground(new Color(3,169,244));

		JLabel title = new JLabel(Storage.tra("options"), JLabel.CENTER);
		title.setFont(Ressources.getFont("RobotoCondensed-Regular.ttf", 72));
		title.setForeground(new Color(33,33,33));
		top.add(title, BorderLayout.PAGE_START);

		this.add(top, BorderLayout.PAGE_START);

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setOpaque(false);

		JPanel options = new JPanel(new GridLayout(6, 2));
		options.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
		options.setOpaque(false);

		options.add(this.createOption(Storage.tra("minWordLenght"), letters_word));

		checkBoxMap.put(Options.SHOW_NUMBER, new CheckBoxOption(Storage.tra("showNumber"), Options.SHOW_NUMBER));
		options.add(checkBoxMap.get(Options.SHOW_NUMBER));

		options.add(this.createOption(Storage.tra("progLanguage"), languages));
		options.add(new JLabel());
		options.add(this.createOption(Storage.tra("mentionsNumber"), mentions_number));
		options.add(new JLabel());
		options.add(this.createOption(Storage.tra("tweetsNumber"), tweets_number));
		options.add(new JLabel());
		options.add(this.createOption(Storage.tra("listLenght"), list_lenght));
		options.add(new JLabel());
		options.add(this.createOption(Storage.tra("statsToShow"), null));
		options.add(new JLabel());

		centerPanel.add(options, BorderLayout.PAGE_START);

		JPanel checkBox = new JPanel(new GridLayout(4, 4, 5, 5));
		checkBox.setBorder(BorderFactory.createEmptyBorder(0, 25, 25, 25));
		checkBox.setOpaque(false);

		checkBoxMap.put(Statistics.TWEETS, new CheckBoxOption(Storage.tra("tweetsStat"), Statistics.TWEETS));
		checkBoxMap.put(Statistics.TIMELINE, new CheckBoxOption(Storage.tra("timelineStat"), Statistics.TIMELINE));
		checkBoxMap.put(Statistics.REPUTE, new CheckBoxOption(Storage.tra("reputeStat"), Statistics.REPUTE));
		checkBoxMap.put(Statistics.SOURCE, new CheckBoxOption(Storage.tra("sourceStat"), Statistics.SOURCE));
		checkBoxMap.put(Statistics.DAYS, new CheckBoxOption(Storage.tra("daysStat"), Statistics.DAYS));
		checkBoxMap.put(Statistics.HOURS, new CheckBoxOption(Storage.tra("hoursStat"), Statistics.HOURS));
		checkBoxMap.put(Statistics.WORDS, new CheckBoxOption(Storage.tra("wordsStat"), Statistics.WORDS));
		checkBoxMap.put(Statistics.HASHTAG, new CheckBoxOption(Storage.tra("hashtagStat"), Statistics.HASHTAG));
		checkBoxMap.put(Statistics.POPULARE, new CheckBoxOption(Storage.tra("popularStat"), Statistics.POPULARE));
		checkBoxMap.put(Statistics.LANG, new CheckBoxOption(Storage.tra("languageStat"), Statistics.LANG));
		checkBoxMap.put(Statistics.MENTIONS_SENT, new CheckBoxOption(Storage.tra("mentionsSent"), Statistics.MENTIONS_SENT));
		checkBoxMap.put(Statistics.MENTIONS_RECEIVED, new CheckBoxOption(Storage.tra("mentionsReceived"), Statistics.MENTIONS_RECEIVED));

		for(Enum<?> data : checkBoxMap.keySet()) {
			if(data != Options.SHOW_NUMBER) {
				checkBox.add(checkBoxMap.get(data));
			}
		}

		centerPanel.add(checkBox, BorderLayout.CENTER);

		this.add(centerPanel, BorderLayout.CENTER);

		JPanel button = new JPanel(new BorderLayout());
		button.setOpaque(false);
		back = new Button(ButtonType.BACK, new int[] {0, 10, 10, 0}, Size.MEDIUM, this);
		button.add(back, BorderLayout.WEST);
		this.add(button, BorderLayout.PAGE_END);
	}

	private JComboBox <String> createComboBox(Enum<?> data, String[] list, String def) {
		JComboBox <String> jcb = new JComboBox <String> (list);
		jcb.addItemListener(this);
		jcb.setFocusable(false);
		((JScrollPane) ((Container) jcb.getUI().getAccessibleChild(jcb, 0)).getComponent(0)).getVerticalScrollBar().setUI(new ScrollbarUI(Position.VERTICAL));
		jcb.setBackground(new Color(179, 229, 252));
		String obj = Storage.getData(data);
		jcb.setSelectedItem((obj != null) ? obj : def);
		return jcb;
	}

	private JPanel createOption(String desc, JComboBox <String> jcb) {
		JPanel pane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pane.setOpaque(false);

		JLabel label = new JLabel(desc);
		label.setFont(Ressources.getFont("SEGOEUI.TTF", 30));
		pane.add(label);
		if(jcb != null) pane.add(jcb);

		return pane;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Frame.setPanel(new MenuPanel());		
	}

	public static int getMaxListLenght() {
		return Integer.parseInt(list_lenght.getSelectedItem().toString());
	}

	public static int getMaxTweetsNumber() {
		return Integer.parseInt(tweets_number.getSelectedItem().toString());
	}

	public static int getMaxMentionsNumber() {
		return Integer.parseInt(mentions_number.getSelectedItem().toString());
	}

	public static int getMinLettersWord() {
		return Integer.parseInt(letters_word.getSelectedItem().toString());
	}

	public static Locale getLocaleLang() {
		try {
			return new Locale(Storage.getData(Options.INTERFACE_LANG).substring(0, 2));
		} catch (NullPointerException e) {
			//If language has never been configured, returned default computer's language
			return new Locale(Locale.getDefault().getDisplayCountry(Locale.ENGLISH).substring(0, 2));
		}
	}

	public static boolean isSelected(Enum<?> data) {
		return checkBoxMap.get(data).isSelected();
	}

	@Override
	public void itemStateChanged(ItemEvent event) {
		if(event.getStateChange() == ItemEvent.SELECTED) {
			JComboBox <?> jcb = (JComboBox <?>) event.getSource();
			String data = event.getItem().toString();

			if(jcb == list_lenght) 			Storage.saveData(Options.LIST_LENGHT, data);
			else if(jcb == tweets_number) 	Storage.saveData(Options.TWEETS_TO_ANALYZE, data);
			else if(jcb == mentions_number)	Storage.saveData(Options.MENTIONS_TO_ANALYZE, data);
			else if(jcb == letters_word) 	Storage.saveData(Options.LETTERS_PER_WORD_MIN, data);
			else if(jcb == languages) {
				//Prevent weird IndexOutOfBound Exception
				languages.hidePopup();
				Storage.saveData(Options.INTERFACE_LANG, data);
				Frame.setPanel(new OptionsPanel());
			}
		}
	}
}
