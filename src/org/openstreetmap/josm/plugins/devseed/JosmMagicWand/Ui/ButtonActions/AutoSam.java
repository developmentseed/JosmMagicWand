package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.Ui.ButtonActions;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.Notification;
import org.openstreetmap.josm.gui.layer.ImageryLayer;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
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

public class AutoSam extends JosmAction {
    private final ImageSamPanelListener listener;

    public AutoSam(ImageSamPanelListener listener) {
        super(tr("AUTO SAM"), "dialogs/auto-sam", tr("Automatic sam"),
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
            checkApi(samImage);

        } else {
            new Notification(tr("An Map active layer is needed.")).setIcon(JOptionPane.ERROR_MESSAGE).setDuration(Notification.TIME_SHORT).show();
        }
    }

    private void checkApi(SamImage samImage) {
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
            });
        });
        apiThread.start();
    }

    private void apiThreadCreateAoi(SamImage samImage) {
        OsmDataLayer dataActiveLayer = CommonUtils.getActiveDataLayerNameOrCreate("Data Layer new");
//        if (dataActiveLayer.getAssociatedFile() == null || dataActiveLayer.requiresSaveToFile()) {
//            new Notification(tr("Save changes to the current Data Layer or associate it with a file.")).setIcon(JOptionPane.ERROR_MESSAGE).setDuration(Notification.TIME_SHORT).show();
//            setEnabled(true);
//            return;
//        }
        setEnabled(false);
        Thread aoiThread = new Thread(() -> {
            try {
                new Notification(tr("Creating Aoi...")).setIcon(JOptionPane.INFORMATION_MESSAGE).setDuration(Notification.TIME_SHORT).show();
                samImage.setEncodeImage();
                new Notification(tr("Automatically segmenting ..")).setIcon(JOptionPane.INFORMATION_MESSAGE).setDuration(Notification.TIME_SHORT).show();
                setEnabled(false);
                OsmDataLayer newLayerSam = samImage.autoAnnotateSam();
                setEnabled(false);
                SwingUtilities.invokeLater(() -> {
                    if (newLayerSam != null) {
                        addSamImage(samImage);
                        CommonUtils.pasteDataFromLayerByName(dataActiveLayer, newLayerSam);
                        samImage.updateCacheImage();
                    }
                    setEnabled(true);
                });
            } catch (Exception e) {
                Logging.error(e);
                setEnabled(true);
            }
        });
        aoiThread.start();
    }


    private void addSamImage(SamImage samImage) {
        if (samImage.getEncode() && !samImage.getImageUrl().isEmpty()) {
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
