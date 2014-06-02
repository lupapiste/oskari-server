package fi.nls.oskari.work.fe;

import java.util.List;

import org.apache.http.client.utils.URIBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

import fi.nls.oskari.pojo.SessionStore;
import fi.nls.oskari.pojo.WFSLayerStore;
import fi.nls.oskari.work.WFSMapLayerJob;
import fi.nls.oskari.work.OWSMapLayerJob.Type;

/* A PoC WFS GET Builder */
public class FEWFSGetQueryArgsBuilder implements FEQueryArgsBuilder {

    @Override
    public void buildParams(URIBuilder builder, Type type, WFSLayerStore layer,
            SessionStore session, List<Double> bounds, MathTransform transform,
            CoordinateReferenceSystem crs) {

        builder.setParameter("SERVICE", "WFS");
        builder.setParameter("REQUEST", "GetFeature");
        builder.setParameter("VERSION", "2.0.0");
        builder.setParameter("TYPENAMES", layer.getFeatureElement());

        builder.setParameter("srsName", layer.getSRSName());

        /*
         * http://services.cuzk.cz/wfs/inspire-au-wfs.asp?service=WFS&request=
         * GetFeature
         * &version=2.0.0&TYPENAMES=AdministrativeUnit&srsName=urn:ogc:
         * def:crs:EPSG
         * ::3035&BBOX=4632011.71875,2991679.6875,4660292.96875,3021406.25
         */

        StringBuffer coordsAsSpaceSeparatedString = new StringBuffer();
        if (type == WFSMapLayerJob.Type.MAP_CLICK) {

            Coordinate c = session.getMapClick();

            ReferencedEnvelope env = new ReferencedEnvelope(new Envelope(c),
                    crs);
            /* env.expandBy(10); */

            coordsAsSpaceSeparatedString.append(Double.toString(c
                    .getOrdinate(0)));
            coordsAsSpaceSeparatedString.append(' ');
            coordsAsSpaceSeparatedString.append(Double.toString(c
                    .getOrdinate(1)));

            builder.setParameter("buffer", Integer.toString(100, 10));

        } else if (bounds != null) {

            ReferencedEnvelope env = new ReferencedEnvelope(
                    new Envelope(bounds.get(0), bounds.get(2), bounds.get(1),
                            bounds.get(3)), crs);
            // env.expandBy(300);

            DirectPosition upperCorner = env.getUpperCorner();
            DirectPosition lowerCorner = env.getLowerCorner();

            coordsAsSpaceSeparatedString.append(Double.toString(lowerCorner
                    .getOrdinate(0)));
            coordsAsSpaceSeparatedString.append(',');
            coordsAsSpaceSeparatedString.append(Double.toString(lowerCorner
                    .getOrdinate(1)));
            coordsAsSpaceSeparatedString.append(',');
            coordsAsSpaceSeparatedString.append(Double.toString(upperCorner
                    .getOrdinate(0)));
            coordsAsSpaceSeparatedString.append(',');
            coordsAsSpaceSeparatedString.append(Double.toString(upperCorner
                    .getOrdinate(1)));

        } else {
            ReferencedEnvelope env = session.getLocation().getEnvelope();
            // env.expandBy(300);

            DirectPosition upperCorner = env.getUpperCorner();
            DirectPosition lowerCorner = env.getLowerCorner();

            coordsAsSpaceSeparatedString.append(Double.toString(lowerCorner
                    .getOrdinate(0)));
            coordsAsSpaceSeparatedString.append(',');
            coordsAsSpaceSeparatedString.append(Double.toString(lowerCorner
                    .getOrdinate(1)));
            coordsAsSpaceSeparatedString.append(',');
            coordsAsSpaceSeparatedString.append(Double.toString(upperCorner
                    .getOrdinate(0)));
            coordsAsSpaceSeparatedString.append(',');
            coordsAsSpaceSeparatedString.append(Double.toString(upperCorner
                    .getOrdinate(1)));
        }

        builder.setParameter("BBOX", coordsAsSpaceSeparatedString.toString());

        //System.err.println("BBOX " + coordsAsSpaceSeparatedString.toString());

    }

}