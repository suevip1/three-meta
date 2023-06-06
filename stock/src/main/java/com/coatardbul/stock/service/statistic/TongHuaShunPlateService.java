package com.coatardbul.stock.service.statistic;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coatardbul.baseCommon.exception.BusinessException;
import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseCommon.util.TongHuaShunUtil;
import com.coatardbul.baseService.service.HttpPoolService;
import com.coatardbul.baseService.service.StockStrategyCommonService;
import com.coatardbul.stock.model.dto.TongHuaShunPlateDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.conn.ConnectTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/12/10
 *
 * @author Su Xiaolei
 */
@Service
@Slf4j
public class TongHuaShunPlateService {

    @Autowired
    HttpPoolService httpService;
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    StockStrategyCommonService stockStrategyCommonService;

    public Object getAllPlate() throws ScriptException, FileNotFoundException, NoSuchMethodException {

        List<Header> headerList = new ArrayList<>();
        String heXinStr = TongHuaShunUtil.getHeXinStr();
        Header cookie = httpService.getHead("Cookie", stockStrategyCommonService.getCookieValue() + heXinStr);
        Header hexin = httpService.getHead("hexin-v", heXinStr);
        Header orign =httpService.getHead ("Origin", "http://www.iwencai.com");

        headerList.add(cookie);
        headerList.add(hexin);
        headerList.add(orign);
        long l = System.currentTimeMillis();
        String response = null;
        String url = "http://www.iwencai.com/unifiedwap/self-stock/plate/list" ;
        try {
            response = httpService.doGet(url, headerList, false);
        } catch (ConnectTimeoutException e) {
            throw new BusinessException("链接异常");
        }
        if (response == null) {
            return new ArrayList<>();
        }
        JSONObject jsonObject = JSONObject.parseObject(response);
        if ("true".equals(jsonObject.getString("success"))) {
            return jsonObject.getJSONArray("data");
        } else {
            return new ArrayList<>();
        }
    }

    public TongHuaShunPlateDTO getPlateStock(TongHuaShunPlateDTO dto) throws ScriptException, FileNotFoundException, NoSuchMethodException {
        Assert.notNull(dto.getSn(), "id不能为空");
        Object allPlate = getAllPlate();
        if(allPlate instanceof  JSONArray){
            JSONArray allPlate1 = (JSONArray) allPlate;
            for(int i=0;i<allPlate1.size();i++){
                JSONArray list = allPlate1.getJSONObject(i).getJSONArray("list");
                List<String> codeArr=new ArrayList<>();
                for(int j=0;j<list.size();j++){
                    String stock = list.getJSONObject(j).getString("stock");
                    codeArr.add(stock);
                }
                dto.setCodeArr(codeArr);
            }
        }
        return dto;
    }



