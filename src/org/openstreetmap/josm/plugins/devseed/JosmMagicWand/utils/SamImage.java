package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import okhttp3.*;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.openstreetmap.josm.data.ProjectionBounds;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.projection.Projection;
import org.openstreetmap.josm.data.projection.Projections;
import org.openstreetmap.josm.tools.Logging;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

@JsonRootName(value = "SamImage")
public class SamImage {


    @JsonIgnore
    private BufferedImage layerImage;
    @JsonIgnore
    private ProjectionBounds projectionBounds;
    @JsonIgnore
    private Projection projection;

    @JsonIgnore
    private EastNorth center;
    //    image
    @JsonProperty("canvasImage")
    private String canvasImage;
    // aoi response
    @JsonProperty("id")
    private String id;
    @JsonProperty("imageUrl")
    private String imageUrl;
    @JsonProperty("projectName")
    private String projectName;
    @JsonProperty("tifUrl")
    private String tifUrl;
    @JsonProperty("zoom")
    private Double zoom;
    @JsonProperty("crs")
    private String crs;
    // encode flag
    @JsonProperty("isEncode")
    private boolean isEncode = false;
    // bbox
    @JsonProperty("bbox")
    private List<Double> bbox;
    @JsonProperty("bbox4326")
    private List<Double> bbox4326;

    @JsonIgnore
    private Polygon bboxPolygon;
    @JsonIgnore
    private Way bboxWay;
    //   name
    @JsonProperty("nameObject")
    private String nameObject;
    @JsonProperty("layerName")
    private String layerName;
    // image shape
    @JsonProperty("imageShape")
    private List<Integer> imageShape;

    // utils
    @JsonIgnore
    private final ObjectMapper objectMapper;
    @JsonIgnore
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    @JsonIgnore
    private final OkHttpClient client;


    public SamImage(ProjectionBounds projectionBounds, Projection currentProjection, Double zoom, BufferedImage layerImage, String layerName) {
        //  image
        this.setId(UUID.randomUUID().toString());
        this.setLayerImage(layerImage);
        this.setCanvasImage(CommonUtils.encodeImageToBase64(layerImage));
        this.setImageShape(new ArrayList<>(Arrays.asList(
                layerImage.getHeight(), layerImage.getWidth()
        )));
        this.setZoom(zoom);
        // projectionBounds
        this.setCrs(currentProjection.toCode());
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
        // bbox 4326
        EastNorth topLeft = new EastNorth(projectionBounds.minEast, projectionBounds.maxNorth);
        EastNorth bottomRight = new EastNorth(projectionBounds.maxEast, projectionBounds.minNorth);

        LatLon latLonTopLeft = currentProjection.eastNorth2latlon(topLeft);
        LatLon latLonBottomRight = currentProjection.eastNorth2latlon(bottomRight);

        Projection epsg4326 = Projections.getProjectionByCode("EPSG:4326");
        EastNorth topLeft4326 = epsg4326.latlon2eastNorth(latLonTopLeft);
        EastNorth bottomRight4326 = epsg4326.latlon2eastNorth(latLonBottomRight);

        this.setBbox4326(new ArrayList<>(Arrays.asList(
                topLeft4326.getX(),
                topLeft4326.getY(),
                bottomRight4326.getX(),
                bottomRight4326.getY()
        )));

        this.setNameObject(CommonUtils.getDateTime() + "__" + CommonUtils.getMapLayerName(layerName) + ".json");
        this.setLayerName(layerName);
        saveCache(this.getNameObject());
        // api client
        this.client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    }

