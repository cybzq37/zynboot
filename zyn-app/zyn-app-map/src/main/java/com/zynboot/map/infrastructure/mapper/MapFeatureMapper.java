package com.zynboot.map.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zynboot.map.infrastructure.entity.MapFeature;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MapFeatureMapper extends BaseMapper<MapFeature> {

    @Select("SELECT count(*) FROM map_feature WHERE layer_id = #{layerId}")
    long countByLayerId(@Param("layerId") String layerId);

    @Select("SELECT count(*) FROM map_feature WHERE source_id = #{sourceId}")
    long countBySourceId(@Param("sourceId") String sourceId);

    /**
     * 带 PostGIS 几何写入的插入。
     * 使用 ST_GeomFromGeoJSON 解析 GeoJSON，再 ST_Transform 到目标 SRID。
     */
    @Insert("INSERT INTO map_feature (id, layer_id, source_id, properties, geometry, create_time) " +
            "VALUES (#{id}, #{layerId}, #{sourceId}, #{properties}::jsonb, " +
            "ST_Transform(ST_GeomFromGeoJSON(#{geometryGeoJson}), CAST(#{targetSrid} AS INTEGER)), " +
            "CURRENT_TIMESTAMP)")
    void insertWithGeometry(@Param("id") long id,
                            @Param("layerId") String layerId,
                            @Param("sourceId") String sourceId,
                            @Param("properties") String propertiesJson,
                            @Param("geometryGeoJson") String geometryGeoJson,
                            @Param("targetSrid") String targetSrid);

    /**
     * 获取图层特征的 ETag（基于 MAX(update_time) + feature_count）。
     */
    @Select("SELECT MD5(COALESCE(MAX(update_time)::text, '') || ':' || COUNT(*)::text) FROM map_feature WHERE layer_id = #{layerId}")
    String getLayerEtag(@Param("layerId") String layerId);
}
