package com.coatardbul.stock.mapper;

import com.coatardbul.baseCommon.model.entity.MsgAckConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MsgAckConfigMapper {
    int deleteByPrimaryKey(String id);

    int insert(MsgAckConfig record);

    int insertSelective(MsgAckConfig record);

    MsgAckConfig selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(MsgAckConfig record);

    int updateByPrimaryKey(MsgAckConfig record);

    List<MsgAckConfig> selectAllByNameLikeAndMsgTypeAndUserId(@Param("likeName") String likeName, @Param("msgType") String msgType, @Param("userId") String userId);

    List<MsgAckConfig> selectAllByNameLikeAndMsgType(@Param("likeName") String likeName, @Param("msgType") String msgType);
}