package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.locationtech.jts.geom.Point;
import org.openstreetmap.josm.tools.Logging;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
public class SamImageGrid extends JPanel {
    private ArrayList<SamImage> samImageList;
    private int maxWidth;
    ObjectMapper objectMapper = new ObjectMapper();

    public SamImageGrid(int maxWidth) {
        this.maxWidth = maxWidth;
        samImageList = new ArrayList<>();
        setLayout(new GridLayout(0, 2, 1, 1));

        //
        File cacheDir = new File(CommonUtils.magicWandCacheDirPath());
        File[] cacheFiles = cacheDir.listFiles((dir, fileName) -> fileName.endsWith(".json"));

        for (File jsonFile : cacheFiles) {
            try {
                SamImage samImage = objectMapper.readValue(jsonFile, SamImage.class);
                addSamImage(samImage);
            } catch (Exception e) {
                Logging.error(e);
            }
        }
    }

    public void addSamImage(SamImage samImage) {
        samImageList.add(samImage);
        updateJpanel();
    }

    public void removeAllSamImage() {
        samImageList.clear();
        updateJpanel();
    }

    public ArrayList<SamImage> getSamImageList() {
        return samImageList;
    }

    public SamImage getSamImageIncludepoint(Point p) {
        for (SamImage s : samImageList) {
            if (s.containsPoint(p)) {
                return s;
            }
        }
        return null;
    }

    public void removeSamImage(int index) {
        if (index >= 0 && index < samImageList.size()) {
            samImageList.remove(index);
            updateJpanel();
        }
    }

    private void updateJpanel() {
        removeAll();
        for (SamImage samImage : samImageList) {
            add(new ImagePanel(samImage, this.maxWidth));
        }
        revalidate();
        repaint();
    }


}