package com.coatardbul.river.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Su Xiaolei
 */
@ApiModel

@Data
public class CalendarDTO {
    /**
     * yyyy-mm
     */
    @ApiModelProperty("开始月分")
    @NotBlank(message = "开始月分不能为空")
    private String beginMonth;
    @ApiModelProperty("结束月分")
    @NotBlank(message = "结束月分不能为空")
    private String endMonth;

    /**
     * 日期属性 1 工作日 3节假日2 休息日
     */
    @ApiModelProperty("日期属性 1 工作日 3节假日2 休息日")
    private Integer dateProp;
}
