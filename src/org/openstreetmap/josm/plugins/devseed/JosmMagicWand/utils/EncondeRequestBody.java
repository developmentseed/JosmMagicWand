package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils;

import java.util.List;

public class EncondeRequestBody {
    private List<Double> bbox;
    private String crs;
    private String image_embeddings;
    private List<Integer> image_shape;
    private int input_label;
    private List<Integer> input_point;
    private double zoom;

    public EncondeRequestBody(List<Double> bbox,  String image_embeddings, List<Integer> image_shape, int input_label, List<Integer> input_point, double zoom) {
        this.bbox = bbox;
        this.crs="EPSG:3857";
        this.image_embeddings = image_embeddings;
        this.image_shape = image_shape;
        this.input_label = input_label;
        this.input_point = input_point;
        this.zoom = zoom;
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

    public String getImage_embeddings() {
        return image_embeddings;
    }

    public void setImage_embeddings(String image_embeddings) {
        this.image_embeddings = image_embeddings;
    }

    public List<Integer> getImage_shape() {
        return image_shape;
    }

    public void setImage_shape(List<Integer> image_shape) {
        this.image_shape = image_shape;
    }

    public int getInput_label() {
        return input_label;
    }

    public void setInput_label(int input_label) {
        this.input_label = input_label;
    }

    public List<Integer> getInput_point() {
        return input_point;
    }

    public void setInput_point(List<Integer> input_point) {
        this.input_point = input_point;
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }
}