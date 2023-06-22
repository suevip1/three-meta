package com.coatardbul.baseService.service;

import com.coatardbul.baseCommon.constants.DictKeyEnum;
import com.coatardbul.baseCommon.model.entity.AuthUser;
import com.coatardbul.baseCommon.util.AESUtil;
import com.coatardbul.baseCommon.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2023/6/22
 *
 * @author Su Xiaolei
 */

@Slf4j
@Service
public class UserBaseService {
    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 获取用户token目前的用户数据
     */
    public AuthUser getUserByToken(String token) {

        String key = (String) redisTemplate.opsForValue().get(DictKeyEnum.AES_KEY.getSign());
        if (StringUtils.isBlank(key)) {
            return null;
        } else {
            try {
                String decrypt = AESUtil.decrypt(token, key);
                AuthUser userDto = JsonUtil.readToValue(decrypt, AuthUser.class);
              return userDto;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return null;
            }
        }
    }

    /**
     * 获取
     * @param token
     * @return
     */
    public String getCurrAccountByToken(String token) {
        AuthUser userByToken = getUserByToken(token);
        if (userByToken == null) {
            return null;
        } else {
            return userByToken.getUsername();
        }

    }
}
