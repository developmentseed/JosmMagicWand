package org.openstreetmap.josm.plugins.devseed.JosmMagicWand;

import org.openstreetmap.josm.gui.SideButton;
import org.openstreetmap.josm.gui.dialogs.ToggleDialog;

import javax.swing.*;
import java.util.List;

import static org.openstreetmap.josm.tools.I18n.tr;

public class MagicWandDialog extends ToggleDialog {
    // general
    private int toleranceValue = 9;

    // Simplify

    final int simplDouglaspValue = 10;

    final int simplTopologyPreservingValue = 20;

    final JLabel toleranceJLabel;

    final JLabel simplPolygonHullJLabel;
    final JLabel simplTopologyPreservingJLabel;
    final JLabel simplDouglaspJLabel;

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
        double decimalPlaces = Math.pow(10, 3);
        double min = 0.7;
        //
        int simplPolygonHullValue = (int) (0.9 * decimalPlaces);
        simplPolygonHullJLabel.setText("Polygon Hull:  " + simplPolygonHullValue / decimalPlaces);
        ToolSettings.setSimplPolygonHull(simplPolygonHullValue / decimalPlaces);
        jpanel.add(simplPolygonHullJLabel);
        //
        JSlider jSlider = new JSlider((int) (min * decimalPlaces), (int) decimalPlaces, simplPolygonHullValue);
        jSlider.setPaintTrack(true);
        jSlider.setPaintTicks(true);
        jSlider.setPaintLabels(true);

        jpanel.add(jSlider);
        jSlider.addChangeListener(changeEvent -> {
            JSlider source = (JSlider) changeEvent.getSource();
            double simplPolygonHullValueDouble = source.getValue();
            if (simplPolygonHullValueDouble <= (min * decimalPlaces)) {
                simplPolygonHullValueDouble = 0.0;
            } else {
                simplPolygonHullValueDouble /= decimalPlaces;
            }

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
        double decimalPlaces = Math.pow(10, 3);

        //
        simplDouglaspJLabel.setText("Douglas:  " + simplDouglaspValue / decimalPlaces);
        ToolSettings.setSimplifyDouglasP(simplDouglaspValue / decimalPlaces);
        jpanel.add(simplDouglaspJLabel);
        //
        JSlider jSlider = new JSlider(0, (int) (0.3 * decimalPlaces), simplDouglaspValue);
        jSlider.setPaintTrack(true);
        jSlider.setPaintTicks(true);
        jSlider.setPaintLabels(true);

        jpanel.add(jSlider);
        jSlider.addChangeListener(changeEvent -> {
            JSlider source = (JSlider) changeEvent.getSource();
            double simplDouglaspDouble = source.getValue() / decimalPlaces;
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
        double decimalPlaces = Math.pow(10, 3);
        //
        simplTopologyPreservingJLabel.setText("Topology:  " + simplTopologyPreservingValue / decimalPlaces);
        ToolSettings.setSimplTopologyPreserving(simplTopologyPreservingValue / decimalPlaces);
        jpanel.add(simplTopologyPreservingJLabel);
        //
        JSlider jSlider = new JSlider(0, (int) (0.3 * decimalPlaces), simplTopologyPreservingValue);
        jSlider.setPaintTrack(true);
        jSlider.setPaintTicks(true);
        jSlider.setPaintLabels(true);

        jpanel.add(jSlider);
        jSlider.addChangeListener(changeEvent -> {
            JSlider source = (JSlider) changeEvent.getSource();
            double simplTopologyPreservingDouble = source.getValue() / decimalPlaces;
            simplTopologyPreservingJLabel.setText(tr("Topology: " + simplTopologyPreservingDouble));
            ToolSettings.setSimplTopologyPreserving(simplTopologyPreservingDouble);
        });
        jpanel.add(new JSeparator());

        return jpanel;
    }

}
