package com.coatardbul.stock.mapper;

import com.coatardbul.baseCommon.model.entity.EsTemplateConfig;import org.apache.ibatis.annotations.Param;import java.util.List;

public interface EsTemplateConfigMapper {
    int deleteByPrimaryKey(String id);

    int insert(EsTemplateConfig record);

    int insertSelective(EsTemplateConfig record);

    EsTemplateConfig selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(EsTemplateConfig record);

    int updateByPrimaryKey(EsTemplateConfig record);

    Integer selectMaxSequent();


    List<EsTemplateConfig> selectAllByEsDataType(@Param("esDataType")String esDataType);


    List<EsTemplateConfig> selectAllByTemplateNameLikeAndEsDataType(@Param("likeTemplateName") String likeTemplateName, @Param("esDataType") String esDataType);

    int deleteByPrimaryKey(@Param("templateId") String templateId, @Param("esDataType") String esDataType);

    EsTemplateConfig selectByPrimaryKey(@Param("templateId") String templateId, @Param("esDataType") String esDataType);
}