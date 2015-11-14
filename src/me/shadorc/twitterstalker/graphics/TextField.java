package me.shadorc.twitterstalker.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.text.DefaultEditorKit;

import me.shadorc.twitterstalker.storage.Storage;

public class TextField extends JTextField {

	private static final long serialVersionUID = 1L;

	public interface Text {
		String PIN = "enterPin";
		String USERNAME = "enterAccount";
		String COMPARISON = "enterComparison";
		String ARCHIVE = "enterArchive";
		String INVALID_PIN = "invalidPin";
		String INVALID_USER = "invalidUser";
		String INVALID_ARCHIVE = "invalidArchive";
		String API_LIMIT = "apiLimit";
		String NO_TWEET = "userNeverTweet";
		String PRIVATE = "privateAccount";
		String ARCHIVE_ERROR = "archiveError";
		String ERROR = "unexpectedError";
	}

	public TextField(String text, Font font) {
		super(text);

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent event) {
				//Open right click drop-down menu
				if(event.isPopupTrigger()) {
					JPopupMenu menu = new JPopupMenu();
					menu.setBackground(Color.WHITE);

					JMenuItem paste = new JMenuItem(new DefaultEditorKit.PasteAction());
					paste.setText(Storage.tra("paste"));
					paste.setBackground(Color.WHITE);
					paste.setFont(Ressources.getFont("RobotoCondensed-Regular.ttf", 15));
					menu.add(paste);

					JMenuItem delete = new JMenuItem(new AbstractAction(Storage.tra("delete")) {
						private static final long serialVersionUID = 1L;

						public void actionPerformed(ActionEvent ae) {
							((JTextField) event.getSource()).setText("");
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
					jtf.setText(text);
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
					} catch (IllegalArgumentException | IllegalAccessException ignored) {	}
				}
			}
		});

		this.setHorizontalAlignment(JTextField.CENTER);
		this.setFont(font);
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

	public void error(String error) {
		this.setForeground(Color.RED);
		this.setText(error);
		//Unfocus the JTextField
		this.getParent().requestFocus();
	}
}
