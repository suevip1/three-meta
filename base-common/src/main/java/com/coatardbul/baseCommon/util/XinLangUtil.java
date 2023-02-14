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
public class XinLangUtil {

    public static Object getHeXinDecode(String response) throws ScriptException, NoSuchMethodException, FileNotFoundException {
        Object result=null;
        // 获取JS执行引擎
        ScriptEngine se = new ScriptEngineManager().getEngineByName("javascript");
        String userPath=System.getProperty("user.dir");
        FileReader fileReader = new FileReader(userPath+"/js/xinlangsdk.js");

        se.eval(fileReader);
        // 是否可调用
        if (se instanceof Invocable) {
            Invocable in = (Invocable) se;
            result =  in.invokeFunction("decode",response);

        }
        return  result;
    }


}
