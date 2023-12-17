package com.coatardbul.stock.service.reqFlow;

import com.coatardbul.baseCommon.model.bo.DingDingMsgBo;
import com.coatardbul.stock.service.msg.MsgAckConfigService;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Note: 流程控制逻辑
 * <p>
 * Date: 2023/11/25
 *
 * @author Su Xiaolei
 */
@Service
@Slf4j
public class FlowService {

    @Autowired
    MsgAckConfigService msgAckConfigService;

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 流程编排
     *
     * @param dto
     * @return
     * @throws ApiException
     */

    public void flowProcess(DingDingMsgBo dto) throws ApiException {
        //目前的流程顺序
        int currFlowSequent = 1;
        //获取所有的顺序流程
        List<Object> sequentFlowList = new ArrayList<>();
        //获取所有的条件流程
        List<Object> conditionFlowList = new ArrayList<>();


    }


    public void singleFlowProcessRec(List<Object> sequentFlowList, List<Object> conditionFlowList, int currFlowSequent, int retryNum, Map<String,String> envMap) throws ApiException {
        //todo 获取当前流程
        if (sequentFlowList.size() > currFlowSequent) {
            return;
        }
        Object o = sequentFlowList.get(currFlowSequent - 1);
        //todo 请求一次，根据结果判断

        //if 结果为什么走条件流程，else
        //else if 结果 满足走下一个流程
        //else 自我循环N次，满足条件走下一个流程
        if (true) {
            //todo 执行条件流程
            if(retryNum>1232){
                return;
            }
            //前置条件
            singleFlowProcessRec(sequentFlowList,conditionFlowList,currFlowSequent,++retryNum,envMap);
        } else if (false) {
            //后置条件
            singleFlowProcessRec(sequentFlowList,conditionFlowList,++currFlowSequent,0,envMap);
        } else {
            //自我循环条件
            //先请求一次
            int queryResult=12321;
            int forNum=10000;
            while (forNum>1&&queryResult>1){
                queryResult=123;
                forNum--;
            }
            if(queryResult>1){
                return;
            }else {
                singleFlowProcessRec(sequentFlowList,conditionFlowList,currFlowSequent,++retryNum,envMap);
            }

        }

    }

}
