package me.shadorc.twitterstalker.statistics;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import me.shadorc.twitterstalker.graphics.Storage.Data;

public class PopularePreview implements HyperlinkListener {

	private JPopupMenu menu = new JPopupMenu();
	private Stats stats;
	private JEditorPane pane;

	public PopularePreview(Stats stats, JEditorPane pane) {
		this.stats = stats;
		this.pane = pane;

		menu.setBackground(new Color(79, 182, 246));
		menu.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}

	@Override
	public void hyperlinkUpdate(final HyperlinkEvent he) {
		if(he.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			String url = he.getURL().toString();

			if(!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Action.BROWSE)) {
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(url), null);
				JOptionPane.showMessageDialog(null, "Erreur lors de l'ouverture, l'URL a été copiée dans le presse-papier.", "Erreur", JOptionPane.ERROR_MESSAGE);
			} else {
				try {
					Desktop.getDesktop().browse(new URI(url));
				} catch (IOException | URISyntaxException e) {
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(url), null);
					JOptionPane.showMessageDialog(null, "Erreur lors de l'ouverture, l'URL a été copiée dans le presse-papier.", "Erreur", JOptionPane.ERROR_MESSAGE);
				}
			}

		} else if(he.getEventType() == HyperlinkEvent.EventType.EXITED) {
			menu.setVisible(false);
			menu.removeAll();

		} else if(he.getEventType() == HyperlinkEvent.EventType.ENTERED) {
			for(WordInfo status : stats.get(Data.POPULARE)) {
				if(status.getStatusUrl().equals(he.getURL().toString())) {
					menu.add(new JLabel("<html><font color='white' size=4>&emsp;" + status.getText() + "&emsp;"));
					menu.add(new JLabel("<html><b>" + new SimpleDateFormat("d MMMM yyyy kk'h'mm").format(status.getDate()), SwingConstants.CENTER));
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