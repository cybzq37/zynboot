package com.zynboot.map.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zynboot.map.infrastructure.entity.MapFeature;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MapFeatureMapper extends BaseMapper<MapFeature> {

    @Select("SELECT count(*) FROM map_feature WHERE layer_id = #{layerId}")
    long countByLayerId(@Param("layerId") String layerId);

    @Select("SELECT count(*) FROM map_feature WHERE source_id = #{sourceId}")
    long countBySourceId(@Param("sourceId") String sourceId);
}
