package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.Dialog;

import org.openstreetmap.josm.gui.SideButton;
import org.openstreetmap.josm.gui.dialogs.ToggleDialog;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.Dialog.ButtonActions.AutoAddTagAction;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.Dialog.ButtonActions.SamEncondeAction;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.ToolSettings;

import javax.swing.*;
import java.util.Arrays;

import static org.openstreetmap.josm.tools.I18n.tr;

public class MagicWandDialog extends ToggleDialog {
    // variables
    final private AutoAddTagAction autoAddTagAction = new AutoAddTagAction();
    final private SamEncondeAction samEncondeAction = new SamEncondeAction();

    public MagicWandDialog() {
        super(tr("Magic Wand Config"), "magicwand.svg", tr("Open MagicWand windows"), null, 200, false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        // tolerance
        panel.add(buildTolerancePanel());
        // simplify
        panel.add(buildPolygonHullPanel());
        panel.add(buildDouglaspPanel());
        panel.add(buildTopologyPreservingPanel());
        panel.add(buildChaikinAnglePanel());
        //  buttons
        //  add
        SideButton addTagButton = new SideButton(autoAddTagAction);
        // sam
        SideButton samButton = new SideButton(samEncondeAction);

        createLayout(panel, true, Arrays.asList(addTagButton, samButton));
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
        double min = 0.5;
        int initValue = (int) (0.95 * decimalPlaces);
        //
        simplPolygonHullJLabel.setText("Exterior contour:  " + initValue / decimalPlaces);
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

            simplPolygonHullJLabel.setText(tr("Exterior contour: " + value));
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
        int initValue = 1000;
        //
        simplDouglaspJLabel.setText("Vertices:  " + initValue / decimalPlaces);
        ToolSettings.setSimplifyDouglasP(initValue / decimalPlaces);
        jpanel.add(simplDouglaspJLabel);
        //
        JSlider jSlider = new JSlider(0, (int) (5 * decimalPlaces), initValue);
        jSlider.setPaintTrack(true);
        jSlider.setPaintTicks(true);
        jSlider.setPaintLabels(true);

        jpanel.add(jSlider);
        jSlider.addChangeListener(changeEvent -> {
            JSlider source = (JSlider) changeEvent.getSource();
            double value = source.getValue() / decimalPlaces;
            simplDouglaspJLabel.setText(tr("Vertices: " + value));
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
        int initValue = 1000;
        //
        simplTopologyPreservingJLabel.setText("Topology:  " + initValue / decimalPlaces);
        ToolSettings.setSimplTopologyPreserving(initValue / decimalPlaces);
        jpanel.add(simplTopologyPreservingJLabel);
        //
        JSlider jSlider = new JSlider(0, (int) (5 * decimalPlaces), initValue);
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


}
