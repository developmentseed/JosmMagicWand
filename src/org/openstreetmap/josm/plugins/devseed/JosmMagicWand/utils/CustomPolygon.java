package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.tools.Logging;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomPolygon {
    private Polygon pol;
    private String id;
    private boolean isUse;
    private Way way;

    @Override
    public String toString() {
        return "CustomPolygon [id=" + id + ", isUse=" + isUse +"]";
    }
    public CustomPolygon() {
        this.id = UUID.randomUUID().toString();
        this.isUse = false;
    }

    public void fromWay(Way w) {
        this.way = w;
        try {
            List<Coordinate> tmpCords = new ArrayList<>();
            for (Node n : w.getNodes()) {
                tmpCords.add(new Coordinate(n.lat(), n.lon()));
            }
            this.pol = (Polygon) CommonUtils.Coordinates2Geometry(tmpCords, true);
        } catch (Exception ex) {
            Logging.error(ex);
        }
    }

    public String id() {
        return id;
    }

    public Polygon polygon() {
        return pol;
    }

    public boolean isEmpty() {
        return pol.isEmpty();
    }

    public Way way() {
        return way;
    }

    public boolean isUse() {
        return isUse;
    }

    public void usePolygon() {
        this.isUse = true;
    }

    public void setPol(Polygon pol) {
        this.pol = pol;
    }
}
