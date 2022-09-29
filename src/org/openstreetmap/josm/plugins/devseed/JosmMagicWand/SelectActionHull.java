package org.openstreetmap.josm.plugins.devseed.JosmMagicWand;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.command.AddCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.DeleteCommand;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.UndoRedoHandler;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.*;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.Notification;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils.CommonUtils;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils.CustomPolygon;
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

public class SelectActionHull extends JosmAction implements DataSelectionListener {
    private final GeometryFactory gf = new GeometryFactory();
    private final CommonUtils commonUtils = new CommonUtils();


    public SelectActionHull() {
        super(tr("hull"), "mapmode/magic-wand-merge", tr("hull nodes"), Shortcut.registerShortcut("data:magicwandhull", tr("Data: {0}", tr("hull nodes")), KeyEvent.VK_4, Shortcut.CTRL), true);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (!isEnabled()) return;
        final Collection<Way> selWays = getSelectedWays();
        if (selWays.isEmpty()) return;
        List<Polygon> Polygon = mergeWays(selWays);
        if (Polygon.isEmpty()) {
            new Notification(tr("No have polygons merge")).setIcon(JOptionPane.WARNING_MESSAGE).setDuration(Notification.TIME_SHORT).show();
            return;
        }
        isdrawWays(Polygon);

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
        List<Polygon> polygons = new ArrayList<>();
        for (Way w : ways) {
            List<Coordinate> tmpCoords = new ArrayList<>();

            for (Node n : w.getNodes()) {
                String[] str_array = n.getKeys().get("x__y").split("__");
                tmpCoords.add(new Coordinate(Double.parseDouble(str_array[0]), Double.parseDouble(str_array[1])));
            }
            if (!tmpCoords.get(tmpCoords.size() - 1).equals(tmpCoords.get(0))) tmpCoords.add(tmpCoords.get(0));
            var geom = gf.createPolygon(tmpCoords.toArray(new Coordinate[]{}));
            var geom_filter = (Polygon) commonUtils.simplifyPolygonHull(geom, ToolSettings.getSimplHull());
            System.out.println("original " + tmpCoords.size() + " simplifyPolygonHull " + geom_filter.getCoordinates().length);

            polygons.add(geom_filter);

        }
        return polygons;
    }

    private boolean isdrawWays(List<Polygon> polygonsMerge) {
        boolean hasDraw = false;
        if (polygonsMerge.isEmpty()) return hasDraw;

        DataSet ds = MainApplication.getLayerManager().getEditDataSet();
        Collection<Command> cmds = new LinkedList<>();
        MapView mapview = MainApplication.getMap().mapView;

        for (Polygon pol : polygonsMerge) {
            Way w = new Way();
            for (Coordinate c : pol.getCoordinates()) {
                Node n = new Node(MainJosmMagicWandPlugin.latlon2eastNorth(mapview.getLatLon(c.getX(), c.getY())));
                n.setKeys(new TagMap("x__y",c.getX()+"__"+c.getY()));

                w.addNode(n);
                cmds.add(new AddCommand(ds, n));
            }

            w.setKeys(new TagMap("hull", Double.toString(ToolSettings.getSimplHull())));
            cmds.add(new AddCommand(ds, w));
            hasDraw = true;

        }

        UndoRedoHandler.getInstance().add(new SequenceCommand(tr("draw merge way"), cmds));
        return hasDraw;
    }


}
