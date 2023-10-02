package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils;

import java.awt.image.BufferedImage;

public class LayerImageValues {
    BufferedImage bufferedImage;
    String layerName;

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    public LayerImageValues() {
    }

}
