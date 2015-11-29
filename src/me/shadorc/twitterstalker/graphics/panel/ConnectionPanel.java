package me.shadorc.twitterstalker.graphics.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import org.apache.commons.io.FileUtils;

import me.shadorc.twitterstalker.Main;
import me.shadorc.twitterstalker.graphics.Button;
import me.shadorc.twitterstalker.graphics.Button.ButtonType;
import me.shadorc.twitterstalker.graphics.Button.Size;
import me.shadorc.twitterstalker.graphics.SearchField;
import me.shadorc.twitterstalker.graphics.SearchField.Text;
import me.shadorc.twitterstalker.storage.Storage;
import me.shadorc.twitterstalker.utility.ArchiveFile;
import me.shadorc.twitterstalker.utility.Ressources;
import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

public class ConnectionPanel extends JPanel implements ActionListener, KeyListener {

	private static final long serialVersionUID = 1L;

	private SearchField field1, field2;
	private JButton search, back;
	private Text text;

	private List <Status> statusList;

	public ConnectionPanel(Text text) {
		super(new GridLayout(4, 0));

		Ressources.stop = false;

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

		this.add(new JLabel(new ImageIcon(this.getClass().getResource("/res/Twitter_icon.png"))));

		JPanel fieldPane = new JPanel();
		fieldPane.setOpaque(false);

		if(text == Text.COMPARISON) {
			fieldPane.setLayout(new GridLayout(2, 0, 0, 40));
			fieldPane.setBorder(BorderFactory.createEmptyBorder(25, 50, 0, 50));

			field2 = new SearchField(text);
			field2.addKeyListener(this);
			fieldPane.add(field2);

		} else {
			fieldPane.setLayout(new BorderLayout());
			fieldPane.setBorder(BorderFactory.createEmptyBorder(110, 50, 0, 50));
		}

		field1 = new SearchField(text);
		field1.addKeyListener(this);
		fieldPane.add(field1);

		this.add(fieldPane, BorderLayout.CENTER);

		JPanel searchPanel = new JPanel(new GridLayout(0, 3));
		searchPanel.setOpaque(false);
		searchPanel.add(new JLabel());

		search = new Button(ButtonType.VALIDATE, Size.NORMAL, new int[] {30, 0, 0, 0}, this);
		search.setDisabledIcon(new ImageIcon(this.getClass().getResource("/res/loading.gif")));
		search.setForeground(Color.WHITE);
		search.setFont(Ressources.getFont("RobotoCondensed-LightItalic.ttf", 20));
		search.setHorizontalTextPosition(JButton.CENTER);
		search.setVerticalTextPosition(JButton.CENTER);
		searchPanel.add(search);

		searchPanel.add(new JLabel());
		this.add(searchPanel);

		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setOpaque(false);

		if(text == Text.PIN) {
			bottomPanel.add(new JLabel(), BorderLayout.WEST);
		} else {
			back = new Button(ButtonType.BACK, Size.MEDIUM, new int[] {95, 10, 0, 0}, this);
			bottomPanel.add(back, BorderLayout.WEST);
		}

		JPanel labelsPanel = new JPanel(new GridLayout(6, 0));
		labelsPanel.setOpaque(false);

		Font font = Ressources.getFont("RobotoCondensed-Light.ttf", 22).deriveFont(Font.BOLD);

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

		if(text == Text.ARCHIVE) {
			this.setArchive();
		}
	}

	private void setArchive() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				//Avoid to be able to change username while loading
				field1.setEditable(false);
				for(FocusListener li : field1.getFocusListeners())	field1.removeFocusListener(li);
				for(MouseListener li : field1.getMouseListeners())	field1.removeMouseListener(li);

				File archiveFile = ArchiveFile.getFile(field1);

				if(archiveFile == null) return;

				field1.setForeground(Color.WHITE);
				field1.setText(Storage.tra("loadingTweet"));

				statusList = new ArrayList <Status> ();

				ArrayList <File> jsonFiles = new ArrayList <File> (Arrays.asList(archiveFile.listFiles()));

