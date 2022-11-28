package com.coatardbul.river.mapper;

import com.coatardbul.river.model.entity.StockQueryTemplate;
import org.apache.ibatis.annotations.Mapper;import org.apache.ibatis.annotations.Param;import java.util.List;

@Mapper
public interface StockQueryTemplateMapper {
    int deleteByPrimaryKey(String id);

    int insert(StockQueryTemplate record);

    int insertSelective(StockQueryTemplate record);

    StockQueryTemplate selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(StockQueryTemplate record);

    int updateByPrimaryKey(StockQueryTemplate record);

    int updateNameById(@Param("updatedName") String updatedName, @Param("id") String id);

    List<StockQueryTemplate> selectAllByNameAndScriptStr(@Param("name") String name, @Param("scriptStr") String scriptStr);

    List<StockQueryTemplate> selectByAll();

    StockQueryTemplate selectByTemplateSign(@Param("templateSign") String templateSign);

    List<StockQueryTemplate> selectByIdAndNameLikeAndExampleStrLike(@Param("id") String id, @Param("likeName") String likeName, @Param("likeExampleStr") String likeExampleStr);
}