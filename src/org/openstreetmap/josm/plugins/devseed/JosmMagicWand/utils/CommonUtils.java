package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils;

import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.util.GeometryFixer;
import org.locationtech.jts.precision.GeometryPrecisionReducer;
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier;
import org.locationtech.jts.simplify.PolygonHullSimplifier;
import org.locationtech.jts.simplify.TopologyPreservingSimplifier;
import org.opencv.core.Point;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;
import org.openstreetmap.josm.command.AddCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.TagMap;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.preferences.JosmBaseDirectories;
import org.openstreetmap.josm.data.projection.Projection;
import org.openstreetmap.josm.data.projection.ProjectionRegistry;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.ToolSettings;
import org.openstreetmap.josm.tools.Logging;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.openstreetmap.josm.tools.I18n.tr;

public class CommonUtils {
    // IMAGE
    public static Mat bufferedImage2Mat(BufferedImage bi) {
        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;

    }

    public static BufferedImage mat2BufferedImageGray(Mat mat) {
        byte[] data1 = new byte[mat.rows() * mat.cols() * (int) (mat.elemSize())];
        mat.get(0, 0, data1);
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), BufferedImage.TYPE_BYTE_GRAY);
        image.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data1);
        return image;
    }

    public static void bufferedImageSaveFile(BufferedImage image, String filePath) {
        try {
            File oupFile = new File(filePath);
            int index = filePath.lastIndexOf('.');
            String extension = "jpg";
            if (index > 0) {
                extension = filePath.substring(index + 1);
            }
            ImageIO.write(image, extension, oupFile);
        } catch (Exception e) {
            Logging.error("Exception " + e + " save image");
        }
    }

    public static BufferedImage convertColorspace(BufferedImage image, int newType) {
        try {
            BufferedImage raw_image = image;
            image = new BufferedImage(raw_image.getWidth(), raw_image.getHeight(), newType);
            ColorConvertOp xformOp = new ColorConvertOp(null);
            xformOp.filter(raw_image, image);
        } catch (Exception e) {
            Logging.error("Exception " + e + " converting image");
        }

        return image;
    }

    public static BufferedImage toBufferedImage(Mat matrix) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (matrix.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = matrix.channels() * matrix.cols() * matrix.rows();
        byte[] buffer = new byte[bufferSize];
        matrix.get(0, 0, buffer); // get all the pixels
        BufferedImage image = new BufferedImage(matrix.cols(), matrix.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
        return image;
    }

    public static MatOfPoint2f toMatOfPointFloat(MatOfPoint mat) {
        MatOfPoint2f matFloat = new MatOfPoint2f();
        mat.convertTo(matFloat, CvType.CV_32FC2);
        return matFloat;
    }

    public static List<MatOfPoint> obtainContour(Mat input) {
        Logging.info("-- obtainContour -- ");
        Mat cropMat = input.submat(2, input.rows(), 2, input.cols());
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchey = new Mat();
        Imgproc.findContours(cropMat, contours, hierarchey, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        return contours;
    }

    public static Mat maskInsideImage(Mat image, Mat mask, Double alpha) {
        Mat mask_gbr = new Mat();
        Mat mask_resize = new Mat();
        Imgproc.cvtColor(mask, mask_gbr, Imgproc.COLOR_GRAY2BGR);
        Imgproc.resize(mask_gbr, mask_resize, new Size(image.width(), image.height()));
        mask_gbr.release();
        Mat mask_tmp3 = new Mat();
        Core.inRange(mask_resize, new Scalar(1.0, 1.0, 1.0), new Scalar(255, 255, 255), mask_tmp3);
        mask_resize.setTo(new Scalar(0, 0, 255), mask_tmp3);
        mask_tmp3.release();
        Mat image_tmp = image.clone();

        Core.addWeighted(mask_resize, alpha, image.clone(), 1.0, 0.0, image_tmp);
        return image_tmp;
    }

    // IMAGE EFFECT
    public static Mat blur(Mat input, int size) {
        Logging.info("-- blur -- " + size);
        Mat outputImage = new Mat(input.rows(), input.cols(), input.type());
        Imgproc.blur(input, outputImage, new Size(size, size));
        return outputImage;
    }

    public static Mat gaussian(Mat input, int size) {
        Logging.info("-- gaussian -- " + size);
        Mat outputImage = new Mat(input.rows(), input.cols(), input.type());
        Imgproc.GaussianBlur(input, outputImage, new Size(size, size), 2);
        return outputImage;
    }

    public static Mat cany(Mat imput, int size) {
        Logging.info("-- cany -- " + size);
        Mat outputImage = new Mat(new Size(1, 1), CvType.CV_8UC1, new Scalar(255));
        Imgproc.Canny(imput, outputImage, size, size * 2 + 1);
        return outputImage;
    }

    public static Mat dilate(Mat input, int size) {
        Logging.info("-- dilate -- " + size);
        Mat outputImage = new Mat(input.rows(), input.cols(), input.type());
        Mat kernel = new Mat(new Size(size, size), CvType.CV_8UC1, new Scalar(255));
        Imgproc.morphologyEx(input, outputImage, Imgproc.MORPH_DILATE, kernel);
        return outputImage;
    }

    public static Mat erode(Mat input, int size) {
        Logging.info("-- erode -- " + size);
        Mat outputImage = new Mat(input.rows(), input.cols(), input.type());
        Mat kernel = new Mat(new Size(size, size), CvType.CV_8UC1, new Scalar(255));
        Imgproc.morphologyEx(input, outputImage, Imgproc.MORPH_ERODE, kernel);
        return outputImage;
    }

    public static Mat open(Mat input, int size) {
        Logging.info("-- open -- " + size);
        Mat outputImage = new Mat(input.rows(), input.cols(), input.type());
        Mat kernel = new Mat(new Size(size, size), CvType.CV_8UC1, new Scalar(255));
        Imgproc.morphologyEx(input, outputImage, Imgproc.MORPH_OPEN, kernel);
        return outputImage;
    }

    public static Mat close(Mat input, int size) {
        Logging.info("-- close -- " + size);
        Mat outputImage = new Mat(input.rows(), input.cols(), input.type());
        Mat kernel = new Mat(new Size(size, size), CvType.CV_8UC1, new Scalar(255));
        Imgproc.morphologyEx(input, outputImage, Imgproc.MORPH_CLOSE, kernel);
        return outputImage;
    }

    public static Mat fastDenoising(Mat input, int elementSize) {
        Mat outputImage = new Mat();
        Photo.fastNlMeansDenoisingColored(input, outputImage, elementSize);
        return outputImage;
    }

    public static Mat median(Mat input, int size) {
        Logging.info("-- median -- " + size);
        Mat outputImage = new Mat(input.rows(), input.cols(), input.type());
        Imgproc.medianBlur(input, outputImage, size);
        return outputImage;
    }

    public static Geometry simplifyDouglasP(Geometry g, double distance) {
        Geometry result = DouglasPeuckerSimplifier.simplify(g, distance);
        Logging.info("-- simplifyDouglasP -- " + distance + " -- " + g.getNumPoints() + " -- " + result.getNumPoints());
        return result;
    }
    // GEOMETRY SIMPLIFY

    public static Geometry simplifyPolygonHull(Geometry g, double vertex) {
        Geometry result = PolygonHullSimplifier.hull(g, true, vertex);
        Logging.info("-- simplifyPolygonHull -- " + vertex + " -- " + g.getNumPoints() + " -- " + result.getNumPoints());
        return result;
    }

    public static Geometry simplifyTopologyPreserving(Geometry g, double distance) {
        Geometry result = TopologyPreservingSimplifier.simplify(g, distance);
        Logging.info("-- simplifyTopologyPreserving -- " + distance + " -- " + g.getNumPoints() + " -- " + result.getNumPoints());
        return result;
    }

    public static Geometry reduceGeometry(Geometry g, int scale) {
        Geometry result = GeometryPrecisionReducer.reduce(g, new PrecisionModel(scale));
        Logging.info("-- reduceGeometry -- " + scale + " -- " + g.getNumPoints() + " -- " + result.getNumPoints());
        return result;
    }

    // GEOMETRY UTILS
    public static Polygon coordinates2Polygon(List<Coordinate> coordinates) throws Exception {
        if (coordinates == null || coordinates.size() < 3) {
            throw new Exception("The coordinate list must have at least 3 points to form a valid polygon.");
        }
        GeometryFactory gf = new GeometryFactory();

        if (!coordinates.get(coordinates.size() - 1).equals(coordinates.get(0))) {
            coordinates.add(coordinates.get(0));
        }

        Polygon tmpPolygon = gf.createPolygon(coordinates.toArray(new Coordinate[]{}));
        if (!tmpPolygon.isValid()) {
            Geometry fixedGeometry = GeometryFixer.fix(tmpPolygon);
            if (fixedGeometry instanceof Polygon) {
                return gf.createPolygon(((Polygon) fixedGeometry).getExteriorRing().getCoordinates());
            } else if (fixedGeometry instanceof MultiPolygon) {
                return gf.createPolygon(((Polygon) fixedGeometry.getGeometryN(0)).getExteriorRing().getCoordinates());
            } else {
                throw new Exception("La corrección de la geometría no produjo un polígono válido.");
            }
        }
        return tmpPolygon;
    }

    public static List<Coordinate> nodes2Coordinates(List<Node> nodes) {
        // always work in "EPSG:3857"
        return nodes.stream().map(n -> {
            EastNorth eastNorth = n.getEastNorth();
            return new Coordinate(eastNorth.getX(), eastNorth.getY());
        }).collect(Collectors.toList());
    }

    public static List<Node> coordinates2Nodes(List<Coordinate> coordinates, Projection projection) {
        return coordinates.stream().map(c -> {
            EastNorth eastNorth = new EastNorth(c.x, c.y);
            LatLon latLon = projection.eastNorth2latlon(eastNorth);
            return new Node(latLon);
        }).collect(Collectors.toList());
    }

    public static Collection<Command> geometry2WayCommands(DataSet ds, List<Geometry> geometries, String tagMapKey, String tagMapValue) throws Exception {
        Collection<Command> cmds = new LinkedList<>();
        Projection projection = ProjectionRegistry.getProjection();
        List<Geometry> geometriesSplit = new ArrayList<>();
        for (Geometry geometry : geometries) {
            if (geometry.getNumGeometries() > 1) {
                for (int j = 0; j < geometry.getNumGeometries(); j++) {
                    geometriesSplit.add(geometry.union());
                    System.out.println("geom interno");
                }
            } else {
                geometriesSplit.add(geometry.union());
                System.out.println("geom externo");
            }
        }

        for (Geometry geometry : geometriesSplit) {
            Way w = new Way();
            List<Node> nodes = coordinates2Nodes(List.of(geometry.union().getCoordinates()), projection);
            int index = 0;
            for (Node n : nodes) {
                if (index == (nodes.size() - 1)) {
                    w.addNode(nodes.get(0));
                } else {
                    w.addNode(n);
                    cmds.add(new AddCommand(ds, n));
                }
                index++;
            }
            if (!(tagMapKey.isBlank() || tagMapValue.isBlank())) {
                w.setKeys(new TagMap(tagMapKey, tagMapValue));
            }
            cmds.add(new AddCommand(ds, w));
        }
        return cmds;
    }

    public static Geometry chaikinAlgotihm(Geometry geometry, double maxAngle) {
        // http://graphics.cs.ucdavis.edu/education/CAGDNotes/Chaikins-Algorithm/Chaikins-Algorithm.html
        if (maxAngle == 0) return geometry;
        Coordinate[] coordinates = geometry.getCoordinates();
        if (coordinates.length <= 3) return geometry;
        try {
            double smoothingFactor = 0.25;
            List<Coordinate> tmpCoords = new ArrayList<>();

            for (int i = 0; i < coordinates.length - 1; i++) {
                Coordinate p2 = coordinates[i];
                Coordinate p1, p3;

                if (i == 0) {
                    p1 = coordinates[coordinates.length - 1];
                    p3 = coordinates[1];
                    if (p2.equals(p1)) {
                        p1 = coordinates[coordinates.length - 2];
                    }
                } else {
                    p1 = coordinates[i - 1];
                    p3 = coordinates[i + 1];
                }

                double angle = Angle.toDegrees(Angle.angleBetween(p1, p2, p3));
                if (angle <= maxAngle) {
                    double x1 = p1.getX() + smoothingFactor * (p2.getX() - p1.getX());
                    double y1 = p1.getY() + smoothingFactor * (p2.getY() - p1.getY());

                    double x2 = p2.getX() + smoothingFactor * (p1.getX() - p2.getX());
                    double y2 = p2.getY() + smoothingFactor * (p1.getY() - p2.getY());
                    tmpCoords.add(new Coordinate(x1, y1));
                    tmpCoords.add(new Coordinate(x2, y2));
                } else {
                    tmpCoords.add(p2);
                }

            }
            Logging.info("-- chaikinAlgotihm -- Ang: " + maxAngle + " Orig: " + coordinates.length + " new : " + tmpCoords.size());

            return simplifyPolygonHull(coordinates2Polygon(tmpCoords), 0.999);
        } catch (Exception e) {
            Logging.error(e);
            return geometry;
        }
    }

    public static List<Geometry> filterByArea(List<Geometry> geometries, double tolerance) {
        double areaMAx = 0;
        for (Geometry geom : geometries) {
            if (geom.getArea() >= areaMAx) {
                areaMAx = geom.getArea();
            }
        }
        double areaTolerance = areaMAx * tolerance;
        return geometries.stream().filter(x -> x.getArea() >= areaTolerance).collect(Collectors.toList());
    }

    // ACTIONS
    public static Mat processImage(Mat mat_image, Mat mat_mask, boolean ctrl_, boolean shift_, int x, int y) throws Exception {
        FloodFillFacade floodFillFacade = new FloodFillFacade();
        Mat mat_flood = new Mat();
        mat_flood.create(new Size(mat_image.cols() + 2, mat_image.rows() + 2), CvType.CV_8UC1);
        mat_flood.setTo(new Scalar(0));
        floodFillFacade.setTolerance(ToolSettings.getTolerance());
        // improve colors
        Mat mat_blur = blur(mat_image, 7);
        floodFillFacade.fill(mat_blur, mat_flood, x, y);

        Mat mat_open = open(mat_flood, 5);
        Mat mat_close = close(mat_open, 5);
//        Mat mat_erode = erode(mat_close, 3);
        Mat mat_dilate = dilate(mat_close, 1);


        if (mat_mask != null) {
            if (ctrl_ && shift_) return mat_dilate.clone();
            // add
            if (ctrl_) {
                Mat mat_tmp = new Mat();
                mat_tmp.create(new Size(mat_image.cols() + 2, mat_image.rows() + 2), CvType.CV_8UC1);
                mat_tmp.setTo(new Scalar(0));
                Core.bitwise_or(mat_mask.clone(), mat_dilate.clone(), mat_tmp);
                return mat_tmp.clone();
            }
            // subs
            if (shift_) {
                Mat mat_tmp = new Mat();
                mat_tmp.create(new Size(mat_image.cols() + 2, mat_image.rows() + 2), CvType.CV_8UC1);
                mat_tmp.setTo(new Scalar(0));
                Core.subtract(mat_mask.clone(), mat_dilate.clone(), mat_tmp);
                Mat mat_tmp_open = open(mat_tmp, 9);
                return mat_tmp_open.clone();
            }
        }

        return mat_dilate.clone();
    }

    public static List<Geometry> mergeGeometry(List<CustomPolygon> polygons) throws Exception {
        Logging.info("-- mergeGeometry --");
        List<Geometry> newPolygons = new ArrayList<>();

        for (CustomPolygon polygon : polygons) {
            Geometry newPolygonGeometry = polygon.polygon().copy();
            boolean isUseTmp = false;
            if (polygon.isUse()) continue;
            for (CustomPolygon polygonSecond : polygons) {
                if (polygonSecond.isUse()) continue;
                if (polygon.id().equals(polygonSecond.id())) continue;
                if (newPolygonGeometry.intersects(polygonSecond.polygon())) {
                    newPolygonGeometry = newPolygonGeometry.union(polygonSecond.polygon());
                    polygonSecond.usePolygon();
                    isUseTmp = true;
                }
            }
            if (isUseTmp) polygon.usePolygon();

            newPolygons.add(simplifyPolygonHull(newPolygonGeometry.copy(), 0.999));
            Logging.info("polygons " + polygons.size() + " newPolygons " + newPolygons.size());
        }
        return newPolygons;
    }

    public static List<Geometry> contourn2Geometry(List<MatOfPoint> contours, MapView mapview) {
        Logging.info("-- contourn2Geometry --");
        List<Geometry> geometries = new ArrayList<>();
        Projection projection = ProjectionRegistry.getProjection();

        for (MatOfPoint contour : contours) {
            if (contour.height() <= 5) continue;
            try {
                if (contour.toList().size() <= 5) continue;

                List<Coordinate> tmpCoords = new ArrayList<>();
                for (Point p : contour.toList()) {
                    LatLon latLon = mapview.getLatLon(p.x, p.y);
                    EastNorth eastNorth = latLon.getEastNorth(projection);
                    Coordinate c = new Coordinate(eastNorth.getX(), eastNorth.getY());
                    tmpCoords.add(c);
                }
                // create multi
                Geometry geometryTmp = coordinates2Polygon(tmpCoords);
                geometries.add(geometryTmp.copy());
            } catch (Exception ex) {
                Logging.error(ex);
            }
        }

        return filterByArea(geometries, 0.1);
    }

    public static Geometry simplifySmoothGeometry(Geometry geometry) {
        if (ToolSettings.getSimplDouglasP() > 0) {
            geometry = CommonUtils.simplifyDouglasP(geometry.copy(), ToolSettings.getSimplDouglasP());
        }
        if (ToolSettings.getSimplPolygonHull() > 0) {
            geometry = CommonUtils.simplifyPolygonHull(geometry.copy(), ToolSettings.getSimplPolygonHull());
        }
        if (ToolSettings.getSimplTopologyPreserving() > 0) {
            geometry = CommonUtils.simplifyTopologyPreserving(geometry.copy(), ToolSettings.getSimplTopologyPreserving());
        }
        if (ToolSettings.getChaikinSmooAngle() > 0) {
            geometry = CommonUtils.chaikinAlgotihm(geometry.copy(), ToolSettings.getChaikinSmooAngle());
        }
        return geometry.copy();
    }

    public static String encodeImageToBase64(BufferedImage image) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            Logging.error(e);
            return "";
        }

    }

    public static BufferedImage decodeBase64ToImage(String base64Image) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
            return ImageIO.read(inputStream);
        } catch (Exception e) {
            Logging.error(e);
            return null;
        }
    }

    public static String magicWandCacheDirPath() {
        String josmCacheDataDir = JosmBaseDirectories.getInstance().getCacheDirectory(true).toString();

        return josmCacheDataDir + File.separator + "magicwand";
    }

    public static boolean existCacheDir() {
        return new File(magicWandCacheDirPath()).exists();
    }

    public static void createCacheDir() {
        String magicWandDirPath = magicWandCacheDirPath();
        File magicWandDir = new File(magicWandDirPath);
        if (!magicWandDir.exists()) {
            boolean wasDirectoryCreated = magicWandDir.mkdir();

            if (wasDirectoryCreated) {
                Logging.info(tr("magicwand " + "Folder 'magicwand' successfully created at: " + magicWandDirPath));
            } else {
                Logging.info(tr("magicwand " + "Failed to create the 'magicwand' folder at: " + magicWandDirPath));
            }
        } else {
            Logging.info(tr("magicwand " + "The 'magicwand' folder already exists at: " + magicWandDirPath));
        }

    }

    public static String getDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd__HH_mm_ss");
        return now.format(formatter);
    }

    public static String getMapLayerName(String layerName) {

        if (layerName.length() >= 50) {
            String normalized = layerName.trim().substring(0, 50).replaceAll("[^a-zA-Z0-9\\s-]", "").replaceAll("\\s+", " ");
            return normalized.replaceAll("\\s", "-").toLowerCase();
        } else if (!layerName.isEmpty()) {
            String normalized = layerName.trim().replaceAll("[^a-zA-Z0-9\\s-]", "").replaceAll("\\s+", " ");
            return normalized.replaceAll("\\s", "-").toLowerCase();
        } else {
            return "no_layer_name";
        }
    }

    public static List<Geometry> extractPolygons(Geometry geometry) {
        List<Geometry> polygons = new ArrayList<>();

        if (geometry instanceof MultiPolygon) {
            for (int i = 0; i < geometry.getNumGeometries(); i++) {
                Geometry subGeometry = geometry.getGeometryN(i);
                polygons.addAll(extractPolygons(subGeometry));
            }
        } else if (geometry instanceof Polygon) {
            if (geometry.getNumPoints() > 5 && geometry.isValid()) {
                polygons.add(geometry);
            }
        }

        return polygons;
    }

    private Mat getKernelFromShape(int elementSize, int elementShape) {
        return Imgproc.getStructuringElement(elementShape, new Size(elementSize * 2 + 1, elementSize * 2 + 1), new Point(elementSize, elementSize));
    }
}
