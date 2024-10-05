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

    public DecodeRequestBody(String action_type, List<Double> bbox, String id, List<List<Double>> point_coords, List<Integer> point_labels, String project, Integer zoom) {
        this.action_type = action_type;
        this.bbox = bbox;
        this.crs = "EPSG:4326";
        this.id = id;
        this.point_coords = point_coords;
        this.point_labels = point_labels;
        this.project = project;
        this.zoom = zoom;
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
}
