package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.util.GeometryCombiner;
import org.locationtech.jts.precision.GeometryPrecisionReducer;
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier;
import org.locationtech.jts.simplify.PolygonHullSimplifier;
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

    public static MatOfPoint2f toMatOfPointFloat(MatOfPoint mat) {
        MatOfPoint2f matFloat = new MatOfPoint2f();
        mat.convertTo(matFloat, CvType.CV_32FC2);
        return matFloat;
    }

    public static Geometry simplifyDP(Geometry g, double distance) {
        return DouglasPeuckerSimplifier.simplify(g, distance);
    }

    public static Geometry reduceGeometry(Geometry g) {
        return GeometryPrecisionReducer.reduce(g, new PrecisionModel(1));
    }

    public static Geometry simplifyPolygonHull(Geometry g, double vertex) {
        return PolygonHullSimplifier.hull(g, true, vertex);
    }

    public List<Geometry> contourn2Geometry(List<MatOfPoint> contours, int size, int limit_x, int limit_y) {
        Logging.info("---------- contourn2Geometry ----------");
        List<Geometry> geometries = new ArrayList<>();
        List<Coordinate> tmpCoordsAll = new ArrayList<>();

        for (MatOfPoint contour : contours) {
            if (contour.height() <= 3) continue;
            try {
                List<Coordinate> tmpCoords = new ArrayList<>();

                for (Point p : contour.toList()) {
                    Coordinate c = new Coordinate(p.x, p.y);
                    if (!tmpCoordsAll.contains(c)) {
                        tmpCoords.add(c);
                        tmpCoordsAll.add(c);
                    }
                }
                // create multi
                if (!tmpCoords.get(tmpCoords.size() - 1).equals(tmpCoords.get(0))) tmpCoords.add(tmpCoords.get(0));
                var geometryTmp = (Geometry) gf.createPolygon(tmpCoords.toArray(new Coordinate[]{}));

                geometryTmp = reduceGeometry(geometryTmp);
                if (geometryTmp.getArea() < 5) continue;

                var geometryTmpSimplify = simplifyPolygonHull(geometryTmp, 0.2);
                geometryTmpSimplify = simplifyDP(geometryTmpSimplify, 0.3);
                geometries.add(geometryTmpSimplify);

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


    public List<Polygon> mergeGeometry(List<CustomPolygon> polygons) {
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

            newPolygons.add((Polygon) simplifyPolygonHull(newPolygon, 0.95));

        }
        Logging.info("polygons " + polygons.size() + " newPolygons " + newPolygons.size());
        return newPolygons;
    }

}