    public SamImage(
            @JsonProperty("canvasImage") String canvasImage,
            @JsonProperty("id") String id,
            @JsonProperty("imageUrl") String imageUrl,
            @JsonProperty("projectName") String projectName,
            @JsonProperty("tifUrl") String tifUrl,
            @JsonProperty("zoom") Double zoom,
            @JsonProperty("crs") String crs,
            @JsonProperty("isEncode") boolean isEncode,
            @JsonProperty("bbox") List<Double> bbox,
            @JsonProperty("bbox4326") List<Double> bbox4326,
            @JsonProperty("nameObject") String nameObject,
            @JsonProperty("layerName") String layerName,
            @JsonProperty("imageShape") List<Integer> imageShape


    ) {
        this.setCanvasImage(canvasImage);
        this.setId(id);
        this.setImageUrl(imageUrl);
        this.setProjectName(projectName);
        this.setTifUrl(tifUrl);
        this.setZoom(zoom);
        this.setCrs(crs);
        this.setEncode(isEncode);
        this.setBbox(bbox);
        this.setBbox4326(bbox4326);
        this.setNameObject(nameObject);
        this.setLayerName(layerName);
        this.setImageShape(imageShape);

        // other fields
        this.setLayerImage(CommonUtils.decodeBase64ToImage(canvasImage));
        ProjectionBounds projectionBoundsTmp = new ProjectionBounds(bbox.get(0), bbox.get(1), bbox.get(2), bbox.get(3));
        this.setProjection(Projections.getProjectionByCode(this.crs));

        this.setCenter(projectionBoundsTmp.getCenter());
        this.setProjectionBounds(projectionBoundsTmp);
        this.setBboxPolygon(createPolygonFromDoubles(bbox));
        this.setBboxWay(createBboxWay());
        //  fetch data
        this.client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

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

    public void updateCacheImage() {
        saveCache(nameObject);
    }

    public void removeCacheImge() {
        try {
            String magicWandDirPath = CommonUtils.magicWandCacheDirPath();
            String filePath = magicWandDirPath + File.separator + nameObject;
            File file = new File(filePath);
            file.delete();
        } catch (Exception e) {
            Logging.error(e);
        }

    }



    public void setEncodeImage() {
        try {
            // request body
            EncondeRequestBody encodeRequestBody = new EncondeRequestBody(getCanvasImage(),"josm_magic_wand", getZoom().intValue(), getBbox4326(), getId());
            String requestBodyJson = this.objectMapper.writeValueAsString(encodeRequestBody);
            RequestBody requestBody = RequestBody.create(JSON, requestBodyJson);

            Request request = new Request.Builder()
                    .url(Constants.ENCODE_URL)
                    .post(requestBody)
                    .build();
            // response
            Response response = this.client.newCall(request).execute();

            String responseData = response.body().string();
            EncodeResponse encodeResponse = this.objectMapper.readValue(responseData, EncodeResponse.class);
            // update some fields
            setTifUrl(encodeResponse.getTifUrl());
            setImageUrl(encodeResponse.getImageUrl());
            setEncode(true);
        } catch (Exception e) {
            Logging.error(e);
            setEncode(false);
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
        return  geometryList;
//        try {
//
//            // request body
//            Coordinate clickCoordinate = mouseClick2Coordinate(x, y);
//
//            DecondeRequestBody decodeRequestBody = new DecondeRequestBody(getBbox(), getImageEmbedding(), getImageShape(), Arrays.asList((int) clickCoordinate.x, (int) clickCoordinate.y));
//            String requestBodyJson = objectMapper.writeValueAsString(decodeRequestBody);
//            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//            RequestBody requestBody = RequestBody.create(JSON, requestBodyJson);
//            //    client
//
//            OkHttpClient client = new OkHttpClient.Builder()
//                    .connectTimeout(3, TimeUnit.SECONDS)
//                    .readTimeout(3, TimeUnit.SECONDS)
//                    .build();
//
//
//            Request request = new Request.Builder()
//                    .url(Constants.DECODE_URL)
//                    .post(requestBody)
//                    .build();
//
//            Response response = client.newCall(request).execute();
//
//            if (response.isSuccessful()) {
//                String responseData = response.body().string();
//                Map<String, Object> dataMap = objectMapper.readValue(responseData, Map.class);
//                if (dataMap.getOrDefault("status", "").equals("success")) {
//                    List<String> geojsonsList = (List<String>) dataMap.get("geojsons");
//                    List<Double> geojsonConfidence = (List<Double>) dataMap.get("confidence_scores");
//                    Double maxVal = Collections.max(geojsonConfidence);
//                    Integer maxIdx = geojsonConfidence.indexOf(maxVal);
//                    Geometry geometryJson = reader.read(geojsonsList.get(maxIdx));
//                    // validate geometry
//                    geometryList = CommonUtils.extractPolygons(geometryJson);
//                }
//            }
//
//        } catch (Exception e) {
//            Logging.error(e);
//        }
//        return CommonUtils.filterByArea(geometryList, 0.1);
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
    // get set

    public BufferedImage getLayerImage() {
        return layerImage;
    }

    public void setLayerImage(BufferedImage layerImage) {
        this.layerImage = layerImage;
    }

    public ProjectionBounds getProjectionBounds() {
        return projectionBounds;
    }

    public void setProjectionBounds(ProjectionBounds projectionBounds) {
        this.projectionBounds = projectionBounds;
    }

    public EastNorth getCenter() {
        return center;
    }

    public void setCenter(EastNorth center) {
        this.center = center;
    }

    public String getCanvasImage() {
        return canvasImage;
    }

    public void setCanvasImage(String canvasImage) {
        this.canvasImage = canvasImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getTifUrl() {
        return tifUrl;
    }

    public void setTifUrl(String tifUrl) {
        this.tifUrl = tifUrl;
    }

    public Double getZoom() {
        return zoom;
    }

    public void setZoom(Double zoom) {
        this.zoom = zoom;
    }

    public String getCrs() {
        return crs;
    }

    public void setCrs(String crs) {
        this.crs = crs;
    }

    public boolean isEncode() {
        return isEncode;
    }

    public void setEncode(boolean encode) {
        isEncode = encode;
    }

    public List<Double> getBbox() {
        return bbox;
    }

    public void setBbox(List<Double> bbox) {
        this.bbox = bbox;
    }

    public Polygon getBboxPolygon() {
        return bboxPolygon;
    }

    public void setBboxPolygon(Polygon bboxPolygon) {
        this.bboxPolygon = bboxPolygon;
    }

    public Way getBboxWay() {
        return bboxWay;
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

    public List<Integer> getImageShape() {
        return imageShape;
    }

    public void setImageShape(List<Integer> imageShape) {
        this.imageShape = imageShape;
    }

    public Projection getProjection() {
        return projection;
    }

    public void setProjection(Projection projection) {
        this.projection = projection;
    }

    public List<Double> getBbox4326() {
        return bbox4326;
    }

    public void setBbox4326(List<Double> bbox4326) {
        this.bbox4326 = bbox4326;
    }
}