    public Object deletePlateStock(TongHuaShunPlateDTO dto) {
        Assert.notNull(dto.getSn(), "id不能为空");

        List<String>newCodeArr=new ArrayList<String>();
        for(String code:dto.getCodeArr()){
            newCodeArr.add(getCodeUrl(code));
        }
        String codeScs=  StringUtils.join(newCodeArr,",");


        List<Header> headerList = new ArrayList<>();
        Header cookie = httpService.getHead("Cookie", "qgqp_b_id=4f2e2113c6a739150b5b25a51c7b360c; mtp=1; ct=EKe_1bn9-NAu7mTEal5v-HNzR1RSx4Ydj-vzD_iTprzAv-VGcJIYXrDhN5WqTiwl-ni94xXjjnwnCLgtrDuK9QIr3T8eF_3gqtCpol0pRzD9RPSYFHtLW-_rZovxyiNNAqzcarGZxmwtCyAD7DmVYnydPuZ9C2OTb_JZhKQFAf0; ut=FobyicMgeV7bfas_M05TDNDG3fRk0e1kQ6e1c03KPGAz6xjYrR7YcvVRHnofxYPrd6MD8il1TTA16QxULLdEuRqH1eIoeph4UaCZN341j5_j3erT8pQPXvR12A6Qq6ciOHFmDoCB92t58_I0-bXc3Sx2SQnqpyrVigVhliUdBbSJaQ1gt6T1DZtARx0Oz1B9FL-muJwr2MO0JjGGQEi85UZ-9w0rNPbSnzyCZpMc8wFNaDztS-TTBiL8r8VemZOsWP2uWJirkm4YwINUHFojAfo_xBKgnss4; pi=3155316106775332%3bi3155316106775332%3b%e8%82%a1%e5%8f%8bd30352080k%3bbbJhSbqg8d0uKcqdiPCv0MNGUu3Q7LG1AUJTew9kaod9efU9FovXvcz9mCVNwp89q5A3OQm1P1AkhdHblqa5P7vfWD%2fIxzyekAe6HjosPF3XuX4T8ZNVye2q1GdT1GQgSBatJJtsG04gNxW5BQWFND0tx3lckjOhVMpv4sb2WfgQTa6Dqzf4MZFJvIt5FzoQ%2fujhvbOS%3bITj9B4wC970hlHRhxHepxgIzJFS6EeqVUsKtTll8kFl378c0tqKYPX306yT8GcdXie0VrT36eLhSoYdSCLjjH1HEdxpyUAAXjzO2YFSzgHqpVyrYZmdkDBT4MeNAhwsFzjwhQVQNd6i%2bMq%2fReue9Ubix2d%2bwaQ%3d%3d; uidal=3155316106775332%e8%82%a1%e5%8f%8bd30352080k; sid=157961489; vtpst=%7c; em-quote-version=topspeed; st_si=35972742814616; HAList=ty-116-00059-%u5929%u8A89%u7F6E%u4E1A%2Cty-116-00700-%u817E%u8BAF%u63A7%u80A1%2Cty-0-002432-%u4E5D%u5B89%u533B%u7597%2Cty-0-001338-%u6C38%u987A%u6CF0%2Cty-0-300059-%u4E1C%u65B9%u8D22%u5BCC%2Cty-1-600493-%u51E4%u7AF9%u7EBA%u7EC7%2Cty-0-000428-%u534E%u5929%u9152%u5E97%2Cty-0-300678-%u4E2D%u79D1%u4FE1%u606F%2Cty-0-000573-%u7CA4%u5B8F%u8FDC%uFF21%2Cty-1-601858-%u4E2D%u56FD%u79D1%u4F20; st_pvi=32108318001519; st_sp=2022-08-02%2021%3A26%3A04; st_inirUrl=https%3A%2F%2Fwww.baidu.com%2Flink; st_sn=29; st_psi=20221210104436303-111000300841-7372401890; st_asi=20221210104117956-113200301712-1935848841-Web_so_zq-3");
        Header referer = httpService.getHead("Referer", "http://quote.eastmoney.com/");
        headerList.add(cookie);
        headerList.add(referer);
        long l = System.currentTimeMillis();
        String response = null;
        String url = "" ;
        try {
            response = httpService.doGet(url, headerList, false);
        } catch (ConnectTimeoutException e) {
            throw new BusinessException("链接异常");
        }
        return dto;
    }


    public void addPlateInfo(TongHuaShunPlateDTO dto) throws ScriptException, FileNotFoundException, NoSuchMethodException {
        List<Header> headerList = new ArrayList<>();
        String heXinStr = TongHuaShunUtil.getHeXinStr();
        Header cookie = httpService.getHead("Cookie", stockStrategyCommonService.getCookieValue() + heXinStr);
        Header hexin = httpService.getHead("hexin-v", heXinStr);
        Header orign =httpService.getHead ("Origin", "http://www.iwencai.com");

        headerList.add(cookie);
        headerList.add(hexin);
        headerList.add(orign);

        List<String>newCodeArr=new ArrayList<>();
        for (String code : dto.getCodeArr()) {
            newCodeArr.add( covertCode(code));
        }
        String join = String.join("|", newCodeArr);
        dto.setCodes(join);
        String response = null;
        String url = "http://www.iwencai.com/iwencai/userinfo/iwc/userinfo/self-stock/plate/add" ;
        try {
            response = httpService.doPost(url,JsonUtil.toJson(dto), headerList, false);
        } catch (ConnectTimeoutException e) {
            throw new BusinessException("链接异常");
        }
        int i = 1;
    }

    private String covertCode(String code){
        if("00".equals(code.substring(0,2))){
            return code+"_33";
        }
        else if("30".equals(code.substring(0,2))){
            return code+"_33";
        }
        else if("60".equals(code.substring(0,2))){
            return code+"_22";
        }
       else if("68".equals(code.substring(0,2))){
            return code+"_17";
        }
        else {
            return code+"_151";
        }
    }

    private static String getCodeUrl(String code) {
        String codeUrl = null;
        if (code.substring(0, 2).equals("00") || code.substring(0, 3).equals("300")) {
            codeUrl = "0$" + code;
        } else {
            codeUrl = "1$" + code;
        }
        return codeUrl;
    }

