package com.coatardbul.stock.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.baseCommon.model.bo.Chip;
import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseService.service.DongFangCommonService;
import com.coatardbul.baseService.service.HttpPoolService;
import com.coatardbul.stock.common.annotation.WebLog;
import com.coatardbul.baseService.feign.BaseServerFeign;
import com.coatardbul.stock.service.base.CosService;
import com.coatardbul.stock.service.base.EmailService;
import com.coatardbul.stock.service.statistic.RedisService;
import com.coatardbul.stock.service.statistic.StockCronRefreshService;
import com.coatardbul.stock.service.statistic.StockQuartzService;
import com.coatardbul.stock.service.statistic.StockSpecialStrategyService;
import com.coatardbul.stock.service.statistic.trade.StockTradeService;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.Bucket;
import com.qcloud.cos.model.COSObjectSummary;
import com.qcloud.cos.model.CannedAccessControlList;
import com.qcloud.cos.model.CreateBucketRequest;
import com.qcloud.cos.model.ListObjectsRequest;
import com.qcloud.cos.model.ObjectListing;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import io.swagger.annotations.Api;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/2/18
 *
 * @author Su Xiaolei
 */
@Slf4j
@RestController
@Api(tags = "")
@RequestMapping("/test")
public class TestController {
    @Autowired
    StockSpecialStrategyService stockSpecialStrategyService;
    @Autowired
    RedisService redisService;
    @Autowired
    EmailService emailService;
    @Autowired
    CosService cosService;
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    StockTradeService stockTradeService;

    @Autowired
    HttpPoolService httpService;
    @Autowired
    StockQuartzService stockQuartzService;
    @Autowired
    BaseServerFeign baseServerFeign;
    @Autowired

    StockCronRefreshService stockCronRefreshService;

    @Autowired
    DongFangCommonService dongFangCommonService;

    @WebLog(value = "")
    @RequestMapping(path = "/test", method = RequestMethod.GET)
    public CommonResult dayStatic() throws Exception {
        String response=null;
        int retryNum = 10;
        while (retryNum > 0) {
             response = dongFangCommonService.getDayKlineChip("002579");
            if (StringUtils.isNotBlank(response)) {
                break;
            } else {
                retryNum--;
            }
        }
        if (!StringUtils.isNotBlank(response)) {
            return null;
        }

        int beginIndex = response.indexOf("(");
        int endIndex = response.lastIndexOf(")");
        response = response.substring(beginIndex + 1, endIndex);
        JSONObject jsonObject = JSONObject.parseObject(response);
        JSONObject data = jsonObject.getJSONObject("data");
        JSONArray klines = data.getJSONArray("klines");
//        return CommonResult.success(klines);

        List<List<String>> helloList = new ArrayList<List<String>>();


        for (int i = 0; i < klines.size(); i++) {
            Object o = klines.get(i);
            if (o instanceof String) {
                List<String> item = new ArrayList<String>();
                String o1 = (String) o;
                String[] split = o1.split(",");
                item.add(split[0]);
                item.add(split[1]);
                item.add(split[2]);
                item.add(split[3]);
                item.add(split[4]);
                item.add(split[5]);
                item.add(split[6]);
                item.add(split[7] + "%");
                item.add(split[10]);
                item.add(split[8]);
                item.add(split[9]);
                helloList.add(item);
            }
        }

//        List<Object> collect = klines.stream().collect(Collectors.toList());
        String result = "";
        // 获取JS执行引擎
        ScriptEngine se = new ScriptEngineManager().getEngineByName("javascript");
        String userPath = System.getProperty("user.dir");
        FileReader fileReader = new FileReader(userPath + "/stock/js/chip.js");

        se.eval(fileReader);
        // 是否可调用
        if (se instanceof Invocable) {
            Invocable in = (Invocable) se;

            String s = JsonUtil.toJson(helloList);

            Object ccc = in.invokeFunction("calcChip", helloList.size() - 1, 150, 120, s);
            //获利比例
            BigDecimal benefitPart = new BigDecimal(((ScriptObjectMirror) ccc).get("benefitPart").toString()).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
            //平均成本
            BigDecimal avgCost = new BigDecimal(((ScriptObjectMirror) ccc).get("avgCost").toString()).setScale(2, BigDecimal.ROUND_HALF_UP);
            //集中度
            Object percentChips = ((ScriptObjectMirror) ccc).get("percentChips");
            Object ninePrecent = ((ScriptObjectMirror) percentChips).get("90");
            String concentration = ((ScriptObjectMirror) ninePrecent).get("concentration").toString();
            BigDecimal bigDecimal = new BigDecimal(concentration).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);

            List<Double> x = getCoordArr("x", ccc);
            List<Double> y = getCoordArr("y", ccc);

            ((ScriptObjectMirror) ccc).get("x");
            Chip reslult = new Chip();
            reslult.setBenefitPart(benefitPart);
            reslult.setAvgCost(avgCost);
            reslult.setConcentration(bigDecimal);
            reslult.setXcoord(x);
            reslult.setYcoord(y);
            return CommonResult.success(reslult);
        }

