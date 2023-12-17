package com.coatardbul.stock.service.statistic;

import com.coatardbul.baseService.entity.feign.CalendarDateDTO;
import com.coatardbul.baseService.service.romote.RiverRemoteService;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2023/7/12
 *
 * @author Su Xiaolei
 */
@Service
public class SeleniumService {

    @Autowired
    RiverRemoteService riverRemoteService;

   public void getIncreaseGreate5Range(CalendarDateDTO dto) throws InterruptedException {
        //获取指定日期范围的时间间隔列表
        List<String> dateIntervalList = riverRemoteService.getDateIntervalList(dto.getBeginDate(), dto.getEndDate());
        //设置基础url
        String baseUrl = "http://124.222.217.230:9627/index91";
        //设置chromedriver路径
        String chromedriverPath = "/usr/local/bin/chromedriver";

        //设置chrome驱动的路径
        System.setProperty("webdriver.chrome.driver", chromedriverPath);

        //初始化一个chrome浏览器实例，实例名称叫driver
        ChromeDriver driver = new ChromeDriver();

        //最大化窗口
        driver.manage().window().maximize();


        //get()打开一个站点
        driver.get(baseUrl);
        //获取输入框元素
        List<WebElement> elements = driver.findElements(By.className("el-input__inner"));
        //鼠标悬停，点击输入框，输入账号
        new Actions(driver)
                .moveToElement(elements.get(0))
                .pause(Duration.ofSeconds(1))
                .clickAndHold()
                .pause(Duration.ofSeconds(1))
                .sendKeys("sxl14459048")
                .perform();

        new Actions(driver)
                .moveToElement(elements.get(1))
                .pause(Duration.ofSeconds(1))
                .clickAndHold()
                .pause(Duration.ofSeconds(1))
                .sendKeys("123456")
                .perform();
        //获取登录按钮
        WebElement login = driver.findElement(By.tagName("button"));
        //鼠标悬停，点击登录按钮
        new Actions(driver)
                .click(login)
                .perform();
        //遍历时间间隔列表
        for (int i = 0; i < dateIntervalList.size(); i++) {
            //打开新的窗口
            driver.executeScript("window.open('" + baseUrl + "','_blank');");
            //获取当前窗口句柄
            Set<String> windowHandles = driver.getWindowHandles();
            //将窗口句柄转换为数组
            Object[] objects = windowHandles.toArray();
            //切换到新打开的窗口
            driver.switchTo().window(objects[i+1].toString());
            //获取输入框元素
            List<WebElement> elements0 = driver.findElements(By.className("el-input__suffix"));
            //鼠标悬停，点击输入框，输入日期
            new Actions(driver).moveToElement(elements0.get(2)).pause(Duration.ofSeconds(1))
                    .click().perform();
            //获取输入框元素
            List<WebElement> elements1 = driver.findElements(By.className("el-input__inner"));
            //鼠标悬停，点击输入框，输入日期
            new Actions(driver).moveToElement(elements1.get(2)).pause(Duration.ofSeconds(1))
                    .click()
                    .sendKeys(dateIntervalList.get(i)).perform();
            //获取按钮元素
            List<WebElement> button = driver.findElements(By.tagName("button"));
            //鼠标悬停，点击按钮
            new Actions(driver).moveToElement(button.get(3)).pause(Duration.ofSeconds(1))
                    .click().perform();
            //初始化计数器
            int num = 0;
            //循环检查进度条是否达到100%
            while (num < 98) {
                //获取进度条元素
                WebElement element = driver.findElement(By.className("el-progress-bar__inner"));
                //获取进度条样式
                String str = element.getAttribute("style");
                //获取进度条结束位置
                int endIndex = str.indexOf("%");
                //截取进度条样式
                str = str.substring(0, endIndex);
                //去除进度条样式中的width:
                str = str.replace("width:", "").trim();
                //将字符串转换为整数
                num = Integer.valueOf(str);
                //等待3秒
                Thread.sleep(3000);
            }
            //鼠标悬停，点击按钮
            new Actions(driver).moveToElement(button.get(6)).pause(Duration.ofSeconds(1))
                    .click().perform();
            //等待3秒
            Thread.sleep(3000);
        }


    }}
