package me.shadorc.twitterstalker.graphics.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.CompoundBorder;

import me.shadorc.twitterstalker.graphics.Frame;
import me.shadorc.twitterstalker.graphics.ScrollbarUI;
import me.shadorc.twitterstalker.graphics.ScrollbarUI.Position;
import me.shadorc.twitterstalker.graphics.SmallButton;
import me.shadorc.twitterstalker.graphics.Storage.Data;
import me.shadorc.twitterstalker.graphics.TextField.Text;
import me.shadorc.twitterstalker.statistics.PopularePreview;
import me.shadorc.twitterstalker.statistics.Stats;
import me.shadorc.twitterstalker.statistics.TwitterUser;
import twitter4j.TwitterException;

public class StatisticsPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JButton back, upload;
	private JPanel textPanel;
	private TwitterUser user;
	private Stats stats;
	private int statsCase;

	StatisticsPanel(String name, JButton button) throws TwitterException {
		super(new BorderLayout());

		try {
			user = new TwitterUser(name);
		} catch (TwitterException e) {
			throw new TwitterException("L'utilisateur n'existe pas.", new Exception(name), 604);
		}

		stats = new Stats(user, button);

		if(Stats.stop == true) return;

		button.setText("Interface");

		this.setBackground(new Color(179, 229, 252));

		JPanel top = new JPanel(new BorderLayout());
		top.setBackground(new Color(68,138,255));
		top.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(183,183,183)), BorderFactory.createEmptyBorder(10, 10, 10, 10)));

		JPanel userInfos = new JPanel(new GridLayout(0, 3));
		userInfos.setOpaque(false);

		Font font = Frame.getFont("RobotoCondensed-Regular.ttf", 40.15f);

		JPanel labelsPane = new JPanel(new GridLayout(2, 0));
		labelsPane.setOpaque(false);

		JLabel followers = new JLabel("Followers : " + user.getFollowersCount());
		followers.setFont(font);
		followers.setForeground(Color.WHITE);
		labelsPane.add(followers);

		JLabel followings = new JLabel("Followings : " + user.getFollowingCount());
		followings.setFont(font);
		followings.setForeground(Color.WHITE);
		labelsPane.add(followings);

		userInfos.add(labelsPane);

		JLabel image = user.getProfileImage();
		image.setHorizontalAlignment(JLabel.CENTER);
		image.setFont(font);
		userInfos.add(image);

		JLabel username = new JLabel("@" + user.getName(), JLabel.LEFT);
		username.setForeground(Color.WHITE);
		username.setFont(font.deriveFont(72f));
		userInfos.add(username);

		top.add(userInfos, BorderLayout.CENTER);

		font = Frame.getFont("RobotoCondensed-Regular.ttf", 14.68f);

		JPanel userInfosStats = new JPanel(new GridLayout(0, 5));
		userInfosStats.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		userInfosStats.setOpaque(false);

		userInfosStats.add(new JLabel());
		userInfosStats.add(new JLabel());
