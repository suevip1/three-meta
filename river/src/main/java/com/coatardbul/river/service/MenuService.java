package com.coatardbul.river.service;

import com.coatardbul.baseCommon.constants.CommonStatusEnum;
import com.coatardbul.baseService.service.SnowFlakeService;
import com.coatardbul.river.mapper.AuthMenuMapper;
import com.coatardbul.river.model.dto.UserDto;
import com.coatardbul.river.model.entity.AuthMenu;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/2/9
 *
 * @author Su Xiaolei
 */
@Service
@Slf4j
public class MenuService {

    @Autowired
    AuthMenuMapper authMenuMapper;
    @Autowired
    SnowFlakeService snowFlakeService;

    public void add(AuthMenu dto) {
        dto.setId(snowFlakeService.getSnowId());
        if(!StringUtils.isNotBlank(dto.getParentMenuId())){
            dto.setParentMenuId("0");
        }
        if(dto.getSequent()==null){
            Integer integer = authMenuMapper.selectMaxSequent();
            if(integer==null){
                dto.setSequent(1);
            }else {
                dto.setSequent(integer.intValue()+1);
            }
        }
        dto.setStatus(CommonStatusEnum.VALID.getCode().intValue());
        authMenuMapper.insert(dto);
    }

    public List<AuthMenu> getAllMenu(AuthMenu dto) {

      return   authMenuMapper.selectAllByParentMenuIdAndMenuNameLikeAndRouterUrl(dto.getParentMenuId(),dto.getMenuName(),dto.getRouterUrl());
    }

    public void modify(AuthMenu dto) {
        authMenuMapper.updateByPrimaryKeySelective(dto);
    }

    public void delete(AuthMenu dto) {
        authMenuMapper.deleteByPrimaryKey(dto.getId());
        authMenuMapper.deleteByParentMenuId(dto.getParentMenuId());
    }

    public List<AuthMenu> getAllMenuByUser(UserDto dto) {
        AuthMenu authMenu = new AuthMenu();
        if(dto.getAccount().equals("sxl14459048")){
            return getAllMenu(authMenu);
        }else {
            List<AuthMenu> allMenu=new ArrayList<>();
            allMenu.addAll(getMenu("1491301519516106752"));
            allMenu.addAll(getMenu("1493889650689966080"));
            allMenu.addAll(getMenu("1506935582004215808"));
            allMenu.addAll(getMenu("1522060470968778752"));
            return allMenu;
        }
    }

    private List<AuthMenu> getMenu(String id){
        AuthMenu authMenu = new AuthMenu();
        authMenu.setParentMenuId(id);
        AuthMenu authMenu1 = authMenuMapper.selectByPrimaryKey(id);
        List<AuthMenu> allMenu = getAllMenu(authMenu);
        allMenu.add(authMenu1);
        return allMenu;

    }
}
