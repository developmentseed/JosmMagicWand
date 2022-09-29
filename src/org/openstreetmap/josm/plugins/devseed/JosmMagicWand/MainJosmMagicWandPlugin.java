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
import org.opencv.core.Core;

import javax.swing.*;

public class MainJosmMagicWandPlugin extends Plugin {
    public static final String NAME = "MagicWand";
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
        nu.pattern.OpenCV.loadShared();
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        JMenu jToolmenu = MainApplication.getMenu().toolsMenu;
        jToolmenu.addSeparator();
        MainMenu.add(jToolmenu, new MergeSelectAction());
//        MainMenu.add(jToolmenu, new SelectActionHull());
//        MainMenu.add(jToolmenu, new SelectActionPerMor());
//        MainMenu.add(jToolmenu, new SelectActionDP());

    }

    @Override
    public void mapFrameInitialized(MapFrame oldFrame, MapFrame newFrame) {
        if (oldFrame == null && newFrame != null) {
            newFrame.addToggleDialog(magicWandDialog);
            MainApplication.getMap().addMapMode(new IconToggleButton(new MagicWandAction()));

        }
    }
}
