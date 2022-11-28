package com.coatardbul.river.local;

import com.coatardbul.baseCommon.exception.BusinessException;
import com.coatardbul.river.common.annotation.WebLog;
import com.coatardbul.river.common.api.CommonResult;
import com.coatardbul.river.common.constants.RequestUrlConstant;
import com.coatardbul.river.model.dto.UserDto;
import com.coatardbul.river.model.entity.AuthMenu;
import com.coatardbul.river.service.MenuService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/2/9
 *
 * @author Su Xiaolei
 */
@Api(value = "菜单")
@Slf4j
@WebLog
@RestController
@ControllerAdvice
@RequestMapping(value = RequestUrlConstant.MENU)
public class MenuController {

    @Autowired
    MenuService menuService;
    /**
     * 新增菜单
     * @param
     * @return
     */
    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public CommonResult<String> add(@RequestBody @Valid AuthMenu dto) throws BusinessException {
        menuService.add(dto);
        return CommonResult.success(null);
    }
    @PostMapping(value = "/modify", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public CommonResult<String> modify(@RequestBody @Valid AuthMenu dto) throws BusinessException {
        menuService.modify(dto);
        return CommonResult.success(null);
    }
    @PostMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public CommonResult<String> delete(@RequestBody @Valid AuthMenu dto) throws BusinessException {
        menuService.delete(dto);
        return CommonResult.success(null);
    }

    /**
     * @param
     * @return
     */
    @PostMapping(value = "/getAllMenu", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public CommonResult<List<AuthMenu>> getAllMenu(@RequestBody @Valid AuthMenu dto) {
        return CommonResult.success( menuService.getAllMenu(dto));
    }

    @PostMapping(value = "/getAllMenuByUser", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public CommonResult<List<AuthMenu>> getAllMenuByUser(@RequestBody @Valid UserDto dto) {
        return CommonResult.success( menuService.getAllMenuByUser(dto));
    }


}
