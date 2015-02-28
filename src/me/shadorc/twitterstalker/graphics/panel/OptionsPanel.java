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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;

import me.shadorc.twitterstalker.graphics.CheckBoxOption;
import me.shadorc.twitterstalker.graphics.Frame;
import me.shadorc.twitterstalker.graphics.ScrollbarUI;
import me.shadorc.twitterstalker.graphics.ScrollbarUI.Position;
import me.shadorc.twitterstalker.graphics.SmallButton;
import me.shadorc.twitterstalker.graphics.Storage;
import me.shadorc.twitterstalker.graphics.Storage.Data;

public class OptionsPanel extends JPanel implements ActionListener, ItemListener {

	private static final long serialVersionUID = 1L;

	private JButton back;

	private static JComboBox <Integer> list_lenght;
	private static JComboBox <Integer> letters_word;
	private static JComboBox <Integer> tweets_number;
	private static JComboBox <Integer> mentions_number;

	private static HashMap <Data, JCheckBox> checkBoxMap;

	public OptionsPanel() {
		super(new BorderLayout());
		this.setBackground(new Color(2, 136, 209));

		JPanel top = new JPanel(new BorderLayout());
		top.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(183,183,183)), BorderFactory.createEmptyBorder(25, 0, 25, 0)));
		top.setBackground(new Color(3,169,244));

		JLabel title = new JLabel("Options", JLabel.CENTER);
		title.setFont(Frame.getFont("RobotoCondensed-Regular.ttf", 72));
		title.setForeground(new Color(33,33,33));
		top.add(title, BorderLayout.PAGE_START);

		this.add(top, BorderLayout.PAGE_START);

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setOpaque(false);

		JPanel options = new JPanel(new GridLayout(5, 0));
		options.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
		options.setOpaque(false);

		list_lenght = this.createComboBox(Data.LIST_LENGHT, new Integer[] {1,2,3,4,5,6,7,8,9,10}, 3);
		letters_word = this.createComboBox(Data.LETTERS_PER_WORD, new Integer[] {1,2,3,4,5,6,7,8,9,10}, 1);
		//Limit : 3200 Tweets
		tweets_number = this.createComboBox(Data.TWEETS_NUMBER, new Integer[] {200,400,600,800,1000,1200,1400,1600,1800,2000,2200,2400,2600,2800,3000}, 3000);
		//Limit : 800 Mentions
		mentions_number = this.createComboBox(Data.MENTIONS_NUMBER, new Integer[] {200,400,600}, 600);

		options.add(this.createOption("Nombre de lettres par mot minimum : ", letters_word));
		options.add(this.createOption("Nombre de mentions : ", mentions_number));
		options.add(this.createOption("Nombre de tweets : ", tweets_number));
		options.add(this.createOption("Taille des listes : ", list_lenght));
		//Align the label with others options
		JPanel pane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pane.setOpaque(false);
		JLabel label = new JLabel("Statistiques à effectuer :");
		label.setFont(Frame.getFont("SEGOEUI.TTF", 30));
		pane.add(label);
		options.add(pane);

		centerPanel.add(options, BorderLayout.PAGE_START);

		JPanel checkBox = new JPanel(new GridLayout(4, 4, 5, 5));
		checkBox.setBorder(BorderFactory.createEmptyBorder(0, 25, 25, 25));
		checkBox.setOpaque(false);

		checkBoxMap = new HashMap <> ();
		checkBoxMap.put(Data.SOURCE, new CheckBoxOption("Applis", Data.SOURCE));
		checkBoxMap.put(Data.HASHTAG, new CheckBoxOption("Hashtag", Data.HASHTAG));
		checkBoxMap.put(Data.WORDS, new CheckBoxOption("Mots", Data.HASHTAG));
		checkBoxMap.put(Data.DAYS, new CheckBoxOption("Jours", Data.DAYS));
		checkBoxMap.put(Data.HOURS, new CheckBoxOption("Heures", Data.HOURS));
		checkBoxMap.put(Data.TIMELINE, new CheckBoxOption("Timeline", Data.TIMELINE));
		checkBoxMap.put(Data.TWEETS, new CheckBoxOption("Tweets", Data.TWEETS));
		checkBoxMap.put(Data.POPULARE, new CheckBoxOption("Populaire", Data.POPULARE));
		checkBoxMap.put(Data.MENTIONS_SENT, new CheckBoxOption("User mentionnés", Data.MENTIONS_SENT));
		checkBoxMap.put(Data.MENTIONS_RECEIVED, new CheckBoxOption("User mentionnant", Data.MENTIONS_RECEIVED));
		checkBoxMap.put(Data.REPUTE, new CheckBoxOption("Renommée", Data.REPUTE));

		for(Data data : checkBoxMap.keySet()) {
			checkBox.add(checkBoxMap.get(data));
		}

		for(int i = 0; i < 4; i++) {
			checkBox.add(new JLabel());
		}

		centerPanel.add(checkBox, BorderLayout.CENTER);

		this.add(centerPanel, BorderLayout.CENTER);

		JPanel button = new JPanel(new BorderLayout());
		button.setOpaque(false);
		back = new SmallButton("Retour", BorderFactory.createEmptyBorder(0, 10, 10, 0));
		back.addActionListener(this);
		button.add(back, BorderLayout.WEST);
		this.add(button, BorderLayout.PAGE_END);
	}

	private JComboBox <Integer> createComboBox(Data data, Integer[] list, int def) {
		JComboBox <Integer> jcb = new JComboBox <Integer> (list);
		jcb.addItemListener(this);
		jcb.setFocusable(false);
		((JScrollPane) ((Container) jcb.getUI().getAccessibleChild(jcb, 0)).getComponent(0)).getVerticalScrollBar().setUI(new ScrollbarUI(Position.VERTICAL));
		jcb.setBackground(new Color(179, 229, 252));
		Object obj = Storage.getData(data);
		if(obj != null) {
			jcb.setSelectedItem(Integer.parseInt(obj.toString()));
		} else {
			jcb.setSelectedItem(def);
		}
		return jcb;
	}

	private JPanel createOption(String text, JComboBox <Integer> jcb) {
		JPanel pane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pane.setOpaque(false);

		JLabel label = new JLabel(text);
		label.setFont(Frame.getFont("SEGOEUI.TTF", 30));
		pane.add(label);
		pane.add(jcb);

		return pane;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Frame.setJPanel(new MenuPanel());		
	}

	public static int getMaxListLenght() {
		return (int) list_lenght.getSelectedItem();
	}

	public static int getMaxTweetsNumber() {
		return (int) tweets_number.getSelectedItem();
	}

	public static int getMaxMentionsNumber() {
		return (int) mentions_number.getSelectedItem();
	}

	public static int getMinLettersWord() {
		return (int) letters_word.getSelectedItem();
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
		}
	}
}
