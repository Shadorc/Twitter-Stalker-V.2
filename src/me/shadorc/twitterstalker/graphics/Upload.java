package me.shadorc.twitterstalker.graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.CellRendererPane;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import twitter4j.StatusUpdate;
import twitter4j.TwitterException;

public class Upload implements Runnable {

	private String message;
	private Container panel;
	private File screen;

	public Upload(String message, Container panel) {

		this.message = message;
		this.panel = panel;
		this.screen = new File("./screen.png");

		try {
			ImageIO.write(this.paintComponent(), "png", screen);
			new Thread(this).start();

		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Erreur lors de la capture d'écran, " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);

		} finally {
			Frame.reset();
		}
	}

	@Override
	public void run() {
		final StatusUpdate status = new StatusUpdate("Twitter Stalker [http://lc.cx/TSDL] : " +  message + ".");
		status.setMedia(screen);

		ImageIcon preview = new ImageIcon(screen.getPath());
		//-1 : conserv aspect ratio
		preview = new ImageIcon(preview.getImage().getScaledInstance(preview.getIconWidth()/3, -1, Image.SCALE_SMOOTH));

		JFrame frame = new JFrame("Partager");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				screen.delete();
			}
		} );

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		panel.setBackground(new Color(179, 229, 252));

		panel.add(new JLabel(preview), BorderLayout.PAGE_START);

		JLabel text = new JLabel(status.getStatus(), JLabel.CENTER);
		text.setFont(Frame.getFont("SEGOEUI.TTF", 25));
		panel.add(text, BorderLayout.CENTER);

		JPanel buttonsPanel = new JPanel(new GridLayout(0, 5));
		buttonsPanel.setOpaque(false);
		buttonsPanel.add(new JLabel());
		buttonsPanel.add(new JLabel());

		final JButton tweet = new JButton("Tweeter");
		tweet.setFocusable(false);
		tweet.setFont(Frame.getFont("SEGOEUI.TTF", 25));
		tweet.setBackground(new Color(85,172,238));
		tweet.setBorder(BorderFactory.createLineBorder(new Color(59, 148, 217), 1, true));
		tweet.setForeground(Color.WHITE);

		tweet.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent event) {
				tweet.setBackground(new Color(85,172,238));
			}

			@Override
			public void mouseEntered(MouseEvent event) {
				tweet.setBackground(new Color(59, 148, 217));
			}
		});

		tweet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					Frame.getTwitter().updateStatus(status);
				} catch (TwitterException e) {
					JOptionPane.showMessageDialog(null, "Erreur lors de l'upload, " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				} finally {
					screen.delete();
				}
			}
		});
		buttonsPanel.add(tweet);

		buttonsPanel.add(new JLabel());
		buttonsPanel.add(new JLabel());
		panel.add(buttonsPanel, BorderLayout.PAGE_END);

		frame.setContentPane(panel);
		frame.pack();
		frame.setIconImage(new ImageIcon(this.getClass().getResource("/res/IconeAppli.png")).getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
	}

	private BufferedImage createBorder(BufferedImage bufferedImage) {
		int borderWidth = 2;
		BufferedImage bi = new BufferedImage(bufferedImage.getWidth(null) + 2 * borderWidth, bufferedImage.getHeight(null) + 2 * borderWidth, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		g.setColor(Color.BLACK);
		g.drawImage(bufferedImage, borderWidth, borderWidth, null);
		g.drawRect(0, 0, bi.getWidth(), bi.getHeight());
		g.dispose();

		return bi;
	}

	private BufferedImage paintComponent() {

		Dimension size = new Dimension((int) panel.getPreferredSize().getWidth() + 50, (int) panel.getPreferredSize().getHeight()+10);
		panel.setSize(size);

		this.layoutComponent(panel);

		BufferedImage img = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);

		CellRendererPane crp = new CellRendererPane();
		crp.add(panel);
		crp.paintComponent(img.createGraphics(), panel, crp, panel.getBounds());

		return this.createBorder(img.getSubimage(0, 0, (int) size.getWidth(), (int) size.getHeight()-66));
	}

	private void layoutComponent(Component c) {
		synchronized (c.getTreeLock()) {
			c.doLayout();
			if(c instanceof Container) {
				for(Component child : ((Container) c).getComponents()) {
					this.layoutComponent(child);
				}
			}
		}
	}
}