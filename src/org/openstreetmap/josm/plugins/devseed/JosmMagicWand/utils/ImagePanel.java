package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.UndoRedoHandler;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.Notification;
import org.openstreetmap.josm.tools.ImageProvider;
import org.openstreetmap.josm.tools.Logging;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.openstreetmap.josm.tools.I18n.tr;

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

//        temporal add bbox
        List<Double> bbox = samImage.getBoox();
        Coordinate[] coordinates = new Coordinate[5];
        coordinates[0] = new Coordinate(bbox.get(0), bbox.get(1));
        coordinates[1] = new Coordinate(bbox.get(0), bbox.get(3));
        coordinates[2] = new Coordinate(bbox.get(2), bbox.get(3));
        coordinates[3] = new Coordinate(bbox.get(2), bbox.get(1));
        coordinates[4] = coordinates[0]; // Cerrar el polígono

        DataSet ds = MainApplication.getLayerManager().getEditDataSet();

        String tagKey = "magic_wand_bbox";
        String tagValue = "yes";


        if (samImage != null) {
            List<Geometry> geometriesMercator = new ArrayList<>();

            try {
                geometriesMercator.add(CommonUtils.coordinates2Geometry(Arrays.stream(coordinates).collect(Collectors.toList()), true));

                Collection<Command> cmds = CommonUtils.geometry2WayCommands(ds, geometriesMercator, tagKey, tagValue);
                UndoRedoHandler.getInstance().add(new SequenceCommand(tr("generate sam ways"), cmds));

            } catch (Exception e) {
                Logging.error(e);
            }

        } else {
            new Notification(tr("Click inside of active AOI to enable Segment Anything Model.")).setIcon(JOptionPane.ERROR_MESSAGE).setDuration(Notification.TIME_SHORT).show();
        }

    }

}