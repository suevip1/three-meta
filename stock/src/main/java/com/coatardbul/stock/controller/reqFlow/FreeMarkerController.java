package com.coatardbul.stock.controller.reqFlow;

import com.coatardbul.baseCommon.model.entity.StockBase;
import freemarker.template.Template;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@Api(tags = "移动")
@RequestMapping("/freeMarker")
public class FreeMarkerController {

    @GetMapping("/basic")
    public String test(Model model) {


        //1.纯文本形式的参数
        model.addAttribute("name", "freemarker");
        //2.实体类相关的参数

        StockBase stockBase = new StockBase();
        stockBase.setCode("001001");
        stockBase.setName("长安汽车");
        stockBase.setIndustry("电子");
        stockBase.setTheme("芯片");
        model.addAttribute("stu", "freemarker");


        return "01-basic";
    }

    @Autowired
    private freemarker.template.Configuration freemarkerConfiguration;

    @GetMapping("/generateCode")
    @ResponseBody
    public String generateCode() throws Exception {
        // 创建一个模板数据
        Template template = freemarkerConfiguration.getTemplate("01-basic.ftl");
        Map<String, Object> data = new HashMap<>();
        StockBase stockBase = new StockBase();
        stockBase.setCode("001001");
        stockBase.setName("长安汽车");
        stockBase.setIndustry("电子");
        stockBase.setTheme("芯片");
        data.put("name", "21312312");
        data.put("stu", stockBase);

        // 将数据与模板结合生成代码
        String generatedCode = FreeMarkerTemplateUtils.processTemplateIntoString(template, data);
        return generatedCode;
    }
}