    public List<String> getCodeUrlList(TongHuaShunPlateDTO dto) {
        List<Header> headerList = new ArrayList<>();
        Header cookie = httpService.getHead("Cookie", "qgqp_b_id=4f2e2113c6a739150b5b25a51c7b360c; mtp=1; ct=EKe_1bn9-NAu7mTEal5v-HNzR1RSx4Ydj-vzD_iTprzAv-VGcJIYXrDhN5WqTiwl-ni94xXjjnwnCLgtrDuK9QIr3T8eF_3gqtCpol0pRzD9RPSYFHtLW-_rZovxyiNNAqzcarGZxmwtCyAD7DmVYnydPuZ9C2OTb_JZhKQFAf0; ut=FobyicMgeV7bfas_M05TDNDG3fRk0e1kQ6e1c03KPGAz6xjYrR7YcvVRHnofxYPrd6MD8il1TTA16QxULLdEuRqH1eIoeph4UaCZN341j5_j3erT8pQPXvR12A6Qq6ciOHFmDoCB92t58_I0-bXc3Sx2SQnqpyrVigVhliUdBbSJaQ1gt6T1DZtARx0Oz1B9FL-muJwr2MO0JjGGQEi85UZ-9w0rNPbSnzyCZpMc8wFNaDztS-TTBiL8r8VemZOsWP2uWJirkm4YwINUHFojAfo_xBKgnss4; pi=3155316106775332%3bi3155316106775332%3b%e8%82%a1%e5%8f%8bd30352080k%3bbbJhSbqg8d0uKcqdiPCv0MNGUu3Q7LG1AUJTew9kaod9efU9FovXvcz9mCVNwp89q5A3OQm1P1AkhdHblqa5P7vfWD%2fIxzyekAe6HjosPF3XuX4T8ZNVye2q1GdT1GQgSBatJJtsG04gNxW5BQWFND0tx3lckjOhVMpv4sb2WfgQTa6Dqzf4MZFJvIt5FzoQ%2fujhvbOS%3bITj9B4wC970hlHRhxHepxgIzJFS6EeqVUsKtTll8kFl378c0tqKYPX306yT8GcdXie0VrT36eLhSoYdSCLjjH1HEdxpyUAAXjzO2YFSzgHqpVyrYZmdkDBT4MeNAhwsFzjwhQVQNd6i%2bMq%2fReue9Ubix2d%2bwaQ%3d%3d; uidal=3155316106775332%e8%82%a1%e5%8f%8bd30352080k; sid=157961489; vtpst=%7c; em-quote-version=topspeed; st_si=35972742814616; HAList=ty-116-00059-%u5929%u8A89%u7F6E%u4E1A%2Cty-116-00700-%u817E%u8BAF%u63A7%u80A1%2Cty-0-002432-%u4E5D%u5B89%u533B%u7597%2Cty-0-001338-%u6C38%u987A%u6CF0%2Cty-0-300059-%u4E1C%u65B9%u8D22%u5BCC%2Cty-1-600493-%u51E4%u7AF9%u7EBA%u7EC7%2Cty-0-000428-%u534E%u5929%u9152%u5E97%2Cty-0-300678-%u4E2D%u79D1%u4FE1%u606F%2Cty-0-000573-%u7CA4%u5B8F%u8FDC%uFF21%2Cty-1-601858-%u4E2D%u56FD%u79D1%u4F20; st_pvi=32108318001519; st_sp=2022-08-02%2021%3A26%3A04; st_inirUrl=https%3A%2F%2Fwww.baidu.com%2Flink; st_sn=29; st_psi=20221210104436303-111000300841-7372401890; st_asi=20221210104117956-113200301712-1935848841-Web_so_zq-3");
        Header referer = httpService.getHead("Referer", "http://quote.eastmoney.com/");
        headerList.add(cookie);
        headerList.add(referer);
        long l = System.currentTimeMillis();
        String response = null;
        String url = "" + l;
        try {
            response = httpService.doGet(url, headerList, false);
        } catch (ConnectTimeoutException e) {
            throw new BusinessException("链接异常");
        }
        if (response == null) {
            return null;
        }

        List<String> plateCodeArr = new ArrayList<>();
        int beginIndex = response.indexOf("(");
        int endIndex = response.lastIndexOf(")");
        response = response.substring(beginIndex + 1, endIndex);
        JSONObject jsonObject = JSONObject.parseObject(response);
        if ("0".equals(jsonObject.getString("state"))) {
            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("stkinfolist");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String security = jsonObject1.getString("security");
                plateCodeArr.add(security.substring(2, 8));
            }
        }
        return plateCodeArr;
    }



    public Object clearPlateStock(TongHuaShunPlateDTO dto) throws ScriptException, FileNotFoundException, NoSuchMethodException {
        TongHuaShunPlateDTO plateStock = getPlateStock(dto);
        deletePlateStock(plateStock);
        return dto;
    }
}
