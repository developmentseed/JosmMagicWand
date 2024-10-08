package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.locationtech.jts.geom.Point;
import org.openstreetmap.josm.tools.Logging;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;

public class SamImageGrid extends JPanel implements ImagePanelListener {
    ObjectMapper objectMapper = new ObjectMapper();
    private final ArrayList<SamImage> samImageList;
    private final int maxWidth;

    public SamImageGrid(int maxWidth) {
        this.maxWidth = maxWidth;
        samImageList = new ArrayList<>();
        setLayout(new GridLayout(0, 2, 1, 1));

        //
        File cacheDir = new File(CommonUtils.magicWandCacheDirPath());
        File[] cacheFiles = cacheDir.listFiles((dir, fileName) -> fileName.endsWith(".json"));
        if (cacheFiles != null) {
            Arrays.sort(cacheFiles, new Comparator<File>() {
                @Override
                public int compare(File file1, File file2) {
                    LocalDateTime dateTime1 = extractDateTimeFromFileName(file1.getName());
                    LocalDateTime dateTime2 = extractDateTimeFromFileName(file2.getName());
                    return dateTime1.compareTo(dateTime2);
                }
            });
        }


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
        if (samImage.isEncode() && !samImage.getImageUrl().isEmpty()) {
            samImageList.add(0, samImage);
        }
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

    private void updateJpanel() {
        removeAll();
        for (SamImage samImage : samImageList) {
            add(new ImagePanel(samImage, this.maxWidth, this));
        }
        revalidate();
        repaint();
    }

    private LocalDateTime extractDateTimeFromFileName(String fileName) {
        try {
            // '2024_10_07__12_17_22__farm.json'
            String[] parts = fileName.split("__");
            String datePart = parts[0]; // '2024_10_07'
            String timePart = parts[1]; // '12_17_22'

            String dateTimeString = datePart + " " + timePart;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd HH_mm_ss");

            return LocalDateTime.parse(dateTimeString, formatter);
        } catch (Exception e) {
            Logging.error("Error parsing date from file name: " + fileName);
            return LocalDateTime.MIN;
        }
    }

    @Override
    public void removeSamImage(SamImage samImage) {
        try {
            samImageList.remove(samImage);
            updateJpanel();
        } catch (Exception e) {
            Logging.error(e);

        }
    }
}