package org.openstreetmap.josm.plugins.devseed.JosmMagicWand;

import org.openstreetmap.josm.gui.dialogs.ToggleDialog;
import org.openstreetmap.josm.gui.SideButton;

import javax.swing.*;

import java.util.List;

import static org.openstreetmap.josm.tools.I18n.tr;

public class MagicWandDialog extends ToggleDialog {
    // general
    private int toleranceValue = 9;

    // Simplify
    private int simplDouglaspValue = 10;

    private int simplPolygonHullValue = 20;
    private int simplTopologyPreservingValue = 2;

    private final JLabel toleranceJLabel;

    private final JLabel simplPolygonHullJLabel;
    private final JLabel simplTopologyPreservingJLabel;
    private final JLabel simplDouglaspJLabel;

    public MagicWandDialog() {
        super(tr("Magic Wand"), "magicwand.svg", tr("Open MagicWand windows"), null, 90);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        // panel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        // Jlabels
        toleranceJLabel = new JLabel();
        //
        simplPolygonHullJLabel = new JLabel();
        simplTopologyPreservingJLabel = new JLabel();
        simplDouglaspJLabel = new JLabel();
        // tolerance
        panel.add(buildTolerancePanel());
//        simplify
        panel.add(buildPolygonHullPanel());
        panel.add(buildDouglaspPanel());
        panel.add(buildTopologyPreservingPanel());

        createLayout(panel, true, List.of(new SideButton[]{}));
    }

    private JPanel buildTolerancePanel() {
        JPanel jpanel = new JPanel();
        jpanel.setLayout(new BoxLayout(jpanel, BoxLayout.Y_AXIS));
        //
        toleranceJLabel.setText("Tolerance:  " + toleranceValue);
        ToolSettings.setTolerance(toleranceValue);
        jpanel.add(toleranceJLabel);

        //
        JSlider jSlider = new JSlider(1, 30, toleranceValue);
        jSlider.setPaintTrack(true);
        jSlider.setPaintTicks(true);
        jSlider.setPaintLabels(true);
        jSlider.setMajorTickSpacing(5);
        jSlider.setMinorTickSpacing(0);

        jpanel.add(jSlider);
        jSlider.addChangeListener(changeEvent -> {
            JSlider source = (JSlider) changeEvent.getSource();
            toleranceValue = source.getValue();
            toleranceJLabel.setText(tr("Tolerance:  " + toleranceValue));
            ToolSettings.setTolerance(toleranceValue);
        });
        jpanel.add(new JSeparator());

        return jpanel;
    }

    private JPanel buildPolygonHullPanel() {
        JPanel jpanel = new JPanel();
        jpanel.setLayout(new BoxLayout(jpanel, BoxLayout.Y_AXIS));
        //
        int decimalPlaces = 3;
        //
        simplPolygonHullJLabel.setText("Polygon Hull:  " + simplPolygonHullValue / Math.pow(10, decimalPlaces));
        ToolSettings.setSimplPolygonHull(simplPolygonHullValue / Math.pow(10, decimalPlaces));
        jpanel.add(simplPolygonHullJLabel);
        //
        JSlider jSlider = new JSlider(0, (int) Math.pow(10, decimalPlaces), simplPolygonHullValue);
        jSlider.setPaintTrack(true);
        jSlider.setPaintTicks(true);
        jSlider.setPaintLabels(true);

        jpanel.add(jSlider);
        jSlider.addChangeListener(changeEvent -> {
            JSlider source = (JSlider) changeEvent.getSource();
            double simplPolygonHullValueDouble = source.getValue() / Math.pow(10, decimalPlaces);
            simplPolygonHullJLabel.setText(tr("Polygon Hull: " + simplPolygonHullValueDouble));
            ToolSettings.setSimplPolygonHull(simplPolygonHullValueDouble);
        });
        jpanel.add(new JSeparator());

        return jpanel;
    }

    private JPanel buildDouglaspPanel() {
        JPanel jpanel = new JPanel();
        jpanel.setLayout(new BoxLayout(jpanel, BoxLayout.Y_AXIS));
        //
        int decimalPlaces = 5;
        //
        simplDouglaspJLabel.setText("Douglas:  " + simplDouglaspValue / Math.pow(10, decimalPlaces));
        ToolSettings.setSimplifyDouglasP(simplDouglaspValue / Math.pow(10, decimalPlaces));
        jpanel.add(simplDouglaspJLabel);
        //
        JSlider jSlider = new JSlider(0, (int) Math.pow(10, decimalPlaces-2), simplDouglaspValue);
        jSlider.setPaintTrack(true);
        jSlider.setPaintTicks(true);
        jSlider.setPaintLabels(true);

        jpanel.add(jSlider);
        jSlider.addChangeListener(changeEvent -> {
            JSlider source = (JSlider) changeEvent.getSource();
            double simplDouglaspDouble = source.getValue() / Math.pow(10, decimalPlaces);
            simplDouglaspJLabel.setText(tr("Douglas: " + simplDouglaspDouble));
            ToolSettings.setSimplifyDouglasP(simplDouglaspDouble);
        });
        jpanel.add(new JSeparator());

        return jpanel;
    }

    private JPanel buildTopologyPreservingPanel() {
        JPanel jpanel = new JPanel();
        jpanel.setLayout(new BoxLayout(jpanel, BoxLayout.Y_AXIS));
        //
        int decimalPlaces = 5;
        //
        simplTopologyPreservingJLabel.setText("Topology:  " + simplTopologyPreservingValue / Math.pow(10, decimalPlaces));
        ToolSettings.setSimplTopologyPreserving(simplTopologyPreservingValue / Math.pow(10, decimalPlaces));
        jpanel.add(simplTopologyPreservingJLabel);
        //
        JSlider jSlider = new JSlider(0, (int) Math.pow(10, decimalPlaces-2), simplTopologyPreservingValue);
        jSlider.setPaintTrack(true);
        jSlider.setPaintTicks(true);
        jSlider.setPaintLabels(true);

        jpanel.add(jSlider);
        jSlider.addChangeListener(changeEvent -> {
            JSlider source = (JSlider) changeEvent.getSource();
            double simplTopologyPreservingDouble = source.getValue() / Math.pow(10, decimalPlaces);
            simplTopologyPreservingJLabel.setText(tr("Topology: " + simplTopologyPreservingDouble));
            ToolSettings.setSimplTopologyPreserving(simplTopologyPreservingDouble);
        });
        jpanel.add(new JSeparator());

        return jpanel;
    }

}
