package org.openstreetmap.josm.plugins.devseed.JosmMagicWand;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.opencv.core.*;
import org.openstreetmap.josm.actions.mapmode.MapMode;
import org.openstreetmap.josm.command.*;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.UndoRedoHandler;
import org.openstreetmap.josm.data.osm.*;
import org.openstreetmap.josm.data.osm.event.SelectionEventManager;
import org.openstreetmap.josm.data.preferences.NamedColorProperty;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.MapViewPaintable;
import org.openstreetmap.josm.gui.util.KeyPressReleaseListener;
import org.openstreetmap.josm.gui.util.ModifierExListener;

import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils.CommonUtils;
import org.openstreetmap.josm.spi.preferences.Config;
import org.openstreetmap.josm.spi.preferences.PreferenceChangedListener;
import org.openstreetmap.josm.gui.Notification;
import org.openstreetmap.josm.tools.ImageProvider;
import org.openstreetmap.josm.tools.Logging;
import org.openstreetmap.josm.tools.Shortcut;

import javax.swing.*;
import java.awt.*;
import java.awt.Point;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.openstreetmap.josm.tools.I18n.marktr;
import static org.openstreetmap.josm.tools.I18n.tr;

public class MagicWandAction extends MapMode implements MapViewPaintable,
        DataSelectionListener, KeyPressReleaseListener, ModifierExListener, MouseWheelListener {

    private static final Cursor CURSOR_CUSTOM = ImageProvider.getCursor("crosshair", "magic-wand");
    private static final Cursor CURSOR_ADD = ImageProvider.getCursor("crosshair", "magic-wand-add");
    private static final Cursor CURSOR_SUBS = ImageProvider.getCursor("crosshair", "magic-wand-subs");


    private enum Mode {
        None, Drawing
    }


    private final Feature feature = new Feature();
    private final CommonUtils commonUtils = new CommonUtils();

    private Mode mode = Mode.None;
    private Mode nextMode = Mode.None;

    private Color selectedColor = Color.red;
    private Point drawStartPos;
    private Point mousePos;

    private final PreferenceChangedListener shapeChangeListener = event -> updCursor();

    // OPEN CV
    private static Mat mat_mask;
    private static Mat mat_image;


    public MagicWandAction() {
        super(tr("MAgic Wand action "), "magic-wand", tr("Magic wand add"), Shortcut.registerShortcut("mapmode:magicwandadd", tr("Mode: {0}", tr("Magic wand add")), KeyEvent.VK_1, Shortcut.CTRL), ImageProvider.getCursor("crosshair", null));

    }

    private Cursor getCursor() {
        try {
            if (ctrl) {
                return CURSOR_ADD;
            }
            if (shift) {
                return CURSOR_SUBS;
            }
            return CURSOR_CUSTOM;
        } catch (Exception e) {
            Logging.error(e);
        }
        return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
    }

    private void setCursor(final Cursor c) {
        MainApplication.getMap().mapView.setNewCursor(c, this);
    }

    @Override
    public void enterMode() {
        super.enterMode();

        MapFrame map = MainApplication.getMap();
        selectedColor = new NamedColorProperty(marktr("selected"), selectedColor).get();
        map.mapView.addMouseListener(this);
        map.mapView.addMouseMotionListener(this);
        map.mapView.addTemporaryLayer(this);
        map.mapView.addMouseWheelListener(this);
        map.keyDetector.addKeyListener(this);
        map.keyDetector.addModifierExListener(this);
        SelectionEventManager.getInstance().addSelectionListener(this);
        Config.getPref().addKeyPreferenceChangeListener("magic_wand_tool.shape", shapeChangeListener);

        updCursor();

    }

    @Override
    public void exitMode() {
        super.exitMode();
        MapFrame map = MainApplication.getMap();
        map.mapView.removeMouseListener(this);
        map.mapView.removeMouseMotionListener(this);
        map.mapView.removeTemporaryLayer(this);
        map.mapView.removeMouseWheelListener(this);
        map.keyDetector.removeKeyListener(this);
        map.keyDetector.removeModifierExListener(this);
        SelectionEventManager.getInstance().removeSelectionListener(this);
        Config.getPref().removeKeyPreferenceChangeListener("magic_wand_tool.shape", shapeChangeListener);
        cleanMasks();
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

    @Override
    public void modifiersExChanged(int modifiers) {
        boolean oldCtrl = ctrl;
        boolean oldShift = shift;
        updateKeyModifiersEx(modifiers);
        if (ctrl != oldCtrl || shift != oldShift) {
            processMouseEvent(null);
            updCursor();
            if (mode != Mode.None) MainApplication.getMap().mapView.repaint();
        }
    }

    @Override
    public void doKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (mode != Mode.None) e.consume();
            cancelDrawing();
        }
        if (e.getKeyCode() == KeyEvent.VK_2 && ctrl) {
            try {
                drawContours();
            } catch (Exception ex) {
                Logging.error(ex);
            }
        }
    }

    @Override
    public void doKeyReleased(KeyEvent e) {
    }

    private Mode modeDrawing() {
        return Mode.Drawing;
    }


    private void processMouseEvent(MouseEvent e) {
        if (e != null) {
            mousePos = e.getPoint();
            updateKeyModifiers(e);
        }
        if (mode == Mode.None) {
            nextMode = Mode.None;
            return;
        }

        if (mode == Mode.Drawing) {
            nextMode = modeDrawing();
        } else {
            throw new AssertionError("Invalid drawing mode");
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        cleanMasks();
    }

    @Override
    public void paint(Graphics2D g, MapView mv, Bounds bbox) {
        if (mat_mask != null && mat_image != null) {
            try {
                Mat new_mask = commonUtils.maskInsideImage(mat_image, mat_mask, 0.45);
                BufferedImage bi_mask = commonUtils.toBufferedImage(new_mask);
                g.drawImage(bi_mask, 0, 0, null);
            } catch (Exception e) {
                Logging.error(e);
            }
        }
        if (mode == Mode.None) {
            return;
        }

        g.setColor(selectedColor);
        g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setStroke(new BasicStroke(1));
    }

    private void drawingStart(MouseEvent e) {
        mousePos = e.getPoint();
        drawStartPos = mousePos;
        mode = Mode.Drawing;
        updateStatusLine();
    }


    @Override
    public void mousePressed(MouseEvent e) {
        Logging.info("-------- mousePressed -----------");
        if (e.getButton() != MouseEvent.BUTTON1) return;
        if (!MainApplication.getMap().mapView.isActiveLayerDrawable()) return;
        requestFocusInMapView();
        if (mode == Mode.None) drawingStart(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Logging.info("-------- mouseDragged -----------");
        if (e.getButton() == MouseEvent.BUTTON1) return;

        cleanMasks();
        processMouseEvent(e);
        updCursor();
        MainApplication.getMap().mapView.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Logging.info("-------- mouseReleased -----------");
        if (e.getButton() != MouseEvent.BUTTON1) return;
        if (!MainApplication.getMap().mapView.isActiveLayerDrawable()) return;
        try {
            drawingFinish(e);
        } catch (Exception ex) {
            Logging.error(ex);
        }
    }


    private void drawingFinish(MouseEvent e) throws Exception {

        if (mat_image == null) {
            mat_image = commonUtils.BufferedImage2Mat(getLayeredImage());
        }

        Logging.info("-------- drawingFinish -----------");
        mat_mask = feature.processImageRaster(mat_image, mat_mask, ctrl, shift, e.getX(), e.getY());
        MainApplication.getMap().repaint();
    }

    private void updCursor() {
        if (!MainApplication.isDisplayingMapView()) return;
        setCursor(getCursor());
    }

    @Override
    public void selectionChanged(SelectionChangeEvent event) {
    }

    @Override
    public boolean layerIsSupported(Layer l) {
        return isEditableDataLayer(l);
    }

    private BufferedImage getLayeredImage() {
        MapView mapView = MainApplication.getMap().mapView;

        BufferedImage bufImage = new BufferedImage(mapView.getWidth(), mapView.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D imgGraphics = bufImage.createGraphics();
        imgGraphics.setClip(0, 0, mapView.getWidth(), mapView.getHeight());

        for (Layer layer : mapView.getLayerManager().getVisibleLayersInZOrder()) {
            if (layer.isVisible() && layer.isBackgroundLayer()) {
                Composite translucent = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) layer.getOpacity());
                imgGraphics.setComposite(translucent);
                mapView.paintLayer(layer, imgGraphics);
            }
        }

        return bufImage;
    }

    private void drawContours() throws Exception {
        if (mat_mask == null) {
            new Notification(tr("No have mask ")).setIcon(JOptionPane.WARNING_MESSAGE).setDuration(Notification.TIME_SHORT).show();
            return;
        }
        if (mat_mask.empty()) {
            new Notification(tr("Mask  empty, select again")).setIcon(JOptionPane.WARNING_MESSAGE).setDuration(Notification.TIME_SHORT).show();
            return;
        }
        List<MatOfPoint> contourns = commonUtils.obtainContour(mat_mask);
        List<Geometry> geometries = commonUtils.contourn2Geometry(contourns, 3, mat_mask.width(), mat_image.height());
        if (geometries.isEmpty()) return;

        DataSet ds = MainApplication.getLayerManager().getEditDataSet();

        Collection<Command> cmds = new LinkedList<>();
        MapView mapview = MainApplication.getMap().mapView;
        for (Geometry geo : geometries) {
            Way w = new Way();
            for (Coordinate c : geo.getCoordinates()) {
                Node n = new Node(MainJosmMagicWandPlugin.latlon2eastNorth(mapview.getLatLon(c.getX(), c.getY())));
//                n.setKeys(new TagMap("x _ y", Double.toString(c.getX()) + "__" + Double.toString(c.getY())));
                w.addNode(n);
                cmds.add(new AddCommand(ds, n));
            }
            w.setKeys(new TagMap("magic_wand", "yes"));
            cmds.add(new AddCommand(ds, w));
        }

        UndoRedoHandler.getInstance().add(new SequenceCommand(tr("draw contours"), cmds));
        cleanMasks();
        mapview.repaint();
    }

    private void cleanMasks() {
        mat_mask = null;
        mat_image = null;
    }
}
