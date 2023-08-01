package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.openstreetmap.josm.data.ProjectionBounds;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.MainJosmMagicWandPlugin;
import org.openstreetmap.josm.tools.Logging;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class SamImage {
    private MapView mapView;
    private BufferedImage layerImage;
    private Graphics g;
    private ImageIcon imageIcon;
    private ProjectionBounds projectionBounds;
    private ProjectionBounds projectionApi;

    //    encode
    private String base64Image;
    private int encodeStatus;
    private EastNorth center;
    private ObjectMapper objectMapper;
    // enconde fields
    private boolean isEncodeImage = false;

    private List<Integer> imageShape;
    private int inputLabel;
    private String crs;
    private List<Double> bbox;
    private double zoom;
    private String imageEmbedding;
    private Polygon bboxPolygon;

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
        return projectionApi;
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
            crs = (String) dataMap.get("crs");
            bbox = (List<Double>) dataMap.get("bbox");
            imageShape = (List<Integer>) dataMap.get("image_shape");
            bboxPolygon = createPolygonFromDoubles((List<Double>) dataMap.get("bbox"));
            EastNorth northwest = new EastNorth(bbox.get(0), bbox.get(1));
            EastNorth southeast = new EastNorth(bbox.get(2), bbox.get(3));

            projectionApi = new ProjectionBounds(northwest, southeast);
            // add fields
            imageEmbedding = (String) dataMap.getOrDefault("image_embeddings", "");
            isEncodeImage = true;
        } catch (Exception e) {
            Logging.error(e);
            isEncodeImage = false;
        }

    }

    public Polygon createPolygonFromDoubles(List<Double> coordinates) {
        if (coordinates.size() != 4) {
            Logging.error("The coordinate array must contain exactly 4 values.");
            LatLon northwest = mapView.getLatLon(0, 0);
            LatLon southeast = mapView.getLatLon(mapView.getWidth(), mapView.getHeight());
            coordinates = new ArrayList<>();
            coordinates.add(northwest.getX());
            coordinates.add(northwest.getY());
            coordinates.add(southeast.getX());
            coordinates.add(southeast.getY());
        }
        Coordinate[] vertices = {
                new Coordinate(coordinates.get(0), coordinates.get(1)),
                new Coordinate(coordinates.get(0), coordinates.get(3)),
                new Coordinate(coordinates.get(2), coordinates.get(3)),
                new Coordinate(coordinates.get(2), coordinates.get(1)),
                new Coordinate(coordinates.get(0), coordinates.get(1))
        };

        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPolygon(vertices);
    }

    public boolean containsPoint(Point p) {
        try {
            return bboxPolygon.contains(p);
        } catch (Exception e) {
            Logging.error(e);
        }
        return false;
    }

    public Geometry fetchDecodePoint(double x, double y) {
        GeoJsonReader reader = new GeoJsonReader();

        try {
            String url = MainJosmMagicWandPlugin.getDotenv().get("DECODE_URL");
            OkHttpClient client = new OkHttpClient();
            ObjectMapper objectMapper = new ObjectMapper();
            Coordinate clickCoordinate = mouseClick2Coordinate(x, y);
            EncondeRequestBody requestBodyData = new EncondeRequestBody(bbox, imageEmbedding, imageShape, 1, Arrays.asList((int) clickCoordinate.x, (int) clickCoordinate.y), 15, crs);
            String requestBodyJson = objectMapper.writeValueAsString(requestBodyData);

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON, requestBodyJson);

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String responseData = response.body().string();
                Map<String, Object> dataMap = objectMapper.readValue(responseData, Map.class);
                if (dataMap.getOrDefault("status", "").equals("success")) {
                    List<String> geojsonsList = (List<String>) dataMap.get("geojsons");
                    List<Double> geojsonConfidence = (List<Double>) dataMap.get("confidence_scores");
                    Double maxVal = Collections.max(geojsonConfidence);
                    Integer maxIdx = geojsonConfidence.indexOf(maxVal);
                    return reader.read(geojsonsList.get(maxIdx));
                }
            }

        } catch (Exception e) {
            Logging.error(e);
            isEncodeImage = false;
        }
        return null;


    }

    public Coordinate mouseClick2Coordinate(double x, double y) {
        double minX = bbox.get(0);
        double minY = bbox.get(1);
        double maxX = bbox.get(2);
        double maxY = bbox.get(3);

        // image size
        int bboxHeight = imageShape.get(0);
        int bboxWidth = imageShape.get(1);

        //  factor (nx, ny)
        double nx = (x - minX) / (maxX - minX);
        double ny = (y - minY) / (maxY - minY);
        // Y i invert
        return new Coordinate(nx * bboxWidth, (1 - ny) * bboxHeight);
    }

    public List<Double> getBoox() {
        return bbox;
    }

}
