package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils;


import java.util.ArrayList;

public interface ImageSamPanelListener {
    void onAddSamImage(SamImage samImage);

    void onRemoveAll();

    ArrayList<SamImage> getSamImageList();

    SamImage getSamImageIncludepoint(double x, double y);

}