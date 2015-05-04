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
import java.util.HashMap;
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
import me.shadorc.twitterstalker.graphics.CheckBoxOption;
import me.shadorc.twitterstalker.graphics.Frame;
import me.shadorc.twitterstalker.graphics.ScrollbarUI;
import me.shadorc.twitterstalker.graphics.ScrollbarUI.Position;
import me.shadorc.twitterstalker.graphics.Storage;
import me.shadorc.twitterstalker.graphics.Storage.Data;

public class OptionsPanel extends JPanel implements ActionListener, ItemListener {

	private static final long serialVersionUID = 1L;

	private static JComboBox <String> list_lenght, letters_word, tweets_number, mentions_number, languages;
	private static HashMap <Data, JCheckBox> checkBoxMap;
	private JButton back;

	public OptionsPanel() {
		super(new BorderLayout());
		this.setBackground(new Color(2, 136, 209));

		JPanel top = new JPanel(new BorderLayout());
		top.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(183,183,183)), BorderFactory.createEmptyBorder(25, 0, 25, 0)));
		top.setBackground(new Color(3,169,244));

		JLabel title = new JLabel(Storage.tra("Options"), JLabel.CENTER);
		title.setFont(Frame.getFont("RobotoCondensed-Regular.ttf", 72));
		title.setForeground(new Color(33,33,33));
		top.add(title, BorderLayout.PAGE_START);

		this.add(top, BorderLayout.PAGE_START);

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setOpaque(false);

		JPanel options = new JPanel(new GridLayout(6, 0));
		options.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
		options.setOpaque(false);

		list_lenght = this.createComboBox(Data.LIST_LENGHT, new String[] {"1","2","3","4","5","6","7","8","9","10"}, "3");
		letters_word = this.createComboBox(Data.LETTERS_PER_WORD, new String[] {"1","2","3","4","5","6","7","8","9","10"}, "1");
		//Limit : 3200 Tweets
		tweets_number = this.createComboBox(Data.TWEETS_NUMBER, new String[] {"200","400","600","800","1000","1200","1400","1600","1800","2000","2200","2400","2600","2800","3000"}, "3000");
		//Limit : 800 Mentions
		mentions_number = this.createComboBox(Data.MENTIONS_NUMBER, new String[] {"200","400","600"}, "600");
		languages = this.createComboBox(Data.INTERFACE_LANG, new String[] {"French","English"}, Locale.getDefault().getDisplayLanguage(Locale.ENGLISH));

		options.add(this.createOption("Nombre de lettres par mot minimum : ", letters_word));
		options.add(this.createOption("Nombre de mentions : ", mentions_number));
		options.add(this.createOption("Nombre de tweets : ", tweets_number));
		options.add(this.createOption("Taille des listes : ", list_lenght));
		options.add(this.createOption("Langue : ", languages));

		//Align the label with others options
		JPanel pane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pane.setOpaque(false);
		JLabel labelStats = new JLabel(Storage.tra("Statistiques à effectuer :"));
		labelStats.setFont(Frame.getFont("SEGOEUI.TTF", 30));
		pane.add(labelStats);
		options.add(pane);

		centerPanel.add(options, BorderLayout.PAGE_START);

		JPanel checkBox = new JPanel(new GridLayout(4, 4, 5, 5));
		checkBox.setBorder(BorderFactory.createEmptyBorder(0, 25, 25, 25));
		checkBox.setOpaque(false);

		checkBoxMap = new HashMap <> ();
		checkBoxMap.put(Data.SOURCE, new CheckBoxOption("Applis", Data.SOURCE));
		checkBoxMap.put(Data.HASHTAG, new CheckBoxOption("Hashtags", Data.HASHTAG));
		checkBoxMap.put(Data.WORDS, new CheckBoxOption("Mots", Data.WORDS));
		checkBoxMap.put(Data.DAYS, new CheckBoxOption("Jours", Data.DAYS));
		checkBoxMap.put(Data.HOURS, new CheckBoxOption("Heures", Data.HOURS));
		checkBoxMap.put(Data.TIMELINE, new CheckBoxOption("Timeline", Data.TIMELINE));
		checkBoxMap.put(Data.TWEETS, new CheckBoxOption("Tweets", Data.TWEETS));
		checkBoxMap.put(Data.POPULARE, new CheckBoxOption("Populaires", Data.POPULARE));
		checkBoxMap.put(Data.LANG, new CheckBoxOption("Langues", Data.LANG));
		checkBoxMap.put(Data.MENTIONS_SENT, new CheckBoxOption("Twittos mentionnés", Data.MENTIONS_SENT));
		checkBoxMap.put(Data.MENTIONS_RECEIVED, new CheckBoxOption("Twittos mentionnant", Data.MENTIONS_RECEIVED));
		checkBoxMap.put(Data.REPUTE, new CheckBoxOption("Renommée", Data.REPUTE));

		for(Data data : checkBoxMap.keySet()) {
			checkBox.add(checkBoxMap.get(data));
		}

		centerPanel.add(checkBox, BorderLayout.CENTER);

		this.add(centerPanel, BorderLayout.CENTER);

		JPanel button = new JPanel(new BorderLayout());
		button.setOpaque(false);
		back = new Button("Retour", new int[] {0, 10, 10, 0}, true, this);
		button.add(back, BorderLayout.WEST);
		this.add(button, BorderLayout.PAGE_END);
	}

	private JComboBox <String> createComboBox(Data data, String[] list, String def) {
		JComboBox <String> jcb = new JComboBox <String> (list);
		jcb.addItemListener(this);
		jcb.setFocusable(false);
		((JScrollPane) ((Container) jcb.getUI().getAccessibleChild(jcb, 0)).getComponent(0)).getVerticalScrollBar().setUI(new ScrollbarUI(Position.VERTICAL));
		jcb.setBackground(new Color(179, 229, 252));
		Object obj = Storage.getData(data);
		if(obj != null) {
			jcb.setSelectedItem(obj.toString());
		} else {
			jcb.setSelectedItem(def);
		}
		return jcb;
	}

	private JPanel createOption(String text, JComboBox <String> jcb) {
		JPanel pane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pane.setOpaque(false);

		JLabel label = new JLabel(Storage.tra(text));
		label.setFont(Frame.getFont("SEGOEUI.TTF", 30));
		pane.add(label);
		pane.add(jcb);

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

	public static String getLang() {
		return languages.getSelectedItem().toString();
	}

	public static Locale getLocaleLang() {
		try {
			return new Locale(Storage.getData(Data.INTERFACE_LANG).substring(0, 2));
		} catch (NullPointerException e) {
			//If language has never been configured, returned default computer's language
			return new Locale(Locale.getDefault().getDisplayCountry(Locale.ENGLISH).substring(0, 2));
		}
	}

	public static boolean isSelected(Data data) {
		return checkBoxMap.get(data).isSelected();
	}

	@Override
	public void itemStateChanged(ItemEvent event) {
		if(event.getStateChange() == ItemEvent.SELECTED) {
			JComboBox <?> jcb = (JComboBox <?>) event.getSource();
			String data = event.getItem().toString();

			if(jcb == list_lenght) 			Storage.saveData(Data.LIST_LENGHT, data);
			else if(jcb == tweets_number) 	Storage.saveData(Data.TWEETS_NUMBER, data);
			else if(jcb == mentions_number)	Storage.saveData(Data.MENTIONS_NUMBER, data);
			else if(jcb == letters_word) 	Storage.saveData(Data.LETTERS_PER_WORD, data);
			else if(jcb == languages) 		Storage.saveData(Data.INTERFACE_LANG, data);
		}
	}
}
