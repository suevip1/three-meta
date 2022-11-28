package com.coatardbul.river.model.feign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AreamFeignInputDto {
    /**
     * 地区名称主键
     */
    @NotEmpty(message = "地区名称主键不能为空")
    private String code;

    /**
     * 地区名称主键
     */
    @NotEmpty(message = "地区名称主键不能为空")
    private String name;
}
