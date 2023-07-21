package org.openstreetmap.josm.plugins.devseed.JosmMagicWand;


import org.openstreetmap.josm.gui.IconToggleButton;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MainMenu;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.Actions.MagicWandAction;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.Actions.MergeSelectAction;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.Actions.SimplifySelectAction;
import org.openstreetmap.josm.plugins.devseed.JosmMagicWand.Dialog.MagicWandDialog;
import org.openstreetmap.josm.tools.Logging;

import javax.swing.*;

public class MainJosmMagicWandPlugin extends Plugin {

    public MainJosmMagicWandPlugin(PluginInformation info) {
        super(info);
        try {
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
        MainMenu.add(jToolmenu, new SimplifySelectAction());

    }

    @Override
    public void mapFrameInitialized(MapFrame oldFrame, MapFrame newFrame) {
        if (oldFrame == null && newFrame != null) {
            newFrame.addToggleDialog( new MagicWandDialog());
            MainApplication.getMap().addMapMode(new IconToggleButton(new MagicWandAction()));

        }
    }
}
