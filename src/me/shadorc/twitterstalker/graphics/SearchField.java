package me.shadorc.twitterstalker.graphics;

import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.lang.reflect.Field;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import me.shadorc.twitterstalker.storage.Storage;
import me.shadorc.twitterstalker.utility.Ressources;

public class SearchField extends JTextField {

	private static final long serialVersionUID = 1L;

	public enum Text {
		PIN,
		ACCOUNT,
		COMPARISON,
		ARCHIVE,

		INVALID_PIN,
		INVALID_USER,
		INVALID_ARCHIVE,
		API_LIMIT,
		NO_TWEET,
		PRIVATE,
		ARCHIVE_ERROR
	}

	public SearchField(Text text) {
		super(Storage.tra(text));

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent event) {

				//Open right click drop-down menu
				if(event.isPopupTrigger()) {
					JPopupMenu menu = new JPopupMenu();
					menu.setBackground(Color.WHITE);

					JMenuItem paste = new JMenuItem(new AbstractAction(Storage.tra("paste")) {
						private static final long serialVersionUID = 1L;

						public void actionPerformed(ActionEvent ae) {
							try {
								String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
								((JTextField) event.getSource()).setText(data);
							} catch (HeadlessException | UnsupportedFlavorException | IOException ignore) { } 
						}
					});
					paste.setBackground(Color.WHITE);
					paste.setFont(Ressources.getFont("RobotoCondensed-Regular.ttf", 15));
					menu.add(paste);

					JMenuItem delete = new JMenuItem(new AbstractAction(Storage.tra("delete")) {
						private static final long serialVersionUID = 1L;

						public void actionPerformed(ActionEvent ae) {
							((JTextField) event.getSource()).setText("");
							((JTextField) event.getSource()).setForeground(Color.WHITE);
							((JTextField) event.getSource()).requestFocus();
						}
					});
					delete.setBackground(Color.WHITE);
					delete.setFont(Ressources.getFont("RobotoCondensed-Regular.ttf", 15));
					menu.add(delete);

					menu.show(event.getComponent(), event.getX(), event.getY());
				}
			}
		});

		this.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent event) {
				JTextField jtf = (JTextField) event.getSource();
				if(jtf.getText().isEmpty()) {
					jtf.setText(Storage.tra(text));
				}
			}

			@Override
			public void focusGained(FocusEvent event) {
				JTextField jtf = (JTextField) event.getSource();
				String text = jtf.getText();

				//field are String from Text interface, error is throw when the object isn't accessible so don't care about it
				for(Field field : Text.class.getFields()) {
					try {
						String defaultText = field.get(field.getName()).toString();
						if(Storage.tra(defaultText).equals(text)) {
							jtf.setForeground(Color.WHITE);
							jtf.setText("");
						}
					} catch (IllegalArgumentException | IllegalAccessException ignore) {}
				}
			}
		});

		this.setHorizontalAlignment(JTextField.CENTER);
		this.setFont(Ressources.getFont("RobotoCondensed-LightItalic.ttf", 48));
		this.setForeground(Color.WHITE);
		this.setBackground(new Color(179, 229, 252));
		this.setBorder(BorderFactory.createLineBorder(new Color(2,113,174), 3));
	}

	public String getUserName() {
		return this.getText().replaceAll("@", "").trim();
	}

	public boolean isValidPin() {
		return this.getText().trim().matches("[0-9]+") && this.getText().trim().length() >= 7;
	}

	public void setErrorText(String error) {
		this.setForeground(Color.RED);
		this.setText(error);
		//Unfocus the JTextField
		this.getParent().requestFocus();
	}
}
