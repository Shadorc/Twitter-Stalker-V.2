package me.shadorc.twitterstalker.graphics;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.DefaultEditorKit;

import me.shadorc.twitterstalker.graphics.Storage.Data;
import me.shadorc.twitterstalker.graphics.panel.OptionsPanel;
import me.shadorc.twitterstalker.statistics.PopularPreview;
import me.shadorc.twitterstalker.statistics.Stats;
import me.shadorc.twitterstalker.statistics.TwitterUser;
import me.shadorc.twitterstalker.statistics.WordInfo;
import twitter4j.TwitterException;

public class EditorPane extends JEditorPane {

	private static final long serialVersionUID = 1L;

	private Stats stats1, stats2;
	private TwitterUser user1, user2;
	private ArrayList <String> phrases;

	public static void get(JPanel pane, final Stats stats, String desc, Data... types) throws TwitterException {
		final JEditorPane editorPane = new JEditorPane();
		editorPane.setContentType("text/html");
		editorPane.setOpaque(false);
		editorPane.setEditable(false);
		/*Allow to set font with HTML content*/
		editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
		editorPane.setFont(Frame.getFont("SEGOEUI.TTF", 26));

		String text = "<font color=#212121>" + desc + "<font color=#727272><style=\"font-size:23\";>";
		//If it's stats contains in array
		if(types.length == 1) {
			Data type = types[0];
			for(int i = 0; i < OptionsPanel.getMaxListLenght(); i++) {
				try {
					text += "<br>&nbsp;&nbsp;";
					if(type.equals(Data.MENTIONS_RECEIVED) || type.equals(Data.MENTIONS_SENT)) {
						try {
							text += "- " + stats.get(type, i).getUserInfo();
						} catch(TwitterException e) {
							text += "- " + Storage.tra("nonExistentUser");
						}

					} else if(type.equals(Data.POPULARE)) {
						text += "- " + "<a href=" + stats.get(type, i).getStatusUrl() + ">" + stats.get(type, i).getStatusInfo() + "</a>";

					} else if(type.equals(Data.FIRST_TALK)) {
						ArrayList <WordInfo> copy = new ArrayList <WordInfo> (stats.get(type));
						Collections.reverse(copy);
						String date = DateFormat.getDateInstance(DateFormat.LONG, OptionsPanel.getLocaleLang()).format(new Date(copy.get(i).getNum()));
						text += "- " + copy.get(i).getUserImage() + " @" + copy.get(i).getWord() + " (" + date + ")";

					} else {
						text += "- " + stats.get(type, i).getPercenInfo();
					}
				} catch (IndexOutOfBoundsException e) {
					if(i == 0) {
						System.err.println("Info : " + Arrays.asList(types) + " ignored.");
						return;
					}
					text += "<br>";
				}
			}
		} else {
			for(Data type : types) {
				if(type == Data.WORDS_PER_TWEET || type == Data.LETTERS_PER_TWEET || type ==  Data.LETTERS_PER_WORD) {
					text += "<br>&nbsp;&nbsp;- " + stats.getUnique(type).getInfo();
				} else {
					text += "<br>&nbsp;&nbsp;- " + stats.getUnique(type).getPercenInfo();
				}
			}
		}
		editorPane.setText(text);

		if(Arrays.asList(types).contains(Data.POPULARE)) {
			editorPane.addHyperlinkListener(new PopularPreview(editorPane, stats));
		}

		if(Arrays.asList(types).contains(Data.FIRST_TALK)) {
			editorPane.addHyperlinkListener(new HyperlinkListener() {
				@Override
				public void hyperlinkUpdate(HyperlinkEvent he) {
					if(he.getEventType() == EventType.ACTIVATED) {
						createFrame(editorPane, stats);
					} else if(he.getEventType() == EventType.EXITED) {
					} else if(he.getEventType() == EventType.ENTERED) {
					}
				}
			});
		}

		pane.add(editorPane);
	}

