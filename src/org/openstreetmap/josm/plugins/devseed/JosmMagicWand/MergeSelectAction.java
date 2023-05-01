package org.openstreetmap.josm.plugins.devseed.JosmMagicWand;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;
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
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils.CustomPolygon;
import org.openstreetmap.josm.tools.Shortcut;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.openstreetmap.josm.tools.I18n.tr;

public class MergeSelectAction extends JosmAction implements DataSelectionListener {
    private final CommonUtils commonUtils = new CommonUtils();


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

        List<Polygon> polygonsMerge = new ArrayList<>();
        try {
            polygonsMerge = mergeWays(selWays);
        } catch (Exception e) {
            new Notification(tr("Incorrect geometry.")).setIcon(JOptionPane.WARNING_MESSAGE).setDuration(Notification.TIME_SHORT).show();
            return;
        }
        if (polygonsMerge.isEmpty()) {
            new Notification(tr("Does not have merged ways.")).setIcon(JOptionPane.WARNING_MESSAGE).setDuration(Notification.TIME_SHORT).show();
            return;
        }
        boolean hasDraw = drawWays(polygonsMerge);
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

    private List<Polygon> mergeWays(Collection<Way> ways) throws Exception {
        List<CustomPolygon> polygons = new ArrayList<>();
        for (Way w : ways) {
            CustomPolygon cp = new CustomPolygon();
            cp.fromWay(w);
            polygons.add(cp);
        }
        return commonUtils.mergeGeometry(polygons);
    }

    private boolean drawWays(List<Polygon> polygonsMerge) {
        boolean hasDraw = false;
        if (polygonsMerge.isEmpty()) return hasDraw;
        DataSet ds = MainApplication.getLayerManager().getEditDataSet();
        Collection<Command> cmds = new LinkedList<>();
        for (Polygon pol : polygonsMerge) {
            Way w = new Way();

            List<Node> nodes = new ArrayList<>();
            for (Coordinate c : pol.getCoordinates()) {
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
            w.setKeys(new TagMap("magic_wand_merge", "yes"));
            cmds.add(new AddCommand(ds, w));
            hasDraw = true;

        }

        UndoRedoHandler.getInstance().add(new SequenceCommand(tr("draw merge way"), cmds));
        return hasDraw;
    }

    private void removeSelected(ActionEvent e) {
        MapFrame map = MainApplication.getMap();
        if (!isEnabled() || !map.mapView.isActiveLayerVisible())
            return;
        map.mapModeDelete.doActionPerformed(e);
    }

}
