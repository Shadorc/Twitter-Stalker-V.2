package me.shadorc.twitterstalker.graphics;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;

import me.shadorc.twitterstalker.storage.Storage;
import me.shadorc.twitterstalker.utility.Ressources;

public class CheckBoxOption extends JCheckBox implements ItemListener {

	private static final long serialVersionUID = 1L;
	private Enum<?> data;

	public CheckBoxOption(Enum<?> data) {
		//If user has never changed this option, set true by default
		//Regex is isued to remove html tag in FIRST_MENTION String
		super(Storage.tra(data).replaceAll("\\(.*?\\)", ""), (Storage.getData(data) == null) ? true : Boolean.valueOf(Storage.getData(data)));
		this.data = data;

		this.setOpaque(false);
		this.setFocusable(false);
		this.addItemListener(this);
		this.setFont(Ressources.getFont("RobotoCondensed-Light.ttf", 30));
	}

	@Override
	public void itemStateChanged(ItemEvent event) {
		Storage.saveData(data, (event.getStateChange() == ItemEvent.SELECTED));
	}
}
