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

public class TextField extends JTextField implements FocusListener {

	private static final long serialVersionUID = 1L;

	private String text;

	public interface Text {
		String PIN = "Veuillez entrer le code PIN";
		String USERNAME = "Veuillez entrer l'@ du compte à analyser";
		String COMPARISON = "Veuillez entrez l'@ d'un compte à analyser";
		String INVALID_PIN = "Merci d'entrer un code PIN valide";
		String INVALID_USER = "Merci d'entrer un utilisateur valide";
		String API_LIMIT = "Limite de l'API atteinte : ";
		String NO_TWEET = "L'utilisateur n'a jamais tweeté";
		String PRIVATE = "Le compte est privé";
		String ERROR = "Erreur inattendue : ";

		ArrayList <String> MESSAGES = new ArrayList <String> (Arrays.asList(
				new String[] {PIN, USERNAME, COMPARISON, INVALID_PIN, INVALID_USER, API_LIMIT, NO_TWEET, PRIVATE, ERROR}));
	}

	public TextField(String text, Font font) {
		super(text);

		this.text = text;

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent event) {
				if(event.isPopupTrigger()) {
					JPopupMenu menu = new JPopupMenu();
					menu.setBackground(Color.WHITE);

					JMenuItem paste = new JMenuItem(new DefaultEditorKit.PasteAction());
					paste.setText(Storage.tra("Coller"));
					paste.setBackground(Color.WHITE);
					paste.setFont(Frame.getFont("RobotoCondensed-Regular.ttf", 15));
					menu.add(paste);

					JMenuItem delete = new JMenuItem(new AbstractAction(Storage.tra("Supprimer")) {
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

			@Override
			public void mousePressed(MouseEvent event) {
				JTextField jtf = (JTextField) event.getSource();
				String text = jtf.getText();
				for(String s : Text.MESSAGES) {
					if(Storage.tra(s).equals(text)) {
						jtf.setForeground(Color.WHITE);
						jtf.setText("");
					}
				}
			}
		});
		this.addFocusListener(this);
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
		return this.getText().matches("[0-9]+") && this.getText().length() >= 7;
	}

	public void error(String error) {
		this.setForeground(Color.RED);
		this.setText(error);
	}

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
		for(String s : Text.MESSAGES) {
			if(Storage.tra(s).equals(text)) {
				jtf.setForeground(Color.WHITE);
				jtf.setText("");
			}
		}
	}
}
