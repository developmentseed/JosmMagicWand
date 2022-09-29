package org.openstreetmap.josm.plugins.devseed.JosmMagicWand;

import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils.CommonUtils;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils.FloodFillFacade;

import org.opencv.core.*;


public class Feature {
    private final CommonUtils commonUtils = new CommonUtils();
    private final FloodFillFacade floodFillFacade = new FloodFillFacade();

    public Mat processImageRaster(Mat mat_image, Mat mat_mask, boolean ctrl_, boolean shift_, int x, int y) throws Exception {

        Mat mat_flood = new Mat();
        mat_flood.create(new Size(mat_image.cols() + 2, mat_image.rows() + 2), CvType.CV_8UC1);
        mat_flood.setTo(new Scalar(0));
        floodFillFacade.setTolerance(ToolSettings.getTolerance());
        //
        Mat mat_blur = commonUtils.blur(mat_image, 7);
        floodFillFacade.fill(mat_blur, mat_flood, x, y);
        Mat mat_open = commonUtils.open(mat_flood, 3);
        Mat mat_close = commonUtils.close(mat_open, 11);
        Mat mat_erode = commonUtils.erode(mat_close, 3);
        Mat mat_dilate = commonUtils.dilate(mat_erode, 3);


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
                return mat_tmp.clone();
            }
        }

        return mat_dilate.clone();
    }


}
