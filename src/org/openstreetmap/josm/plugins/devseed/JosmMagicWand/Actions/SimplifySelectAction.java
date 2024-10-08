package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.Actions;

import org.locationtech.jts.geom.Coordinate;
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
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.ToolSettings;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils.CommonUtils;
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

public class SimplifySelectAction extends JosmAction implements DataSelectionListener {


    public SimplifySelectAction() {
        super(tr("Simplify way"), "mapmode/magic-wand-simplify", tr("Simplify multiple geometries"), Shortcut.registerShortcut("data:magicwandsimplify", tr("Data: {0}", tr("Simplify multiple geometries")), KeyEvent.VK_4, Shortcut.CTRL), true);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (!isEnabled()) return;
        Collection<Way> selWays = getSelectedWays();
        if (selWays.isEmpty()) {
            new Notification(tr("No ways selected.")).setIcon(JOptionPane.WARNING_MESSAGE).setDuration(Notification.TIME_SHORT).show();
            return;
        }

        try {
            List<Geometry> geometrySimplify = simplifyWays(selWays);
            boolean hasDraw = drawWays(geometrySimplify);
            if (hasDraw) {
                removeSelected(actionEvent);
            }
        } catch (Exception e) {
            Logging.error(e);
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

    private List<Geometry> simplifyWays(Collection<Way> ways) throws Exception {
        List<Geometry> geometries = new ArrayList<>();
        for (Way w : ways) {
            List<Coordinate> coordsMercator = CommonUtils.nodes2Coordinates(w.getNodes());
            Geometry geometryMercator = CommonUtils.coordinates2Polygon(coordsMercator);
            Geometry geometrySimplify = CommonUtils.simplifySmoothGeometry(geometryMercator);
            geometries.add(geometrySimplify);
        }
        return geometries;
    }

    private boolean drawWays(List<Geometry> geometrySimplify) {
        if (geometrySimplify.isEmpty()) return false;
        DataSet ds = MainApplication.getLayerManager().getEditDataSet();
        try {
            String tagKey = "";
            String tagValue = "";
            if (ToolSettings.getAutoTags() != null && !ToolSettings.getAutoTags().isEmpty()) {
                List<String> strings = Arrays.asList(ToolSettings.getAutoTags().split("="));
                if (strings.size() > 1) {
                    tagKey = strings.get(0);
                    tagValue = strings.get(1);
                }
            }
            Collection<Command> cmds = CommonUtils.geometry2WayCommands(ds, geometrySimplify, tagKey, tagValue);
            UndoRedoHandler.getInstance().add(new SequenceCommand(tr("simplify way"), cmds));
            return !cmds.isEmpty();
        } catch (Exception e) {
            Logging.error(e);
            return false;
        }
    }

    private void removeSelected(ActionEvent e) {
        MapFrame map = MainApplication.getMap();
        if (!isEnabled() || !map.mapView.isActiveLayerVisible()) return;
        map.mapModeDelete.doActionPerformed(e);
    }

}
