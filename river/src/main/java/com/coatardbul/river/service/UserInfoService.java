package com.coatardbul.river.service;

import com.coatardbul.baseCommon.constants.DictKeyEnum;
import com.coatardbul.baseCommon.constants.DictTypeEnum;
import com.coatardbul.baseCommon.util.AESUtil;
import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.river.mapper.AuthUserMapper;
import com.coatardbul.river.mapper.DictInfoMapper;
import com.coatardbul.river.model.dto.UserDto;
import com.coatardbul.baseCommon.model.entity.AuthUser;
import com.coatardbul.river.model.entity.DictInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class UserInfoService {

    @Resource
    private AuthUserMapper authUserMapper;

    @Resource
    private DictInfoMapper dictInfoMapper;


    public AuthUser selectByPrimaryKey(String userId) {
        return authUserMapper.selectByPrimaryKey(userId);
    }

    public String login(UserDto userDto) {
        List<AuthUser> authUsers = authUserMapper.selectAllByUsernameAndPassword(userDto.getAccount(), userDto.getPassword());
        List<DictInfo> dictInfos = dictInfoMapper.selectAllByBusiTypeAndSignKey(DictTypeEnum.ENCRYPT.getSign(), DictKeyEnum.AES_KEY.getSign());
        if (authUsers.size() > 0 && dictInfos.size() > 0) {
            AuthUser authUser = authUsers.get(0);
            if (authUser.getExpireTime() == null) {
                return "";
            } else if (authUser.getExpireTime().before(new Date())) {
                return "";
            }
            authUser.setGmtCreate(new Date());
            String encrypt = AESUtil.encrypt(JsonUtil.toJson(authUser), dictInfos.get(0).getSignValue());
            return encrypt;
        } else {
            return "";
        }
    }

    /**
     * 验证token有效性,符合返回true,
     *
     * @param token
     * @return
     */
    public Boolean verifyUserValid(String token) {
        if (StringUtils.isNotBlank(token)) {
            List<DictInfo> dictInfos = dictInfoMapper.selectAllByBusiTypeAndSignKey(DictTypeEnum.ENCRYPT.getSign(), DictKeyEnum.AES_KEY.getSign());
            if (dictInfos.size() > 0) {
                try {
                    String decrypt = AESUtil.decrypt(token, dictInfos.get(0).getSignValue());
                    AuthUser userDto = JsonUtil.readToValue(decrypt, AuthUser.class);
                    List<AuthUser> authUsers = authUserMapper.selectAllByUsernameAndPassword(userDto.getUsername(), userDto.getPassword());
                    if (authUsers.size() > 0) {
                        return true;
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    return false;
                }
            }
        }

        return false;

    }


    public String getCurrUserName(HttpServletRequest request) {

        String token = request.getHeader("token");
        if (StringUtils.isNotBlank(token)) {
            List<DictInfo> dictInfos = dictInfoMapper.selectAllByBusiTypeAndSignKey(DictTypeEnum.ENCRYPT.getSign(), DictKeyEnum.AES_KEY.getSign());
            if (dictInfos.size() > 0) {
                try {
                    String decrypt = AESUtil.decrypt(token, dictInfos.get(0).getSignValue());
                    AuthUser userDto = JsonUtil.readToValue(decrypt, AuthUser.class);
                    return userDto.getUsername();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return null;
    }


}
