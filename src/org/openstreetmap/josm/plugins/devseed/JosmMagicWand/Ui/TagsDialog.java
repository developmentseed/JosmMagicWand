package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.Ui;

import org.openstreetmap.josm.gui.ExtendedDialog;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.Notification;
import org.openstreetmap.josm.gui.widgets.HistoryComboBox;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.ToolSettings;
import org.openstreetmap.josm.tools.GBC;

import javax.swing.*;
import java.awt.*;

import static org.openstreetmap.josm.tools.I18n.tr;

public class TagsDialog extends ExtendedDialog {

    private final HistoryComboBox addTags = new HistoryComboBox();
    private final JPanel mainPanel;

    public TagsDialog() {
        super(MainApplication.getMainFrame(), tr("Magic Wand Auto tags"), tr("Ok"), tr("Cancel"));
        setButtonIcons("ok", "cancel");
        setCancelButton(2);
        mainPanel = new JPanel(new GridBagLayout());

        mainPanel.add(new JLabel("<html> Auto add tags"
                +"<br><br>"+tr("Please add in the format: key=value")), GBC.eol().fill(GBC.HORIZONTAL));

        addTags.setEditable(true);
        addTags.getModel().prefs().load("magicwand.tags-history");
        addTags.setAutocompleteEnabled(true);
        mainPanel.add(addTags, GBC.eop().fill(GBC.HORIZONTAL));

        if (ToolSettings.getAutoTags() != null && !ToolSettings.getAutoTags().isEmpty()) {
            addTags.setText(ToolSettings.getAutoTags());

       }

        setContent(mainPanel, false);
        setupDialog();
        setVisible(true);
    }

    public void saveSettings() {
        if (addTags.getText().isEmpty()) {
            ToolSettings.setAutoTags("");
            return;
        }
        String regex = "^[a-zA-Z0-9]+=[a-zA-Z0-9]+$";
        if (addTags.getText().matches(regex)) {
            ToolSettings.setAutoTags(addTags.getText());
            addTags.addItem(addTags.getText());
            addTags.getModel().prefs().save("magicwand.tags-history");
        } else {
            new Notification(tr("Label is in the wrong format")).setIcon(JOptionPane.WARNING_MESSAGE).setDuration(Notification.TIME_SHORT).show();
        }

    }

}
