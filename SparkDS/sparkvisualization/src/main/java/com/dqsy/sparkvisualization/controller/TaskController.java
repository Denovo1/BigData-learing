package com.dqsy.sparkvisualization.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dqsy.sparkvisualization.entity.*;
import com.dqsy.sparkvisualization.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author liusinan
 * @version 1.0.0
 * @ClassName TaskController.java
 * @Description TODO
 * @createTime 2020年03月27日 15:06:00
 */
@Controller
@RequestMapping("/index")
@Slf4j
public class TaskController {

    private static final String[] provinces = "北京市,天津市,上海市,重庆市,河北省,山西省,台湾省,辽宁省,吉林省,黑龙江省,江苏省,浙江省,安徽省,福建省,江西省,山东省,河南省,湖北省,湖南省,广东省,甘肃省,四川省,贵州省,海南省,云南省,青海省,陕西省,广西壮族自治区,西藏自治区,宁夏回族自治区,新疆维吾尔自治区,内蒙古自治区,澳门特别行政区,香港特别行政区".split(",");

    @Autowired
    private ITaskService taskService;

    @Autowired
    private ISessionService sessionService;

    @Autowired
    private IAdService adService;

    @Autowired
    private IAreaService areaService;

    @RequestMapping("session")
    public String index(Model model) {
        List<Task> taskList = taskService.getAllTaskList();
        model.addAttribute("tasks", taskList);
        return "index";
    }

    @RequestMapping(value = "sessionindex", method = {RequestMethod.GET, RequestMethod.POST})
    public String sessionindex(Model model, Integer taskid) {
        SessionAggrStat session = sessionService.getAllSessionAggrStat(taskid);
        String str = sessionService.getstr(session);
        JSONArray jsonArray = JSONArray.parseArray("[" + str + "]");
        model.addAttribute("str", jsonArray);
        model.addAttribute("taskid", taskid);
        return "sessionshow";
    }

    @RequestMapping(value = "pageindex", method = {RequestMethod.GET, RequestMethod.POST})
    public String pageindex(Model model, Integer taskid) {
        model.addAttribute("taskid", taskid);
        return "pageshow";
    }

    @RequestMapping(value = "adindex", method = {RequestMethod.GET, RequestMethod.POST})
    public String adindex(Model model, Integer adid) {
        // 各省Top3
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
            String str = "pro" + i;
            model.addAttribute(str, tmp);
        }

        // 黑名单
        List<AdBlacklist> adBlackList = adService.getAdBlackList();
        String blackCount = "今天黑名单新增了 " + adBlackList.size() + " 个用户";
        model.addAttribute("blackuser", blackCount);
        model.addAttribute("theadid", adid);

        return "ad";
    }

    @RequestMapping(value = "alladindex", method = {RequestMethod.GET, RequestMethod.POST})
    public String alladindex(Model model, Integer adid) {
        // 各省Top3
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
            String str = "pro" + i;
            model.addAttribute(str, tmp);
        }

        // 黑名单
        List<AdBlacklist> adBlackList = adService.getAdBlackList();
        String blackCount = "今天黑名单新增了 " + adBlackList.size() + " 个用户";
        model.addAttribute("blackuser", blackCount);
        model.addAttribute("theadid", adid);

        return "allad";
    }

    @RequestMapping(value = "areaindex", method = {RequestMethod.GET, RequestMethod.POST})
    public String areaindex(Model model, Integer taskid) {
        List<AreaTop3Product> arealist = areaService.getAreaList(taskid);
        for (int i = 0; i < arealist.size(); i++) {

            long task = arealist.get(i).getTaskid();
            String area = arealist.get(i).getArea();
            String areaLevel = arealist.get(i).getAreaLevel();
            long productid = arealist.get(i).getProductid();
            String cityInfos = arealist.get(i).getCityInfos();
            long clickCount = arealist.get(i).getClickCount();
            String productName = arealist.get(i).getProductName();
            String productStatus = arealist.get(i).getProductStatus();

            if (area.equals("China South")) {
                area = "华南";
            } else if (area.equals("China Middle")) {
                area = "华中";
            } else if (area.equals("China North")) {
                area = "华北";
            } else if (area.equals("West North")) {
                area = "西北";
            } else if (area.equals("China East")) {
                area = "华东";
            } else if (area.equals("East North")) {
                area = "东北";
            } else {
                area = "西南";
            }

            if (productStatus.equals("Third Party")) {
                productStatus = "第三方";
            } else {
                productStatus = "自营";
            }

            arealist.set(i, new AreaTop3Product(task, area, areaLevel, productid, cityInfos, clickCount, productName, productStatus));
        }

        model.addAttribute("arealist", arealist);
        model.addAttribute("taskid", taskid);
        return "areashow";
    }

}