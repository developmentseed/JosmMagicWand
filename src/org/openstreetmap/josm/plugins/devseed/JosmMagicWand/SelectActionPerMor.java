package org.openstreetmap.josm.plugins.devseed.JosmMagicWand;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.command.AddCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.UndoRedoHandler;
import org.openstreetmap.josm.data.osm.*;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.Notification;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils.CommonUtils;
import org.openstreetmap.josm.tools.Geometry;
import org.openstreetmap.josm.tools.Shortcut;

import javax.sound.sampled.Line;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.openstreetmap.josm.tools.I18n.tr;

public class SelectActionPerMor extends JosmAction implements DataSelectionListener {
    private final GeometryFactory gf = new GeometryFactory();
    private final CommonUtils commonUtils = new CommonUtils();


    public SelectActionPerMor() {
        super(tr("per chaikin"), "mapmode/magic-wand-merge", tr("hull nodes"), Shortcut.registerShortcut("data:magicwandperchaikin", tr("Data: {0}", tr("perchaikin nodes")), KeyEvent.VK_5, Shortcut.CTRL), true);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (!isEnabled()) return;
        final Collection<Way> selWays = getSelectedWays();
        if (selWays.isEmpty()) return;
        List<LineString> Polygon = mergeWays(selWays);
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

    private List<LineString> mergeWays(Collection<Way> ways) {
        List<LineString> polygons = new ArrayList<>();
        for (Way w : ways) {
            List<Coordinate> tmpCoords = new ArrayList<>();

            for (Node n : w.getNodes()) {
                tmpCoords.add(new Coordinate(n.getEastNorth().getX(), n.getEastNorth().getY()));
            }

//            var newPoints = commonUtils.chaikinAlgotihm( tmpCoords, 140);
//            var geom = gf.createLineString(newPoints.toArray(new Coordinate[]{}));
//            System.out.println("original " + tmpCoords.size() + " chaikinAlgotihm " + geom.getCoordinates().length);
//
//            polygons.add(geom);

        }
        return polygons;
    }

    private boolean isdrawWays(List<LineString> polygonsMerge) {
        boolean hasDraw = false;
        if (polygonsMerge.isEmpty()) return false;

        DataSet ds = MainApplication.getLayerManager().getEditDataSet();
        Collection<Command> cmds = new LinkedList<>();
        MapView mapview = MainApplication.getMap().mapView;

        for (LineString pol : polygonsMerge) {
            Way w = new Way();
            for (Coordinate c : pol.getCoordinates()) {
                Node n = new Node(MainJosmMagicWandPlugin.latlon2eastNorth(mapview.getLatLon(c.getX(), c.getY())));

                w.addNode(n);
                cmds.add(new AddCommand(ds, n));
            }

            w.setKeys(new TagMap("perchaikin", Double.toString(ToolSettings.getSimplPerMor())));
            cmds.add(new AddCommand(ds, w));
            hasDraw = true;

        }

        UndoRedoHandler.getInstance().add(new SequenceCommand(tr("draw merge way"), cmds));
        return hasDraw;
    }


}
