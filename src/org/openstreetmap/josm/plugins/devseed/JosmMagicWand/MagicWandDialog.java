package org.openstreetmap.josm.plugins.devseed.JosmMagicWand;

import org.openstreetmap.josm.gui.dialogs.ToggleDialog;
import org.openstreetmap.josm.gui.SideButton;

import javax.swing.*;

import java.awt.*;
import java.util.List;

import static org.openstreetmap.josm.tools.I18n.tr;

public class MagicWandDialog extends ToggleDialog {
    private int valSlider = 7;
    private final JLabel toleranceStr;

    public MagicWandDialog() {
        super(tr("Magic Wand"), "magicwand.svg", tr("Open MagicWand windows"), null, 90);

        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        // user
        toleranceStr = new JLabel(tr("Tolerance:  " + this.valSlider));
        ToolSettings.setTolerance(this.valSlider);
        panel.add(toleranceStr);
        panel.add(buildTolerance());
        setMinimumSize(new Dimension(200,110));
        createLayout(panel, false, List.of(new SideButton[]{}));
    }

    private JPanel buildTolerance() {
        JPanel jpTolerance = new JPanel(new BorderLayout());

        JSlider jSlider = new JSlider(1, 50, valSlider);
        jSlider.setPaintTrack(true);
        jSlider.setPaintTicks(true);
        jSlider.setPaintLabels(true);
        jSlider.setMajorTickSpacing(5);
        jSlider.setMinorTickSpacing(0);

        jpTolerance.add(jSlider, BorderLayout.NORTH);
        jpTolerance.setMinimumSize(new Dimension(200,70));
        jSlider.addChangeListener(changeEvent -> {
            JSlider source = (JSlider) changeEvent.getSource();
            this.valSlider = source.getValue();
            toleranceStr.setText(tr("Tolerance:  " + this.valSlider));
            ToolSettings.setTolerance(this.valSlider);
        });

        return jpTolerance;
    }

}
