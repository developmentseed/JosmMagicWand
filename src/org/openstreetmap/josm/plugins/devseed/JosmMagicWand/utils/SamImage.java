package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.util.GeometryFixer;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.openstreetmap.josm.data.ProjectionBounds;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.tools.Logging;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

@JsonRootName(value = "SamImage")
public class SamImage {

    @JsonIgnore
    private final ObjectMapper objectMapper = new ObjectMapper();
    // fields

    @JsonIgnore
    private BufferedImage layerImage;
    @JsonIgnore
    private ProjectionBounds projectionBounds;
    //    encode
    @JsonProperty("base64Image")
    private String base64Image;

    @JsonIgnore
    private EastNorth center;
    // enconde fields

    @JsonProperty("isEncodeImage")
    private boolean isEncodeImage = false;
    @JsonProperty("imageShape")
    private List<Integer> imageShape;
    @JsonProperty("crs")
    private String crs = "EPSG:3857";
    @JsonProperty("bbox")
    private List<Double> bbox;
    @JsonProperty("imageEmbedding")
    private String imageEmbedding;
    @JsonIgnore
    private Polygon bboxPolygon;
    @JsonIgnore
    private Way bboxWay;
    @JsonProperty("nameObject")
    private String nameObject;
    @JsonProperty("layerName")
    private String layerName;


    public SamImage(ProjectionBounds projectionBounds, BufferedImage layerImage, String layerName) {
        //  image
        this.setLayerImage(layerImage);
        this.setBase64Image(CommonUtils.encodeImageToBase64(layerImage));
        // create imageShape
        this.setImageShape(new ArrayList<>(Arrays.asList(
                layerImage.getHeight(), layerImage.getWidth()
        )));

        // projectionBounds
        this.setCenter(projectionBounds.getCenter());
        this.setProjectionBounds(projectionBounds);
        // create bbox
        this.setBbox(new ArrayList<>(Arrays.asList(
                projectionBounds.getMin().getX(),
                projectionBounds.getMin().getY(),
                projectionBounds.getMax().getX(),
                projectionBounds.getMax().getY())));
        this.setBboxPolygon(createPolygonFromDoubles(this.getBbox()));
        this.setBboxWay(createBboxWay());
        this.setNameObject(CommonUtils.getDateTime() + "__" + CommonUtils.getMapLayerName(layerName) + ".json");
        this.setLayerName(layerName);
        saveCache(this.nameObject);
    }

    public SamImage(
            @JsonProperty("nameObject") String nameObject,
            @JsonProperty("base64Image") String base64Image,
            @JsonProperty("isEncodeImage") boolean isEncodeImage,
            @JsonProperty("imageShape") List<Integer> imageShape,
            @JsonProperty("crs") String crs,
            @JsonProperty("bbox") List<Double> bbox,
            @JsonProperty("imageEmbedding") String imageEmbedding,
            @JsonProperty("layerName") String layerName

    ) {
        this.setNameObject(nameObject);
        this.setBase64Image(base64Image);
        this.setLayerImage(CommonUtils.decodeBase64ToImage(base64Image));
        this.setEncodeImage(isEncodeImage);
        this.setImageShape(imageShape);
        this.setCrs(crs);
        this.setBbox(bbox);
        ProjectionBounds projectionBoundsTmp = new ProjectionBounds(bbox.get(0), bbox.get(1), bbox.get(2), bbox.get(3));
        this.setProjectionBounds(projectionBoundsTmp);
        this.setBboxPolygon(createPolygonFromDoubles(this.getBbox()));
        this.setBboxWay(createBboxWay());
        this.setImageEmbedding(imageEmbedding);
        this.setLayerName(layerName);

    }

    private void saveCache(String fileName) {
        if (!CommonUtils.existCacheDir()) {
            CommonUtils.createCacheDir();
        }
        String magicWandDirPath = CommonUtils.magicWandCacheDirPath();
        String filePath = magicWandDirPath + File.separator + fileName;
        try {
            File file = new File(filePath);
            objectMapper.writeValue(file, this);

            Logging.info("Object save in : " + filePath);
        } catch (Exception e) {
            Logging.error(e);
            e.printStackTrace();
        }
    }
    public void updateCacheImage(){
        saveCache(this.nameObject);
    }

    public BufferedImage getLayerImage() {
        return layerImage;
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
            EncondeRequestBody encodeRequestBody = new EncondeRequestBody(getBase64Image());
            String requestBodyJson = objectMapper.writeValueAsString(encodeRequestBody);
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON, requestBodyJson);

