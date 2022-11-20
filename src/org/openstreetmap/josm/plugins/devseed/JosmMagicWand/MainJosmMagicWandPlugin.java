package org.openstreetmap.josm.plugins.devseed.JosmMagicWand;


import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.coor.ILatLon;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.projection.Projection;
import org.openstreetmap.josm.data.projection.Projections;
import org.openstreetmap.josm.gui.IconToggleButton;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MainMenu;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.tools.Logging;

import javax.swing.*;

public class MainJosmMagicWandPlugin extends Plugin {
    MagicWandDialog magicWandDialog = new MagicWandDialog();

    public static final Projection MERCATOR = Projections.getProjectionByCode("EPSG:3857"); // Mercator

    public static EastNorth latlon2eastNorth(ILatLon p) {
        return MERCATOR.latlon2eastNorth(p);
    }

    public static LatLon eastNorth2latlon(EastNorth p) {
        return MERCATOR.eastNorth2latlon(p);
    }

    public MainJosmMagicWandPlugin(PluginInformation info) {
        super(info);
        try {
            System.out.println(Double.parseDouble(System.getProperty("java.specification.version")));
            if (Double.parseDouble(System.getProperty("java.specification.version")) >= 12) {
                nu.pattern.OpenCV.loadLocally();
            } else {
                nu.pattern.OpenCV.loadShared();
            }
        } catch (Exception e) {
            Logging.error(e.getMessage());
            throw new RuntimeException(e);
        }

        JMenu jToolmenu = MainApplication.getMenu().toolsMenu;
        jToolmenu.addSeparator();
        MainMenu.add(jToolmenu, new MergeSelectAction());

    }

    @Override
    public void mapFrameInitialized(MapFrame oldFrame, MapFrame newFrame) {
        if (oldFrame == null && newFrame != null) {
            newFrame.addToggleDialog(magicWandDialog);
            MainApplication.getMap().addMapMode(new IconToggleButton(new MagicWandAction()));

        }
    }
}
