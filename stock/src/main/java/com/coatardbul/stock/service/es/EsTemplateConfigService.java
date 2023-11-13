package com.coatardbul.stock.service.es;

import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseService.service.SnowFlakeService;
import com.coatardbul.baseService.utils.RedisKeyUtils;
import com.coatardbul.stock.mapper.EsTemplateConfigMapper;
import com.coatardbul.stock.model.entity.EsTemplateConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2023/11/13
 *
 * @author Su Xiaolei
 */
@Component
@Slf4j
public class EsTemplateConfigService {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    SnowFlakeService snowFlakeService;
    @Autowired
    EsTemplateConfigMapper esTemplateConfigMapper;

    public void add(EsTemplateConfig dto) {
        dto.setId(snowFlakeService.getSnowId());
        Integer integer = esTemplateConfigMapper.selectMaxSequent();
        if(integer==null){
            dto.setSequent(1);
        }else {
            integer=integer+5;
            dto.setSequent(integer);
        }
        esTemplateConfigMapper.insert(dto);
        redisTemplate.opsForValue().set(RedisKeyUtils.getEsTemplateConfig(dto.getTemplateId(),dto.getEsDataType()), JsonUtil.toJson(dto));
    }

    public void modify(EsTemplateConfig dto) {
        esTemplateConfigMapper.updateByPrimaryKeySelective(dto);
        redisTemplate.opsForValue().set(RedisKeyUtils.getEsTemplateConfig(dto.getTemplateId(),dto.getEsDataType()), JsonUtil.toJson(dto));
    }

    public void delete(EsTemplateConfig dto) {
        esTemplateConfigMapper.deleteByPrimaryKey(dto.getId());
        redisTemplate.delete(RedisKeyUtils.getEsTemplateConfig(dto.getTemplateId(),dto.getEsDataType()));
    }

    public List<EsTemplateConfig> getList(EsTemplateConfig dto) {

       return esTemplateConfigMapper.selectAllByTemplateNameLikeAndEsDataType(dto.getTemplateName(), dto.getEsDataType());
    }

    public EsTemplateConfig getQuery(EsTemplateConfig dto) {
        EsTemplateConfig esTemplateConfig = esTemplateConfigMapper.selectByPrimaryKey(dto.getId());
        return esTemplateConfig;
    }
}
