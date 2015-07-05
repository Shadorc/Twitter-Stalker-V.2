package me.shadorc.twitterstalker.statistics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.DefaultEditorKit;

import me.shadorc.infonet.Infonet;
import me.shadorc.twitterstalker.graphics.EditorPane;
import me.shadorc.twitterstalker.graphics.Storage;
import me.shadorc.twitterstalker.graphics.Storage.Data;
import me.shadorc.twitterstalker.graphics.panel.OptionsPanel;
import twitter4j.MediaEntity;
import twitter4j.MediaEntity.Size;
import twitter4j.Status;

public class PopularPreview implements HyperlinkListener {

	private JPopupMenu menu;
	private JEditorPane editorPane;
	private Stats stats;
	private Data data;

	public PopularPreview(JEditorPane editorPane, Stats stats, Data data) {
		this.editorPane = editorPane;
		this.stats = stats;
		this.data = data;
		this.menu = new JPopupMenu();

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
	public void hyperlinkUpdate(final HyperlinkEvent he) {
		if(he.getEventType() == EventType.ACTIVATED) {
			if(he.getDescription().equals("search")) {
				this.createFrame(editorPane, stats);
			} else {
				Infonet.open(he.getURL().toString(), true);
			}

		} else if(he.getEventType() == EventType.EXITED) {
			menu.setVisible(false);
			menu.removeAll();

		} else if(he.getEventType() == EventType.ENTERED) {
			if(!he.getDescription().equals("search")) {
				for(WordInfo wi : stats.get(data)) {
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

	private void createFrame(final JEditorPane editorPane, final Stats stats) {
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

		saisis.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					for(WordInfo user : stats.get(Data.FIRST_TALK)) {
						if(user.getWord().equals(saisis.getText().replaceAll("@", ""))) {
							String text = editorPane.getText();
							editorPane.setText(text.substring(0, text.indexOf(")")+1) + "<br>&nbsp;&nbsp;- " + user.getFirstTalkInfo());
							frame.dispose();
							return;
						}
					}
				}
			}
		});
		bottom.add(saisis, BorderLayout.CENTER);

		JButton ok = new JButton(Storage.tra("ok"));
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for(WordInfo user : stats.get(Data.FIRST_TALK)) {
					if(user.getWord().equals(saisis.getText().replaceAll("@", ""))) {
						String text = editorPane.getText();
						editorPane.setText(text.substring(0, text.indexOf(")")+1) + "<br>&nbsp;&nbsp;- " + user.getFirstTalkInfo());
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
}