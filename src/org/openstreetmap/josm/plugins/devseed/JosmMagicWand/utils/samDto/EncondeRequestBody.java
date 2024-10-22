package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils.samDto;

import java.util.List;

public class EncondeRequestBody {
    private String canvas_image;
    private String crs;
    private String id;
    private String project;
    private Integer zoom;
    private List<Double> bbox;
    private String return_format;
    private double simplify_tolerance;
    private int area_val;

    public EncondeRequestBody(String canvas_image, String project, Integer zoom, List<Double> bbox, String id, int area_val, double simplify_tolerance) {
        this.canvas_image = canvas_image;
        this.crs = "EPSG:4326";
        this.id = id;
        this.project = project;
        this.zoom = zoom;
        this.bbox = bbox;
        this.return_format = "geojson";
        this.area_val = area_val;
        this.simplify_tolerance = simplify_tolerance;

    }

    @Override
    public String toString() {
        return "EncondeRequestBody{" +
                "bbox=" + bbox +
                ", canvas_image='" + canvas_image + '\'' +
                ", crs='" + crs + '\'' +
                ", id='" + id + '\'' +
                ", project='" + project + '\'' +
                ", zoom=" + zoom +
                '}';
    }

    public Integer getZoom() {
        return zoom;
    }

    public void setZoom(Integer zoom) {
        this.zoom = zoom;
    }

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

    public String getCrs() {
        return crs;
    }

    public void setCrs(String crs) {
        this.crs = crs;
    }

    public String getCanvas_image() {
        return canvas_image;
    }

    public void setCanvas_image(String canvas_image) {
        this.canvas_image = canvas_image;
    }

    public List<Double> getBbox() {
        return bbox;
    }

    public void setBbox(List<Double> bbox) {
        this.bbox = bbox;
    }

    public String getReturn_format() {
        return return_format;
    }

    public void setReturn_format(String return_format) {
        this.return_format = return_format;
    }

    public int getArea_val() {
        return area_val;
    }

    public void setArea_val(int area_val) {
        this.area_val = area_val;
    }

    public double getSimplify_tolerance() {
        return simplify_tolerance;
    }

    public void setSimplify_tolerance(double simplify_tolerance) {
        this.simplify_tolerance = simplify_tolerance;
    }
}
