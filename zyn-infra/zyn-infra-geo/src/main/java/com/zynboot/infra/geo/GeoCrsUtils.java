package com.zynboot.infra.geo;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;

import java.time.Duration;

public final class GeoCrsUtils {

    private static final int MAX_CACHE_SIZE = 256;

    private static final LoadingCache<String, CoordinateReferenceSystem> CRS_CACHE = Caffeine.newBuilder()
            .maximumSize(MAX_CACHE_SIZE)
            .expireAfterAccess(Duration.ofHours(1))
            .build(epsgCode -> CRS.decode(epsgCode, true));

    private static final LoadingCache<String, MathTransform> TRANSFORM_CACHE = Caffeine.newBuilder()
            .maximumSize(MAX_CACHE_SIZE)
            .expireAfterAccess(Duration.ofHours(1))
            .build(key -> {
                String[] parts = key.split("->", 2);
                return CRS.findMathTransform(CRS_CACHE.get(parts[0]), CRS_CACHE.get(parts[1]), true);
            });

    private GeoCrsUtils() {
    }

    public static CoordinateReferenceSystem decode(String epsgCode) {
        requireEpsgCode(epsgCode, "epsgCode");
        try {
            return CRS_CACHE.get(epsgCode);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid EPSG code: " + epsgCode, e);
        }
    }

    public static int toSrid(String epsgCode) {
        requireEpsgCode(epsgCode, "epsgCode");
        int separatorIndex = epsgCode.indexOf(':');
        String srid = separatorIndex >= 0 ? epsgCode.substring(separatorIndex + 1) : epsgCode;
        return Integer.parseInt(srid.trim());
    }

    public static String toWkt(String epsgCode) throws Exception {
        return decode(epsgCode).toWKT();
    }

    public static MathTransform createTransform(String sourceEpsg, String targetEpsg) {
        requireEpsgCode(sourceEpsg, "sourceEpsg");
        requireEpsgCode(targetEpsg, "targetEpsg");
        String cacheKey = sourceEpsg + "->" + targetEpsg;
        try {
            return TRANSFORM_CACHE.get(cacheKey);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create CRS transform from " + sourceEpsg + " to " + targetEpsg, e);
        }
    }

    public static Geometry transform(Geometry geometry, String sourceEpsg, String targetEpsg) throws Exception {
        if (geometry == null) {
            throw new IllegalArgumentException("geometry must not be null");
        }

        CoordinateReferenceSystem source = decode(sourceEpsg);
        CoordinateReferenceSystem target = decode(targetEpsg);
        Geometry result;
        if (CRS.equalsIgnoreMetadata(source, target)) {
            result = (Geometry) geometry.copy();
        } else {
            result = JTS.transform(geometry, createTransform(sourceEpsg, targetEpsg));
        }
        result.setSRID(toSrid(targetEpsg));
        return result;
    }

    public static String transformWkt(String wkt, String sourceEpsg, String targetEpsg) throws Exception {
        return GeoFormatUtils.geometryToWkt(transform(GeoFormatUtils.wktToGeometry(wkt), sourceEpsg, targetEpsg));
    }

    public static String transformGeoJson(String geoJson, String sourceEpsg, String targetEpsg) throws Exception {
        return GeoFormatUtils.geometryToGeoJson(transform(GeoFormatUtils.geoJsonToGeometry(geoJson), sourceEpsg, targetEpsg));
    }

    public static Geometry transformCsv(String csv, GeometryType geometryType, String sourceEpsg, String targetEpsg) throws Exception {
        return transform(GeoFormatUtils.csvToGeometry(csv, geometryType), sourceEpsg, targetEpsg);
    }

    private static void requireEpsgCode(String epsgCode, String paramName) {
        if (epsgCode == null || epsgCode.isBlank()) {
            throw new IllegalArgumentException(paramName + " must not be blank");
        }
    }
}
