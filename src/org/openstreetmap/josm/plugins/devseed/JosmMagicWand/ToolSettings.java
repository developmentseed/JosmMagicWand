package org.openstreetmap.josm.plugins.devseed.JosmMagicWand;

public final class ToolSettings {
    private ToolSettings(){}
    private static int tolerance;
    private static int maskClose;
    private static int maskMedian;

    public static int getMaskOpen() {
        return maskOpen;
    }

    public static void setMaskOpen(int maskOpen) {
        ToolSettings.maskOpen = maskOpen;
    }

    private static int maskOpen;

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

}
