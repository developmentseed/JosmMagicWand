package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils;


import org.openstreetmap.josm.data.osm.Way;

import java.util.ArrayList;

public interface ImageSamPanelListener {
    void onAddSamImage(SamImage samImage);

    void onRemoveAll();

    ArrayList<SamImage> getSamImageList();

    SamImage getSamImageIncludepoint(double x, double y);

    void addLayer();

    void addBboxLayer(Way way);

}