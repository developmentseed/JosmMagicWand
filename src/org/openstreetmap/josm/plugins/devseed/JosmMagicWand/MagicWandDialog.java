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
        // mat close
        // panel.add(buildCloseMat());
        // mat median
        // panel.add(buildOpenMat());
        // generation
//        panel.add(buildSimpHull());
//        panel.add(buildSimplPerMor());
//        panel.add(buildSimplDp());
        //
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

    private JPanel buildSimpHull() {
        JPanel jpanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        ToolSettings.setSimplHull(simplHull);
        simplHullJLabel.setText(tr("sHull " + simplHull));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.5;
        jpanel.add(simplHullJLabel, c);
        //
        JSlider jSlider = new JSlider(1, 100, (int) (simplHull * 10));
        jSlider.setPaintTrack(true);
        jSlider.setPaintTicks(true);
        jSlider.setPaintLabels(true);
        jSlider.setMajorTickSpacing(20);
        jSlider.setMinorTickSpacing(5);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 2;
        jpanel.add(jSlider, c);

        jSlider.addChangeListener(changeEvent -> {
            JSlider source = (JSlider) changeEvent.getSource();
            simplHull = (double) Math.round(source.getValue() * 10) / 1000;

            simplHullJLabel.setText(tr("sHull " + simplHull));
            ToolSettings.setSimplHull(simplHull);
        });

        return jpanel;
    }

    private JPanel buildSimplPerMor() {
        JPanel jpanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        ToolSettings.setSimplPerMor(simplPerMor);
        simplPerMorJLabel.setText(tr("perMor " + simplPerMor));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.5;
        jpanel.add(simplPerMorJLabel, c);
        //
        JSlider jSlider = new JSlider(1, 100, (int) (simplPerMor * 10));
        jSlider.setPaintTrack(true);
        jSlider.setPaintTicks(true);
        jSlider.setPaintLabels(true);
        jSlider.setMajorTickSpacing(20);
        jSlider.setMinorTickSpacing(5);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 2;
        jpanel.add(jSlider, c);

        jSlider.addChangeListener(changeEvent -> {
            JSlider source = (JSlider) changeEvent.getSource();
            simplPerMor = (double) Math.round(source.getValue() * 10) / 100;

            simplPerMorJLabel.setText(tr("perMor " + simplPerMor));
            ToolSettings.setSimplPerMor(simplPerMor);
        });

        return jpanel;
    }

    private JPanel buildSimplDp() {
        JPanel jpanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        ToolSettings.setSimplDP(simplDp);
        simplDpJLabel.setText(tr("sDp " + simplDp));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.5;
        jpanel.add(simplDpJLabel, c);
        //
        JSlider jSlider = new JSlider(1, 100, (int) (simplDp * 10));
        jSlider.setPaintTrack(true);
        jSlider.setPaintTicks(true);
        jSlider.setPaintLabels(true);
        jSlider.setMajorTickSpacing(20);
        jSlider.setMinorTickSpacing(5);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 2;
        jpanel.add(jSlider, c);

        jSlider.addChangeListener(changeEvent -> {
            JSlider source = (JSlider) changeEvent.getSource();
            simplDp = (double) Math.round(source.getValue() * 10) / 100;

            simplDpJLabel.setText(tr("sDp " + simplDp));
            ToolSettings.setSimplDP(simplDp);
        });

        return jpanel;
    }
}
