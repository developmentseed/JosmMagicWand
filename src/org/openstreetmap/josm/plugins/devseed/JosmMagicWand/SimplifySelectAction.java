package org.openstreetmap.josm.plugins.devseed.JosmMagicWand;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.command.AddCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.UndoRedoHandler;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.*;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.gui.Notification;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils.CommonUtils;
import org.openstreetmap.josm.tools.Logging;
import org.openstreetmap.josm.tools.Shortcut;

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
//        if (hasDraw) {
//            removeSelected(actionEvent);
//        }
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
            Geometry geometry = CommonUtils.coordinates2Geometry(CommonUtils.nodes2Coordinates(w.getNodes()), true);
            if (ToolSettings.getSimplifyDouglasP() > 0) {
                geometry = CommonUtils.simplifyDouglasP(geometry.copy(), ToolSettings.getSimplifyDouglasP());
            }
            if (ToolSettings.getSimplPolygonHull() > 0) {
                geometry = CommonUtils.simplifyPolygonHull(geometry.copy(), ToolSettings.getSimplPolygonHull());
            }
            if (ToolSettings.getSimplTopologyPreserving() > 0) {
                geometry = CommonUtils.simplifyTopologyPreserving(geometry.copy(), ToolSettings.getSimplTopologyPreserving());
            }
            geometries.add(geometry.copy());
        }
        return geometries;
    }

    private boolean drawWays(List<Geometry> geometrySimplify) {
        boolean hasDraw = false;
        if (geometrySimplify.isEmpty()) return hasDraw;
        DataSet ds = MainApplication.getLayerManager().getEditDataSet();
        Collection<Command> cmds = new LinkedList<>();
        for (Geometry geometry : geometrySimplify) {
            Way w = new Way();

            List<Node> nodes = new ArrayList<>();
            for (Coordinate c : geometry.getCoordinates()) {
                Node n = new Node(new LatLon(c.getX(), c.getY()));
                nodes.add(n);
            }

            int index = 0;
            for (Node n : nodes) {
                if (index == (nodes.size() - 1)) {
                    w.addNode(nodes.get(0));
                } else {
                    w.addNode(n);
                    cmds.add(new AddCommand(ds, n));
                }
                index++;
            }
            w.setKeys(new TagMap("magic_wand_simplify", "yes"));
            cmds.add(new AddCommand(ds, w));
            hasDraw = true;
        }

        UndoRedoHandler.getInstance().add(new SequenceCommand(tr("simplify way"), cmds));
        return hasDraw;
    }

    private void removeSelected(ActionEvent e) {
        MapFrame map = MainApplication.getMap();
        if (!isEnabled() || !map.mapView.isActiveLayerVisible()) return;
        map.mapModeDelete.doActionPerformed(e);
    }

}
