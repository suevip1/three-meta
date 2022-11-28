package com.coatardbul.river.service.impl;

import com.coatardbul.river.mapper.AuthUserMapper;
import com.coatardbul.river.model.dto.UserDto;
import com.coatardbul.river.model.entity.AuthUser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserInfoService  {

    @Resource
    private AuthUserMapper authUserMapper;

    

    
    public AuthUser selectByPrimaryKey(String userId) {
        return authUserMapper.selectByPrimaryKey(userId);
    }

    


    

    public  List<AuthUser>  login(UserDto userDto) {
        return authUserMapper.selectAllByUsernameAndPassword(userDto.getAccount(), userDto.getPassword());
    }
}
