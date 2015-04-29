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

public class Upload {

	private String message;
	private File screen;

	public Upload(String message, Container panel) {

		this.message = message;
		this.screen = new File("./screen.png");

		try {
			BufferedImage image = this.getScreenshot(panel);
			System.err.println("Taille de la fenêtre : " + image.getWidth());
			//TODO: Change width condition
			if(image.getWidth() > 1000) {
				image = this.splitImage(image);
			}
			image = this.addBorder(image);
			ImageIO.write(image, "png", screen);
			this.showPreview();

		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, Storage.tra("Erreur lors de la capture d'écran, ") + e.getMessage(), Storage.tra("Erreur"), JOptionPane.ERROR_MESSAGE);

		} finally {
			Frame.reset();
		}
	}

	private void showPreview() {
		final StatusUpdate status = new StatusUpdate("Twitter Stalker [http://lc.cx/TSDL] : " +  message + ".");
		status.setMedia(screen);

		ImageIcon preview = new ImageIcon(screen.getPath());
		//-1 : conserv aspect ratio
		preview = new ImageIcon(preview.getImage().getScaledInstance(preview.getIconWidth()/3, -1, Image.SCALE_SMOOTH));
		JFrame frame = new JFrame(Storage.tra("Partager"));
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				screen.delete();
			}
		} );

		final JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		panel.setBackground(new Color(179, 229, 252));

		panel.add(new JLabel(preview), BorderLayout.PAGE_START);

		JLabel text = new JLabel(status.getStatus(), JLabel.CENTER);
		text.setFont(Frame.getFont("SEGOEUI.TTF", 25));
		panel.add(text, BorderLayout.CENTER);

		final JPanel buttonsPanel = new JPanel(new GridLayout(0, 3));
		buttonsPanel.setOpaque(false);

		final JButton tweet = new JButton(Storage.tra("Tweeter"));
		tweet.setFocusable(false);
		tweet.setFont(Frame.getFont("SEGOEUI.TTF", 25));
		tweet.setBackground(new Color(85,172,238));
		tweet.setBorder(BorderFactory.createLineBorder(new Color(59, 148, 217), 1, true));
		tweet.setForeground(Color.WHITE);

		final JLabel info = new JLabel();
		info.setHorizontalAlignment(JLabel.CENTER);
		info.setFont(Frame.getFont("SEGOEUI.TTF", 25));
		info.setBorder(BorderFactory.createLineBorder(new Color(59, 148, 217), 1, true));

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
				buttonsPanel.remove(tweet);
				info.setText(Storage.tra("Chargement..."));
				buttonsPanel.removeAll();
				buttonsPanel.add(new JLabel());
				buttonsPanel.add(info);
				buttonsPanel.revalidate();
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Frame.getTwitter().updateStatus(status);
							info.setText(Storage.tra("Terminé"));
						} catch (TwitterException e) {
							info.setForeground(Color.RED);
							info.setText(Storage.tra("Erreur") + " !" + e.getMessage());
							e.printStackTrace();
						} finally {
							screen.delete();
						}
					}
				}).start();
			}
		});
		buttonsPanel.add(new JLabel());
		buttonsPanel.add(tweet);

		panel.add(buttonsPanel, BorderLayout.PAGE_END);

		frame.setContentPane(panel);
		frame.pack();
		frame.setIconImage(new ImageIcon(this.getClass().getResource("/res/IconeAppli.png")).getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
	}

	private BufferedImage getScreenshot(Container panel) {
		Dimension size = new Dimension((int) panel.getPreferredSize().getWidth() + 50, (int) panel.getPreferredSize().getHeight()+10);
		panel.setSize(size);

		this.layoutComponent(panel);

		BufferedImage img = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);

		//Remove buttons panel
		panel.getComponent(2).setVisible(false);

		CellRendererPane crp = new CellRendererPane();
		crp.add(panel);
		crp.paintComponent(img.createGraphics(), panel, crp, panel.getBounds());

		//Add buttons panel
		panel.getComponent(2).setVisible(true);

		return img;
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

	private BufferedImage splitImage(BufferedImage image) {

		BufferedImage image1 = image.getSubimage(0, 0, image.getWidth(), image.getHeight()/2);
		BufferedImage image2 = image.getSubimage(0, image1.getHeight(), image.getWidth(), image.getHeight()/2);

		//Border between two images
		int borderSize = 1;

		BufferedImage img = new BufferedImage(image1.getWidth() + image2.getWidth() + borderSize, Math.max(image1.getHeight(), image2.getHeight()), BufferedImage.TYPE_INT_RGB);

		Graphics2D g = img.createGraphics();
		g.drawImage(image1, 0, 0, null);
		g.drawImage(image2, image1.getWidth() + borderSize, 0, null);
		g.dispose();

		return img;
	}

	private BufferedImage addBorder(BufferedImage bufferedImage) {
		int borderWidth = 2;
		BufferedImage bi = new BufferedImage(bufferedImage.getWidth(null) + 2 * borderWidth, bufferedImage.getHeight(null) + 2 * borderWidth, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		g.setColor(Color.BLACK);
		g.drawImage(bufferedImage, borderWidth, borderWidth, null);
		g.drawRect(0, 0, bi.getWidth(), bi.getHeight());
		g.dispose();

		return bi;
	}
}