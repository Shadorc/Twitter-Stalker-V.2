package me.shadorc.twitterstalker.utility;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import me.shadorc.infonet.Infonet;
import me.shadorc.twitterstalker.graphics.SearchField;
import me.shadorc.twitterstalker.graphics.SearchField.Text;
import me.shadorc.twitterstalker.graphics.panel.MenuPanel;
import me.shadorc.twitterstalker.storage.Storage;

public class ArchiveFile {

	public static File getFile(SearchField field) {
		File file = null;
		while(file == null || !file.exists()) {

			//Change UIManager look to look like the operating system one, this is for the JFileChooser
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
				e.printStackTrace();
			}

			JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.home"), "Desktop"));
			JButton help = new JButton(Storage.tra("archiveHelp"));
			help.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JEditorPane textPane = new JEditorPane("text/html", Storage.tra("archiveHelpText"));
					textPane.setEditable(false);
					textPane.setOpaque(false);
					textPane.addHyperlinkListener(new HyperlinkListener() {
						@Override
						public void hyperlinkUpdate(HyperlinkEvent he) {
							if(he.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
								Infonet.open(he.getURL().toString(), true);
							}
						}
					});

					JOptionPane.showMessageDialog(null, textPane, Storage.tra("archiveHelp"), JOptionPane.QUESTION_MESSAGE, Ressources.getBigIcon());
				}
			});
			chooser.add(help, BorderLayout.SOUTH);
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			int choice = chooser.showOpenDialog(null);

			//Reset UIManager look to avoid changing buttons, drop-downs menus...
			try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
				e.printStackTrace();
			}

			if(choice == JFileChooser.APPROVE_OPTION) {
				file = new File(chooser.getSelectedFile().getPath() + "/data/js/tweets");
			} else if(choice == JFileChooser.CANCEL_OPTION) {
				Ressources.getFrame().setPanel(new MenuPanel());
				return null;
			}

			if(!file.exists()) {
				field.setErrorText(Storage.tra(Text.INVALID_ARCHIVE));
			}
		}
		return file;
	}
}
