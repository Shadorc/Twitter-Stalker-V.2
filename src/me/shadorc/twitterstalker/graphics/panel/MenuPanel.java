package me.shadorc.twitterstalker.graphics.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;

import me.shadorc.twitterstalker.graphics.Button;
import me.shadorc.twitterstalker.graphics.Frame;
import me.shadorc.twitterstalker.graphics.Storage;
import me.shadorc.twitterstalker.graphics.TextField.Text;

public class MenuPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JButton statistics, comparison, archive, options;

	public MenuPanel() {
		super(new BorderLayout());
		this.setBackground(new Color(3,169,244));

		JLabel title = new JLabel(Storage.tra("Que voulez-vous faire ?"), JLabel.CENTER);

		title.setForeground(new Color(33,33,33));
		title.setFont(Frame.getFont("RobotoCondensed-LightItalic.ttf", 72));
		title.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(183,183,183)), BorderFactory.createEmptyBorder(25, 0, 25, 0)));
		this.add(title, BorderLayout.PAGE_START);

		JPanel center = new JPanel(new BorderLayout());
		center.setBackground(new Color(2,136,209));

		JPanel optionButtonPane = new JPanel(new GridLayout(6, 0));
		optionButtonPane.setOpaque(false);
		for(int i = 0; i < 5; i++) {
			optionButtonPane.add(new JLabel());
		}
		options = new Button("Options", new int[] {15, 10, 0, 0}, true, this);
		optionButtonPane.add(options);

		center.add(optionButtonPane, BorderLayout.WEST);

		JPanel buttons = new JPanel(new GridLayout(3, 0, 50, 50));
		buttons.setOpaque(false);
		buttons.setBorder(BorderFactory.createEmptyBorder(65, 155, 65, 155));

		statistics = this.createJButton(Storage.tra("Statistiques"));
		buttons.add(statistics);

		comparison = this.createJButton(Storage.tra("Comparaison"));
		buttons.add(comparison);

		archive = this.createJButton(Storage.tra("Archive"));
		buttons.add(archive);

		center.add(buttons, BorderLayout.CENTER);

		//This button is to have equal distance between border and menu
		JButton fictitious = new JButton(new ImageIcon(this.getClass().getResource("/res/fictitious.png")));
		fictitious.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
		fictitious.setContentAreaFilled(false);
		fictitious.setFocusable(false);
		center.add(fictitious, BorderLayout.EAST);

		this.add(center, BorderLayout.CENTER);
	}

	private JButton createJButton(String name) {
		Font font = Frame.getFont("RobotoCondensed-Regular.ttf", 72);

		JButton bu = new JButton(name);
		bu.setFont(font);
		bu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				JButton bu = (JButton) e.getSource();
				bu.setBackground(new Color(120, 180, 215));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				JButton bu = (JButton) e.getSource();
				bu.setBackground(new Color(179,229,252));
			}
		});
		bu.addActionListener(this);
		bu.setFocusable(false);
		bu.setBackground(new Color(179,229,252));
		bu.setForeground(new Color(114,114,114));
		bu.setBorder(BorderFactory.createLineBorder(new Color(72,154,199), 2));
		return bu;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == statistics) {
			Frame.setPanel(new ConnectionPanel(Storage.tra(Text.USERNAME)));
		} else if(e.getSource() == comparison) {
			Frame.setPanel(new ConnectionPanel(Storage.tra(Text.COMPARISON)));
		} else if(e.getSource() == archive) {
			Frame.setPanel(new ConnectionPanel(Storage.tra(Text.ARCHIVE)));
		} else if(e.getSource() == options) {
			Frame.setPanel(new OptionsPanel());
		}
	}
}
