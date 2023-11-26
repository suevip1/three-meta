package com.coatardbul.stock.service.msg;

import com.coatardbul.baseCommon.exception.BusinessException;
import com.coatardbul.baseCommon.model.bo.DingDingMsgBo;
import com.coatardbul.baseCommon.model.entity.MsgAckConfig;
import com.coatardbul.baseCommon.util.DateTimeUtil;
import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseService.utils.RedisKeyUtils;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Note: 文档地址：https://open.dingtalk.com/document/orgapp/send-normal-messages
 * https://open.dingtalk.com/document/robots/custom-robot-access
 * <p>
 * Date: 2023/11/25
 *
 * @author Su Xiaolei
 */
@Service
@Slf4j
public class DingDingService {

    @Autowired
    MsgAckConfigService msgAckConfigService;

    @Autowired
    RedisTemplate redisTemplate;
    /**
     * 其他类型，频率控制，
     * 每个机器人每分钟最多发送20条消息到群里，如果超过20条，会限流10分钟。
     *
     * @param dto
     * @return
     * @throws ApiException
     */
    public String sendMsg(DingDingMsgBo dto) throws ApiException {
        Set keys = redisTemplate.keys(RedisKeyUtils.getMsgAckSendTimePattern(dto.getMsgAckConfigId()));
        if(keys.size()>=20){
            try {
                log.info("发送消息次数过多，小憩一下");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error(e.getMessage(),e);
            }
           return sendMsg(dto);
        }
        //此处投机取巧直接写死了，后续有其他眼球再改
        Date beforeAfterDate = DateTimeUtil.getBeforeAfterDate(-1, Calendar.SECOND);
        String dateFormat = DateTimeUtil.getDateFormat(beforeAfterDate, DateTimeUtil.YYYY_MM_DD_HH_MM_SS);
        String msgAckSendTime = RedisKeyUtils.getMsgAckSendTime(dto.getMsgAckConfigId(), dateFormat);
        String currDateFormat = DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS);
        String currMsgAckSendTimeKey = RedisKeyUtils.getMsgAckSendTime(dto.getMsgAckConfigId(), currDateFormat);

        if(redisTemplate.hasKey(msgAckSendTime) || redisTemplate.hasKey(currMsgAckSendTimeKey)){
            try {
                log.info("发送消息频率过快，小憩一下");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error(e.getMessage(),e);
            }
          return   sendMsg(dto);
        }
        return sendDingDingMsg(dto);
    }

    private  String sendDingDingMsg(DingDingMsgBo dto) throws ApiException {
        //设置超时时间为一分钟
        String msgAckSendTimeKey = RedisKeyUtils.getMsgAckSendTime(dto.getMsgAckConfigId(), DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
        log.info("redisKey"+msgAckSendTimeKey);
        redisTemplate.opsForValue().set(msgAckSendTimeKey, JsonUtil.toJson(dto),1, TimeUnit.MINUTES);
        //发送消息
        MsgAckConfig msgAckConfig = getMsgAckConfig(dto.getMsgAckConfigId());
        DingTalkClient client = new DefaultDingTalkClient(msgAckConfig.getParam1());
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype(dto.getMsgType());
        if("text".equals(dto.getMsgType())){
            OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
            if(!StringUtils.isNotBlank(dto.getContent())){
                throw new BusinessException("发送的消息不能为空");
            }
            text.setContent(msgAckConfig.getParam2()+" "+ dto.getContent());
            request.setText(text);
        }
        if("link".equals(dto.getMsgType())){
            OapiRobotSendRequest.Link link = new OapiRobotSendRequest.Link();
            if(!StringUtils.isNotBlank(dto.getTitle())
                    ||!StringUtils.isNotBlank(dto.getText())
                    ||!StringUtils.isNotBlank(dto.getMessageUrl())

            ){
                throw new BusinessException("发送的消息不能为空");
            }
            link.setMessageUrl(dto.getMessageUrl());
            link.setPicUrl("");
            link.setTitle(dto.getTitle());
            link.setText(dto.getText());
            request.setLink(link);
        }

        if("markdown".equals(dto.getMsgType())){
            OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
            if(!StringUtils.isNotBlank(dto.getTitle())
                    ||!StringUtils.isNotBlank(dto.getText())
            ){
                throw new BusinessException("发送的消息不能为空");
            }

            markdown.setTitle(dto.getTitle());
            markdown.setText(dto.getText());
            request.setMarkdown(markdown);
        }

        OapiRobotSendResponse response = client.execute(request);
        log.info("钉钉消息结果"+response.getMsg());
        return response.getMsg();
    }


    public MsgAckConfig getMsgAckConfig(String msgAckConfigId){
        MsgAckConfig msgAckConfig=new MsgAckConfig();
        msgAckConfig.setId(msgAckConfigId);
        MsgAckConfig query = msgAckConfigService.getQuery(msgAckConfig);
      return query;
    }
}
