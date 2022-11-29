package com.coatardbul.stock.model.bo;

import com.coatardbul.baseCommon.model.bo.LimitStrongWeakBO;
import lombok.Data;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/4/11
 *
 * @author Su Xiaolei
 */
@Data
public class LimitBaseInfoBO extends LimitStrongWeakBO {
    private String name;

    private String code;
}
