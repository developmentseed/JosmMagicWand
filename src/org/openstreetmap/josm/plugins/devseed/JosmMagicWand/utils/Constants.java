package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils;

public class Constants {
    public static final String SAM_API = "https://samgeo-api.geocompas.ai";
    public static final String ENCODE = buildEndpoint("aoi");
    public static final String DECODE = buildEndpoint("segment_predictor");
    public static final String AUTOMATIC = buildEndpoint("segment_automatic");

    private static String buildEndpoint(String path) {
        return SAM_API +"/"+ path;
    }
}