	private static void createFrame(final JEditorPane editorPane, final Stats stats) {
		final JFormattedTextField saisis = new JFormattedTextField();
		saisis.addMouseListener(new MouseAdapter() {
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

		final JFrame frame = new JFrame(Storage.tra("search"));
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel pane = new JPanel(new GridLayout(2, 0));

		JLabel info = new JLabel(Storage.tra("userSearch"), JLabel.CENTER);
		pane.add(info);

		JPanel bottom = new JPanel(new BorderLayout());

		saisis.addKeyListener(null);
		bottom.add(saisis, BorderLayout.CENTER);

		JButton ok = new JButton(Storage.tra("ok"));
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for(WordInfo user : stats.get(Data.FIRST_TALK)) {
					if(user.getWord().equals(saisis.getText().replaceAll("@", ""))) {
						String date = DateFormat.getDateInstance(DateFormat.LONG, OptionsPanel.getLocaleLang()).format(new Date(user.getNum()));
						String text = editorPane.getText().substring(0, editorPane.getText().indexOf(")")+1) + "<br>&nbsp;&nbsp;";
						try {
							text += "- " + user.getUserImage() + " @" + user.getWord() + " (" + date + ")";
						} catch (TwitterException e1) {
							//User doesn't exist anymore
							text += "- @" + user.getWord() + " (" + date + ")";
						}
						editorPane.setText(text);
						frame.dispose();
						return;
					}
				}
			}
		});
		bottom.add(ok, BorderLayout.EAST);

		pane.add(bottom);

		frame.setContentPane(pane);
		frame.pack();
		frame.setIconImage(new ImageIcon(EditorPane.class.getResource("/res/IconeAppli.png")).getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));
		frame.setSize(500, 80);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public EditorPane(Stats stats1, Stats stats2, TwitterUser user1, TwitterUser user2) throws TwitterException {
		super();
		this.setContentType("text/html");
		this.setOpaque(false);
		this.setEditable(false);
		/*Allow to set font with HTML content*/
		this.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
		this.setFont(Frame.getFont("SEGOEUI.TTF", 26));

		this.stats1 = stats1;
		this.stats2 = stats2;
		this.user1 = user1;
		this.user2 = user2;

		phrases = new ArrayList <String> ();

		this.compare(Data.TWEET_PER_DAYS, Storage.tra("tweetMore"), false);
		this.compare(Data.RETWEET_BY_ME, Storage.tra("retweetMore"), true);
		this.compare(Data.MENTIONS_COUNT, Storage.tra("moreMentions"), true);
		this.compare(Data.POPULARE, Storage.tra("morePopular"), false);
		this.compare(Data.URL, Storage.tra("moreUrl"), true);
		this.compare(Data.MEDIA, Storage.tra("moreMedias"), true);

		String sunday = new DateFormatSymbols(OptionsPanel.getLocaleLang()).getWeekdays()[1];
		if(stats1.get(Data.SOURCE).size() == 1 && stats1.get(Data.SOURCE).get(0).getWord().equals("Twitter Web Client"))	Storage.tra(Storage.tra("onlyTwitterWeb"));
		if(stats1.get(Data.HOURS).get(0).getNum() > stats2.get(Data.HOURS).get(0).getNum())									Storage.tra(Storage.tra("tweetLater"));
		if(stats1.get(Data.DAYS).get(0).getWord().equalsIgnoreCase(sunday))													Storage.tra(Storage.tra("sundayTweet"));
		if(user1.getFollowingCount()/2 > user1.getFollowersCount())															Storage.tra(Storage.tra("followALot"));
		if(stats1.getUnique(Data.TWEET_PER_DAYS).getRatio() >= 200)															Storage.tra(Storage.tra("tweetALot"));
		if(user1.getAge() > 1825) 																							Storage.tra(Storage.tra("oldOnTwitter"));

		String text = "<font color=#212121>" + user1.getName() + "<font color=#727272><style=\"font-size:23\";><ul>";
		Collections.shuffle(phrases);
		for(int i = 0; i < phrases.size() && i < OptionsPanel.getMaxListLenght(); i++) {
			text += "<li>" + phrases.get(i) + "</li>";
		}
		text += "</lu>";

		this.setText(text);
	}

	private void compare(Data data, String line, boolean proportional) {
		double stat1, stat2;
		try {
			stat1 = stats1.get(data).get(0).getNum();
			stat2 = stats2.get(data).get(0).getNum();
		} catch(NullPointerException e) {
			stat1 = stats1.getUnique(data).getNum();
			stat2 = stats2.getUnique(data).getNum();
		}

		if(proportional) {
			stat1 /= user1.getTweetsAnalyzed();
			stat2 /= user2.getTweetsAnalyzed();
		}
		if(stat1 > stat2) {
			phrases.add(line);
		}
	}
}