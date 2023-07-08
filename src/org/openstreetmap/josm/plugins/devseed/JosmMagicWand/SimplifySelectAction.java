package org.openstreetmap.josm.plugins.devseed.JosmMagicWand;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.command.AddCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.UndoRedoHandler;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.*;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.gui.Notification;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils.CommonUtils;
import org.openstreetmap.josm.tools.Logging;
import org.openstreetmap.josm.tools.Shortcut;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.projection.Projection;
import org.openstreetmap.josm.data.projection.Projections;
import org.openstreetmap.josm.data.projection.ProjectionRegistry;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.openstreetmap.josm.tools.I18n.tr;

public class SimplifySelectAction extends JosmAction implements DataSelectionListener {


    public SimplifySelectAction() {
        super(tr("Simplify way"), "mapmode/magic-wand-merge", tr("Simplify multiple geometries"), Shortcut.registerShortcut("data:magicwandsimplify", tr("Data: {0}", tr("Simplify multiple geometries")), KeyEvent.VK_4, Shortcut.CTRL), true);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (!isEnabled()) return;
        Collection<Way> selWays = getSelectedWays();
        if (selWays.isEmpty()) {
            new Notification(tr("No ways selected.")).setIcon(JOptionPane.WARNING_MESSAGE).setDuration(Notification.TIME_SHORT).show();
            return;
        }
        List<Geometry> geometrySimplify = new ArrayList<>();
        try {
            geometrySimplify = simplifyWays(selWays);
        } catch (Exception e) {
            Logging.error(e);
        }
        boolean hasDraw = drawWays(geometrySimplify);
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

    private List<Geometry> simplifyWays(Collection<Way> ways) throws Exception {
        List<Geometry> geometries = new ArrayList<>();
        for (Way w : ways) {
            List<Coordinate> coordsMercator = CommonUtils.nodes2Coordinates(w.getNodes());
            Geometry geometryMercator = CommonUtils.coordinates2Geometry(coordsMercator, true);
            Geometry geometrySimplify = CommonUtils.simplifyGeometry(geometryMercator);
            geometries.add(geometrySimplify);
        }
        return geometries;
    }

    private boolean drawWays(List<Geometry> geometrySimplify) {
        if (geometrySimplify.isEmpty()) return false;
        DataSet ds = MainApplication.getLayerManager().getEditDataSet();
        try {
            Collection<Command> cmds = CommonUtils.geometry2WayCommands(ds, geometrySimplify, "magic_wand_simplify", "yes");
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
