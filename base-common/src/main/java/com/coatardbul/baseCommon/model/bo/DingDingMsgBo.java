package com.coatardbul.baseCommon.model.bo;

import lombok.Data;

/**
 * <p>
 *
 *     https://open.dingtalk.com/document/orgapp/send-normal-messages
 * Note:
 * <p>
 * Date: 2023/11/25
 *
 * @author Su Xiaolei
 */
@Data
public class DingDingMsgBo {


    private String msgAckConfigId;

    /**
     * 消息类型
     *text，
     * image，
     * file，
     * markdown，
     * action_card，
     * link，
     */
    private String msgType;



    private String content;


    private String title;

    /**
     * text类型
     */
    private String text;


    private String messageUrl;

    /**
     * 此消息类型为固定actionCard
     */
    private String actionCard;

    /**
     * 被@人的手机号
     */
    private String at;

    /**
     * 此消息类型为固定feedCard
     */
    private String feedCard;

    /**
     * 消息类型，此时固定为:link
     */
    private String link;

    /**
     * 此消息类型为固定markdown
     */
    private String markdown;



}

