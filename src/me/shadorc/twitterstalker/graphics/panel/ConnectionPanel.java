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
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.ColorUIResource;

import me.shadorc.twitterstalker.graphics.Button;
import me.shadorc.twitterstalker.graphics.Button.Size;
import me.shadorc.twitterstalker.graphics.Frame;
import me.shadorc.twitterstalker.graphics.Storage;
import me.shadorc.twitterstalker.graphics.TextField;
import me.shadorc.twitterstalker.graphics.TextField.Text;
import me.shadorc.twitterstalker.statistics.Stats;
import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

public class ConnectionPanel extends JPanel implements ActionListener, KeyListener {

	private static final long serialVersionUID = 1L;

	private TextField field1, field2;
	private JButton search, back;
	private String text;

	private File file;
	private List <Status> statusList;

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

		if(text.equals(Storage.tra(Text.USERNAME)) || text.equals(Storage.tra(Text.PIN)) || text.equals(Storage.tra(Text.ARCHIVE))) {
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

		search = new Button("Valider", new int[] {30, 0, 0, 0}, Size.NORMAL, this);
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

		if(text.equals(Storage.tra(Text.USERNAME)) || text.equals(Storage.tra(Text.COMPARISON)) || text.equals(Storage.tra(Text.ARCHIVE))) {
			back = new Button("Retour", new int[] {95, 10, 0, 0}, Size.MEDIUM, this);
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

		if(text.equals(Storage.tra(Text.ARCHIVE))) {
			this.setArchive();
		}
	}

	private void setArchive() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(file == null || !file.exists()) {
					//Change UIManager look to look like the operating system one, this is for the JFileChooser
					try {
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
						e.printStackTrace();
					}

					JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.home"), "Desktop"));
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

					int choice = chooser.showOpenDialog(null);

					//Reset UIManager look to avoid changing buttons, drop-downs menus...
					try {
						UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
					} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
						e.printStackTrace();
					}

					if(choice == JFileChooser.APPROVE_OPTION) {
						file = new File(chooser.getSelectedFile().getPath() + "/data/js/tweets");
					} else if(choice == JFileChooser.CANCEL_OPTION) {
						Frame.setPanel(new MenuPanel());
						return;
					}

					if(!file.exists()) {
						field1.error(Text.INVALID_ARCHIVE);
					}
				}

				field1.setForeground(Color.WHITE);
				field1.setText(Storage.tra("Chargement des tweets..."));

				statusList = new ArrayList <Status> ();

				ArrayList <File> jsonFiles = new ArrayList <File> (Arrays.asList(file.listFiles()));

				search.setEnabled(false);
				for(File f : jsonFiles) {
					search.setText(new DecimalFormat("#.#").format((jsonFiles.indexOf(f)+1.0)*100.0/jsonFiles.size()) + "%");
					try {
						//Read all file
						String file = new String(Files.readAllBytes(Paths.get(f.getPath())), StandardCharsets.UTF_8);
						//Remove useless first line to get only JSON
						file = file.substring(file.indexOf("["));

						//Create an array with all SJON object in the file
						JSONArray json = new JSONArray(file);

						//Iterate the whole array of JSON objects
						for(int i = 0; i < json.length(); i++) {
							try {
								statusList.add(TwitterObjectFactory.createStatus(json.getJSONObject(i).toString()));
							} catch (TwitterException | JSONException e) {
								field1.error(Text.ARCHIVE_ERROR);
								e.printStackTrace();
							}
						}
					} catch (IOException | JSONException e) {
						field1.error(Text.ARCHIVE_ERROR);
						search.setText(null);
						e.printStackTrace();
					}
				}

				field1.setText(statusList.get(0).getUser().getScreenName());
				valid();
			}
		}).start();
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
		if(search.isEnabled() || text.equals(Storage.tra(Text.ARCHIVE))) {
			if(text.equals(Storage.tra(Text.PIN))) {
				//If field contains only numbers and the pin is more than 6 characters
				if(field1.isValidPin()) {
					Frame.connect(field1.getText());
				} else {
					field1.error(Storage.tra(Text.INVALID_PIN));
				}

			} else if(text.equals(Storage.tra(Text.USERNAME)) || text.equals(Storage.tra(Text.ARCHIVE))) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							statsPanel = new StatisticsPanel(field1.getUserName(), search, statusList);
							if(Stats.stop == true) return;
							Frame.setPanel(statsPanel);
						} catch (TwitterException e) {
							e.printStackTrace();

							String error;
							if(e.getErrorCode() == 88) {
								error = Storage.tra(Text.API_LIMIT) + Storage.tra("déblocage dans ") + e.getRateLimitStatus().getSecondsUntilReset() + "s.";
							} else {
								switch(e.getStatusCode()) {
									case 401:
										error = Storage.tra(Text.PRIVATE);
										break;
									case 600:
										error = Storage.tra(Text.NO_TWEET);
										break;
									case 604:
										error = Storage.tra(Text.INVALID_USER);
										break;
									default:
										error = Storage.tra(Text.ERROR) + " " + e.getMessage();
										break;
								}
							}
							field1.error(error);
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

							String message = e.getCause().getMessage();
							String globalEr = null;
							String error = null;

							if(e.getErrorCode() == 88) {
								globalEr = Storage.tra(Text.API_LIMIT) + Storage.tra("déblocage dans ") + e.getRateLimitStatus().getSecondsUntilReset() + "s.";
							} else {
								switch(e.getStatusCode()) {
									case 401:
										error = Storage.tra(Text.PRIVATE);
										break;
									case 600:
										error = Storage.tra(Text.NO_TWEET);
										break;
									case 604:
										error = Storage.tra(Text.INVALID_USER);
										break;
									default:
										globalEr = Storage.tra(Text.ERROR) + e.getMessage();
										break;
								}
							}

							if(globalEr != null) {
								field1.error(globalEr);
								field2.error(globalEr);
							} else if(field1.getText().equals(message) || message.contains("1")) { //If the error is caused by the first field
								field1.error(error);
							} else {
								field2.error(error);
							}
						}
					}
				}).start();
			}
		}
	}
}