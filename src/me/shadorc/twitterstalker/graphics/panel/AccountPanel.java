package me.shadorc.twitterstalker.graphics.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.CompoundBorder;

import me.shadorc.twitterstalker.graphics.Button;
import me.shadorc.twitterstalker.graphics.Button.ButtonType;
import me.shadorc.twitterstalker.graphics.Button.Size;
import me.shadorc.twitterstalker.graphics.ScrollbarUI;
import me.shadorc.twitterstalker.graphics.ScrollbarUI.Position;
import me.shadorc.twitterstalker.graphics.SearchField.Text;
import me.shadorc.twitterstalker.graphics.Share;
import me.shadorc.twitterstalker.statistics.Stats;
import me.shadorc.twitterstalker.statistics.TwitterUser;
import me.shadorc.twitterstalker.storage.Data.NumbersEnum;
import me.shadorc.twitterstalker.storage.Storage;
import me.shadorc.twitterstalker.utility.Ressources;
import twitter4j.Status;
import twitter4j.TwitterException;

public class AccountPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JButton back, upload;
	private TwitterUser user;
	private boolean isArchive;

	protected AccountPanel(String name, JButton button, List <Status> statusList) throws TwitterException {
		super(new BorderLayout());

		user = new TwitterUser(name);

		isArchive = (statusList != null);

		Stats stats = new Stats(user, button, statusList, true);

		if(Ressources.stop) return;

		button.setText(Storage.tra("interface"));

		this.setBackground(new Color(179, 229, 252));

		JPanel top = new JPanel(new BorderLayout());
		top.setBackground(new Color(68,138,255));
		top.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(183,183,183)), BorderFactory.createEmptyBorder(10, 10, 10, 10)));

		JPanel userInfos = new JPanel(new BorderLayout());
		userInfos.setOpaque(false);

		Font font = Ressources.getFont("RobotoCondensed-Regular.ttf", 40);

		JPanel labelsPane = new JPanel(new GridLayout(2, 0));
		labelsPane.setOpaque(false);

		JLabel followers = new JLabel(Storage.tra("follower") + user.getFollowersCount());
		followers.setFont(font);
		followers.setForeground(Color.WHITE);
		labelsPane.add(followers);

		JLabel followings = new JLabel(Storage.tra("following") + user.getFollowingCount());
		followings.setFont(font);
		followings.setForeground(Color.WHITE);
		labelsPane.add(followings);

		userInfos.add(labelsPane, BorderLayout.WEST);

		JLabel image = user.getProfileImage();
		image.setHorizontalAlignment(JLabel.CENTER);
		userInfos.add(image, BorderLayout.CENTER);

		font = font.deriveFont(72f);

		JLabel username = new JLabel("@" + user.getName(), JLabel.RIGHT);
		username.setForeground(Color.WHITE);
		username.setFont(font);
		userInfos.add(username, BorderLayout.EAST);

		//Set the same width to the two panel to have the user image centered. Condition is to avoid to have truncated text
		if(labelsPane.getPreferredSize().getWidth() > username.getPreferredSize().getWidth()) {
			username.setPreferredSize(labelsPane.getPreferredSize());
		} else {
			labelsPane.setPreferredSize(username.getPreferredSize());
		}

		top.add(userInfos, BorderLayout.CENTER);

		font = font.deriveFont(16f);

		JPanel userInfosStats = new JPanel(new GridLayout(0, 5));
		userInfosStats.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		userInfosStats.setOpaque(false);

		userInfosStats.add(new JLabel());
		userInfosStats.add(new JLabel());

		JLabel tweets = new JLabel(Storage.tra("tweetsAnalyzed") + user.getTweetsAnalyzed() + "/" + user.getTweetsPosted(), JLabel.CENTER);
		tweets.setForeground(Color.WHITE);
		tweets.setFont(font);
		userInfosStats.add(tweets);

		JLabel tweetsDays = new JLabel(Ressources.removeHTML(stats.get(NumbersEnum.TWEETS_PER_DAY).toString()), JLabel.CENTER);
		tweetsDays.setForeground(Color.WHITE);
		tweetsDays.setFont(font);
		userInfosStats.add(tweetsDays);

		JLabel age = new JLabel(Storage.tra("joined") + user.getCreatedAt() + " (" + user.getAge() + Storage.tra("days") + ")", JLabel.CENTER);
		age.setForeground(Color.WHITE);
		age.setFont(font);
		userInfosStats.add(age);

		top.add(userInfosStats, BorderLayout.PAGE_END);

		this.add(top, BorderLayout.PAGE_START);

		JPanel statsPanel = new StatsPanel(stats, isArchive);

		JScrollPane scrollPane = new JScrollPane(statsPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setOpaque(false);
		scrollPane.setBorder(null);
		scrollPane.getVerticalScrollBar().setUI(new ScrollbarUI(Position.VERTICAL));
		scrollPane.getHorizontalScrollBar().setUI(new ScrollbarUI(Position.HORIZONTAL));

		if(statsPanel.getComponentCount() == 0) {
			statsPanel.setLayout(new BorderLayout());
			JLabel error = new JLabel(Storage.tra("noStatError"), JLabel.CENTER);
			error.setFont(Ressources.getFont("RobotoCondensed-Regular.ttf", 30));
			statsPanel.add(error, BorderLayout.CENTER);
		} else {
			statsPanel.setLayout(new GridLayout((int) Math.ceil(statsPanel.getComponents().length/3.0), 3, 15, 15));
		}

		this.add(scrollPane, BorderLayout.CENTER);

		JPanel buttonsPanel = new JPanel(new GridLayout(0, 14));
		buttonsPanel.setOpaque(false);

		back = new Button(ButtonType.BACK, Size.MEDIUM, new int[] {10, 0, 10, 20}, this);
		buttonsPanel.add(back);

		for(int i = 0; i < 12; i++) {
			buttonsPanel.add(new JLabel());
		}

		upload = new Button(ButtonType.UPLOAD, Size.MEDIUM, new int[] {10, 20, 10, 0}, this);
		upload.setToolTipText(Storage.tra("shareStat"));
		buttonsPanel.add(upload);

		this.add(buttonsPanel, BorderLayout.PAGE_END);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton bu = (JButton) e.getSource();

		if(bu == back) {
			Ressources.getFrame().setPanel(isArchive ? new MenuPanel() : new ConnectionPanel(Text.ACCOUNT));
		} else if(bu == upload) {
			new Share("@" + user.getName() + "'s stats");
		}
	}
}
