package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.Ui.ButtonActions;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.ToolSettings;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils.ImageSamPanelListener;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils.SamImage;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import static org.openstreetmap.josm.tools.I18n.tr;

public class SamEncondeAction extends JosmAction {
    private ImageSamPanelListener listener;

    public SamEncondeAction(ImageSamPanelListener listener) {
        super(tr("Sam"), "dialogs/add", tr("Add a new key/value pair to geometries"),
                null, false);
        this.listener = listener;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MapView mapView = MainApplication.getMap().mapView;
        BufferedImage bufferedImage = getLayeredImage(mapView);
        SamImage samImage = new SamImage(mapView, bufferedImage);
        listener.onAddSamImage(samImage);
    }

    private BufferedImage getLayeredImage(MapView mapView) {

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
}
