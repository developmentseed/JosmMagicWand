package org.openstreetmap.josm.plugins.devseed.JosmMagicWand;

import org.openstreetmap.josm.gui.dialogs.ToggleDialog;
import org.openstreetmap.josm.gui.SideButton;

import javax.swing.*;

import java.awt.*;
import java.util.List;

import static org.openstreetmap.josm.tools.I18n.tr;

public class MagicWandDialog extends ToggleDialog {
    private int toleranceValue = 7;
    private int maskCloseValue = 5;
    private int maskOpen = 5;
    private final JLabel toleranceJLabel;
    private final JLabel maskCloseJLabel;
    private final JLabel maskOpenJLabel;

    public MagicWandDialog() {
        super(tr("Magic Wand"), "magicwand.svg", tr("Open MagicWand windows"), null, 90);

        JPanel panel = new JPanel(new GridLayout(4, 1));
        panel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        // Jlabels
        toleranceJLabel = new JLabel();
        maskCloseJLabel = new JLabel();
        maskOpenJLabel = new JLabel();

        //
        ToolSettings.setTolerance(toleranceValue);
        toleranceJLabel.setText(tr("Tolerance:  " + toleranceValue));
        panel.add(toleranceJLabel);
        panel.add(buildTolerance());
        // mat close
        // panel.add(buildCloseMat());
        // mat median
        // panel.add(buildOpenMat());
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

    private JPanel buildCloseMat() {
        JPanel jpanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        ToolSettings.setMaskClose(maskCloseValue);
        maskCloseJLabel.setText(tr("clos " + maskCloseValue));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.5;
        jpanel.add(maskCloseJLabel, c);
        //
        JSlider jSlider = new JSlider(1, 21, maskCloseValue);
        jSlider.setPaintTrack(true);
        jSlider.setPaintTicks(true);
        jSlider.setPaintLabels(true);
        jSlider.setMajorTickSpacing(5);
        jSlider.setMinorTickSpacing(2);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 2;
        jpanel.add(jSlider, c);

        jSlider.addChangeListener(changeEvent -> {
            JSlider source = (JSlider) changeEvent.getSource();
            maskCloseValue = source.getValue();
            if (source.getValue() % 2 == 0) maskCloseValue = source.getValue() + 1;

            maskCloseJLabel.setText(tr("clos:  " + maskCloseValue));
            ToolSettings.setMaskClose(maskCloseValue);
        });

        return jpanel;
    }

    private JPanel buildOpenMat() {
        JPanel jpanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        ToolSettings.setMaskOpen(maskOpen);
        maskOpenJLabel.setText(tr("ope " + maskOpen));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.5;
        jpanel.add(maskOpenJLabel, c);
        //
        JSlider jSlider = new JSlider(1, 21, maskOpen);
        jSlider.setPaintTrack(true);
        jSlider.setPaintTicks(true);
        jSlider.setPaintLabels(true);
        jSlider.setMajorTickSpacing(5);
        jSlider.setMinorTickSpacing(2);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 2;
        jpanel.add(jSlider, c);

        jSlider.addChangeListener(changeEvent -> {
            JSlider source = (JSlider) changeEvent.getSource();
            maskOpen = source.getValue();
            if (source.getValue() % 2 == 0) maskOpen = source.getValue() + 1;
            maskOpenJLabel.setText(tr("ope " + maskOpen));
            ToolSettings.setMaskOpen(maskOpen);
        });

        return jpanel;
    }

}
