package org.openstreetmap.josm.plugins.devseed.JosmMagicWand;

public final class ToolSettings {
    private ToolSettings() {
    }

    private static int tolerance;
    private static int maskClose;
    private static int maskMedian;
    private static double simplHull;
    private static double simplPerMor;
    private static double simplDP;

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

    public static double getSimplHull() {
        return simplHull;
    }

    public static void setSimplHull(double simplHull) {
        ToolSettings.simplHull = simplHull;
    }

    public static double getSimplPerMor() {
        return simplPerMor;
    }

    public static void setSimplPerMor(double simplPerMor) {
        ToolSettings.simplPerMor = simplPerMor;
    }
    public static double getSimplDP() {
        return simplDP;
    }

    public static void setSimplDP(double simplDP) {
        ToolSettings.simplDP = simplDP;
    }
}
