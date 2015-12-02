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
import me.shadorc.twitterstalker.graphics.ScrollbarUI;
import me.shadorc.twitterstalker.graphics.ScrollbarUI.Position;
import me.shadorc.twitterstalker.storage.Data.Category;
import me.shadorc.twitterstalker.storage.Data.Options;
import me.shadorc.twitterstalker.storage.Data.UsersEnum;
import me.shadorc.twitterstalker.storage.Data.WordsEnum;
import me.shadorc.twitterstalker.storage.Storage;
import me.shadorc.twitterstalker.utility.Ressources;

public class OptionsPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private static LinkedHashMap <Enum<?>, JCheckBox> checkBoxMap;
	private static HashMap <Options, JComboBox <String>> comboBox;

	private JButton back;

	public static void init() {
		comboBox = new HashMap<>();
		comboBox.put(Options.LIST_LENGHT, createComboBox(Options.LIST_LENGHT, new String[] {"1","2","3","4","5","6","7","8","9","10"}, "3"));
		comboBox.put(Options.LETTERS_PER_WORD_MIN, createComboBox(Options.LETTERS_PER_WORD_MIN, new String[] {"1","2","3","4","5","6","7","8","9","10"}, "1"));
		comboBox.put(Options.TWEETS_TO_ANALYZE, createComboBox(Options.TWEETS_TO_ANALYZE, new String[] {"200","400","600","800","1000","1200","1400","1600","1800","2000","2200","2400","2600","2800","3000"}, "3000"));
		comboBox.put(Options.MENTIONS_TO_ANALYZE, createComboBox(Options.MENTIONS_TO_ANALYZE, new String[] {"200","400","600"}, "600"));
		comboBox.put(Options.INTERFACE_LANG, createComboBox(Options.INTERFACE_LANG, new String[] {"French","English"}, Locale.getDefault().getDisplayLanguage(Locale.ENGLISH)));

		checkBoxMap = new LinkedHashMap <Enum<?>, JCheckBox> ();
		for(Object enumsArray : new Object[] {Category.values(), WordsEnum.values(), UsersEnum.values()}) {
			for(Enum<?> stat : (Enum<?>[]) enumsArray) {
				checkBoxMap.put(stat, new CheckBoxOption(stat));
			}
		}
	}

	private static JComboBox <String> createComboBox(Enum<?> data, String[] list, String def) {
		JComboBox <String> jcb = new JComboBox <String> (list);
		jcb.setName(data.toString());
		jcb.setFocusable(false);
		jcb.setBackground(new Color(179, 229, 252));
		((JScrollPane) ((Container) jcb.getUI().getAccessibleChild(jcb, 0)).getComponent(0)).getVerticalScrollBar().setUI(new ScrollbarUI(Position.VERTICAL));
		String obj = Storage.getData(data);
		jcb.setSelectedItem((obj != null) ? obj : def);

		jcb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				if(event.getStateChange() == ItemEvent.SELECTED) {
					JComboBox <?> jcb = (JComboBox <?>) event.getSource();
					Storage.saveData(jcb.getName(), jcb.getSelectedItem());
					//Update Frame if language is changed
					if(jcb.getName().equals(Options.INTERFACE_LANG.toString())) {
						//Prevent weird IndexOutOfBound Exception
						jcb.hidePopup();
						OptionsPanel.init();
						Ressources.getFrame().setPanel(new OptionsPanel());
					}
				}
			}
		});

		return jcb;
	}

	protected OptionsPanel() {
		super(new BorderLayout());
		this.setBackground(new Color(2, 136, 209));

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

		JPanel options = new JPanel(new GridLayout(6, 1));
		options.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
		options.setOpaque(false);

		for(Options option : Options.values()) {
			options.add(this.createOption(Storage.tra(option), comboBox.get(option)));
		}

		options.add(this.createOption(Storage.tra("statsToShow"), null));

		centerPanel.add(options, BorderLayout.PAGE_START);

		JPanel checkBox = new JPanel(new GridLayout(4, 4, 5, 5));
		checkBox.setBorder(BorderFactory.createEmptyBorder(0, 25, 25, 25));
		checkBox.setOpaque(false);

		for(Enum <?> key : checkBoxMap.keySet()) {
			checkBox.add(checkBoxMap.get(key));
		}

		centerPanel.add(checkBox, BorderLayout.CENTER);

		this.add(centerPanel, BorderLayout.CENTER);

		JPanel buttonPane = new JPanel(new BorderLayout());
		buttonPane.setOpaque(false);
		back = new Button(ButtonType.BACK, Size.MEDIUM, new int[] {0, 10, 14, 0}, this);
		buttonPane.add(back, BorderLayout.WEST);
		this.add(buttonPane, BorderLayout.PAGE_END);
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

	public static int get(Options option) {
		return Integer.parseInt(comboBox.get(option).getSelectedItem().toString());
	}

	public static boolean isSelected(Enum<?> data) {
		return checkBoxMap.get(data).isSelected();
	}

	public static Locale getLocaleLang() {
		String lang = Storage.getData(Options.INTERFACE_LANG);
		if(lang == null) {
			//If language has never been configured, returned default computer's language
			return new Locale(Locale.getDefault().getDisplayCountry(Locale.ENGLISH).substring(0, 2));
		} else {
			return new Locale(lang.substring(0, 2));
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Ressources.getFrame().setPanel(new MenuPanel());		
	}
}
