package me.shadorc.twitterstalker.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.text.DefaultEditorKit;

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

		ArrayList <String> MESSAGES = new ArrayList <String> (Arrays.asList(
				new String[] {PIN, USERNAME, COMPARISON, ARCHIVE, INVALID_PIN, INVALID_USER, INVALID_ARCHIVE, API_LIMIT, NO_TWEET, PRIVATE, ARCHIVE_ERROR, ERROR}));
	}

	public TextField(final String text, Font font) {
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
					paste.setFont(Frame.getFont("RobotoCondensed-Regular.ttf", 15));
					menu.add(paste);

					JMenuItem delete = new JMenuItem(new AbstractAction(Storage.tra("delete")) {
						private static final long serialVersionUID = 1L;

						public void actionPerformed(ActionEvent ae) {
							((JTextField) event.getSource()).setText("");
							((JTextField) event.getSource()).requestFocus();
						}
					});
					delete.setBackground(Color.WHITE);
					delete.setFont(Frame.getFont("RobotoCondensed-Regular.ttf", 15));
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
				//If the text is a default message, delete it.
				for(String s : Text.MESSAGES) {
					if(Storage.tra(s).equals(text)) {
						jtf.setForeground(Color.WHITE);
						jtf.setText("");
					}
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
		return this.getText().replaceAll("@", "");
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
