package me.shadorc.twitterstalker.statistics;

import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;

import me.shadorc.infonet.Infonet;
import me.shadorc.twitterstalker.graphics.Storage.Data;
import me.shadorc.twitterstalker.graphics.panel.OptionsPanel;
import twitter4j.MediaEntity;
import twitter4j.MediaEntity.Size;
import twitter4j.Status;

public class PopularPreview implements HyperlinkListener {

	private Stats stats;
	private JPopupMenu menu;

	public PopularPreview(JEditorPane pane, Stats stats) {
		this.stats = stats;
		this.menu = new JPopupMenu();

		menu.setBackground(new Color(79, 182, 246));
		menu.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		pane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent event) {
				menu.setVisible(false);
				menu.removeAll();
			}
		});
	}

	@Override
	public void hyperlinkUpdate(final HyperlinkEvent he) {
		if(he.getEventType() == EventType.ACTIVATED) {
			Infonet.open(he.getURL().toString(), true);

		} else if(he.getEventType() == EventType.EXITED) {
			menu.setVisible(false);
			menu.removeAll();

		} else if(he.getEventType() == EventType.ENTERED) {
			for(WordInfo wi : stats.get(Data.POPULARE)) {
				Status status = wi.getStatus();
				if(wi.getStatusUrl().equals(he.getURL().toString())) {
					String tweet = "<html><center><font color='white' size=4>&emsp;" + status.getText().replaceAll("\n", "&emsp;<br>&emsp;") + "&emsp;";

					if(status.getMediaEntities().length != 0) {
						MediaEntity img = status.getMediaEntities()[0];
						Size size = img.getSizes().get(Size.SMALL);
						tweet += "<br>&emsp;<img src=" + img.getMediaURLHttps() + " width=" + size.getWidth() + " height=" + size.getHeight() + " border=1 align=middle>&emsp;";
					}

					menu.add(new JLabel(tweet));

					DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, OptionsPanel.getLocaleLang());
					menu.add(new JLabel("<html><b><i>" + df.format(status.getCreatedAt()), SwingConstants.CENTER));

					Point mouse = MouseInfo.getPointerInfo().getLocation();
					menu.show(null, (int) mouse.getX() + 50, (int) ((int) mouse.getY() - menu.getPreferredSize().getHeight() - 20));
					break;
				}
			}
		}
	}
}