				search.setEnabled(false);
				for(File file : jsonFiles) {
					search.setText(Ressources.format((jsonFiles.indexOf(file)+1.0)*100.0/jsonFiles.size()) + "%");
					try {
						String rawJSON = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

						//Remove useless first line to get only JSON
						if(rawJSON.indexOf("[") != -1) {
							rawJSON = rawJSON.substring(rawJSON.indexOf("["));
						}

						//Create an array with all JSON objects in the file
						JSONArray json = new JSONArray(rawJSON);

						//Iterate the whole array of JSON objects
						for(int i = 0; i < json.length(); i++) {
							try {
								statusList.add(TwitterObjectFactory.createStatus(json.getJSONObject(i).toString()));
							} catch (TwitterException | JSONException e) {
								field1.setErrorText(Storage.tra(Text.ARCHIVE_ERROR) + " : " + e.getMessage());
								e.printStackTrace();
							}
						}
					} catch (IOException | JSONException e) {
						field1.setErrorText(Storage.tra(Text.ARCHIVE_ERROR) + " : " + e.getMessage());
						search.setText(null);
						e.printStackTrace();
					}
				}

				field1.setText(statusList.get(0).getUser().getScreenName());
				ConnectionPanel.this.valid();
			}
		}).start();
	}

	public void invalidPin() {
		field1.setErrorText(Storage.tra(Text.INVALID_PIN));
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == search) {
			this.valid();
		} else if(event.getSource() == back) {
			Ressources.stop = true;
			Ressources.getFrame().setPanel(new MenuPanel());
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			this.valid();
		}
	}

	private void valid() {
		new Thread(new Runnable() {
			@SuppressWarnings("incomplete-switch")
			@Override
			public void run() {
				if(search.isEnabled() || text == Text.ARCHIVE) {

					search.setEnabled(false);
					search.setText(Storage.tra("loading"));

					field1.getParent().requestFocus();
					field1.setEditable(false);
					if(field2 != null) field2.setEditable(false);

					switch(text) {
						case PIN:
							if(field1.isValidPin()) {
								Main.connect(field1.getText().trim());
							} else {
								field1.setErrorText(Storage.tra(Text.INVALID_PIN));
							}
							break;

						case ARCHIVE:
						case ACCOUNT:
							try {
								AccountPanel statsPanel = new AccountPanel(field1.getUserName(), search, statusList);
								if(Ressources.stop) return;
								Ressources.getFrame().setPanel(statsPanel);
							} catch (TwitterException e) {
								if(e.exceededRateLimitation()) {
									field1.setErrorText(Storage.tra(Text.API_LIMIT) + Storage.tra("unlockIn") + e.getRateLimitStatus().getSecondsUntilReset() + "s.");
								} else {
									field1.setErrorText(e.getMessage());
								}
							}
							break;

						case COMPARISON:
							try {
								ComparisonPanel comparePanel = new ComparisonPanel(field1.getUserName(), field2.getUserName(), search);
								if(Ressources.stop) return;
								Ressources.getFrame().setPanel(comparePanel);
							} catch (TwitterException e) {
								//StatusCode: -1 is a personal error message from TwitterUser
								if(e.getStatusCode() == -1) {
									if(field1.getUserName().equals(e.getCause().getMessage()))	field1.setErrorText(e.getMessage());
									if(field2.getUserName().equals(e.getCause().getMessage()))	field2.setErrorText(e.getMessage());

								} else {
									String message;
									if(e.exceededRateLimitation()) {
										message = Storage.tra(Text.API_LIMIT) + Storage.tra("unlockIn") + e.getRateLimitStatus().getSecondsUntilReset() + "s.";
									} else {
										message = e.getMessage();
									}

									field1.setErrorText(message);
									field2.setErrorText(message);
								}
							}
							break;
					}

					search.setEnabled(true);
					search.setText(null);

					field1.setEditable(true);
					if(field2 != null) field2.setEditable(true);
				}
			}
		}).start();
	}

	@Override
	public void keyReleased(KeyEvent e) { }

	@Override
	public void keyTyped(KeyEvent e) { }
}