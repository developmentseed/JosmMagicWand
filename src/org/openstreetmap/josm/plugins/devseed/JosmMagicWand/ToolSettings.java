package org.openstreetmap.josm.plugins.devseed.JosmMagicWand;

public final class ToolSettings {
    private ToolSettings(){}
    private static int tolerance;

    public static int getTolerance() {
        return tolerance;
    }

    public static void setTolerance(int tolerance) {
        ToolSettings.tolerance = tolerance;
    }
}
