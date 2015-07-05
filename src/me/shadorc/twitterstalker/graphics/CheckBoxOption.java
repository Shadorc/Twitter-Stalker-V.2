package me.shadorc.twitterstalker.graphics;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;

import me.shadorc.twitterstalker.graphics.Storage.Data;

public class CheckBoxOption extends JCheckBox implements ItemListener {

	private static final long serialVersionUID = 1L;
	private Data data;

	public CheckBoxOption(String name, Data data) {
		//If user has never changed this option, set true by default
		super(name, Storage.getData(data) == null ? true : Boolean.valueOf(Storage.getData(data)));

		this.data = data;

		this.setOpaque(false);
		this.setFocusable(false);
		this.addItemListener(this);
		this.setFont(Frame.getFont("RobotoCondensed-Light.ttf", 30));
	}

	@Override
	public void itemStateChanged(ItemEvent event) {
		Storage.saveData(data, Boolean.toString(event.getStateChange() == ItemEvent.SELECTED));
	}
}