//		userInfosStats.add(new JLabel());

		JLabel tweets = new JLabel("Tweets analysés : " + user.getTweetsAnalysed() + "/" + user.getTweetsPosted());
		tweets.setForeground(Color.WHITE);
		tweets.setFont(font);
		userInfosStats.add(tweets);

		JLabel tweetsDays = new JLabel("Nombre de tweets/jour : " + user.getTweetsPerDay(stats));
		tweetsDays.setForeground(Color.WHITE);
		tweetsDays.setFont(font);
		userInfosStats.add(tweetsDays);

		JLabel age = new JLabel("Inscris le : " + user.getCreatedAt() + " (" + user.getAge() + " jours)");
		age.setForeground(Color.WHITE);
		age.setFont(font);
		userInfosStats.add(age);

		top.add(userInfosStats, BorderLayout.PAGE_END);

		this.add(top, BorderLayout.PAGE_START);

		textPanel = new JPanel();
		textPanel.setOpaque(false);

		JScrollPane jsp = new JScrollPane(textPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jsp.getVerticalScrollBar().setUnitIncrement(16);
		jsp.getViewport().setOpaque(false);
		jsp.setOpaque(false);
		jsp.setBorder(null);
		jsp.getVerticalScrollBar().setUI(new ScrollbarUI(Position.VERTICAL));
		jsp.getHorizontalScrollBar().setUI(new ScrollbarUI(Position.HORIZONTAL));

		statsCase = 0;

		if(OptionsPanel.isSelected(Data.TWEETS))	this.createJEP("Tweets", Data.WORDS_PER_TWEET, Data.LETTERS_PER_TWEET, Data.LETTERS_PER_WORD);
		if(OptionsPanel.isSelected(Data.TIMELINE))	this.createJEP("Timeline", Data.PURETWEETS, Data.MENTIONS, Data.RETWEET_BY_ME);
		if(OptionsPanel.isSelected(Data.REPUTE))	this.createJEP("Renommée", Data.FAVORITE, Data.RETWEET);
		if(OptionsPanel.isSelected(Data.SOURCE))	this.createJEP("Sources", Data.SOURCE);
		if(OptionsPanel.isSelected(Data.DAYS))		this.createJEP("Jours", Data.DAYS);
		if(OptionsPanel.isSelected(Data.HOURS))		this.createJEP("Heures", Data.HOURS);
		if(OptionsPanel.isSelected(Data.WORDS))		this.createJEP("Mots", Data.WORDS);
		if(OptionsPanel.isSelected(Data.HASHTAG))	this.createJEP("Hashtags", Data.HASHTAG);
		if(OptionsPanel.isSelected(Data.POPULARE))	this.createJEP("Populaires", Data.POPULARE);
		if(OptionsPanel.isSelected(Data.MENTIONS_SENT))	this.createJEP("Utilisateurs mentionnés", Data.MENTIONS_SENT);
		if(OptionsPanel.isSelected(Data.MENTIONS_RECEIVED))	this.createJEP("Utilisateurs mentionnant", Data.MENTIONS_RECEIVED);

		if(statsCase == 0) {
			textPanel.setLayout(new BorderLayout());
			JLabel error = new JLabel("Aucune statistique n'a été sélectionnée. Désolé, mais le bug est dans un autre château.", JLabel.CENTER);
			error.setFont(Frame.getFont("RobotoCondensed-Regular.ttf", 30));
			textPanel.add(error, JLabel.CENTER);
		} else {
			textPanel.setLayout(new GridLayout((int) Math.ceil(statsCase/3.0), 3, 15, 15));
		}

		this.add(jsp, BorderLayout.CENTER);

		JPanel buttonsPanel = new JPanel(new GridLayout(0, 14));
		buttonsPanel.setOpaque(false);

		back = new SmallButton("Retour", BorderFactory.createEmptyBorder(10, 0, 10, 20));
		back.addActionListener(this);
		buttonsPanel.add(back);

		for(int i = 0; i < 12; i++) {
			buttonsPanel.add(new JLabel());
		}

		upload = new SmallButton("Upload", BorderFactory.createEmptyBorder(10, 20, 10, 0));
		upload.setToolTipText("Partager les statistiques");
		upload.addActionListener(this);
		buttonsPanel.add(upload);

		this.add(buttonsPanel, BorderLayout.PAGE_END);
	}

	private void createJEP(String desc, Data... types) throws TwitterException {
		JEditorPane area = new JEditorPane();
		area.setContentType("text/html");
		area.setOpaque(false);
		area.setEditable(false);
		/*Allow to set font with HTML content*/
		area.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
		area.setFont(Frame.getFont("SEGOEUI.TTF", 26));

		String text = "<font color=#212121>" + desc + "<font color=#727272><style=\"font-size:23\";>";
		//If it's stats contains in array
		if(types.length == 1) {
			Data type = types[0];
			for(int i = 0; i < OptionsPanel.getMaxListLenght(); i++) {
				try {
					text += "<br>&nbsp;&nbsp;";
					if(type.equals(Data.MENTIONS_RECEIVED) || type.equals(Data.MENTIONS_SENT)) {
						text += "- " + stats.get(type, i).getUserInfo();
					} else if(type.equals(Data.POPULARE)) {
						text += "- " + "<a href=" + stats.get(type, i).getStatusUrl() + ">" + stats.get(type, i).getInfo() + "</a>";
					} else {
						text += "- " + stats.get(type, i).getInfo();
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
				text += "<br>&nbsp;&nbsp;- " + stats.getUnique(type).getDesc();
			}
		}
		area.setText(text);

		if(Arrays.asList(types).contains(Data.POPULARE)) {
			area.addHyperlinkListener(new PopularePreview(stats, area));
		}

		textPanel.add(area);
		statsCase++;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton bu = (JButton) e.getSource();

		if(bu == back) {
			Frame.setJPanel(new ConnectionPanel(Text.USERNAME));
		} else if(bu == upload) {
			Frame.upload("@" + user.getName() + "'s stats");
		}
	}
}
