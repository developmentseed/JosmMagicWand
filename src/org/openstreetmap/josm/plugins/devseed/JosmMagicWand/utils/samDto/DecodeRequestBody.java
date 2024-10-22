package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils.samDto;

import java.util.List;

public class DecodeRequestBody {
    private String action_type;
    private List<Double> bbox;
    private String crs;
    private String id;
    private List<List<Double>> point_coords;
    private List<Integer> point_labels;
    private String project;
    private Integer zoom;
    private String return_format;
    private double simplify_tolerance;
    private int area_val;

    public DecodeRequestBody(String action_type, List<Double> bbox, String id, List<List<Double>> point_coords, List<Integer> point_labels, String project, Integer zoom) {
        this.action_type = action_type;
        this.bbox = bbox;
        this.crs = "EPSG:4326";
        this.id = id;
        this.point_coords = point_coords;
        this.point_labels = point_labels;
        this.project = project;
        this.zoom = zoom;
        this.return_format = "geojson";
        this.simplify_tolerance = 0.000002;
        this.area_val = 15;
    }

    @Override
    public String toString() {
        return "DecodeRequestBody{" +
                "action_type='" + action_type + '\'' +
                ", bbox=" + bbox +
                ", crs='" + crs + '\'' +
                ", id='" + id + '\'' +
                ", point_coords=" + point_coords +
                ", point_labels=" + point_labels +
                ", project='" + project + '\'' +
                ", zoom=" + zoom +
                '}';
    }

    public String getAction_type() {
        return action_type;
    }

    public void setAction_type(String action_type) {
        this.action_type = action_type;
    }

    public List<Double> getBbox() {
        return bbox;
    }

    public void setBbox(List<Double> bbox) {
        this.bbox = bbox;
    }

    public String getCrs() {
        return crs;
    }

    public void setCrs(String crs) {
        this.crs = crs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<List<Double>> getPoint_coords() {
        return point_coords;
    }

    public void setPoint_coords(List<List<Double>> point_coords) {
        this.point_coords = point_coords;
    }

    public List<Integer> getPoint_labels() {
        return point_labels;
    }

    public void setPoint_labels(List<Integer> point_labels) {
        this.point_labels = point_labels;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public Integer getZoom() {
        return zoom;
    }

    public void setZoom(Integer zoom) {
        this.zoom = zoom;
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
