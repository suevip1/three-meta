package com.coatardbul.baseCommon.util;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/2/25
 *
 * @author Su Xiaolei
 */
public class TongHuaShunUtil {

    public static String getHeXinStr() throws ScriptException, NoSuchMethodException, FileNotFoundException {
        String result="";
        // 获取JS执行引擎
        ScriptEngine se = new ScriptEngineManager().getEngineByName("javascript");
        String userPath=System.getProperty("user.dir");
        FileReader fileReader = new FileReader(userPath+"/js/aes.min.js");

        se.eval(fileReader);
        // 是否可调用
        if (se instanceof Invocable) {
            Invocable in = (Invocable) se;
             result = (String) in.invokeFunction("Get_v");
        }
        return result;
    }

    public static String getTrans(String chinaName) throws ScriptException, NoSuchMethodException, FileNotFoundException {
        String result="";
        // 获取JS执行引擎
        ScriptEngine se = new ScriptEngineManager().getEngineByName("javascript");
        String userPath=System.getProperty("user.dir");
        FileReader fileReader = new FileReader(userPath+"/js/trans.js");

        se.eval(fileReader);
        // 是否可调用
        if (se instanceof Invocable) {
            Invocable in = (Invocable) se;
            result = (String) in.invokeFunction("trans",chinaName);
        }
        return  result;
    }
}
