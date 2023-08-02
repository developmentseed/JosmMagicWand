package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.openstreetmap.josm.data.ProjectionBounds;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.MainJosmMagicWandPlugin;
import org.openstreetmap.josm.tools.Logging;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class SamImage {
    private BufferedImage layerImage;
    private ImageIcon imageIcon;
    private ProjectionBounds projectionBounds;
    //    encode
    private String base64Image;

    private EastNorth center;
    private final ObjectMapper objectMapper = new ObjectMapper();
    // enconde fields
    private boolean isEncodeImage = false;
    private List<Integer> imageShape;
    private String crs = "EPSG:3857";
    private List<Double> bbox;
    private String imageEmbedding;
    private Polygon bboxPolygon;

    public SamImage(ProjectionBounds projectionBounds, BufferedImage layerImage) {
        //  image
        this.layerImage = layerImage;
        this.base64Image = CommonUtils.encodeImageToBase64(layerImage);
        this.imageIcon = new ImageIcon(layerImage);
        // create imageShape
        this.imageShape = new ArrayList<>(Arrays.asList(
                layerImage.getHeight(), layerImage.getWidth()
        ));

        // projectionBounds
        this.center = projectionBounds.getCenter();
        this.projectionBounds = projectionBounds;
        // create bbox
        this.bbox = new ArrayList<>(Arrays.asList(
                projectionBounds.getMin().getX(),
                projectionBounds.getMin().getY(),
                projectionBounds.getMax().getX(),
                projectionBounds.getMax().getY()));
        this.bboxPolygon = createPolygonFromDoubles(this.bbox);
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
            // request body
            EncondeRequestBody encodeRequestBody = new EncondeRequestBody(base64Image);
            String requestBodyJson = objectMapper.writeValueAsString(encodeRequestBody);
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON, requestBodyJson);

            //    client
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(3, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            String url = MainJosmMagicWandPlugin.getDotenv().get("ENCODE_URL");

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            // response
            Response response = client.newCall(request).execute();

            String responseData = response.body().string();
            Map<String, Object> dataMap = objectMapper.readValue(responseData, Map.class);
            // get fields
            imageEmbedding = (String) dataMap.getOrDefault("image_embeddings", "");
            isEncodeImage = true;
        } catch (Exception e) {
            Logging.error(e);
            isEncodeImage = false;
        }

    }

    public Polygon createPolygonFromDoubles(List<Double> coordinates) {
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

    public Geometry fetchDecodePoint(double x, double y) {
        GeoJsonReader reader = new GeoJsonReader();
        String url = MainJosmMagicWandPlugin.getDotenv().get("DECODE_URL");

        try {

            // request body
            Coordinate clickCoordinate = mouseClick2Coordinate(x, y);

            DecondeRequestBody decodeRequestBody = new DecondeRequestBody(bbox, imageEmbedding, imageShape, 1, Arrays.asList((int) clickCoordinate.x, (int) clickCoordinate.y), 15, crs);
            String requestBodyJson = objectMapper.writeValueAsString(decodeRequestBody);
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON, requestBodyJson);

            //    client

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(3, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.SECONDS)
                    .build();


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

    public boolean containsPoint(Point p) {
        try {
            return bboxPolygon.contains(p);
        } catch (Exception e) {
            Logging.error(e);
        }
        return false;
    }
}
