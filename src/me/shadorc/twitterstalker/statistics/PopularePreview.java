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

public class PopularePreview implements HyperlinkListener {

	private JPopupMenu menu = new JPopupMenu();
	private JEditorPane pane;
	private Stats stats;

	public PopularePreview(JEditorPane pane, Stats stats) {
		this.pane = pane;
		this.stats = stats;

		menu.setBackground(new Color(79, 182, 246));
		menu.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}

	@Override
	public void hyperlinkUpdate(final HyperlinkEvent he) {
		if(he.getEventType() == EventType.ACTIVATED) {
			Infonet.open(he.getURL().toString(), true);

		} else if(he.getEventType() == EventType.EXITED) {
			menu.setVisible(false);
			menu.removeAll();

		} else if(he.getEventType() == EventType.ENTERED) {
			for(WordInfo status : stats.get(Data.POPULARE)) {
				if(status.getStatusUrl().equals(he.getURL().toString())) {
					menu.add(new JLabel("<html><font color='white' size=4>&emsp;" + status.getText() + "&emsp;"));

					DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, OptionsPanel.getLocaleLang());
					menu.add(new JLabel("<html><b>" + df.format(status.getDate()), SwingConstants.CENTER));

					Point mouse = MouseInfo.getPointerInfo().getLocation();
					menu.show(null, (int) mouse.getX() - 100, (int) mouse.getY() - 60);
					pane.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseExited(MouseEvent arg0) {
							menu.setVisible(false);
							menu.removeAll();
						}
					});
					menu.setVisible(true);
					break;
				}
			}
		}
	}
}