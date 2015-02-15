package me.shadorc.twitterstalker.graphics.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import me.shadorc.twitterstalker.graphics.Frame;
import me.shadorc.twitterstalker.graphics.SmallButton;
import me.shadorc.twitterstalker.statistics.Stats;
import twitter4j.TwitterException;

public class ConnectionPanel extends JPanel implements FocusListener, ActionListener, KeyListener {

	private static final long serialVersionUID = 1L;

	private JTextField field1, field2;
	private JButton search, back;
	private String text;

	private StatisticsPanel statsPanel;
	private ComparisonPanel comparePanel;

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

		String[] MESSAGES = new String[] {PIN, USERNAME, COMPARISON, INVALID_PIN, INVALID_USER, API_LIMIT, NO_TWEET, ERROR, PRIVATE};
	}

	public ConnectionPanel(String text) {
		super(new GridLayout(4, 0));

		//This permit the JTextField aren't focused by default to be able to see the text, and we can unfocus them by clicking outside
		this.setFocusable(true);
		this.requestFocus();
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ConnectionPanel.this.requestFocus();
			}
		});

		//Set the color of all disabled buttons to White, this allows to have white text when searching stats
		UIManager.put("Button.disabledText", new ColorUIResource(Color.WHITE));

		this.text = text;
		this.setBackground(new Color(2, 136, 209));

		this.add(new JLabel(new ImageIcon(this.getClass().getResource("/res/Icone.png"))));

		Font font = Frame.getFont("RobotoCondensed-LightItalic.ttf", 48);

		JPanel fieldPane = new JPanel();
		fieldPane.setOpaque(false);

		if(text.equals(Text.USERNAME) || text.equals(Text.PIN)) {
			fieldPane.setLayout(new BorderLayout());
			fieldPane.setBorder(BorderFactory.createEmptyBorder(110, 50, 0, 50));

		} else if(text.equals(Text.COMPARISON)) {
			fieldPane.setLayout(new GridLayout(2, 0, 0, 40));
			fieldPane.setBorder(BorderFactory.createEmptyBorder(25, 50, 0, 50));

			field2 = new JTextField(text);
			field2.addFocusListener(this);
			field2.addKeyListener(this);
			field2.setHorizontalAlignment(JTextField.CENTER);
			field2.setFont(font);
			field2.setForeground(Color.WHITE);
			field2.setBackground(new Color(179, 229, 252));
			field2.setBorder(BorderFactory.createLineBorder(new Color(2,113,174), 3));
			fieldPane.add(field2);
		}

		field1 = new JTextField(text);
		field1.addFocusListener(this);
		field1.addKeyListener(this);
		field1.setHorizontalAlignment(JTextField.CENTER);
		field1.setFont(font);
		field1.setForeground(Color.WHITE);
		field1.setBackground(new Color(179, 229, 252));
		field1.setBorder(BorderFactory.createLineBorder(new Color(2,113,174), 3));
		fieldPane.add(field1);

		this.add(fieldPane, BorderLayout.CENTER);

		JPanel searchPanel = new JPanel(new GridLayout(0, 3, 425, 0));
		searchPanel.setOpaque(false);
		searchPanel.add(new JLabel());

		search = new JButton(new ImageIcon(this.getClass().getResource("/res/Bouton Valider.png")));
		search.setContentAreaFilled(false);
		search.setPressedIcon(new ImageIcon(this.getClass().getResource("/res/Bouton Valider2.png")));
		search.setDisabledIcon(new ImageIcon(this.getClass().getResource("/res/loading.gif")));
		search.addActionListener(this);
		search.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				JButton bu = (JButton) e.getSource();
				bu.setIcon(new ImageIcon(this.getClass().getResource("/res/Bouton Valider3.png")));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				JButton bu = (JButton) e.getSource();
				bu.setIcon(new ImageIcon(this.getClass().getResource("/res/Bouton Valider.png")));
			}
		});
		search.setFocusable(false);
		search.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
		search.setBackground(null);
		search.setForeground(Color.WHITE);
		search.setFont(Frame.getFont("RobotoCondensed-LightItalic.ttf", 20));
		search.setHorizontalTextPosition(JButton.CENTER);
		search.setVerticalTextPosition(JButton.CENTER);
		searchPanel.add(search);

		searchPanel.add(new JLabel());
		this.add(searchPanel);

		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setOpaque(false);

		if(text.equals(Text.USERNAME) || text.equals(Text.COMPARISON)) {
			back = new SmallButton("Retour", BorderFactory.createEmptyBorder(95, 10, 0, 0));
			back.addActionListener(this);
			bottomPanel.add(back, BorderLayout.WEST);
		} else {
			bottomPanel.add(new JLabel(), BorderLayout.WEST);
		}

		JPanel labelsPanel = new JPanel(new GridLayout(6, 0));
		labelsPanel.setOpaque(false);

		font = Frame.getFont("RobotoCondensed-Light.ttf", 22).deriveFont(Font.BOLD);

		JLabel design = new JLabel("Design : @Dasporal", JLabel.RIGHT);
		design.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));		
		design.setForeground(Color.WHITE);
		design.setFont(font);

		JLabel programming = new JLabel("Programming : @Shad0rc", JLabel.RIGHT);
		programming.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 9));
		programming.setForeground(Color.WHITE);
		programming.setFont(font);

		labelsPanel.add(new JLabel());
		labelsPanel.add(new JLabel());
		labelsPanel.add(new JLabel());
		labelsPanel.add(new JLabel());
		labelsPanel.add(design);
		labelsPanel.add(programming);
		bottomPanel.add(labelsPanel, BorderLayout.EAST);
		this.add(bottomPanel);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == search) {
			this.valid();
		} else if(event.getSource() == back) {
			Stats.stop();
			Frame.setJPanel(new MenuPanel());
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			this.valid();
		}
	}

	private void valid() {
		if(text.equals(Text.PIN)) {
			//If field contains only numbers and the pin is more than 6 characters
			if(field1.getText().matches("[0-9]+") && field1.getText().length() >= 7) {
				Frame.connect(field1.getText());
			} else {
				this.setError(Text.INVALID_PIN);
			}

		} else if(text.equals(Text.USERNAME)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						statsPanel = new StatisticsPanel(field1.getText().replaceAll("@", ""), search);
						if(Stats.stop == true) return;
						Frame.setJPanel(statsPanel);
					} catch (TwitterException e) {
						field1.setForeground(Color.RED);
						if(e.getErrorCode() == 88) {
							field1.setText(Text.API_LIMIT + " Déblocage dans " + e.getRateLimitStatus().getSecondsUntilReset() + "s.");
						} else if(e.getStatusCode() == 600) {
							field1.setText(Text.NO_TWEET);
						} else if(e.getStatusCode() == 604) {
							field1.setText(Text.INVALID_USER);
						} else if(e.getStatusCode() == 401) {
							field1.setText(Text.PRIVATE);
						} else {
							field1.setText(Text.ERROR + " " + e.getMessage());
						}
					}
				}
			}).start();

		} else if(text.equals(Text.COMPARISON)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						comparePanel = new ComparisonPanel(field1.getText().replaceAll("@", ""), field2.getText().replaceAll("@",  ""), search);
						if(Stats.stop == true) return;
						Frame.setJPanel(comparePanel);
					} catch (TwitterException e) {
						String message = e.getCause().getMessage();

						if(e.getErrorCode() == 88) {
							field1.setForeground(Color.RED);
							field2.setForeground(Color.RED);
							field1.setText(Text.API_LIMIT + "déblocage dans " + e.getRateLimitStatus().getSecondsUntilReset() + "s.");
							field2.setText(Text.API_LIMIT + "déblocage dans " + e.getRateLimitStatus().getSecondsUntilReset() + "s.");
						} else if(e.getStatusCode() == 600) {
							if(field1.getText().equals(message)) {
								field1.setForeground(Color.RED);
								field1.setText(Text.NO_TWEET);
							} else {
								field1.setForeground(Color.RED);
								field1.setText(Text.NO_TWEET);
							}
						} else if(e.getStatusCode() == 604) {
							if(message.contains("1")) {
								field1.setForeground(Color.RED);
								field1.setText(Text.INVALID_USER);
							} else {
								field2.setForeground(Color.RED);
								field2.setText(Text.INVALID_USER);
							}
						} else if(e.getStatusCode() == 401) {
							if(field1.getText().equals(message)) {
								field1.setForeground(Color.RED);
								field1.setText(Text.PRIVATE);
							} else {
								field2.setForeground(Color.RED);
								field2.setText(Text.PRIVATE);
							}
						} else {
							field1.setForeground(Color.RED);
							field2.setForeground(Color.RED);
							field1.setText(Text.ERROR + e.getMessage());
							field2.setText(Text.ERROR + e.getMessage());
						}
					}
				}
			}).start();
		}
	}

	public void setError(String text) {
		field1.setForeground(Color.RED);
		field1.setText(text);
	}

	@Override
	public void focusLost(FocusEvent e) {
		JTextField jtf = (JTextField) e.getSource();
		if(jtf.getText().isEmpty()) {
			jtf.setText(text);
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		JTextField jtf = (JTextField) e.getSource();
		String text = jtf.getText();
		if(Arrays.asList(Text.MESSAGES).contains(text)) {
			jtf.setForeground(Color.WHITE);
			jtf.setText("");
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {	}

	@Override
	public void keyTyped(KeyEvent e) {	}
}
