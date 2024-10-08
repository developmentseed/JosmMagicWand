package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.Ui;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.SideButton;
import org.openstreetmap.josm.gui.dialogs.ToggleDialog;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.Actions.MergeSelectAction;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.Actions.SimplifySelectAction;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.ToolSettings;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.Ui.ButtonActions.AutoAddTagAction;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.Ui.ButtonActions.SamEncondeAction;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils.ImageSamPanelListener;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils.SamImage;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils.SamImageGrid;
import org.openstreetmap.josm.tools.Logging;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static org.openstreetmap.josm.tools.I18n.tr;

public class MagicWandDialog extends ToggleDialog implements ImageSamPanelListener {
    // variables
    private SamImageGrid samImageGrid;
    private final JPanel mainJpanel;
    private OsmDataLayer uneditableLayer = null;
    private final boolean canSamAoi = false;

    public MagicWandDialog() {
        super(tr("Magic Wand"), "magicwand-info.svg", tr("Open MagicWand windows"), null, 200, false);

        mainJpanel = new JPanel();
        mainJpanel.setLayout(new BoxLayout(mainJpanel, BoxLayout.Y_AXIS));

        JPanel optionPanel = new JPanel();
        optionPanel.setLayout(new GridLayout(5, 1, 3, 3));
        // tolerance
        optionPanel.add(buildTolerancePanel());
        // simplify
        optionPanel.add(buildPolygonHullPanel());
        optionPanel.add(buildDouglaspPanel());
        optionPanel.add(buildTopologyPreservingPanel());
        optionPanel.add(buildChaikinAnglePanel());
        // add mainJpanel
        mainJpanel.add(optionPanel);
        // sam image

        mainJpanel.add(buildSamImagesPanel());
        // layer
        initLayer();
        //  buttons
        // mege
        SideButton mergeGeometry = new SideButton(new MergeSelectAction());
        SideButton simplifyGeometry = new SideButton(new SimplifySelectAction());

        //  add
        SideButton addTagButton = new SideButton(new AutoAddTagAction());
        // sam
        SideButton samButton = new SideButton(new SamEncondeAction(this));

        createLayout(mainJpanel, true, Arrays.asList(mergeGeometry,simplifyGeometry, addTagButton, samButton));
    }

    private JPanel buildTolerancePanel() {
        JPanel jpanel = new JPanel();
        jpanel.setLayout(new BoxLayout(jpanel, BoxLayout.Y_AXIS));
        jpanel.setPreferredSize(new Dimension(0, 25));
        int initValue = 9;
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Tolerance:  " + initValue);
        jpanel.setBorder(titledBorder);
        ToolSettings.setTolerance(initValue);
        //
        JSlider jSlider = new JSlider(1, 30, initValue);
        jSlider.setPaintTrack(true);
        jSlider.setPaintTicks(true);
        jSlider.setPaintLabels(true);

        jpanel.add(jSlider);
        jSlider.addChangeListener(changeEvent -> {
            JSlider source = (JSlider) changeEvent.getSource();
            int value = source.getValue();
            titledBorder.setTitle(tr("Tolerance:  " + value));
            ToolSettings.setTolerance(value);
            jpanel.repaint();
        });

        return jpanel;
    }

    private JPanel buildPolygonHullPanel() {
        JPanel jpanel = new JPanel();
        jpanel.setLayout(new BoxLayout(jpanel, BoxLayout.Y_AXIS));
        jpanel.setPreferredSize(new Dimension(0, 25));
        //
        double decimalPlaces = Math.pow(10, 3);
        double min = 0.5;
        int initValue = (int) (0.95 * decimalPlaces);
        //
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Exterior contour:  " + initValue / decimalPlaces);
        jpanel.setBorder(titledBorder);

        ToolSettings.setSimplPolygonHull(initValue / decimalPlaces);
        //
        JSlider jSlider = new JSlider((int) (min * decimalPlaces), (int) decimalPlaces, initValue);
        jSlider.setPaintTrack(true);
        jSlider.setPaintTicks(true);
        jSlider.setPaintLabels(true);

        jpanel.add(jSlider);
        jSlider.addChangeListener(changeEvent -> {
            JSlider source = (JSlider) changeEvent.getSource();
            double value = source.getValue();
            if (value <= (min * decimalPlaces)) {
                value = 0.0;
            } else {
                value /= decimalPlaces;
            }

            titledBorder.setTitle(tr("Exterior contour: " + value));
            ToolSettings.setSimplPolygonHull(value);
            jpanel.repaint();
        });


        return jpanel;
    }

    private JPanel buildDouglaspPanel() {
        JPanel jpanel = new JPanel();
        jpanel.setLayout(new BoxLayout(jpanel, BoxLayout.Y_AXIS));
        //
        double decimalPlaces = Math.pow(10, 3);
        int initValue = 1000;
        //
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Vertices:  " + initValue / decimalPlaces);
        jpanel.setBorder(titledBorder);
        ToolSettings.setSimplDouglasP(initValue / decimalPlaces);
        //
        JSlider jSlider = new JSlider(0, (int) (5 * decimalPlaces), initValue);
        jSlider.setPaintTrack(true);
        jSlider.setPaintTicks(true);
        jSlider.setPaintLabels(true);

        jpanel.add(jSlider);
        jSlider.addChangeListener(changeEvent -> {
            JSlider source = (JSlider) changeEvent.getSource();
            double value = source.getValue() / decimalPlaces;
            titledBorder.setTitle(tr("Vertices: " + value));
            ToolSettings.setSimplDouglasP(value);
            jpanel.repaint();
        });


        return jpanel;
    }

