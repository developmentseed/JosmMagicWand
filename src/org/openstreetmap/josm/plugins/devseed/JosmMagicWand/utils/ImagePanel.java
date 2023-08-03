package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils;

import org.openstreetmap.josm.data.ProjectionBounds;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.tools.ImageProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;


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

        addLayer();

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
        ProjectionBounds projectionBounds = samImage.getProjectionBounds();
        mapView.zoomTo(projectionBounds);
        mapView.zoomIn();
        mapView.repaint();
    }

    private void addLayer() {
        DataSet dataSet = new DataSet();
        Layer activeLayer = MainApplication.getLayerManager().getActiveDataLayer();

        int activeLayerIndex = MainApplication.getLayerManager().getLayers().indexOf(activeLayer);
        EastNorth center = MainApplication.getMap().mapView.getCenter();
        double scale = MainApplication.getMap().mapView.getScale();

        List<Double> bbox = samImage.getBoox();

        Node node1 = new Node(new EastNorth(bbox.get(0), bbox.get(1)));
        Node node2 = new Node(new EastNorth(bbox.get(0), bbox.get(3)));
        Node node3 = new Node(new EastNorth(bbox.get(2), bbox.get(3)));
        Node node4 = new Node(new EastNorth(bbox.get(2), bbox.get(1)));

        Way way = new Way();
        way.addNode(node1);
        way.addNode(node2);
        way.addNode(node3);
        way.addNode(node4);
        way.addNode(node1);
        //
        dataSet.addPrimitive(node1);
        dataSet.addPrimitive(node2);
        dataSet.addPrimitive(node3);
        dataSet.addPrimitive(node4);
        dataSet.addPrimitive(way);

        OsmDataLayer uneditableLayer = new OsmDataLayer(dataSet, "Uneditable Layer", null);
        uneditableLayer.setUploadDiscouraged(true);

        MainApplication.getLayerManager().addLayer(uneditableLayer);
        MainApplication.getLayerManager().moveLayer(uneditableLayer, activeLayerIndex);
        MainApplication.getLayerManager().setActiveLayer(activeLayer);



    }


}