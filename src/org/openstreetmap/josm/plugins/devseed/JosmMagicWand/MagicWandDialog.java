package org.openstreetmap.josm.plugins.devseed.JosmMagicWand;

import org.openstreetmap.josm.gui.dialogs.ToggleDialog;
import org.openstreetmap.josm.gui.SideButton;

import javax.swing.*;

import java.awt.*;
import java.util.List;

import static org.openstreetmap.josm.tools.I18n.tr;

public class MagicWandDialog extends ToggleDialog {
    private int toleranceValue = 9;
    private int maskCloseValue = 5;
    private int maskOpen = 5;
    private double simplHull = 0.2;
    private double simplPerMor = 0.2;
    private double simplDp = 0.2;

    private final JLabel toleranceJLabel;
    private final JLabel maskCloseJLabel;
    private final JLabel maskOpenJLabel;
    private final JLabel simplHullJLabel;
    private final JLabel simplPerMorJLabel;
    private final JLabel simplDpJLabel;

    public MagicWandDialog() {
        super(tr("Magic Wand"), "magicwand.svg", tr("Open MagicWand windows"), null, 90);

        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        // Jlabels
        toleranceJLabel = new JLabel();
        maskCloseJLabel = new JLabel();
        maskOpenJLabel = new JLabel();
        //
        simplHullJLabel = new JLabel();
        simplPerMorJLabel = new JLabel();
        simplDpJLabel = new JLabel();

        //
        ToolSettings.setTolerance(toleranceValue);
        toleranceJLabel.setText(tr("Tolerance:  " + toleranceValue));
        panel.add(toleranceJLabel);
        panel.add(buildTolerance());
        createLayout(panel, false, List.of(new SideButton[]{}));
    }

    private JPanel buildTolerance() {
        JPanel jpanel = new JPanel(new BorderLayout());
        //
        JSlider jSlider = new JSlider(1, 50, toleranceValue);
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

        return jpanel;
    }
}