            //    client
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(3, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url(Constants.ENCODE_URL)
                    .post(requestBody)
                    .build();
            // response
            Response response = client.newCall(request).execute();

            String responseData = response.body().string();
            Map<String, Object> dataMap = objectMapper.readValue(responseData, Map.class);
            // get fields
            setImageEmbedding((String) dataMap.getOrDefault("image_embedding", ""));
            setEncodeImage(true);
        } catch (Exception e) {
            Logging.error(e);
            setEncodeImage(false);
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

    public List<Geometry> fetchDecodePoint(double x, double y) {
        List<Geometry> geometryList = new ArrayList<>();
        GeoJsonReader reader = new GeoJsonReader();

        try {

            // request body
            Coordinate clickCoordinate = mouseClick2Coordinate(x, y);

            DecondeRequestBody decodeRequestBody = new DecondeRequestBody(getBbox(), getImageEmbedding(), getImageShape(), 1, Arrays.asList((int) clickCoordinate.x, (int) clickCoordinate.y), 15, getCrs());
            String requestBodyJson = objectMapper.writeValueAsString(decodeRequestBody);
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON, requestBodyJson);

            //    client

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(3, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.SECONDS)
                    .build();


            Request request = new Request.Builder()
                    .url(Constants.DECODE_URL)
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
                    Geometry geometryJson = reader.read(geojsonsList.get(maxIdx));

                    // validate geometry
                    if (geometryJson.getNumGeometries() > 1) {
                        for (int i = 0; i < geometryJson.getNumGeometries(); i++) {
                            Geometry g = geometryJson.getGeometryN(i).union().getBoundary();
                            if (g.getNumPoints() <= 5) continue;
                            if (g.isValid()) {
                                geometryList.add(g);
                            } else {
                                geometryList.add(GeometryFixer.fix(g));
                            }
                        }
                    } else {
                        if (geometryJson.isValid()) {
                            geometryList.add(geometryJson.union().getBoundary());
                        } else {
                            geometryList.add(GeometryFixer.fix(geometryJson.union().getBoundary()));
                        }
                    }

                }
            }

        } catch (
                Exception e) {
            Logging.error(e);
        }
        return CommonUtils.filterByArea(geometryList, 0.15);
    }

    public Coordinate mouseClick2Coordinate(double x, double y) {
        double minX = getBbox().get(0);
        double minY = getBbox().get(1);
        double maxX = getBbox().get(2);
        double maxY = getBbox().get(3);

        // image size
        int bboxHeight = getImageShape().get(0);
        int bboxWidth = getImageShape().get(1);

        //  factor (nx, ny)
        double nx = (x - minX) / (maxX - minX);
        double ny = (y - minY) / (maxY - minY);
        // Y i invert
        return new Coordinate(nx * bboxWidth, (1 - ny) * bboxHeight);
    }


    public boolean containsPoint(Point p) {
        try {
            return getBboxPolygon().contains(p);
        } catch (Exception e) {
            Logging.error(e);
        }
        return false;
    }

    private Way createBboxWay() {
        Node node1 = new Node(new EastNorth(getBbox().get(0), getBbox().get(1)));
        Node node2 = new Node(new EastNorth(getBbox().get(0), getBbox().get(3)));
        Node node3 = new Node(new EastNorth(getBbox().get(2), getBbox().get(3)));
        Node node4 = new Node(new EastNorth(getBbox().get(2), getBbox().get(1)));

        Way way = new Way();
        way.addNode(node1);
        way.addNode(node2);
        way.addNode(node3);
        way.addNode(node4);
        way.addNode(node1);
        return way;
    }

    public Way getBboxWay() {
        return bboxWay;
    }

    public void setLayerImage(BufferedImage layerImage) {
        this.layerImage = layerImage;
    }

    public void setProjectionBounds(ProjectionBounds projectionBounds) {
        this.projectionBounds = projectionBounds;
    }

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    public void setCenter(EastNorth center) {
        this.center = center;
    }

    public void setEncodeImage(boolean encodeImage) {
        isEncodeImage = encodeImage;
    }

    public List<Integer> getImageShape() {
        return imageShape;
    }

    public void setImageShape(List<Integer> imageShape) {
        this.imageShape = imageShape;
    }

    public String getCrs() {
        return crs;
    }

    public void setCrs(String crs) {
        this.crs = crs;
    }

    public List<Double> getBbox() {
        return bbox;
    }

    public void setBbox(List<Double> bbox) {
        this.bbox = bbox;
    }

    public String getImageEmbedding() {
        return imageEmbedding;
    }

    public void setImageEmbedding(String imageEmbedding) {
        this.imageEmbedding = imageEmbedding;
    }

    public Polygon getBboxPolygon() {
        return bboxPolygon;
    }

    public void setBboxPolygon(Polygon bboxPolygon) {
        this.bboxPolygon = bboxPolygon;
    }

    public void setBboxWay(Way bboxWay) {
        this.bboxWay = bboxWay;
    }

    public String getNameObject() {
        return nameObject;
    }

    public void setNameObject(String nameObject) {
        this.nameObject = nameObject;
    }

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }
}
