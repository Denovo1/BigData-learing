package com.dqsy.sparkvisualization.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dqsy.sparkvisualization.KafkaProducer.MessageProducer;
import com.dqsy.sparkvisualization.entity.*;
import com.dqsy.sparkvisualization.service.IAdService;
import com.dqsy.sparkvisualization.util.AddressUtils;
import com.dqsy.sparkvisualization.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author liusinan
 * @version 1.0.0
 * @ClassName SessionController.java
 * @Description TODO
 * @createTime 2020年03月29日 19:07:00
 */
@RestController
@RequestMapping("/adshow")
@Slf4j
public class AdController {

    @Autowired
    private IAdService adService;

    private static final String[] provinces = "北京市,天津市,上海市,重庆市,河北省,山西省,台湾省,辽宁省,吉林省,黑龙江省,江苏省,浙江省,安徽省,福建省,江西省,山东省,河南省,湖北省,湖南省,广东省,甘肃省,四川省,贵州省,海南省,云南省,青海省,陕西省,广西壮族自治区,西藏自治区,宁夏回族自治区,新疆维吾尔自治区,内蒙古自治区,澳门特别行政区,香港特别行政区".split(",");

    @RequestMapping(value = "/show", method = {RequestMethod.GET, RequestMethod.POST}, produces = "application/json")
    public Object show(Integer adid) {
        List<AdClickTrend> adClickTrend = adService.getAdClickTrend(adid);

        List<AdTmp> adTmpList1 = new LinkedList<>();
        for (AdClickTrend clickTrend : adClickTrend) {
            adTmpList1.add(new AdTmp(Integer.valueOf(clickTrend.getHour()), Integer.valueOf(clickTrend.getMinute()), clickTrend.getClickCount()));
        }

        List<AdTmp> adTmpList2 = new LinkedList<>();
        for (int i = 0; i <= 23; i++) {
            for (int j = 0; j <= 59; j++) {
                adTmpList2.add(new AdTmp(i, j, 0));
            }
        }

        for (int i = 0; i < adTmpList2.size(); i++) {
            for (int j = 0; j < adTmpList1.size(); j++) {
                if (adTmpList2.get(i).getHour() == adTmpList1.get(j).getHour() && adTmpList2.get(i).getMinute() == adTmpList1.get(j).getMinute()) {
                    adTmpList2.get(i).setClickCount(adTmpList1.get(j).getClickCount());
                }
            }
        }

        JSONArray array = JSONArray.parseArray(JSON.toJSONString(adTmpList2));
        return array;
    }

    @RequestMapping(value = "/showall", method = {RequestMethod.GET, RequestMethod.POST}, produces = "application/json")
    public Object showall() {
        List<AdClickTrend> adClickTrend = adService.getAllAdClickTrend();

        List<AdTmp> adTmpList1 = new LinkedList<>();
        for (AdClickTrend clickTrend : adClickTrend) {
            adTmpList1.add(new AdTmp(Integer.valueOf(clickTrend.getHour()), Integer.valueOf(clickTrend.getMinute()), clickTrend.getClickCount()));
        }

        List<AdTmp> adTmpList2 = new LinkedList<>();
        for (int i = 0; i <= 23; i++) {
            for (int j = 0; j <= 59; j++) {
                adTmpList2.add(new AdTmp(i, j, 0));
            }
        }

        for (int i = 0; i < adTmpList2.size(); i++) {
            for (int j = 0; j < adTmpList1.size(); j++) {
                if (adTmpList2.get(i).getHour() == adTmpList1.get(j).getHour() && adTmpList2.get(i).getMinute() == adTmpList1.get(j).getMinute()) {
                    adTmpList2.get(i).setClickCount(adTmpList1.get(j).getClickCount());
                }
            }
        }

        JSONArray array = JSONArray.parseArray(JSON.toJSONString(adTmpList2));
        return array;
    }

    @RequestMapping(value = "/getblacklist", method = {RequestMethod.GET, RequestMethod.POST}, produces = "application/json")
    public Object getblacklist() {
        List<AdBlacklist> adBlackList = adService.getAdBlackList();
        String blackCount = "今天黑名单新增了 " + adBlackList.size() + " 个用户";
        Tmp tmp = new Tmp("strvalue", blackCount);
        return JSON.toJSON(tmp);
    }

    @RequestMapping(value = "/gettop", method = {RequestMethod.GET, RequestMethod.POST}, produces = "application/json")
    public Object gettop(Integer adid) {
        // 各省Top3
        List<AdProvinceTop3Temp> res = new LinkedList<>();
        for (int i = 0; i < provinces.length; i++) {
            List<AdProvinceTop3> provinceTop3 = adService.getAdProvinceTop3(provinces[i]);
            List<AdProvinceTop3Temp> tmp = new LinkedList<>();
            String tt = "";
            for (AdProvinceTop3 adProvinceTop3 : provinceTop3) {
                tt += adProvinceTop3.getAdid().toString() + ",";
            }
            int length = tt.length();
            AdProvinceTop3Temp top3Temp = new AdProvinceTop3Temp(provinceTop3.get(0).getProvince(),
                    tt.substring(0, length - 1));
            tmp.add(top3Temp);
            res.add(tmp.get(0));
        }
        return JSON.toJSON(res);
    }

    @RequestMapping(value = "/adClick", method = {RequestMethod.GET, RequestMethod.POST})
    public Object adClickindex(Integer uid, Integer adid, HttpServletRequest request) throws UnsupportedEncodingException {
//        https://api.ttt.sh/ip/qqwry/112.103.180.115
        long time = new Date().getTime();
        String ipAddress = IpUtil.getV4IP();
        String str = AddressUtils.getAddresses(ipAddress, "UTF-8");

        String province = str.split("-")[0];
        String city = str.split("-")[1];
        String message = time + " " + province + " " + city + " "
                + uid + " " + adid;

        // 自定义KafkaProducer
        MessageProducer messageProducer = new MessageProducer();
        messageProducer.run(message);

        return "ad" + adid;
    }
}
