package com.coatardbul.stock.service.statistic;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coatardbul.baseCommon.exception.BusinessException;
import com.coatardbul.baseService.service.HttpPoolService;
import com.coatardbul.stock.mapper.StockBaseMapper;
import com.coatardbul.stock.model.entity.StockBase;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.conn.ConnectTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2023/6/1
 *
 * @author Su Xiaolei
 */
@Service
@Slf4j
public class DongFangSortService {

    @Autowired
    HttpPoolService httpService;
    @Autowired
    StockBaseMapper stockBaseMapper;
    public Object getSpeed() {

        List<Header> headerList = new ArrayList<>();
        Header cookie = httpService.getHead("Cookie", "qgqp_b_id=4f2e2113c6a739150b5b25a51c7b360c; mtp=1; ct=EKe_1bn9-NAu7mTEal5v-HNzR1RSx4Ydj-vzD_iTprzAv-VGcJIYXrDhN5WqTiwl-ni94xXjjnwnCLgtrDuK9QIr3T8eF_3gqtCpol0pRzD9RPSYFHtLW-_rZovxyiNNAqzcarGZxmwtCyAD7DmVYnydPuZ9C2OTb_JZhKQFAf0; ut=FobyicMgeV7bfas_M05TDNDG3fRk0e1kQ6e1c03KPGAz6xjYrR7YcvVRHnofxYPrd6MD8il1TTA16QxULLdEuRqH1eIoeph4UaCZN341j5_j3erT8pQPXvR12A6Qq6ciOHFmDoCB92t58_I0-bXc3Sx2SQnqpyrVigVhliUdBbSJaQ1gt6T1DZtARx0Oz1B9FL-muJwr2MO0JjGGQEi85UZ-9w0rNPbSnzyCZpMc8wFNaDztS-TTBiL8r8VemZOsWP2uWJirkm4YwINUHFojAfo_xBKgnss4; pi=3155316106775332%3bi3155316106775332%3b%e8%82%a1%e5%8f%8bd30352080k%3bbbJhSbqg8d0uKcqdiPCv0MNGUu3Q7LG1AUJTew9kaod9efU9FovXvcz9mCVNwp89q5A3OQm1P1AkhdHblqa5P7vfWD%2fIxzyekAe6HjosPF3XuX4T8ZNVye2q1GdT1GQgSBatJJtsG04gNxW5BQWFND0tx3lckjOhVMpv4sb2WfgQTa6Dqzf4MZFJvIt5FzoQ%2fujhvbOS%3bITj9B4wC970hlHRhxHepxgIzJFS6EeqVUsKtTll8kFl378c0tqKYPX306yT8GcdXie0VrT36eLhSoYdSCLjjH1HEdxpyUAAXjzO2YFSzgHqpVyrYZmdkDBT4MeNAhwsFzjwhQVQNd6i%2bMq%2fReue9Ubix2d%2bwaQ%3d%3d; uidal=3155316106775332%e8%82%a1%e5%8f%8bd30352080k; sid=157961489; vtpst=%7c; em-quote-version=topspeed; st_si=35972742814616; HAList=ty-116-00059-%u5929%u8A89%u7F6E%u4E1A%2Cty-116-00700-%u817E%u8BAF%u63A7%u80A1%2Cty-0-002432-%u4E5D%u5B89%u533B%u7597%2Cty-0-001338-%u6C38%u987A%u6CF0%2Cty-0-300059-%u4E1C%u65B9%u8D22%u5BCC%2Cty-1-600493-%u51E4%u7AF9%u7EBA%u7EC7%2Cty-0-000428-%u534E%u5929%u9152%u5E97%2Cty-0-300678-%u4E2D%u79D1%u4FE1%u606F%2Cty-0-000573-%u7CA4%u5B8F%u8FDC%uFF21%2Cty-1-601858-%u4E2D%u56FD%u79D1%u4F20; st_pvi=32108318001519; st_sp=2022-08-02%2021%3A26%3A04; st_inirUrl=https%3A%2F%2Fwww.baidu.com%2Flink; st_sn=29; st_psi=20221210104436303-111000300841-7372401890; st_asi=20221210104117956-113200301712-1935848841-Web_so_zq-3");
        Header referer = httpService.getHead("Referer", "http://quote.eastmoney.com/");
        headerList.add(cookie);
        headerList.add(referer);
        long l = System.currentTimeMillis();
        String response = null;
        String url = "https://32.push2.eastmoney.com/api/qt/clist/get?" +
                "cb=jQuery1124029900100396333995_" + (l - 33)+
                "&pn=1&pz=20&po=1&np=1&ut=bd1d9ddb04089700cf9c27f6f7426281&fltt=2&invt=2" +
                "&wbp2u=6751315946175528|0|1|0|web&fid=f11&fs=m:0+t:6,m:0+t:80,m:1+t:2,m:1+t:23,m:0+t:81+s:2048" +
                "&fields=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f12,f13,f14,f15,f16,f17,f18,f20,f21,f23,f24,f25,f22,f11,f62,f128,f136,f115,f152" +
                  "&" +
                "_=" + l;
        url = url.replaceAll("\\|", "%124");
        try {
            response = httpService.doGet(url, headerList, false);
        } catch (ConnectTimeoutException e) {
            throw new BusinessException("链接异常");
        }
        if (response == null) {
            return new ArrayList<>();
        }

        int beginIndex = response.indexOf("(");
        int endIndex = response.lastIndexOf(")");
        response = response.substring(beginIndex + 1, endIndex);
        JSONObject jsonObject = JSONObject.parseObject(response);
        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("diff");
        for(int i=0;i<jsonArray.size();i++){
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            String code = jsonObject1.getString("f12");
            StockBase stockBase = stockBaseMapper.selectByPrimaryKey(code);
            jsonObject1.put("概念",stockBase.getTheme());
            jsonObject1.put("行业",stockBase.getIndustry());
        }

        return jsonArray;

    }
}
