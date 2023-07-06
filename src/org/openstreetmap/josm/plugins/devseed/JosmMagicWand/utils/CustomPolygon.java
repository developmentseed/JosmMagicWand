package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils;

import org.locationtech.jts.geom.Polygon;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.tools.Logging;

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
            this.pol = (Polygon) CommonUtils.coordinates2Geometry(CommonUtils.nodes2Coordinates(w.getNodes()), true);
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
