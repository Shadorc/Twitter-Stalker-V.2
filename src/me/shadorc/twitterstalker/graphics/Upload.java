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
import java.util.ArrayList;

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

	private static double HEIGHT_LIMIT = 2000;

	private String message;
	private File screen;

	public Upload(String message, Container panel) {

		this.message = message;
		this.screen = new File("./screen.png");

		try {
			BufferedImage image = this.getScreenshot(panel);
			if(image.getHeight() > HEIGHT_LIMIT) {
				image = this.splitImage(image, (int) Math.ceil(image.getHeight()/HEIGHT_LIMIT));
			}
			image = this.addBorder(image);

			/*Resize Image for better Twitter's viewing*/
			Image tmp = image.getScaledInstance(-1, 570, Image.SCALE_SMOOTH);
			image = new BufferedImage(tmp.getWidth(null), tmp.getHeight(null), BufferedImage.TYPE_INT_ARGB);

			Graphics2D g2d = image.createGraphics();
			g2d.drawImage(tmp, 0, 0, null);
			g2d.dispose();

			ImageIO.write(image, "png", screen);
			this.showPreview();

		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, Storage.tra("screenshotError") + e.getMessage(), Storage.tra("error"), JOptionPane.ERROR_MESSAGE);

		} finally {
			Frame.reset();
		}
	}

	private BufferedImage getScreenshot(Container panel) {
		Dimension size = new Dimension((int) panel.getPreferredSize().getWidth()+50, (int) panel.getPreferredSize().getHeight()+10);
		panel.setSize(size);

		this.layoutComponent(panel);

		BufferedImage img = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);

		//Remove buttons panel
		panel.getComponent(2).setVisible(false);

		CellRendererPane crp = new CellRendererPane();
		crp.add(panel);
		crp.paintComponent(img.createGraphics(), panel, crp, panel.getBounds());

		//Re-add buttons panel
		panel.getComponent(2).setVisible(true);

		return img;
	}

	private void layoutComponent(Component comp) {
		synchronized (comp.getTreeLock()) {
			comp.doLayout();
			if(comp instanceof Container) {
				for(Component child : ((Container) comp).getComponents()) {
					this.layoutComponent(child);
				}
			}
		}
	}

	private BufferedImage splitImage(BufferedImage image, int split) {

		ArrayList <BufferedImage> images = new ArrayList <BufferedImage> ();

		for(int i = 0; i < split; i++) {
			int startY = (i > 0) ? (images.get(i-1).getHeight()*i) : 0;
			images.add(image.getSubimage(0, startY, image.getWidth(), image.getHeight()/split));
		}

		//Border between two images
		int borderSize = 1;

		BufferedImage img = new BufferedImage(image.getWidth()*split + borderSize*(split-1), image.getHeight()/split, BufferedImage.TYPE_INT_RGB);

		Graphics2D g = img.createGraphics();
		for(int  i = 0; i < split; i++) {
			int startX = (i > 0) ? (images.get(i-1).getWidth()*i + borderSize*i) : 0;
			g.drawImage(images.get(i), startX, 0, null);
		}
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

	private void showPreview() {
		final StatusUpdate status = new StatusUpdate("Twitter Stalker [http://lc.cx/TSDL] : " +  message + ".");
		status.setMedia(screen);

		JFrame frame = new JFrame(Storage.tra("share"));
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

		panel.add(new JLabel(new ImageIcon(screen.getPath())), BorderLayout.PAGE_START);

		JLabel text = new JLabel(status.getStatus(), JLabel.CENTER);
		text.setFont(Frame.getFont("SEGOEUI.TTF", 25));
		panel.add(text, BorderLayout.CENTER);

		final JPanel buttonsPanel = new JPanel(new GridLayout(0, 3));
		buttonsPanel.setOpaque(false);

		final JButton tweet = new JButton(Storage.tra("tweet"));
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
				info.setText(Storage.tra("loading"));
				buttonsPanel.removeAll();
				buttonsPanel.add(new JLabel());
				buttonsPanel.add(info);
				buttonsPanel.revalidate();
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Frame.getTwitter().updateStatus(status);
							info.setText(Storage.tra("finished"));
						} catch (TwitterException e) {
							info.setForeground(Color.RED);
							info.setText(Storage.tra("error") + " ! " + e.getMessage());
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
}