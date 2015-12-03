package me.shadorc.twitterstalker.graphics.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
import twitter4j.TwitterException;

public class ComparisonPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JButton back, upload;
	private TwitterUser user1, user2;

	protected ComparisonPanel(String name1, String name2, JButton button) throws TwitterException {
		super(new BorderLayout());

		user1 = new TwitterUser(name1);
		user2 = new TwitterUser(name2);

		Stats stats1 = new Stats(user1, button, null, false);
		Stats stats2 = new Stats(user2, button, null, false);

		if(Ressources.stop == true) return;

		button.setText(Storage.tra("interface"));

		this.setBackground(new Color(179, 229, 252));

		JPanel top = new JPanel(new GridLayout(0, 2));
		top.setOpaque(false);

		top.add(this.createUserJPanel(user1, stats1));
		top.add(this.createUserJPanel(user2, stats2));

		this.add(top, BorderLayout.PAGE_START);

		JPanel center = new JPanel(new GridLayout(0, 2));
		center.setOpaque(false);

		JPanel statsPanel1 = new StatsPanel(stats1, stats2, user1, user2);
		statsPanel1.setLayout(new GridLayout(statsPanel1.getComponentCount(), 0));

		JPanel statsPanel2 = new StatsPanel(stats2, stats1, user2, user1);
		statsPanel2.setLayout(new GridLayout(statsPanel2.getComponentCount(), 0));

		for(Component comp : statsPanel1.getComponents()) {
			if(!this.contains(statsPanel2, comp)) {
				statsPanel1.remove(comp);
				((GridLayout) statsPanel1.getLayout()).setRows(((GridLayout) statsPanel1.getLayout()).getRows()-1);
				System.out.println("'" + comp.getName() + "' ignored in panel 1");
			}
		}

		for(Component comp : statsPanel2.getComponents()) {
			if(!this.contains(statsPanel1, comp)) {
				statsPanel2.remove(comp);
				((GridLayout) statsPanel2.getLayout()).setRows(((GridLayout) statsPanel2.getLayout()).getRows()-1);
				System.out.println("'" + comp.getName() + "' ignored in panel 2");
			}
		}

		//If there's no stat to show
		if(statsPanel1.getComponentCount() == 0 || statsPanel1.getComponentCount() == 0) {
			center.setLayout(new BorderLayout());
			JLabel error = new JLabel(Storage.tra("noStatError"), JLabel.CENTER);
			error.setFont(Ressources.getFont("RobotoCondensed-Regular.ttf", 30));
			center.add(error, JLabel.CENTER);
		} else {
			center.add(statsPanel1);
			center.add(statsPanel2);
		}

		JScrollPane centerScroll = new JScrollPane(center, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		centerScroll.getVerticalScrollBar().setUnitIncrement(20);
		centerScroll.getViewport().setOpaque(false);
		centerScroll.setOpaque(false);
		centerScroll.setBorder(null);
		centerScroll.getVerticalScrollBar().setUI(new ScrollbarUI(Position.VERTICAL));
		centerScroll.getHorizontalScrollBar().setUI(new ScrollbarUI(Position.HORIZONTAL));

		this.add(centerScroll, BorderLayout.CENTER);

		JPanel buttonsPanel = new JPanel(new GridLayout(0, 14));
		buttonsPanel.setOpaque(false);

		back = new Button(ButtonType.BACK, Size.MEDIUM, new int[] {0, 0, 10, 20}, this);
		buttonsPanel.add(back);

		for(int i = 0; i < 12; i++) {
			buttonsPanel.add(new JLabel());
		}

		upload = new Button(ButtonType.UPLOAD, Size.MEDIUM, new int[] {0, 20, 10, 0}, this);
		upload.setToolTipText(Storage.tra("shareStat"));
		buttonsPanel.add(upload);

		this.add(buttonsPanel, BorderLayout.PAGE_END);
	}

	private boolean contains(JPanel panel, Component comp) {
		for(Component comp2 : panel.getComponents()) {
			if(comp2.getName().equals(comp.getName())) {
				return true;
			}
		}
		return false;
	}

	private JPanel createUserJPanel(TwitterUser user, Stats stats) {
		JPanel descPanel = new JPanel(new BorderLayout());
		descPanel.setBackground(new Color(68, 138, 255));
		descPanel.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(183,183,183)), BorderFactory.createEmptyBorder(5, 10, 5, 30)));

		descPanel.add(user.getProfileImage(), BorderLayout.WEST);
		JLabel name = new JLabel("@" + user.getName());
		name.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
		name.setFont(Ressources.getFont("RobotoCondensed-Regular.ttf", 40));
		name.setForeground(Color.WHITE);

		//Reduce name's size according to the difference in width
		if(name.getPreferredSize().getWidth() > 200) {
			name.setFont(Ressources.getFont("RobotoCondensed-Regular.ttf", (int) (40-(name.getPreferredSize().getWidth()-200)/10)));
		}

		descPanel.add(name, BorderLayout.CENTER);

		JPanel infosPanel = new JPanel(new GridLayout(7, 0));
		infosPanel.setOpaque(false);
		infosPanel.add(new JLabel());
		infosPanel.add(this.createLabel(Storage.tra("follower") + user.getFollowersCount()));
		infosPanel.add(this.createLabel(Storage.tra("following") + user.getFollowingCount()));
		infosPanel.add(this.createLabel(Storage.tra("memberSince") + user.getAge() + Storage.tra("days")));
		infosPanel.add(this.createLabel(Storage.tra("tweetsAnalyzed") + user.getTweetsAnalyzed() + "/" + user.getTweetsPosted()));
		infosPanel.add(this.createLabel(Ressources.removeHTML(stats.get(NumbersEnum.TWEETS_PER_DAY).toString())));
		descPanel.add(infosPanel, BorderLayout.EAST);

		return descPanel;
	}

	private JLabel createLabel(String infos) {
		JLabel label = new JLabel(infos, JLabel.RIGHT);
		label.setFont(Ressources.getFont("RobotoCondensed-Regular.ttf", 18));
		label.setForeground(Color.WHITE);
		return label;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton bu = (JButton) e.getSource();

		if(bu == back) {
			Ressources.frame.setPanel(new ConnectionPanel(Text.COMPARISON));
		} else if(bu == upload) {
			new Share("Comparison between @" + user1.getName() + " & @" + user2.getName());
		}
	}
}