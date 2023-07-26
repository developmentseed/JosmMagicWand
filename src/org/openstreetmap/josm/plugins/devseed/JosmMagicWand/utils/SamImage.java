package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.openstreetmap.josm.data.ProjectionBounds;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.MainJosmMagicWandPlugin;
import org.openstreetmap.josm.tools.Logging;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SamImage {
    private MapView mapView;
    private BufferedImage layerImage;
    private Graphics g;
    private ImageIcon imageIcon;
    private ProjectionBounds projectionBounds;
    //    encode
    private String base64Image;
    private int encodeStatus;
    private EastNorth center;
    private ObjectMapper objectMapper;
    // enconde fields
    private boolean isEncodeImage = false;

    private List imageShape;
    private int inputLabel;
    private String crs;
    private List bbox;
    private double zoom;
    private String imageEmbedding;

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
        this.projectionBounds = mapView.getProjectionBounds();

        objectMapper = new ObjectMapper();
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

    public ProjectionBounds getProjectionBounds() {
        return projectionBounds;
    }

    public boolean isEncodeImage() {
        return isEncodeImage;
    }

    public void setEncodeImage() {
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            String url = MainJosmMagicWandPlugin.getDotenv().get("ENCODE_URL");

            Request request = new Request.Builder().url(url).get().build();
            Response response = client.newCall(request).execute();

            String responseData = response.body().string();
            Map<String, Object> dataMap = objectMapper.readValue(responseData, Map.class);
            // add fields
            imageEmbedding = (String) dataMap.getOrDefault("image_embeddings", "");
            Logging.info("Value: " + imageEmbedding);
            isEncodeImage = true;
        } catch (Exception e) {
            Logging.error(e);
            isEncodeImage = false;
        }

    }
}
