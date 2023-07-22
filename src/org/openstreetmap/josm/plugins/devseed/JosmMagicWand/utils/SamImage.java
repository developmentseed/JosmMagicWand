package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils;

import org.openstreetmap.josm.gui.MapView;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class SamImage {
    private MapView mapView;
    private BufferedImage layerImage;

    public ImageIcon getImageIcon() {
        return imageIcon;
    }

    private ImageIcon imageIcon;
    private String bbox;
    //    encode
    private String encodeImage;
    private String base64Image;
    private int encodeStatus;

    public SamImage(MapView mapView, BufferedImage layerImage) {
        this.mapView = mapView;
        this.layerImage = layerImage;
        this.base64Image = CommonUtils.encodeImageToBase64(layerImage);
        this.imageIcon = new ImageIcon(layerImage);
    }

    public MapView getMapView() {
        return mapView;
    }

    public void setMapView(MapView mapView) {
        this.mapView = mapView;
    }

    public BufferedImage getLayerImage() {
        return layerImage;
    }

    public void setLayerImage(BufferedImage layerImage) {
        this.layerImage = layerImage;
    }

    public String getBbox() {
        return bbox;
    }

    public void setBbox(String bbox) {
        this.bbox = bbox;
    }

    public String getEncodeImage() {
        return encodeImage;
    }

    public void setEncodeImage(String encodeImage) {
        this.encodeImage = encodeImage;
    }

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    public int getEncodeStatus() {
        return encodeStatus;
    }

    public void setEncodeStatus(int encodeStatus) {
        this.encodeStatus = encodeStatus;
    }
//  decode


}
