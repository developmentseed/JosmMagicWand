package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils;

import org.openstreetmap.josm.data.ProjectionBounds;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.gui.MapView;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import okhttp3.OkHttpClient;

public class SamImage {
    private MapView mapView;
    private BufferedImage layerImage;
    private Graphics g;
    private ImageIcon imageIcon;
    private ProjectionBounds bbox;
    //    encode
    private String encodeImage;
    private String base64Image;
    private int encodeStatus;
    private EastNorth center;
    private double zoom;

    public SamImage(MapView mapView, BufferedImage layerImage) {
        this.mapView = mapView;
        this.layerImage = layerImage;
        this.base64Image = CommonUtils.encodeImageToBase64(layerImage);
        this.imageIcon = new ImageIcon(layerImage);
        this.center = mapView.getCenter();
        this.g = mapView.getGraphics();

//        LatLon northwest = mapView.getLatLon(0, 0);
//        LatLon southeast = mapView.getLatLon(mapView.getWidth(), mapView.getHeight());
//        this.bbox = new Bounds(northwest, southeast);
        this.bbox = mapView.getProjectionBounds();


    }

    public BufferedImage getLayerImage() {
        return layerImage;
    }

    public void drawLatLonCrosshair(MapView mapView) {
        System.out.println("draw");
    }

    public EastNorth getCenter() {
        return center;
    }

    public ProjectionBounds getBbox() {
        return bbox;
    }

    public double getZoom() {
        return zoom;
    }

    public void setEncodeImage() {

    }
}
