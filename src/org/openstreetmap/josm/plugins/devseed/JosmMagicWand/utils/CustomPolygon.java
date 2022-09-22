package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
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
    private final GeometryFactory gf = new GeometryFactory();

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
            if (!tmpCords.get(tmpCords.size() - 1).equals(tmpCords.get(0))) tmpCords.add(tmpCords.get(0));
            this.pol = gf.createPolygon(tmpCords.toArray(new Coordinate[]{}));
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
