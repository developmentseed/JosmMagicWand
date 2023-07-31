package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils;

import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.tools.ImageProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel {

    private BufferedImage image;
    private JButton zoomJButton;
    private JButton deleteJButton;
    private int maxHeight;
    private int maxWidth;


    private SamImage samImage;

    private JLabel imageLabel;

    public ImagePanel(SamImage samImage, int maxWidth) {
        setLayout(null);
        this.samImage = samImage;
        this.image = samImage.getLayerImage();
        this.maxWidth = maxWidth;

        this.maxHeight = getMaxHeight(samImage.getLayerImage(), maxWidth);

        setupButtons();
        // image
        Graphics2D g2d = this.image.createGraphics();
        g2d.dispose();
        ImageIcon icono = new ImageIcon(getScaledImage(this.image, maxWidth, this.maxHeight));
        imageLabel = new JLabel(icono);
        add(imageLabel);
        imageLabel.setBounds(0, 0, this.maxWidth, this.maxHeight);
        setPreferredSize(new Dimension(this.maxWidth, this.maxHeight));
        // setup actions

    }

    private Image getScaledImage(BufferedImage originalImage, int maxWidth, int maxHeight) {
        int width = originalImage.getWidth();

        if (width <= maxWidth) {
            return originalImage;
        }

        return originalImage.getScaledInstance(maxWidth, maxHeight, Image.SCALE_SMOOTH);
    }

    private int getMaxHeight(BufferedImage originalImage, int maxWidth) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        double radio = (double) width / height;

        if (width <= maxWidth) {
            return height;
        }
        return (int) (maxWidth / radio);

    }

    private void setupButtons() {
        // Creamos los botones
        ImageIcon zoomIco = new ImageProvider("dialogs", "zoom-best-fit").get();
        ImageIcon deleteIco = new ImageProvider("dialogs", "delete").get();

        zoomJButton = new JButton(zoomIco);

        zoomJButton.setBounds(20, this.maxHeight - 32, zoomIco.getIconWidth(), zoomIco.getIconHeight());
        zoomJButton.setContentAreaFilled(false);

        zoomJButton.addActionListener(e -> zoomAction());
        add(zoomJButton);

        deleteJButton = new JButton(deleteIco);
        deleteJButton.setBounds(70, this.maxHeight - 32, deleteIco.getIconWidth(), deleteIco.getIconHeight());
        deleteJButton.setContentAreaFilled(false);
        add(deleteJButton);

    }

    private void zoomAction() {
        MapView mapView = MainApplication.getMap().mapView;
        System.out.println("click zoom");
        mapView.zoomTo(samImage.getProjectionBounds());
//        mapView.zoomTo(samImage.getCenter());
    }

}