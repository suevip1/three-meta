package com.coatardbul.river.local;

import com.coatardbul.baseCommon.exception.BusinessException;
import com.coatardbul.river.common.api.CommonResult;
import com.coatardbul.river.model.dto.StockTemplateDTO;
import com.coatardbul.river.model.dto.StockTemplateListQueryDTO;
import com.coatardbul.river.model.dto.StockTemplateQueryDTO;
import com.coatardbul.river.model.entity.StockQueryTemplate;
import com.coatardbul.river.service.StockTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/1/1
 *
 * @author Su Xiaolei
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/stockTemplate")
public class StockTemplateController {

    @Autowired
    StockTemplateService stockTemplateService;


    /**
     * 添加问句模型
     * @param
     * @return
     */
    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public CommonResult<String> add(@RequestBody @Valid StockTemplateDTO dto) throws BusinessException, ParseException {
        stockTemplateService.add(dto);
        return CommonResult.success(null);
    }

    /**
     * @param
     * @return
     */
    @PostMapping(value = "/modify", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public CommonResult<String> modify(@RequestBody @Valid StockTemplateDTO dto) throws ParseException {
        stockTemplateService.modify(dto);
        return CommonResult.success(null);
    }

    /**
     * 每次点击查询，会增加一次热点数量
     * @param dto
     * @return
     */
    @PostMapping(value = "/addHot", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public CommonResult<String> addHot(@RequestBody @Valid StockTemplateDTO dto) {
        stockTemplateService.addHot(dto);
        return CommonResult.success(null);
    }
    /**
     * @param
     * @return
     */
    @PostMapping(value = "/findOne", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public CommonResult<StockQueryTemplate> findOne(@RequestBody @Valid StockTemplateDTO dto) {
        return CommonResult.success(stockTemplateService.findOne(dto));
    }

    /**
     * @param
     * @return
     */
    @PostMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public CommonResult<String> delete(@RequestBody @Valid StockTemplateDTO dto) throws BusinessException {
        stockTemplateService.delete(dto);
        return CommonResult.success(null);
    }

    /**
     * 查询列表
     *
     * @param
     * @return
     */
    @PostMapping(value = "/getList", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public CommonResult<List<StockQueryTemplate>> getList(@RequestBody StockTemplateListQueryDTO dto) {
        return CommonResult.success(stockTemplateService.getList(dto));
    }

    /**
     * 根据id,日期，时间， 获取问句
     */
    @PostMapping(value = "/getQuery", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public CommonResult<String> getQuery(@RequestBody @Valid StockTemplateQueryDTO dto) throws BusinessException {

        try {
            String query = stockTemplateService.getQuery(dto);
            return CommonResult.success(query);
        } catch (ParseException e) {
            log.error(e.getMessage(),e);
            throw new BusinessException("时间格式异常");
        }
    }

}
