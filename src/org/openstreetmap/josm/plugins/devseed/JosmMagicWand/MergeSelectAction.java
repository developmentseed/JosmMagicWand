package org.openstreetmap.josm.plugins.devseed.JosmMagicWand;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.*;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.command.*;
import org.openstreetmap.josm.data.UndoRedoHandler;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.*;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.Notification;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils.CommonUtils;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils.CustomPolygon;
import org.openstreetmap.josm.tools.Logging;
import org.openstreetmap.josm.tools.Shortcut;

import javax.swing.*;

public class MergeSelectAction extends JosmAction implements DataSelectionListener {
    private final CommonUtils commonUtils = new CommonUtils();


    public MergeSelectAction() {
        super(tr("Merge way"), "mapmode/magic-wand-merge", tr("merge multiple geometries into one"), Shortcut.registerShortcut("data:magicwandmerge", tr("Data: {0}", tr("merge multiple geometries into one")), KeyEvent.VK_3, Shortcut.CTRL), true);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (!isEnabled()) return;
        final Collection<Way> selWays = getSelectedWays();
        if (selWays.isEmpty()) return;
        List<Polygon> polygonsMerge = mergeWays(selWays);
        if (polygonsMerge.isEmpty()) {
            new Notification(tr("No have polygons merge")).setIcon(JOptionPane.WARNING_MESSAGE).setDuration(Notification.TIME_SHORT).show();
            return;
        }
        boolean hasDraw = isdrawWays(polygonsMerge);
        if (hasDraw) {
            removeSelected(selWays);
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

    private List<Polygon> mergeWays(Collection<Way> ways) {
        List<CustomPolygon> polygons = new ArrayList<>();
        for (Way w : ways) {
            CustomPolygon cp = new CustomPolygon();
            cp.fromWay(w);
            polygons.add(cp);
        }
        return commonUtils.mergeGeometry(polygons);
    }

    private boolean isdrawWays(List<Polygon> polygonsMerge) {
        boolean hasDraw = false;
        if (polygonsMerge.isEmpty()) return hasDraw;
        DataSet ds = MainApplication.getLayerManager().getEditDataSet();
        Collection<Command> cmds = new LinkedList<>();
        for (Polygon pol : polygonsMerge) {
            Way w = new Way();
            for (Coordinate c : pol.getCoordinates()) {
                Node n = new Node(new LatLon(c.getX(), c.getY()));
                w.addNode(n);
                cmds.add(new AddCommand(ds, n));
            }
            w.setKeys(new TagMap("merge", "yes"));
            cmds.add(new AddCommand(ds, w));
            hasDraw = true;
        }

        UndoRedoHandler.getInstance().add(new SequenceCommand(tr("draw merge way"), cmds));
        return hasDraw;
    }

    private void removeSelected(Collection<Way> selWays) {
        Collection<Command> cmds = new LinkedList<>();
        boolean needUnglue = false;

        for (Way w : selWays) {
            for (Node node : w.getNodes()) {
                if (node.getParentWays().size() > 1) needUnglue = true;
            }
        }

        Logging.error("need needUnglue: " + needUnglue);
        
        List<Long> nodesId = new ArrayList<>();
        for (Way w : selWays) {
            try {
                List<Node> nodesRemoveTmp = new ArrayList<>();
                for (Node node : w.getNodes()) {
                    if (!nodesId.contains(node.getUniqueId())) {
                        nodesId.add(node.getUniqueId());
                        nodesRemoveTmp.add(node);
                    }
                }
                cmds.add(new DeleteCommand(w));
                cmds.add(new DeleteCommand(nodesRemoveTmp));
            } catch (Exception ex) {
                Logging.error(ex);
            }
        }

        UndoRedoHandler.getInstance().add(new SequenceCommand(tr("remove ways"), cmds));
    }

}
