package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.precision.GeometryPrecisionReducer;
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier;
import org.locationtech.jts.simplify.PolygonHullSimplifier;
import org.locationtech.jts.simplify.TopologyPreservingSimplifier;
import org.opencv.core.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;
import org.openstreetmap.josm.tools.Logging;

public class CommonUtils {
    private final GeometryFactory gf = new GeometryFactory();

    public Mat BufferedImage2Mat(BufferedImage bi) {
        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;

    }

    public BufferedImage mat2BufferedImageGray(Mat mat) {
        byte[] data1 = new byte[mat.rows() * mat.cols() * (int) (mat.elemSize())];
        mat.get(0, 0, data1);
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), BufferedImage.TYPE_BYTE_GRAY);
        image.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data1);
        return image;
    }

    public void bufferedImageSaveFile(BufferedImage image, String filePath) throws Exception {
        File oupFile = new File(filePath);
        int index = filePath.lastIndexOf('.');
        String extension = "jpg";
        if (index > 0) {
            extension = filePath.substring(index + 1);
        }
        ImageIO.write(image, extension, oupFile);
    }

    public BufferedImage convertColorspace(BufferedImage image, int newType) {
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

    public BufferedImage toBufferedImage(Mat matrix) {
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

    public Mat blur(Mat input, int size) {
        Logging.info("---------- blur ---------- " + size);
        Mat outputImage = new Mat(input.rows(), input.cols(), input.type());
        Imgproc.blur(input, outputImage, new Size(size, size));
        return outputImage;
    }

    public Mat gaussian(Mat input, int size) {
        Logging.info("---------- gaussian ---------- " + size);
        Mat outputImage = new Mat(input.rows(), input.cols(), input.type());
        Imgproc.GaussianBlur(input, outputImage, new Size(size, size), 2);
        return outputImage;
    }

    public Mat cany(Mat imput, int size) {
        Logging.info("---------- cany ---------- " + size);
        Mat outputImage = new Mat(new Size(1, 1), CvType.CV_8UC1, new Scalar(255));
        Imgproc.Canny(imput, outputImage, size, size * 2 + 1);
        return outputImage;
    }

    public Mat dilate(Mat input, int size) {
        Logging.info("---------- dilate ---------- " + size);
        Mat outputImage = new Mat(input.rows(), input.cols(), input.type());
        Mat kernel = new Mat(new Size(size, size), CvType.CV_8UC1, new Scalar(255));
        Imgproc.morphologyEx(input, outputImage, Imgproc.MORPH_DILATE, kernel);
        return outputImage;
    }

    public Mat erode(Mat input, int size) {
        Logging.info("---------- erode ---------- " + size);
        Mat outputImage = new Mat(input.rows(), input.cols(), input.type());
        Mat kernel = new Mat(new Size(size, size), CvType.CV_8UC1, new Scalar(255));
        Imgproc.morphologyEx(input, outputImage, Imgproc.MORPH_ERODE, kernel);
        return outputImage;
    }

    public Mat open(Mat input, int size) {
        Logging.info("---------- open ---------- " + size);
        Mat outputImage = new Mat(input.rows(), input.cols(), input.type());
        Mat kernel = new Mat(new Size(size, size), CvType.CV_8UC1, new Scalar(255));
        Imgproc.morphologyEx(input, outputImage, Imgproc.MORPH_OPEN, kernel);
        return outputImage;
    }

    public Mat close(Mat input, int size) {
        Logging.info("---------- close ---------- " + size);
        Mat outputImage = new Mat(input.rows(), input.cols(), input.type());
        Mat kernel = new Mat(new Size(size, size), CvType.CV_8UC1, new Scalar(255));
        Imgproc.morphologyEx(input, outputImage, Imgproc.MORPH_CLOSE, kernel);
        return outputImage;
    }

    public Mat fastDenoising(Mat input, int elementSize) {
        Mat outputImage = new Mat();
        Photo.fastNlMeansDenoisingColored(input, outputImage, elementSize);
        return outputImage;
    }

    public Mat median(Mat input, int size) {
        Logging.info("---------- median ---------- " + size);
        Mat outputImage = new Mat(input.rows(), input.cols(), input.type());
        Imgproc.medianBlur(input, outputImage, size);
        return outputImage;
    }

    private Mat getKernelFromShape(int elementSize, int elementShape) {
        return Imgproc.getStructuringElement(elementShape, new Size(elementSize * 2 + 1, elementSize * 2 + 1), new Point(elementSize, elementSize));
    }

    public List<MatOfPoint> obtainContour(Mat input) {
        Logging.info("---------- obtainContour ---------- ");
        Mat cropMat = input.submat(2, input.rows(), 2, input.cols());
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchey = new Mat();
        Imgproc.findContours(cropMat, contours, hierarchey, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        return contours;
    }

    public MatOfPoint2f toMatOfPointFloat(MatOfPoint mat) {
        MatOfPoint2f matFloat = new MatOfPoint2f();
        mat.convertTo(matFloat, CvType.CV_32FC2);
        return matFloat;
    }

    public Geometry simplifyDP(Geometry g, double distance) {
        return DouglasPeuckerSimplifier.simplify(g, distance);
    }

    public Geometry reduceGeometry(Geometry g, int scale) {
        return GeometryPrecisionReducer.reduce(g, new PrecisionModel(scale));
    }

    public Geometry simplifyPolygonHull(Geometry g, double vertex) {
        return PolygonHullSimplifier.hull(g, true, vertex);
    }

    public Geometry simplifyTopologyPreservingSimplifier(Geometry g, double distance) {
        return TopologyPreservingSimplifier.simplify(g, distance);
    }

    public Geometry Coordinates2Geometry(List<Coordinate> coordinates, boolean close) throws Exception {
        if (close) {
            if (!coordinates.get(coordinates.size() - 1).equals(coordinates.get(0)))
                coordinates.add(coordinates.get(0));
            var tmpPolygon = gf.createPolygon(coordinates.toArray(new Coordinate[]{}));
            if (!tmpPolygon.isValid()) {
                return tmpPolygon.buffer(0);
            }
            return tmpPolygon;
        }
        return gf.createLineString(coordinates.toArray(new Coordinate[]{}));
    }

    public List<Geometry> contourn2Geometry(List<MatOfPoint> contours, double simpHull, double simpDp, double smallHoleTolerance, double chaikinAngle) {
        Logging.info("---------- contourn2Geometry ----------");
        List<Geometry> geometries = new ArrayList<>();

        for (MatOfPoint contour : contours) {
            if (contour.height() <= 3) continue;
            try {
                List<Coordinate> tmpCoords = new ArrayList<>();

                for (Point p : contour.toList()) {
                    Coordinate c = new Coordinate(p.x, p.y);
                    tmpCoords.add(c);
                }
                if (tmpCoords.size() < 5) continue;

                // create multi
                var geometryTmp = (Geometry) Coordinates2Geometry(tmpCoords, true);

                geometryTmp = reduceGeometry(geometryTmp, 1);


                if (simpHull > 0) {
                    geometryTmp = simplifyPolygonHull(geometryTmp, simpHull);
                    Logging.info("simplifyPolygonHull  " + simpHull + " pts: " + geometryTmp.getCoordinates().length);
                }
                if (simpDp > 0) {
                    geometryTmp = simplifyDP(geometryTmp, simpDp);
                    Logging.info("simplifyDP  " + simpDp + " pts: " + geometryTmp.getCoordinates().length);
                }
                if (smallHoleTolerance > 0) {
                    geometryTmp = removeSmallHole((Polygon) geometryTmp.copy(), smallHoleTolerance, 5);
                    Logging.info("removeSmallHole  " + smallHoleTolerance + " pts: " + geometryTmp.getCoordinates().length);
                }
                if (chaikinAngle > 0) {
                    geometryTmp = chaikinAlgotihm(geometryTmp, chaikinAngle);
                    Logging.info("chaikinAlgotihm  " + chaikinAngle + " pts: " + geometryTmp.getCoordinates().length);
                }
                geometries.add(geometryTmp);

            } catch (Exception ex) {
                Logging.error(ex);
            }
        }


        return geometries;
    }

    public Mat maskInsideImage(Mat image, Mat mask, Double alpha) {
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


    public List<Polygon> mergeGeometry(List<CustomPolygon> polygons)  throws Exception{
        Logging.info("---------- mergeGeometry ----------");
        List<Polygon> newPolygons = new ArrayList<>();

        for (CustomPolygon polygon : polygons) {
            Polygon newPolygon = (Polygon) polygon.polygon().copy();
            boolean isUseTmp = false;
            if (polygon.isUse()) continue;
            for (CustomPolygon polygonSecond : polygons) {
                if (polygonSecond.isUse()) continue;
                if (polygon.id().equals(polygonSecond.id())) continue;
                if (newPolygon.intersects(polygonSecond.polygon())) {
                    newPolygon = (Polygon) newPolygon.union(polygonSecond.polygon());
                    polygonSecond.usePolygon();
                    isUseTmp = true;
                }
            }
            if (isUseTmp) polygon.usePolygon();

            newPolygons.add((Polygon) simplifyPolygonHull(newPolygon, 0.8));

        }
        Logging.info("polygons " + polygons.size() + " newPolygons " + newPolygons.size());
        return newPolygons;
    }

    public Geometry chaikinAlgotihm(Geometry geometry, double maxAngle) throws Exception {
        // http://graphics.cs.ucdavis.edu/education/CAGDNotes/Chaikins-Algorithm/Chaikins-Algorithm.html
        Coordinate[] coordinates = geometry.getCoordinates();
        if (coordinates.length == 0) return geometry;

        List<Coordinate> tmpCoords = new ArrayList<>();
        tmpCoords.add(new Coordinate(coordinates[0].x, coordinates[0].y));
        int coordinatesSize = coordinates.length;
        for (int i = 0; i < coordinatesSize - 1; i++) {

            Coordinate pb = new Coordinate(coordinates[i].x, coordinates[i].y);
            Coordinate pc = coordinates[i + 1];
            if (pb.distance(pc) >= 50) {
                tmpCoords.add(pb);
                continue;
            }

            if (i > 0) {
                Coordinate pa = coordinates[i - 1];
                double angle = angleLine(pa, pb, pc);
                boolean isRect = 89 <= angle && angle <= 91;
                if (isRect || maxAngle <= angle) {
                    if (tmpCoords.contains(pb)) tmpCoords.add(pb);
                    continue;
                }
            }

            var pbx = pb.getX();
            var pby = pb.getY();
            var pcx = pc.getX();
            var pcy = pc.getY();

            Coordinate Q = new Coordinate(0.75 * pbx + 0.25 * pcx, 0.75 * pby + 0.25 * pcy);
            Coordinate R = new Coordinate(0.25 * pbx + 0.75 * pcx, 0.25 * pby + 0.75 * pcy);
            tmpCoords.add(Q);
            tmpCoords.add(R);

        }

        return Coordinates2Geometry(tmpCoords,true);

    }

    public Polygon removeSmallHole(Polygon geometry, double tolerance, int nextPoints) throws Exception {
        List<Coordinate> tmpCoords = new ArrayList<>();
        double perimeter = Math.abs(geometry.getLength());
        if (perimeter == 0.0) return geometry;

        var perimeterTolerance = perimeter * tolerance;

        int coodinatesLenght = geometry.getCoordinates().length;
        Coordinate[] coordinates = geometry.getCoordinates();
        int i = 0;

        while (i <= coodinatesLenght - 1) {
            int tmpIndex = i;
            Coordinate p0 = new Coordinate(coordinates[i].x, coordinates[i].y);
            if (!tmpCoords.contains(p0)) {
                tmpCoords.add(p0);
            }
            for (int j = 1; j < nextPoints; j++) {
                if ((i + j) >= coodinatesLenght) continue;
                Coordinate p1 = coordinates[i + j];

                if (p0.distance(p1) <= perimeterTolerance) {
                    tmpIndex = i + j;
                }
            }
            if (i == tmpIndex) {
                i++;
            } else {
                i = tmpIndex;
            }
        }

        return (Polygon) Coordinates2Geometry(tmpCoords,true);
    }

    public double mPoints(Coordinate ca, Coordinate cb) {
        return (cb.getY() - ca.getY()) / (cb.getX() - ca.getX());
    }

    public double angleLine(Coordinate a, Coordinate b, Coordinate c) {
        double mab = mPoints(a, b);
        double mbc = mPoints(b, c);
        return Math.atan((mbc - mab) / (1 + (mbc * mab)));
    }


//    public List<List<Coordinate>> chuckCoordinates(List<Coordinate> coordinates,double distance,double angle){
//        List<List<Coordinate>> outputListCoordinates = new ArrayList<>();
//
//
//
//    }

}
