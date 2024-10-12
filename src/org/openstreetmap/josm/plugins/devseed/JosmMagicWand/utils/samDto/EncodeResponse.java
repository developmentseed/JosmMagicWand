package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils.samDto;

public class EncodeResponse {
    private String project;
    private String id;
    private double[] bbox;
    private int zoom;
    private String imageUrl;
    private String tifUrl;
    private String return_format;
    public EncodeResponse() {
    }

    public EncodeResponse(String project, String id, double[] bbox, int zoom, String imageUrl, String tifUrl, String return_format) {
        this.project = project;
        this.id = id;
        this.bbox = bbox;
        this.zoom = zoom;
        this.imageUrl = imageUrl;
        this.tifUrl = tifUrl;
        this.return_format = return_format ;
    }

    // Getters y Setters
    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double[] getBbox() {
        return bbox;
    }

    public void setBbox(double[] bbox) {
        this.bbox = bbox;
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTifUrl() {
        return tifUrl;
    }

    public void setTifUrl(String tifUrl) {
        this.tifUrl = tifUrl;
    }

    public String getReturn_format() {
        return return_format;
    }

    public void setReturn_format(String return_format) {
        this.return_format = return_format;
    }
}

