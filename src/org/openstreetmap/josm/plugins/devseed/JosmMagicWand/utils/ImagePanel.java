package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils;

import org.openstreetmap.josm.data.ProjectionBounds;
import org.openstreetmap.josm.data.imagery.ImageryInfo;
import org.openstreetmap.josm.data.imagery.ImageryLayerInfo;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.layer.ImageryLayer;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.tools.ImageProvider;
import org.openstreetmap.josm.tools.Logging;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.Collectors;

import static org.openstreetmap.josm.gui.MainApplication.getMap;


public class ImagePanel extends JPanel {

    private final BufferedImage image;
    private JButton zoomJButton;
    private JButton deleteJButton;
    private final int maxHeight;
    private final int maxWidth;

    private final SamImage samImage;

    private final JLabel imageLabel;
    private final ImagePanelListener listener;

    public ImagePanel(SamImage samImage, int maxWidth, ImagePanelListener listener) {
        setLayout(null);
        this.samImage = samImage;
        this.image = samImage.getLayerImage();
        this.maxWidth = maxWidth;

        this.maxHeight = getMaxHeight(samImage.getLayerImage(), maxWidth);
        this.listener = listener;
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
        deleteJButton.addActionListener(e -> deleteAction());

        add(deleteJButton);

    }

    private void zoomAction() {
        MapView mapView = getMap().mapView;

        try {
            // add layer
            List<Layer> activeLayers = mapView.getLayerManager()
                    .getVisibleLayersInZOrder()
                    .stream()
                    .filter(layer -> (layer instanceof ImageryLayer && layer.isVisible() && layer.getName().equals(samImage.getLayerName())))
                    .collect(Collectors.toList());

            if (activeLayers.isEmpty()) {
                List<ImageryInfo> imagerySources = ImageryLayerInfo
                        .instance
                        .getLayers()
                        .stream()
                        .filter(layer -> (layer.getName().equals(samImage.getLayerName())))
                        .collect(Collectors.toList());

                if (!imagerySources.isEmpty()) {
                    ImageryInfo imageryInfo = imagerySources.get(0);
                    mapView.getLayerManager().addLayer(ImageryLayer.create(imageryInfo));
                }
            }
        } catch (Exception e) {
            Logging.error(e);
        }

        ProjectionBounds projectionBounds = samImage.getProjectionBounds();
        mapView.zoomTo(projectionBounds);
        mapView.zoomIn();
        mapView.repaint();
    }

    private void deleteAction() {
        try {
            samImage.removeCacheImge();
            listener.removeSamImage(samImage);
        } catch (Exception e) {
            Logging.error(e);
        }
    }
}