        return CommonResult.success(null);

//        stockQuartzService.xxx();
//        List<Header> headerList = new ArrayList<>();
//        Header cookie = httpService.getHead("Referer", "https://finance.sina.com.cn/realstock/company/sz002866/nc.shtml");
//        headerList.add(cookie);
//
//        String s = httpService.doGet("https://hq.sinajs.cn/rn=1666375012860&list=sz002866,sz002866_i,bk_new_qtxy", headerList, false);
//        System.out.println(s);
    }


    private List<Double> getCoordArr(String key, Object ccc) {
        List<Double> result = new ArrayList<Double>();
        Object x = ((ScriptObjectMirror) ccc).get(key);
        for (int i = 0; i < 150; i++) {
            Object o = ((ScriptObjectMirror) x).get(String.valueOf(i));
            result.add((Double) o);
        }
        return result;
    }

    @RequestMapping(path = "/test1", method = RequestMethod.POST)
    public String cosUpload(MultipartFile file) throws Exception {
        // 1 初始化用户身份信息（secretId, secretKey）。
// SECRETID和SECRETKEY请登录访问管理控制台 https://console.cloud.tencent.com/cam/capi 进行查看和管理
        String secretId = "AKIDc4JL8gy1oNhmbAUYNwGK9i26vWyB9uPf";
        String secretKey = "3qOhkX5OREl36uUrzrUryqSovnPlriKK";
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);

// 2 设置 bucketName 的地域, COS 地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
// clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
        Region region = new Region("ap-nanjing");
        ClientConfig clientConfig = new ClientConfig(region);
// 这里建议设置使用 https 协议
// 从 5.6.54 版本开始，默认使用了 https
        clientConfig.setHttpProtocol(HttpProtocol.https);
// 3 生成 cos 客户端。
        COSClient cosClient = new COSClient(cred, clientConfig);


        String bucketName = "stable-up-1254163062"; //存储桶名称，格式：BucketName-APPID
        CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
// 设置 bucketName 的权限为 Private(私有读写)、其他可选有 PublicRead（公有读私有写）、PublicReadWrite（公有读写）
        createBucketRequest.setCannedAcl(CannedAccessControlList.Private);
        List<Bucket> buckets = cosClient.listBuckets();
        for (Bucket bucketElement : buckets) {
            String bucketName1 = bucketElement.getName();
            String bucketLocation = bucketElement.getLocation();
            System.out.println(bucketName1);
        }
        // 指定要上传的文件
        File localFile = new File("/Users/coatardbul/Library/CloudStorage/OneDrive-个人/文档/亚信/davp/百度地图/纹理图片/555.jpg");
// 指定文件将要存放的存储桶
// 指定文件上传到 COS 上的路径，即对象键。例如对象键为folder/picture.jpg，则表示将文件 picture.jpg 上传到 folder 路径下
        //文件的名称和后缀
        String key = "images/444.jpg";
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        putObjectResult.getETag();


        // Bucket的命名格式为 BucketName-APPID ，此处填写的存储桶名称必须为此格式
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
        // 设置bucket名称
        listObjectsRequest.setBucketName(bucketName);
        // prefix表示列出的object的key以prefix开始
        listObjectsRequest.setPrefix("images/");
        // deliter表示分隔符, 设置为/表示列出当前目录下的object, 设置为空表示列出所有的object
        listObjectsRequest.setDelimiter("/");
        // 设置最大遍历出多少个对象, 一次listobject最大支持1000
        listObjectsRequest.setMaxKeys(1000);
        ObjectListing objectListing = null;
        do {
            try {
                objectListing = cosClient.listObjects(listObjectsRequest);
            } catch (CosServiceException e) {
                e.printStackTrace();
            } catch (CosClientException e) {
                e.printStackTrace();
            }
            // common prefix表示表示被delimiter截断的路径, 如delimter设置为/, common prefix则表示所有子目录的路径
            List<String> commonPrefixs = objectListing.getCommonPrefixes();

            // object summary表示所有列出的object列表
            List<COSObjectSummary> cosObjectSummaries = objectListing.getObjectSummaries();
            for (COSObjectSummary cosObjectSummary : cosObjectSummaries) {
                // 文件的路径key
                String key1 = cosObjectSummary.getKey();
                // 文件的etag
                String etag = cosObjectSummary.getETag();
                // 文件的长度
                long fileSize = cosObjectSummary.getSize();
                // 文件的存储类型
                String storageClasses = cosObjectSummary.getStorageClass();
            }

            String nextMarker = objectListing.getNextMarker();
            listObjectsRequest.setMarker(nextMarker);
        } while (objectListing.isTruncated());

        URL objectUrl = cosClient.getObjectUrl(bucketName, "555.jpg");
        objectUrl.getPath();
//        // Bucket的命名格式为 BucketName-APPID ，此处填写的存储桶名称必须为此格式
//        String bucketName = "examplebucket-1250000000";
//// 指定被删除的文件在 COS 上的路径，即对象键。例如对象键为folder/picture.jpg，则表示删除位于 folder 路径下的文件 picture.jpg
//        String key = "exampleobject";
//        cosClient.deleteObject(bucketName, key);


        return null;

    }


}

