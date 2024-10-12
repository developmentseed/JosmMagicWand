package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.Ui.ButtonActions;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.Notification;
import org.openstreetmap.josm.gui.layer.ImageryLayer;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils.CommonUtils;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils.ImageSamPanelListener;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils.LayerImageValues;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils.SamImage;
import org.openstreetmap.josm.tools.Logging;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.List;

import static org.openstreetmap.josm.tools.I18n.tr;

public class SamEncondeAction extends JosmAction {
    private final ImageSamPanelListener listener;

    public SamEncondeAction(ImageSamPanelListener listener) {
        super(tr("SAM AOI"), "dialogs/magic-wand-encode", tr("Add a new SAM AOI"),
                null, false);
        this.listener = listener;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MapView mapView = MainApplication.getMap().mapView;
        // check layers
        List<Layer> targetLayer = MainApplication.getLayerManager().getLayers();
        boolean hasMapLayer = false;
        for (Layer layer : targetLayer) {
            if (layer instanceof ImageryLayer && layer.isVisible()) {
                hasMapLayer = true;
                break;
            }
        }

        if (hasMapLayer) {
            LayerImageValues layerImageValues = getLayeredImage(mapView);
            SamImage samImage = new SamImage(mapView.getProjectionBounds(), mapView.getProjection(), mapView.getScale(), layerImageValues.getBufferedImage(), layerImageValues.getLayerName());

            // effect
            setEnabled(false);
            checkApi(samImage);

        } else {
            new Notification(tr("An active layer is needed.")).setIcon(JOptionPane.ERROR_MESSAGE).setDuration(Notification.TIME_SHORT).show();
        }
    }

    private void checkApi(SamImage samImage) {
        setEnabled(false);
        Thread apiThread = new Thread(() -> {
            String device = CommonUtils.serverSamLive();
            SwingUtilities.invokeLater(() -> {
                if (!device.isEmpty()) {
                    Logging.info("Server is online: " + device);
                    apiThreadCreateAoi(samImage);
                } else {
                    Logging.error("Server is down");
                    new Notification(tr("SAM server not reachable.")).setIcon(JOptionPane.ERROR_MESSAGE).setDuration(Notification.TIME_SHORT).show();
                }
                setEnabled(true);

            });
        });
        apiThread.start();
    }

    private void apiThreadCreateAoi(SamImage samImage) {
        setEnabled(false);
        Thread aoiThread = new Thread(() -> {
            samImage.setEncodeImage();
            SwingUtilities.invokeLater(() -> {
                addSamImage(samImage);
                samImage.updateCacheImage();
                setEnabled(true);
            });
        });
        aoiThread.start();
    }

    private void addSamImage(SamImage samImage) {
        if (samImage.getEncode()) {
            listener.addLayer();
            listener.addBboxLayer(samImage.getBboxWay());
            listener.onAddSamImage(samImage);
            new Notification(tr("Added a sam image.")).setIcon(JOptionPane.INFORMATION_MESSAGE).setDuration(Notification.TIME_SHORT).show();
        } else {
            new Notification(tr("Error adding sam image.")).setIcon(JOptionPane.ERROR_MESSAGE).setDuration(Notification.TIME_SHORT).show();
        }
    }

    private LayerImageValues getLayeredImage(MapView mapView) {
        LayerImageValues layerImageValues = new LayerImageValues();
        BufferedImage bufImage = new BufferedImage(mapView.getWidth(), mapView.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D imgGraphics = bufImage.createGraphics();
        imgGraphics.setClip(0, 0, mapView.getWidth(), mapView.getHeight());

        for (Layer layer : mapView.getLayerManager().getVisibleLayersInZOrder()) {
            if (layer.isVisible() && layer.isBackgroundLayer()) {
                Composite translucent = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) layer.getOpacity());
                imgGraphics.setComposite(translucent);
                mapView.paintLayer(layer, imgGraphics);
                layerImageValues.setBufferedImage(bufImage);
                layerImageValues.setLayerName(layer.getName());
            }

        }

        return layerImageValues;
    }
}
