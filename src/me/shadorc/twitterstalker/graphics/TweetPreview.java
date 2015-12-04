package me.shadorc.twitterstalker.graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.List;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.DefaultEditorKit;

import me.shadorc.infonet.Infonet;
import me.shadorc.twitterstalker.Main;
import me.shadorc.twitterstalker.graphics.SearchField.Text;
import me.shadorc.twitterstalker.graphics.panel.OptionsPanel;
import me.shadorc.twitterstalker.statistics.RoundedImage;
import me.shadorc.twitterstalker.statistics.RoundedImage.Scaling;
import me.shadorc.twitterstalker.statistics.Stats;
import me.shadorc.twitterstalker.statistics.UserStats;
import me.shadorc.twitterstalker.storage.Data.Options;
import me.shadorc.twitterstalker.storage.Data.UsersEnum;
import me.shadorc.twitterstalker.storage.Storage;
import me.shadorc.twitterstalker.utility.Ressources;
import twitter4j.MediaEntity;
import twitter4j.MediaEntity.Size;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.UserMentionEntity;

public class TweetPreview implements HyperlinkListener {

	private HashMap <String, JPopupMenu> menus;

	private JEditorPane editorPane;
	private JButton ok, reset;
	private JFrame frame;
	private Stats stats;

	public TweetPreview(JEditorPane editorPane, Stats stats) {
		this.editorPane = editorPane;
		this.stats = stats;
		this.menus = new HashMap<>();

		editorPane.requestFocusInWindow();
		editorPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent event) {
				for(String key : menus.keySet()) {
					menus.get(key).setVisible(false);
				}
			}
		});
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent he) {
		String url = he.getURL().toString();

		if(he.getEventType() == EventType.ACTIVATED) {
			if(he.getDescription().equals("search")) {
				if(frame == null) {
					this.showSearchFrame(stats);
				} else {
					frame.setVisible(true);
					frame.toFront();
				}
			} else {
				Infonet.open(url, true);
			}

		} else if(he.getEventType() == EventType.EXITED) {
			if(menus.containsKey(url)) {
				menus.get(url).setVisible(false);
			}

		} else if(he.getEventType() == EventType.ENTERED) {
			if(he.getDescription().equals("search")) return;

			Point mouse = MouseInfo.getPointerInfo().getLocation();

			if(menus.containsKey(url)) {
				menus.get(url).show(null, (int) (mouse.getX() + 50), (int) (mouse.getY() - menus.get(url).getPreferredSize().getHeight() - 20));
			}

			else {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							JPopupMenu menu = new JPopupMenu();
							menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS)); 
							menu.setBackground(new Color(79, 182, 246));
							menu.setBorder(BorderFactory.createLineBorder(Color.BLACK));

							JLabel load = new JLabel(Storage.tra("loading") + "...");
							load.setFont(new Font("Arial", Font.PLAIN, 20));
							menu.add(load);

							Point mouse = MouseInfo.getPointerInfo().getLocation();
							menu.show(null, (int) (mouse.getX() + 50), (int) (mouse.getY() - menu.getPreferredSize().getHeight() - 20));

							menus.put(url, menu);

							Status status = Main.getTwitter().showStatus(Long.parseLong(url.substring(url.lastIndexOf("/")+1)));

							/*User Panel*/
							JPanel userPanel = new JPanel(new BorderLayout());
							userPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
							userPanel.setOpaque(false);

							userPanel.add(new JLabel(RoundedImage.create(status.getUser().getOriginalProfileImageURL().replaceAll(".jpeg", "_400x400.jpeg"), Scaling.THUMB)), BorderLayout.WEST);

							JPanel names = new JPanel(new GridLayout(4, 2));
							names.setBorder(BorderFactory.createEmptyBorder(25, 3, 0, 3));
							names.setOpaque(false);

							JLabel nameLab = new JLabel(status.getUser().getName());
							nameLab.setOpaque(false);
							nameLab.setForeground(Color.BLACK);
							nameLab.setFont(new Font("Arial", Font.BOLD, 15));
							names.add(nameLab);

							DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, OptionsPanel.getLocaleLang());
							names.add(new JLabel(df.format(status.getCreatedAt()), JLabel.RIGHT));

							JLabel screenNameLab = new JLabel("@" + status.getUser().getScreenName());
							screenNameLab.setOpaque(false);
							screenNameLab.setForeground(Color.BLACK);
							screenNameLab.setFont(new Font("Arial", Font.PLAIN, 15));
							names.add(screenNameLab);

							names.add(new JLabel());
							names.add(new JLabel());
							names.add(new JLabel());
							names.add(new JLabel());
							names.add(new JLabel());

							userPanel.add(names, BorderLayout.CENTER);
							/*User Panel End*/

							JTextArea tweet = new JTextArea(status.getText());
							tweet.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
							tweet.setEditable(false);  
							tweet.setOpaque(false);  
							tweet.setLineWrap(true);
							tweet.setWrapStyleWord(true);
							tweet.setForeground(Color.BLACK);
							tweet.setFont(new Font("Arial", Font.PLAIN, 17));

							JPanel mediasPanel = new JPanel(new GridLayout());
							mediasPanel.setOpaque(false);
							mediasPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
							for(int i = 0; i < status.getExtendedMediaEntities().length; i++) {
								MediaEntity media = status.getExtendedMediaEntities()[i];
								Size size = media.getSizes().get(status.getExtendedMediaEntities().length > 2 ? Size.THUMB : Size.SMALL);

								mediasPanel.add(new JLabel("<html>"
										+ "<img src=" + media.getMediaURLHttps() 
										+ " width=" + size.getWidth()
										+ " height=" + size.getHeight()
										+ " border=1>"
										+ "</html>"));
							}

							menu.removeAll();

							menu.add(userPanel);
							menu.add(tweet);
							menu.add(mediasPanel);

							//If the popup is already closed, don't open it
							if(!menu.isVisible()) return;

							int width = (int) (mediasPanel.getComponentCount() != 0 ? menu.getPreferredSize().getWidth() : 500);
							int height = (int) (menu.getPreferredSize().getHeight() + 40);

							menu.setVisible(false);
							menu.setPreferredSize(new Dimension(width, height));
							menu.setMinimumSize(new Dimension(width, height));
							menu.show(null, (int) (mouse.getX() + 50), (int) (mouse.getY() - height - 20));

							menus.put(url, menu);
						} catch (NumberFormatException | TwitterException ignore) {	}
					}
				}).start();
			}
		}
	}

	private void showSearchFrame(Stats stats) {
		Font font = Ressources.getFont("RobotoCondensed-LightItalic.ttf", 20);

		UIManager.put("Button.disabledText", Color.LIGHT_GRAY);

		frame = new JFrame(Storage.tra("search"));
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel pane = new JPanel(new GridLayout(2, 0));
		pane.setBackground(new Color(179, 229, 252));

		JPanel top = new JPanel(new BorderLayout());
		top.setOpaque(false);

		JLabel info = new JLabel(Storage.tra("userSearch"), JLabel.CENTER);
		info.setFont(font);
		top.add(info, BorderLayout.CENTER);

		reset = new JButton(Storage.tra("reset"));
		reset.setEnabled(false);
		reset.setFocusable(false);
		reset.setBackground(Color.WHITE);
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				reset.setEnabled(false);
				new Thread(new Runnable() {
					@Override
					public void run() {
						List<UserStats> list = stats.get(UsersEnum.FIRST_TALK);

						if(list.isEmpty()) return;

						String text = "<font color=#212121>" + Storage.tra(UsersEnum.FIRST_TALK) + "<font color=#727272><style=\"font-size:23\";>";
						for(int i = 0; i < OptionsPanel.get(Options.LIST_LENGHT); i++) {
							text += "<br>";
							if(i < list.size()) {
								text += "&nbsp;&nbsp;- " + list.get(i).toString();
							}
						}

						editorPane.setText(text);
					}
				}).start();
			}
		});
		top.add(reset, BorderLayout.EAST);

		pane.add(top);

		JPanel bottom = new JPanel(new BorderLayout());
		bottom.setOpaque(false);

		JFormattedTextField userInput = new JFormattedTextField();
		userInput.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.isPopupTrigger()) {
					JPopupMenu menu = new JPopupMenu();
					JMenuItem item = new JMenuItem(new DefaultEditorKit.PasteAction());
					item.setText(Storage.tra("paste"));
					menu.add(item);
					menu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		userInput.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if(userInput.getText().equals(Storage.tra(Text.INVALID_USER))) {
					userInput.setForeground(Color.BLACK);
					userInput.setText("");
				}				
			}
		});
		userInput.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					search(userInput, frame);
				}
			}
		});
		bottom.add(userInput, BorderLayout.CENTER);

		ok = new JButton(Storage.tra("ok"));
		ok.setFocusable(false);
		ok.setBackground(Color.WHITE);
		ok.setPreferredSize(reset.getPreferredSize());
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				search(userInput, frame);
			}
		});
		bottom.add(ok, BorderLayout.EAST);

		pane.add(bottom);

		frame.setContentPane(pane);
		frame.pack();
		frame.setIconImage(Ressources.smallIcon.getImage());
		frame.setSize(500, 80);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private void search(JFormattedTextField userInput, JFrame frame) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				ok.setText(Storage.tra("loading"));
				ok.setEnabled(false);
				try {
					UserStats user = find(userInput.getText().replaceAll("@", ""));
					String text = "<font color=#212121>" + Storage.tra(UsersEnum.FIRST_TALK) + "<font color=#727272><style=\"font-size:23\";>";
					editorPane.setText(text + "<br>&nbsp;&nbsp;- " + user);
					frame.setVisible(false);
					reset.setEnabled(true);
					userInput.setText("");
				} catch (TwitterException e) {
					userInput.setForeground(Color.RED);
					userInput.setText(Storage.tra(Text.INVALID_USER));
					userInput.getParent().requestFocusInWindow();
				}
				ok.setEnabled(true);
				ok.setText(Storage.tra("ok"));
			}
		}).start();
	}

	private UserStats find(String name) throws TwitterException {
		List <UserStats> list = stats.get(UsersEnum.FIRST_TALK);
		for(int i = 0; i < list.size(); i++) {
			for(UserMentionEntity mention : list.get(i).getStatus().getUserMentionEntities()) {
				if(mention.getScreenName().equals(name)) {
					return new UserStats(mention.getScreenName(), list.get(i).getStatus());
				}
			}
		}
		throw new TwitterException(name);
	}
}