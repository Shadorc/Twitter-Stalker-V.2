package me.shadorc.twitterstalker.graphics.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import me.shadorc.twitterstalker.graphics.Frame;
import me.shadorc.twitterstalker.graphics.Button;
import me.shadorc.twitterstalker.graphics.Storage;
import me.shadorc.twitterstalker.graphics.TextField;
import me.shadorc.twitterstalker.graphics.TextField.Text;
import me.shadorc.twitterstalker.statistics.Stats;
import twitter4j.TwitterException;

public class ConnectionPanel extends JPanel implements ActionListener, KeyListener {

	private static final long serialVersionUID = 1L;

	private TextField field1, field2;
	private JButton search, back;
	private String text;

	private StatisticsPanel statsPanel;
	private ComparisonPanel comparePanel;

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

		if(text.equals(Storage.tra(Text.USERNAME)) || text.equals(Storage.tra(Text.PIN))) {
			fieldPane.setLayout(new BorderLayout());
			fieldPane.setBorder(BorderFactory.createEmptyBorder(110, 50, 0, 50));

		} else if(text.equals(Storage.tra(Text.COMPARISON))) {
			fieldPane.setLayout(new GridLayout(2, 0, 0, 40));
			fieldPane.setBorder(BorderFactory.createEmptyBorder(25, 50, 0, 50));

			field2 = new TextField(text, font);
			field2.addKeyListener(this);
			fieldPane.add(field2);
		}

		field1 = new TextField(text, font);
		field1.addKeyListener(this);
		fieldPane.add(field1);

		this.add(fieldPane, BorderLayout.CENTER);

		JPanel searchPanel = new JPanel(new GridLayout(0, 3));
		searchPanel.setOpaque(false);
		searchPanel.add(new JLabel());

		search = new Button("Valider", new int[] {30, 0, 0, 0}, false, this);
		search.setDisabledIcon(new ImageIcon(this.getClass().getResource("/res/loading.gif")));
		search.setForeground(Color.WHITE);
		search.setFont(Frame.getFont("RobotoCondensed-LightItalic.ttf", 20));
		search.setHorizontalTextPosition(JButton.CENTER);
		search.setVerticalTextPosition(JButton.CENTER);
		searchPanel.add(search);

		searchPanel.add(new JLabel());
		this.add(searchPanel);

		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setOpaque(false);

		if(text.equals(Storage.tra(Text.USERNAME)) || text.equals(Storage.tra(Text.COMPARISON))) {
			back = new Button("Retour", new int[] {95, 10, 0, 0}, true, this);
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

	public void invalidPin() {
		field1.error(Storage.tra(Text.INVALID_PIN));
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == search) {
			this.valid();
		} else if(event.getSource() == back) {
			Stats.stop();
			Frame.setPanel(new MenuPanel());
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			this.valid();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) { }

	@Override
	public void keyTyped(KeyEvent e) { }

	private void valid() {
		if(search.isEnabled()) {
			if(text.equals(Storage.tra(Text.PIN))) {
				//If field contains only numbers and the pin is more than 6 characters
				if(field1.isValidPin()) {
					Frame.connect(field1.getText());
				} else {
					field1.error(Storage.tra(Text.INVALID_PIN));
				}

			} else if(text.equals(Storage.tra(Text.USERNAME))) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							statsPanel = new StatisticsPanel(field1.getText().replaceAll("@", ""), search);
							if(Stats.stop == true) return;
							Frame.setPanel(statsPanel);
						} catch (TwitterException e) {
							e.printStackTrace();

							if(e.getErrorCode() == 88) {
								field1.error(Storage.tra(Text.API_LIMIT) + Storage.tra("déblocage dans ") + e.getRateLimitStatus().getSecondsUntilReset() + "s.");
							} else if(e.getStatusCode() == 600) {
								field1.error(Storage.tra(Text.NO_TWEET));
							} else if(e.getStatusCode() == 604) {
								field1.error(Storage.tra(Text.INVALID_USER));
							} else if(e.getStatusCode() == 401) {
								field1.error(Storage.tra(Text.PRIVATE));
							} else {
								field1.error(Storage.tra(Text.ERROR) + " " + e.getMessage());
							}
						}
					}
				}).start();

			} else if(text.equals(Storage.tra(Text.COMPARISON))) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							comparePanel = new ComparisonPanel(field1.getUserName(), field2.getUserName(), search);
							if(Stats.stop == true) return;
							Frame.setPanel(comparePanel);
						} catch (TwitterException e) {
							e.printStackTrace();

							if(e.getErrorCode() == 88) {
								field1.error(Storage.tra(Text.API_LIMIT) + Storage.tra("déblocage dans ") + e.getRateLimitStatus().getSecondsUntilReset() + "s.");
								field2.error(Storage.tra(Text.API_LIMIT) + Storage.tra("déblocage dans ") + e.getRateLimitStatus().getSecondsUntilReset() + "s.");
							} else if(e.getStatusCode() == 600) {
								String message = e.getCause().getMessage();
								if(field1.getText().equals(message)) {
									field1.error(Storage.tra(Text.NO_TWEET));
								} else {
									field2.error(Storage.tra(Text.NO_TWEET));
								}
							} else if(e.getStatusCode() == 604) {
								String message = e.getCause().getMessage();
								if(message.contains("1")) {
									field1.error(Storage.tra(Text.INVALID_USER));
								} else {
									field2.error(Storage.tra(Text.INVALID_USER));
								}
							} else if(e.getStatusCode() == 401) {
								String message = e.getCause().getMessage();
								if(field1.getText().equals(message)) {
									field1.error(Storage.tra(Text.PRIVATE));
								} else {
									field2.error(Storage.tra(Text.PRIVATE));
								}
							} else {
								field1.error(Storage.tra(Text.ERROR + e.getMessage()));
								field2.error(Storage.tra(Text.ERROR + e.getMessage()));
							}
						}
					}
				}).start();
			}
		}
	}
}