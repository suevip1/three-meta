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
        List<String> dateIntervalList = riverRemoteService.getDateIntervalList(dto.getBeginDate(), dto.getEndDate());
        String baseUrl = "http://124.222.217.230:9627/index91";
        String chromedriverPath = "/usr/local/bin/chromedriver";

        System.setProperty("webdriver.chrome.driver", chromedriverPath);

        //初始化一个chrome浏览器实例，实例名称叫driver
        ChromeDriver driver = new ChromeDriver();

        //最大化窗口
        driver.manage().window().maximize();


        //get()打开一个站点
        driver.get(baseUrl);
        List<WebElement> elements = driver.findElements(By.className("el-input__inner"));
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
        WebElement login = driver.findElement(By.tagName("button"));
        new Actions(driver)
                .click(login)
                .perform();
        for (int i = 0; i < dateIntervalList.size(); i++) {
            driver.executeScript("window.open('" + baseUrl + "','_blank');");
            Set<String> windowHandles = driver.getWindowHandles();
            Object[] objects = windowHandles.toArray();
            driver.switchTo().window(objects[i+1].toString());
            List<WebElement> elements0 = driver.findElements(By.className("el-input__suffix"));
            new Actions(driver).moveToElement(elements0.get(2)).pause(Duration.ofSeconds(1))
                    .click().perform();
            List<WebElement> elements1 = driver.findElements(By.className("el-input__inner"));
            new Actions(driver).moveToElement(elements1.get(2)).pause(Duration.ofSeconds(1))
                    .click()
                    .sendKeys(dateIntervalList.get(i)).perform();
            List<WebElement> button = driver.findElements(By.tagName("button"));
            new Actions(driver).moveToElement(button.get(3)).pause(Duration.ofSeconds(1))
                    .click().perform();
            int num = 0;
            while (num < 98) {
                WebElement element = driver.findElement(By.className("el-progress-bar__inner"));
                String str = element.getAttribute("style");
                int endIndex = str.indexOf("%");
                str = str.substring(0, endIndex);
                str = str.replace("width:", "").trim();
                num = Integer.valueOf(str);
                Thread.sleep(3000);
            }
            new Actions(driver).moveToElement(button.get(6)).pause(Duration.ofSeconds(1))
                    .click().perform();
            Thread.sleep(3000);
        }


    }
}
