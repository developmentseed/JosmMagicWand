package org.openstreetmap.josm.plugins.devseed.JosmMagicWand;

import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils.SamImage;

import java.util.ArrayList;
import java.util.List;

public final class ToolSettings {
    private static int tolerance;
    private static int maskClose;
    private static int maskMedian;
    private static int maskOpen;
    // simplify
    private static double simplDouglasP;
    private static double simplPolygonHull;
    private static double simplTopologyPreserving;
    //smooth
    private static double chaikinSmooDistance;
    private static double chaikinSmooAngle;
    // tags
    private static String autoTags;
    // sam images
    private static List<SamImage> samImagesList = new ArrayList<>();
    private ToolSettings() {
    }

    public static List<SamImage> getSamImagesList() {
        return samImagesList;
    }

    public static void setSamImagesList(List<SamImage> samImagesList) {
        ToolSettings.samImagesList = samImagesList;
    }

    public static void setSamImage(SamImage samImage) {
        ToolSettings.samImagesList.add(samImage);
    }

    public static void clearSamImagesList() {
        ToolSettings.samImagesList.clear();
    }

    public static int getMaskOpen() {
        return maskOpen;
    }

    public static void setMaskOpen(int maskOpen) {
        ToolSettings.maskOpen = maskOpen;
    }


    public static int getTolerance() {
        return tolerance;
    }

    public static void setTolerance(int tolerance) {
        ToolSettings.tolerance = tolerance;
    }

    public static int getMaskClose() {
        return maskClose;
    }

    public static void setMaskClose(int maskClose) {
        ToolSettings.maskClose = maskClose;
    }

    public static int getMaskMedian() {
        return maskMedian;
    }

    public static void setMaskMedian(int maskMedian) {
        ToolSettings.maskMedian = maskMedian;
    }

    public static double getSimplPolygonHull() {
        return simplPolygonHull;
    }

    public static void setSimplPolygonHull(double simplPolygonHull) {
        ToolSettings.simplPolygonHull = simplPolygonHull;
    }

    public static double getSimplTopologyPreserving() {
        return simplTopologyPreserving;
    }

    public static void setSimplTopologyPreserving(double simplTopologyPreserving) {
        ToolSettings.simplTopologyPreserving = simplTopologyPreserving;
    }

    public static double getSimplDouglasP() {
        return simplDouglasP;
    }

    public static void setSimplDouglasP(double simplDouglasP) {
        ToolSettings.simplDouglasP = simplDouglasP;
    }

    public static double getChaikinSmooAngle() {
        return chaikinSmooAngle;
    }

    public static void setChaikinSmooAngle(double chaikinSmooAngle) {
        ToolSettings.chaikinSmooAngle = chaikinSmooAngle;
    }

    public static double getChaikinSmooDistance() {
        return chaikinSmooDistance;
    }

    public static void setChaikinSmooDistance(double chaikinSmooDistance) {
        ToolSettings.chaikinSmooDistance = chaikinSmooDistance;
    }

    public static String getAutoTags() {
        return ToolSettings.autoTags;
    }

    public static void setAutoTags(String autoTags) {
        ToolSettings.autoTags = autoTags;
    }
}