    private JPanel buildTopologyPreservingPanel() {
        JPanel jpanel = new JPanel();
        jpanel.setLayout(new BoxLayout(jpanel, BoxLayout.Y_AXIS));
        //
        double decimalPlaces = Math.pow(10, 3);
        int initValue = 1000;
        //
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Topology:  " + initValue / decimalPlaces);
        jpanel.setBorder(titledBorder);
        ToolSettings.setSimplTopologyPreserving(initValue / decimalPlaces);
        //
        JSlider jSlider = new JSlider(0, (int) (5 * decimalPlaces), initValue);
        jSlider.setPaintTrack(true);
        jSlider.setPaintTicks(true);
        jSlider.setPaintLabels(true);

        jpanel.add(jSlider);
        jSlider.addChangeListener(changeEvent -> {
            JSlider source = (JSlider) changeEvent.getSource();
            double value = source.getValue() / decimalPlaces;
            titledBorder.setTitle(tr("Topology: " + value));
            ToolSettings.setSimplTopologyPreserving(value);
            jpanel.repaint();
        });


        return jpanel;
    }

    private JPanel buildChaikinAnglePanel() {
        JPanel jpanel = new JPanel();
        jpanel.setLayout(new BoxLayout(jpanel, BoxLayout.Y_AXIS));
        //
        int initValue = 110;
        double minValue = 20.0;
        //
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Smooth Angle:  " + initValue);
        jpanel.setBorder(titledBorder);
        ToolSettings.setChaikinSmooAngle(initValue);
        //
        JSlider jSlider = new JSlider((int) minValue, 170, initValue);
        jSlider.setPaintTrack(true);
        jSlider.setPaintTicks(true);
        jSlider.setPaintLabels(true);

        jpanel.add(jSlider);
        jSlider.addChangeListener(changeEvent -> {
            JSlider source = (JSlider) changeEvent.getSource();
            double value = source.getValue();
            if (value <= minValue) {
                value = 0.0;
            }
            titledBorder.setTitle(tr("Smooth Angle: " + value));
            ToolSettings.setChaikinSmooAngle(value);
            jpanel.repaint();
        });


        return jpanel;
    }

    private JPanel buildSamImagesPanel() {
        samImageGrid = new SamImageGrid(150);
        samImageGrid.setLayout(new GridLayout(0, 2, 2, 3));

        return samImageGrid;
    }

    private void initLayer() {
        if (uneditableLayer == null || !MainApplication.getLayerManager().containsLayer(uneditableLayer)) {
            DataSet bboxDataset = new DataSet();
            uneditableLayer = new OsmDataLayer(bboxDataset, "Magic Wand Uneditable Layer", null);
            uneditableLayer.setUploadDiscouraged(false);
        }
    }

    @Override
    public void onAddSamImage(SamImage samImage) {
        samImageGrid.addSamImage(samImage);
    }

    @Override
    public void onRemoveAll() {
        samImageGrid.removeAllSamImage();
    }

    @Override
    public ArrayList<SamImage> getSamImageList() {
        return samImageGrid.getSamImageList();
    }

    @Override
    public SamImage getSamImageIncludepoint(double x, double y) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate coordinate = new Coordinate(x, y);
        Point point = geometryFactory.createPoint(coordinate);

        return samImageGrid.getSamImageIncludepoint(point);
    }

    @Override
    public void addLayer() {
        try {
            initLayer();

            Layer activeLayer = MainApplication.getLayerManager().getActiveDataLayer();
            if (activeLayer == null) {
                activeLayer = new OsmDataLayer(new DataSet(), "Data Layer ", null);
                MainApplication.getLayerManager().addLayer(activeLayer);
            }
            int indexActiveLayer = MainApplication.getLayerManager().getLayers().indexOf(activeLayer);

            if (!MainApplication.getLayerManager().containsLayer(uneditableLayer)) {
                MainApplication.getLayerManager().addLayer(uneditableLayer);
                MainApplication.getLayerManager().setActiveLayer(activeLayer);
            }
            int indexUneditableLayer = MainApplication.getLayerManager().getLayers().indexOf(uneditableLayer);

            if (indexActiveLayer >= indexUneditableLayer) {
                MainApplication.getLayerManager().moveLayer(uneditableLayer, indexActiveLayer);
            }
        } catch (Exception e) {
            Logging.error(e);
        }
    }

    @Override
    public void addBboxLayer(Way way) {
        for (Node node : way.getNodes()) {
            if (!uneditableLayer.getDataSet().containsNode(node)) {
                uneditableLayer.getDataSet().addPrimitive(node);
            }
        }
        uneditableLayer.getDataSet().addPrimitive(way);
    }

}
