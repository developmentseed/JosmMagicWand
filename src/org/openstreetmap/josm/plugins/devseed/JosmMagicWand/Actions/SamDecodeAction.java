package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.Actions;

import org.locationtech.jts.geom.Geometry;
import org.openstreetmap.josm.actions.mapmode.MapMode;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.UndoRedoHandler;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.projection.Projection;
import org.openstreetmap.josm.data.projection.Projections;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.Notification;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils.CommonUtils;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils.ImageSamPanelListener;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils.SamImage;
import org.openstreetmap.josm.tools.ImageProvider;
import org.openstreetmap.josm.tools.Logging;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.openstreetmap.josm.tools.I18n.tr;

public class SamDecodeAction extends MapMode implements MouseListener {
    private static final Cursor CURSOR_CUSTOM = ImageProvider.getCursor("crosshair", "magic-wand-sam");
    private Mode mode = Mode.None;
    private final Mode nextMode = Mode.None;
    private Point drawStartPos;
    private Point mousePos;
    private final ImageSamPanelListener listener;
    public SamDecodeAction(ImageSamPanelListener listener) {
        super(tr("Magic Wand SAM"), "magic-wand-sam", tr("Magic Wand SAM action"), null, ImageProvider.getCursor("crosshair", null));
        this.listener = listener;
    }

    private Cursor getCursor() {
        return CURSOR_CUSTOM;
    }

    private void setCursor(final Cursor c) {
        MainApplication.getMap().mapView.setNewCursor(c, this);
    }

    private void updCursor() {
        if (!MainApplication.isDisplayingMapView()) return;
        setCursor(getCursor());
    }

    @Override
    public void enterMode() {
        super.enterMode();

        MapFrame map = MainApplication.getMap();
        map.mapView.addMouseListener(this);
        map.mapView.addMouseMotionListener(this);

        updCursor();

    }

    @Override
    public void exitMode() {
        super.exitMode();
        MapFrame map = MainApplication.getMap();
        map.mapView.removeMouseListener(this);
        map.mapView.removeMouseMotionListener(this);
        if (mode != Mode.None) map.mapView.repaint();
        mode = Mode.None;
    }

    public final void cancelDrawing() {
        mode = Mode.None;
        MapFrame map = MainApplication.getMap();
        if (map == null || map.mapView == null) return;
        map.statusLine.setHeading(-1);
        map.statusLine.setAngle(-1);
        map.mapView.repaint();
        updateStatusLine();
    }

    private void drawingStart(MouseEvent e) {
        mousePos = e.getPoint();
        drawStartPos = mousePos;
        mode = Mode.Drawing;
        updateStatusLine();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Logging.info("-------- mouseReleased -----------");
        if (e.getButton() != MouseEvent.BUTTON1) return;
        if (!MainApplication.getMap().mapView.isActiveLayerDrawable()) return;

        Thread apiThread = new Thread(() -> {
            try {
                drawWays(e);
            } catch (Exception ex) {
                Logging.error(ex);
                new Notification(tr("Error data generation.")).setIcon(JOptionPane.ERROR_MESSAGE).setDuration(Notification.TIME_SHORT).show();
            }
            SwingUtilities.invokeLater(() -> {
                Logging.error("later");
            });
        });
        apiThread.start();
    }

    private void drawWays(MouseEvent e) throws Exception {

        MapView mapView = MainApplication.getMap().mapView;
        DataSet ds = MainApplication.getLayerManager().getEditDataSet();

        String tagKey = "";
        String tagValue = "";

        LatLon latLon = mapView.getLatLon(e.getX(), e.getY());
        Projection currentProjection = mapView.getProjection();
        EastNorth eastNorth = latLon.getEastNorth(currentProjection);

        SamImage samImage = listener.getSamImageIncludepoint(eastNorth.getX(), eastNorth.getY());
        if (samImage == null) {
            new Notification(tr("Click inside of active AOI to enable Segment Anything Model.")).setIcon(JOptionPane.ERROR_MESSAGE).setDuration(Notification.TIME_SHORT).show();
            return ;
        }
        Projection epsg4326 = Projections.getProjectionByCode("EPSG:4326");
        EastNorth eastNort4326 = epsg4326.latlon2eastNorth(latLon);


        OsmDataLayer samLayer = samImage.fetchDecodePoint(eastNort4326.getX(), eastNort4326.getY());
        if (samLayer == null) {
            new Notification(tr("Error fetch data.")).setIcon(JOptionPane.ERROR_MESSAGE).setDuration(Notification.TIME_SHORT).show();
            return ;
        }

    }

    private enum Mode {
        None, Drawing
    }


}



