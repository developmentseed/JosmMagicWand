package org.openstreetmap.josm.plugins.devseed.JosmMagicWand;

import org.openstreetmap.josm.gui.SideButton;
import org.openstreetmap.josm.gui.dialogs.ToggleDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import static org.openstreetmap.josm.tools.I18n.tr;

public class MagicWandDialog extends ToggleDialog {
    // variables

    public MagicWandDialog() {
        super(tr("Magic Wand"), "magicwand.svg", tr("Open MagicWand windows"), null, 90);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        // tolerance
        panel.add(buildTolerancePanel());
        // simplify
        panel.add(buildPolygonHullPanel());
        panel.add(buildDouglaspPanel());
        panel.add(buildTopologyPreservingPanel());
        panel.add(buildChaikinAnglePanel());
        panel.add(buildAutoAddTag());

        createLayout(panel, true, List.of(new SideButton[]{}));
    }

    private JPanel buildTolerancePanel() {
        JPanel jpanel = new JPanel();
        jpanel.setLayout(new BoxLayout(jpanel, BoxLayout.Y_AXIS));
        //
        int initValue = 9;
        JLabel toleranceJLabel = new JLabel();
        toleranceJLabel.setText("Tolerance:  " + initValue);
        ToolSettings.setTolerance(initValue);
        jpanel.add(toleranceJLabel);
        //
        JSlider jSlider = new JSlider(1, 30, initValue);
        jSlider.setPaintTrack(true);
        jSlider.setPaintTicks(true);
        jSlider.setPaintLabels(true);
        jSlider.setMajorTickSpacing(5);
        jSlider.setMinorTickSpacing(0);

        jpanel.add(jSlider);
        jSlider.addChangeListener(changeEvent -> {
            JSlider source = (JSlider) changeEvent.getSource();
            int value = source.getValue();
            toleranceJLabel.setText(tr("Tolerance:  " + value));
            ToolSettings.setTolerance(value);
        });
        jpanel.add(new JSeparator());

        return jpanel;
    }

    private JPanel buildPolygonHullPanel() {
        JPanel jpanel = new JPanel();
        jpanel.setLayout(new BoxLayout(jpanel, BoxLayout.Y_AXIS));
        //
        JLabel simplPolygonHullJLabel = new JLabel();
        double decimalPlaces = Math.pow(10, 3);
        double min = 0.7;
        int initValue = (int) (0.95 * decimalPlaces);
        //
        simplPolygonHullJLabel.setText("Polygon Hull:  " + initValue / decimalPlaces);
        ToolSettings.setSimplPolygonHull(initValue / decimalPlaces);
        jpanel.add(simplPolygonHullJLabel);
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

            simplPolygonHullJLabel.setText(tr("Polygon Hull: " + value));
            ToolSettings.setSimplPolygonHull(value);
        });
        jpanel.add(new JSeparator());

        return jpanel;
    }

    private JPanel buildDouglaspPanel() {
        JPanel jpanel = new JPanel();
        jpanel.setLayout(new BoxLayout(jpanel, BoxLayout.Y_AXIS));
        //
        JLabel simplDouglaspJLabel = new JLabel();
        double decimalPlaces = Math.pow(10, 3);
        int initValue = 2000;
        //
        simplDouglaspJLabel.setText("Douglas:  " + initValue / decimalPlaces);
        ToolSettings.setSimplifyDouglasP(initValue / decimalPlaces);
        jpanel.add(simplDouglaspJLabel);
        //
        JSlider jSlider = new JSlider(0, (int) (3 * decimalPlaces), initValue);
        jSlider.setPaintTrack(true);
        jSlider.setPaintTicks(true);
        jSlider.setPaintLabels(true);

        jpanel.add(jSlider);
        jSlider.addChangeListener(changeEvent -> {
            JSlider source = (JSlider) changeEvent.getSource();
            double value = source.getValue() / decimalPlaces;
            simplDouglaspJLabel.setText(tr("Douglas: " + value));
            ToolSettings.setSimplifyDouglasP(value);
        });
        jpanel.add(new JSeparator());

        return jpanel;
    }

    private JPanel buildTopologyPreservingPanel() {
        JPanel jpanel = new JPanel();
        jpanel.setLayout(new BoxLayout(jpanel, BoxLayout.Y_AXIS));
        //
        JLabel simplTopologyPreservingJLabel = new JLabel();
        double decimalPlaces = Math.pow(10, 3);
        int initValue = 2000;
        //
        simplTopologyPreservingJLabel.setText("Topology:  " + initValue / decimalPlaces);
        ToolSettings.setSimplTopologyPreserving(initValue / decimalPlaces);
        jpanel.add(simplTopologyPreservingJLabel);
        //
        JSlider jSlider = new JSlider(0, (int) (3 * decimalPlaces), initValue);
        jSlider.setPaintTrack(true);
        jSlider.setPaintTicks(true);
        jSlider.setPaintLabels(true);

        jpanel.add(jSlider);
        jSlider.addChangeListener(changeEvent -> {
            JSlider source = (JSlider) changeEvent.getSource();
            double value = source.getValue() / decimalPlaces;
            simplTopologyPreservingJLabel.setText(tr("Topology: " + value));
            ToolSettings.setSimplTopologyPreserving(value);
        });
        jpanel.add(new JSeparator());

        return jpanel;
    }

    private JPanel buildChaikinAnglePanel() {
        JPanel jpanel = new JPanel();
        jpanel.setLayout(new BoxLayout(jpanel, BoxLayout.Y_AXIS));
        //
        int initValue = 110;
        double minValue = 20.0;
        JLabel chaikinSmootherAngleJLabel = new JLabel();
        chaikinSmootherAngleJLabel.setText("Smooth Angle:  " + initValue);
        ToolSettings.setChaikinSmooAngle(initValue);
        jpanel.add(chaikinSmootherAngleJLabel);
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
            chaikinSmootherAngleJLabel.setText(tr("Smooth Angle: " + value));
            ToolSettings.setChaikinSmooAngle(value);
        });
        jpanel.add(new JSeparator());

        return jpanel;
    }

    private JPanel buildAutoAddTag() {
        JPanel jpanel = new JPanel();
        jpanel.setLayout(new FlowLayout());
        JButton button = new JButton("add Tag");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TagsDialog tagsDialog = new TagsDialog();
                if (tagsDialog.getValue() != 1) return;
                tagsDialog.saveSettings();
            }
        });
        jpanel.add(button);
        return jpanel;
    }

}
