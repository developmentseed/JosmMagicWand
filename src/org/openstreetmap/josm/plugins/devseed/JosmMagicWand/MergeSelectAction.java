package org.openstreetmap.josm.plugins.devseed.JosmMagicWand;

import org.locationtech.jts.geom.Geometry;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.UndoRedoHandler;
import org.openstreetmap.josm.data.osm.DataSelectionListener;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.gui.Notification;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils.CommonUtils;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils.CustomPolygon;
import org.openstreetmap.josm.tools.Logging;
import org.openstreetmap.josm.tools.Shortcut;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.openstreetmap.josm.tools.I18n.tr;

public class MergeSelectAction extends JosmAction implements DataSelectionListener {


    public MergeSelectAction() {
        super(tr("Merge way"), "mapmode/magic-wand-merge", tr("merge multiple geometries into one"), Shortcut.registerShortcut("data:magicwandmerge", tr("Data: {0}", tr("merge multiple geometries into one")), KeyEvent.VK_3, Shortcut.CTRL), true);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (!isEnabled()) return;
        final Collection<Way> selWays = getSelectedWays();
        if (selWays.isEmpty()) {
            new Notification(tr("No ways selected.")).setIcon(JOptionPane.WARNING_MESSAGE).setDuration(Notification.TIME_SHORT).show();
            return;
        }

        List<Geometry> geometryMerge;
        try {
            geometryMerge = mergeWays(selWays);
        } catch (Exception e) {
            new Notification(tr("Incorrect geometry.")).setIcon(JOptionPane.WARNING_MESSAGE).setDuration(Notification.TIME_SHORT).show();
            return;
        }
        if (geometryMerge.isEmpty()) {
            new Notification(tr("Does not have merged ways.")).setIcon(JOptionPane.WARNING_MESSAGE).setDuration(Notification.TIME_SHORT).show();
            return;
        }
        boolean hasDraw = drawWays(geometryMerge);
        if (hasDraw) {
            removeSelected(actionEvent);
        }

    }

    @Override
    public void selectionChanged(SelectionChangeEvent event) {
        updateEnabledStateOnCurrentSelection();
    }

    @Override
    protected void updateEnabledState(Collection<? extends OsmPrimitive> selection) {
        updateEnabledStateOnModifiableSelection(selection);
    }

    private Collection<Way> getSelectedWays() {
        return getLayerManager().getEditDataSet().getSelectedWays();
    }

    private List<Geometry> mergeWays(Collection<Way> ways) throws Exception {
        List<CustomPolygon> polygons = new ArrayList<>();
        for (Way w : ways) {
            CustomPolygon cp = new CustomPolygon();
            cp.fromWay(w);
            polygons.add(cp);
        }
        return CommonUtils.mergeGeometry(polygons);
    }

    private boolean drawWays(List<Geometry> geometries) {
        if (geometries.isEmpty()) return false;
        DataSet ds = MainApplication.getLayerManager().getEditDataSet();

        try {
            String tagKey = "magic_wand_merge";
            String tagValue = "yes";
            if (ToolSettings.getAutoTags()!= null && !ToolSettings.getAutoTags().isEmpty()){
                List<String> strings = Arrays.asList(ToolSettings.getAutoTags().split("="));
                tagKey = strings.get(0);
                tagValue = strings.get(1);
            }
            Collection<Command> cmds = CommonUtils.geometry2WayCommands(ds, geometries, tagKey, tagValue);
            UndoRedoHandler.getInstance().add(new SequenceCommand(tr("draw merge way"), cmds));
            return !cmds.isEmpty();
        } catch (Exception e) {
            Logging.error(e);
            return false;
        }
    }

    private void removeSelected(ActionEvent e) {
        MapFrame map = MainApplication.getMap();
        if (!isEnabled() || !map.mapView.isActiveLayerVisible()) {
            return;
        }
        map.mapModeDelete.doActionPerformed(e);
    }

}
