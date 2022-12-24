package com.coatardbul.baseCommon.model.bo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/12/15
 *
 * @author Su Xiaolei
 */
@Data
public class ChipPosition {


    private List<List<String>> dayKlineList;


    private Map<String, Integer> datePositionMap;
}
