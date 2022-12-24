package com.coatardbul.baseCommon.util;

import com.coatardbul.baseCommon.model.bo.Chip;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/2/25
 *
 * @author Su Xiaolei
 */
public class DongCaiUtil {

    /**
     * 获取筹码js对象
     *
     * @return
     * @throws ScriptException
     * @throws NoSuchMethodException
     * @throws FileNotFoundException
     */
    public static Invocable getChipInvocable() throws ScriptException, FileNotFoundException {
        String result = "";
        // 获取JS执行引擎
        ScriptEngine se = new ScriptEngineManager().getEngineByName("javascript");
        String userPath = System.getProperty("user.dir");
        FileReader fileReader = new FileReader(userPath + "/js/chip.js");

        se.eval(fileReader);
        // 是否可调用
        // 是否可调用
        if (se instanceof Invocable) {
            Invocable in = (Invocable) se;

            return in;
//            String jsonStr = JsonUtil.toJson(helloList);

//            Object ccc = in.invokeFunction("calcChip",2348,150,120, jsonStr);

        }
        return null;
    }


    public static Chip convert(Object ccc) {
        Chip result = new Chip();


        //获利比例
        BigDecimal benefitPart = new BigDecimal(((ScriptObjectMirror) ccc).get("benefitPart").toString()).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
        //平均成本
        BigDecimal avgCost = new BigDecimal(((ScriptObjectMirror) ccc).get("avgCost").toString()).setScale(2, BigDecimal.ROUND_HALF_UP);
        //集中度
        Object percentChips = ((ScriptObjectMirror) ccc).get("percentChips");
        Object ninePrecent = ((ScriptObjectMirror) percentChips).get("90");
        String concentrationStr = ((ScriptObjectMirror) ninePrecent).get("concentration").toString();
        BigDecimal concentration = new BigDecimal(concentrationStr).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);

        result.setBenefitPart(benefitPart);
        result.setAvgCost(avgCost);
        result.setConcentration(concentration);
        result.setXcoord(getCoordArr("x", ccc));
        result.setYcoord(getCoordArr("y", ccc));
        return result;
    }

    private static List<Double> getCoordArr(String key, Object ccc) {
        List<Double> result = new ArrayList<Double>();
        Object x = ((ScriptObjectMirror) ccc).get(key);
        for (int i = 0; i < 150; i++) {
            Object o = ((ScriptObjectMirror) x).get(String.valueOf(i));
            result.add((Double) o);
        }
        return result;
    }


}
