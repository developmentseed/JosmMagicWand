package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.Ui.ButtonActions;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.Ui.TagsDialog;

import java.awt.event.ActionEvent;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.openstreetmap.josm.tools.I18n.tr;

public class AutoAddTagAction extends JosmAction {
    AtomicBoolean isPerforming = new AtomicBoolean(false);

    public AutoAddTagAction() {
        super(tr("Add tag"), "dialogs/add", tr("Add a new key/value tag to geometries"),
                null, false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!/*successful*/isPerforming.compareAndSet(false, true)) {
            return;
        }
        try {
            TagsDialog tagsDialog = new TagsDialog();
            if (tagsDialog.getValue() != 1) return;
            tagsDialog.saveSettings();
        } finally {
            isPerforming.set(false);
        }
    }
}
