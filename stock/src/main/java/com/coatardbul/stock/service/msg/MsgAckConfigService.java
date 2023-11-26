package com.coatardbul.stock.service.msg;

import com.coatardbul.baseCommon.model.entity.MsgAckConfig;
import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseService.service.SnowFlakeService;
import com.coatardbul.baseService.utils.RedisKeyUtils;
import com.coatardbul.stock.mapper.MsgAckConfigMapper;
import com.coatardbul.stock.service.StockUserBaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2023/11/25
 *
 * @author Su Xiaolei
 */
@Service
@Slf4j
public class MsgAckConfigService {


    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    SnowFlakeService snowFlakeService;

    @Autowired
    MsgAckConfigMapper msgAckConfigMapper;
    @Autowired
    StockUserBaseService stockUserBaseService;

    public void add(MsgAckConfig dto) {
        dto.setId(snowFlakeService.getSnowId());
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        String userName = stockUserBaseService.getCurrUserName(request);
        dto.setUserId(userName);
        msgAckConfigMapper.insert(dto);
        redisTemplate.opsForValue().set(RedisKeyUtils.getMsgAckConfig(dto.getId()), JsonUtil.toJson(dto));
    }

    public void modify(MsgAckConfig dto) {
        msgAckConfigMapper.updateByPrimaryKey(dto);
        redisTemplate.opsForValue().set(RedisKeyUtils.getMsgAckConfig(dto.getId()), JsonUtil.toJson(dto));
    }

    public void delete(MsgAckConfig dto) {
        msgAckConfigMapper.deleteByPrimaryKey(dto.getId());
        redisTemplate.delete(RedisKeyUtils.getMsgAckConfig(dto.getId()));
    }

    public List<MsgAckConfig> getList(MsgAckConfig dto) {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        String userName = stockUserBaseService.getCurrUserName(request);
        return msgAckConfigMapper.selectAllByNameLikeAndMsgTypeAndUserId(dto.getName(), dto.getMsgType(),userName);
    }

    public MsgAckConfig getQuery(MsgAckConfig dto) {
        String msgAckConfig = RedisKeyUtils.getMsgAckConfig(dto.getId());
        if(redisTemplate.hasKey(msgAckConfig)){
            String jsonStr = (String) redisTemplate.opsForValue().get(msgAckConfig);
            return JsonUtil.readToValue(jsonStr, MsgAckConfig.class);
        }
        MsgAckConfig esTemplateConfig = msgAckConfigMapper.selectByPrimaryKey(dto.getId());
        return esTemplateConfig;
    }
}
