package me.shadorc.twitterstalker.graphics;

import java.awt.BorderLayout;
import java.awt.Color;
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

	private JPopupMenu menu;
	private JEditorPane editorPane;
	private JFrame frame;
	private Stats stats;
	private JButton ok, reset;

	public TweetPreview(JEditorPane editorPane, Stats stats) {
		this.editorPane = editorPane;
		this.stats = stats;
		this.menu = new JPopupMenu();

		editorPane.requestFocusInWindow();

		menu.setBackground(new Color(79, 182, 246));
		menu.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		editorPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent event) {
				menu.setVisible(false);
				menu.removeAll();
			}
		});
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent he) {
		if(he.getEventType() == EventType.ACTIVATED) {
			if(he.getDescription().equals("search")) {
				if(frame == null) {
					this.showSearchFrame(stats);
				} else {
					frame.setVisible(true);
					frame.toFront();
				}
			} else {
				Infonet.open(he.getURL().toString(), true);
			}

		} else if(he.getEventType() == EventType.EXITED) {
			menu.setVisible(false);
			menu.removeAll();

		} else if(he.getEventType() == EventType.ENTERED) {
			if(he.getDescription().equals("search")) return;

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Point mouse = MouseInfo.getPointerInfo().getLocation();

						menu.add(new JLabel("<html>&emsp;" + Storage.tra("loading") + "...&emsp;</html>"));
						menu.show(null, (int) (mouse.getX() + 50), (int) (mouse.getY() - menu.getPreferredSize().getHeight() - 20));

						String url = he.getURL().toString();
						//Get Status' ID
						Status status = Main.getTwitter().showStatus(Long.parseLong(url.substring(url.lastIndexOf("/")+1)));

						String tweet = "<html><center><font color='white' size=4>&emsp;" + status.getText().replaceAll("\n", "&emsp;<br>&emsp;") + "&emsp;";

						for(MediaEntity media : status.getMediaEntities()) {
							Size size = media.getSizes().get(Size.SMALL);
							tweet += "<br>&emsp;<img src=" + media.getMediaURLHttps() + " width=" + size.getWidth() + " height=" + size.getHeight() + " border=1 align=middle>&emsp;";
						}

						menu.removeAll();

						menu.add(new JLabel(tweet));

						DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, OptionsPanel.getLocaleLang());
						menu.add(new JLabel("<html><b><i>" + df.format(status.getCreatedAt()), SwingConstants.CENTER));

						//If the popup is already closed, don't open it
						if(!menu.isVisible()) return;

						menu.setVisible(false);
						menu.show(null, (int) (mouse.getX() + 50), (int) (mouse.getY() - menu.getPreferredSize().getHeight() - 20));
					} catch (NumberFormatException | TwitterException ignore) {	}
				}
			}).start();
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
		frame.setIconImage(Ressources.getSmallIcon().getImage()	);
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
					return new UserStats(mention.getId(), list.get(i).getStatus());
				}
			}
		}
		throw new TwitterException(name);
	}
}