package com.ey.service.wp.output.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import com.ey.service.system.report.ReportManager;
import com.ey.service.wp.output.HExportManager;
import com.ey.util.fileexport.Constants;
import com.ey.util.fileexport.FileExportUtils;
import com.ey.util.fileexport.FreeMarkerUtils;

/**
 * @name HExportService
 * @description 底稿输出服务--H
 * @author Dai Zong	2017年12月11日
 */
@Service("hExportService")
public class HExportService extends BaseExportService implements HExportManager{

    @Resource(name = "reportService")
    private ReportManager reportService;

    /**
     * 生成文件内容
     * @author Dai Zong 2017年10月17日
     *
     * @param fundId
     * @param periodStr
     * @param fundInfo
     * @return
     * @throws Exception
     */
    private String generateFileContent(String fundId, String periodStr, Map<String, String> fundInfo, String templatePath) throws Exception {
        Map<String, Object> dataMap = new HashMap<String, Object>();

        Long period = Long.parseLong(periodStr.substring(0, 4));
        Long month = Long.parseLong(periodStr.substring(4, 6));
        Long day = Long.parseLong(periodStr.substring(6, 8));

        dataMap.put("period", period);
        dataMap.put("month", month);
        dataMap.put("day", day);
        dataMap.put("fundInfo", fundInfo);
        dataMap.put("extraFundInfo", this.getExtraFundInfo(fundId, periodStr));

        dataMap.put("H", this.getHData(fundId, periodStr));
        dataMap.put("H300", this.getH300Data(fundId, periodStr));
        dataMap.put("H400", this.getH400Data(fundId, periodStr));
        dataMap.put("H500", this.getH500Data(fundId, periodStr));
        dataMap.put("H800", this.getH800Data(fundId, periodStr));
        dataMap.put("H10000", this.getH10000Data(fundId, periodStr));
        dataMap.put("H11000", this.getH11000Data(fundId, periodStr));

        return FreeMarkerUtils.processTemplateToStrUseAbsPath(dataMap, templatePath, Constants.EXPORT_TEMPLATE_FILE_NAME_H);
    }

	@Override
    public boolean doExport(HttpServletRequest request, HttpServletResponse response, String fundId, String periodStr, String templatePath) throws Exception {
	    Map<String, String> fundInfo = this.selectFundInfo(fundId);
        fundInfo.put("periodStr", periodStr);
        String xmlStr = this.generateFileContent(fundId, periodStr, fundInfo, templatePath);
        FileExportUtils.writeFileToHttpResponse(request, response, FreeMarkerUtils.simpleReplace(Constants.EXPORT_AIM_FILE_NAME_H, fundInfo), xmlStr);
        return true;
    }

	@Override
    public boolean doExport(String folederName, String fileName, String fundId, String periodStr, String templatePath) throws Exception {
	    Map<String, String> fundInfo = this.selectFundInfo(fundId);
        fundInfo.put("periodStr", periodStr);
	    String xmlStr = this.generateFileContent(fundId, periodStr, fundInfo, templatePath);
        FileExportUtils.writeFileToDisk(folederName, FreeMarkerUtils.simpleReplace(fileName, fundInfo), xmlStr);
        return true;
    }

	/**
     * 获取基金额外属性
     * @author Dai Zong 2017年12月2日
     *
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getExtraFundInfo(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);

        @SuppressWarnings("unchecked")
        Map<String,Object> fundInfo = (Map<String,Object>)this.dao.findForObject("TExportMapper.selectExtraFundInfo", queryMap);
        if(fundInfo == null) {
            fundInfo = new HashMap<>();
        }

        Integer startYear = 2000, startMonth = 01, startDay = 01;
        if(fundInfo.get("dateFrom") != null) {
            String[] splits = String.valueOf(fundInfo.get("dateFrom")).split("-");
            try {
                if(splits.length == 1) {
                    startYear = Integer.parseInt(splits[0]);
                }else if(splits.length == 2) {
                    startYear = Integer.parseInt(splits[0]);
                    startMonth = Integer.parseInt(splits[1]);
                }else if(splits.length >= 3) {
                    startYear = Integer.parseInt(splits[0]);
                    startMonth = Integer.parseInt(splits[1]);
                    startDay = Integer.parseInt(splits[2]);
                }
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        fundInfo.put("startYear", startYear);
        fundInfo.put("startMonth", startMonth);
        fundInfo.put("startDay", startDay);
        return fundInfo;
    }

	/**
     * 处理sheet页H的数据
     * @author Dai Zong 2017年12月11日
     *
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getHData(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> metaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectHData", queryMap);
        if(metaDataList == null) {
            metaDataList = new ArrayList<>();
        }
        Map<String, Map<String, List<Map<String, Object>>>> groups = metaDataList.stream().collect(Collectors.groupingBy(item -> {
            Map<String,Object> map = (Map<String,Object>)item;
            return String.valueOf(map.get("item"));
        }, Collectors.groupingBy(item -> {
            Map<String,Object> map = (Map<String,Object>)item;
            return String.valueOf(map.get("type"));
        })));

        Map<String, List<Map<String, Object>>> temp = null;
        temp = groups.get("股票投资")==null ? new HashMap<>() : groups.get("股票投资");
        Map<String,Object> attr1 = CollectionUtils.isEmpty(temp.get("COST")) ? new HashMap<>() : temp.get("COST").get(0);
        Map<String,Object> attr2 = CollectionUtils.isEmpty(temp.get("MKT_VALUE")) ? new HashMap<>() : temp.get("MKT_VALUE").get(0);
        Map<String,Object> attr3 = CollectionUtils.isEmpty(temp.get("APPRECIATION")) ? new HashMap<>() : temp.get("APPRECIATION").get(0);
        temp = groups.get("债券投资")==null ? new HashMap<>() : groups.get("债券投资");
        Map<String,Object> attr4 = CollectionUtils.isEmpty(temp.get("COST")) ? new HashMap<>() : temp.get("COST").get(0);
        Map<String,Object> attr24 = CollectionUtils.isEmpty(temp.get("COST(EXPECT INTERST)")) ? new HashMap<>() : temp.get("COST(EXPECT INTERST)").get(0);
        Map<String,Object> attr25 = CollectionUtils.isEmpty(temp.get("INTERST")) ? new HashMap<>() : temp.get("INTERST").get(0);
        Map<String,Object> attr5 = CollectionUtils.isEmpty(temp.get("MKT_VALUE")) ? new HashMap<>() : temp.get("MKT_VALUE").get(0);
        Map<String,Object> attr6 = CollectionUtils.isEmpty(temp.get("APPRECIATION")) ? new HashMap<>() : temp.get("APPRECIATION").get(0);
        temp = groups.get("资产支持证券投资")==null ? new HashMap<>() : groups.get("资产支持证券投资");
        Map<String,Object> attr7 = CollectionUtils.isEmpty(temp.get("COST")) ? new HashMap<>() : temp.get("COST").get(0);
        Map<String,Object> attr26 = CollectionUtils.isEmpty(temp.get("COST(EXPECT INTERST)")) ? new HashMap<>() : temp.get("COST(EXPECT INTERST)").get(0);
        Map<String,Object> attr27 = CollectionUtils.isEmpty(temp.get("INTERST")) ? new HashMap<>() : temp.get("INTERST").get(0);
        Map<String,Object> attr8 = CollectionUtils.isEmpty(temp.get("MKT_VALUE")) ? new HashMap<>() : temp.get("MKT_VALUE").get(0);
        Map<String,Object> attr9 = CollectionUtils.isEmpty(temp.get("APPRECIATION")) ? new HashMap<>() : temp.get("APPRECIATION").get(0);
        temp = groups.get("基金投资")==null ? new HashMap<>() : groups.get("基金投资");
        Map<String,Object> attr10 = CollectionUtils.isEmpty(temp.get("COST")) ? new HashMap<>() : temp.get("COST").get(0);
        Map<String,Object> attr11 = CollectionUtils.isEmpty(temp.get("MKT_VALUE")) ? new HashMap<>() : temp.get("MKT_VALUE").get(0);
        Map<String,Object> attr12 = CollectionUtils.isEmpty(temp.get("APPRECIATION")) ? new HashMap<>() : temp.get("APPRECIATION").get(0);
        temp = groups.get("衍生金融工具")==null ? new HashMap<>() : groups.get("衍生金融工具");
        Map<String,Object> attr13 = CollectionUtils.isEmpty(temp.get("COST")) ? new HashMap<>() : temp.get("COST").get(0);
        Map<String,Object> attr14 = CollectionUtils.isEmpty(temp.get("MKT_VALUE")) ? new HashMap<>() : temp.get("MKT_VALUE").get(0);
        Map<String,Object> attr15 = CollectionUtils.isEmpty(temp.get("APPRECIATION")) ? new HashMap<>() : temp.get("APPRECIATION").get(0);
        temp = groups.get("贵金属投资-金交所黄金合约")==null ? new HashMap<>() : groups.get("贵金属投资-金交所黄金合约");
        Map<String,Object> attr16 = CollectionUtils.isEmpty(temp.get("COST")) ? new HashMap<>() : temp.get("COST").get(0);
        Map<String,Object> attr17 = CollectionUtils.isEmpty(temp.get("MKT_VALUE")) ? new HashMap<>() : temp.get("MKT_VALUE").get(0);
        Map<String,Object> attr18 = CollectionUtils.isEmpty(temp.get("APPRECIATION")) ? new HashMap<>() : temp.get("APPRECIATION").get(0);
        temp = groups.get("其他")==null ? new HashMap<>() : groups.get("其他");
        Map<String,Object> attr19 = CollectionUtils.isEmpty(temp.get("COST")) ? new HashMap<>() : temp.get("COST").get(0);
        Map<String,Object> attr20 = CollectionUtils.isEmpty(temp.get("MKT_VALUE")) ? new HashMap<>() : temp.get("MKT_VALUE").get(0);
        Map<String,Object> attr21 = CollectionUtils.isEmpty(temp.get("APPRECIATION")) ? new HashMap<>() : temp.get("APPRECIATION").get(0);
        temp = groups.get("买入返售金融资产")==null ? new HashMap<>() : groups.get("买入返售金融资产");
        Map<String,Object> attr22 = CollectionUtils.isEmpty(temp.get("NULL")) ? new HashMap<>() : temp.get("NULL").get(0);
        temp = groups.get("债权投资")==null ? new HashMap<>() : groups.get("债权投资");
        Map<String,Object> attr23 = CollectionUtils.isEmpty(temp.get("COST")) ? new HashMap<>() : temp.get("COST").get(0);
        result.put("attr1", attr1);
        result.put("attr2", attr2);
        result.put("attr3", attr3);
        result.put("attr4", attr4);
        result.put("attr5", attr5);
        result.put("attr6", attr6);
        result.put("attr7", attr7);
        result.put("attr8", attr8);
        result.put("attr9", attr9);
        result.put("attr10", attr10);
        result.put("attr11", attr11);
        result.put("attr12", attr12);
        result.put("attr13", attr13);
        result.put("attr14", attr14);
        result.put("attr15", attr15);
        result.put("attr16", attr16);
        result.put("attr17", attr17);
        result.put("attr18", attr18);
        result.put("attr19", attr19);
        result.put("attr20", attr20);
        result.put("attr21", attr21);
        result.put("attr22", attr22);
        result.put("attr23", attr23);
        result.put("attr24", attr24);
        result.put("attr25", attr25);
        result.put("attr26", attr26);
        result.put("attr27", attr27);
        return result;
    }

    /**
     * 处理sheet页H300的数据
     * @author Dai Zong 2017年12月12日
     *
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getH300Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();

        //========process dataMap for main view begin========
        Map<String, Object> main = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> mainMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH300Data", queryMap);
        if(mainMetaDataList == null) {
            mainMetaDataList = new ArrayList<>();
        }
        Map<String, List<Map<String, Object>>> groups = mainMetaDataList.stream().collect(Collectors.groupingBy(item -> {
            Map<String,Object> map = (Map<String,Object>)item;
            return String.valueOf(map.get("type"));
        }));
        Map<String,Object> attr1 = new HashMap<>();
        Map<String,Object> attr2 = new HashMap<>();
        Map<String,Object> attr3 = new HashMap<>();
        Map<String,Object> attr4 = new HashMap<>();
        Map<String,Object> attr5 = new HashMap<>();
        Map<String,Object> attr6 = new HashMap<>();
        Map<String,Object> attr7 = new HashMap<>();
        Map<String,Object> attr8 = new HashMap<>();
        List<Map<String,Object>> tempList = null;
        tempList = groups.get("股票")==null ? new ArrayList<>() : groups.get("股票");
        attr1 = CollectionUtils.isEmpty(tempList) ? attr1 : tempList.get(0);
        tempList = groups.get("贵金属投资-金交所黄金合约")==null ? new ArrayList<>() : groups.get("贵金属投资-金交所黄金合约");
        attr2 = CollectionUtils.isEmpty(tempList) ? attr2 : tempList.get(0);
        tempList = groups.get("债券")==null ? new ArrayList<>() : groups.get("债券");
        for(Map<String,Object> map : tempList) {
            if("交易所".equals(map.get("subtype"))) {
                attr3 = map;
            }else if("银行间".equals(map.get("subtype"))) {
                attr4 = map;
            }
        }
        tempList = groups.get("资产支持证券投资")==null ? new ArrayList<>() : groups.get("资产支持证券投资");
        attr5 = CollectionUtils.isEmpty(tempList) ? attr5 : tempList.get(0);
        tempList = groups.get("基金投资")==null ? new ArrayList<>() : groups.get("基金投资");
        attr6 = CollectionUtils.isEmpty(tempList) ? attr6 : tempList.get(0);
        tempList = groups.get("其他")==null ? new ArrayList<>() : groups.get("其他");
        attr7 = CollectionUtils.isEmpty(tempList) ? attr7 : tempList.get(0);
        tempList = groups.get("衍生金融工具")==null ? new ArrayList<>() : groups.get("衍生金融工具");
        for(Map<String,Object> map : tempList) {
            if("期货".equals(map.get("subtype"))) {
                attr8 = map;
            }
        }
        main.put("attr1", attr1);
        main.put("attr2", attr2);
        main.put("attr3", attr3);
        main.put("attr4", attr4);
        main.put("attr5", attr5);
        main.put("attr6", attr6);
        main.put("attr7", attr7);
        main.put("attr8", attr8);
        //========process dataMap for main view end========
        //========process dataMap for interestRatePeriods view begin========
        Map<String,Object> interestRatePeriod = new HashMap<>();

        @SuppressWarnings("unchecked")
        List<String> periodMetaDataList = (List<String>)this.dao.findForList("HExportMapper.selectH300InterestRatePeriodsData", queryMap);
        if(periodMetaDataList == null) {
            periodMetaDataList = new ArrayList<>();
        }
        interestRatePeriod.put("list", periodMetaDataList);
        interestRatePeriod.put("count", periodMetaDataList.size());
        //========process dataMap for interestRatePeriods view end========
        //========process dataMap for related view begin========
        Map<String,Object> related = new HashMap<>();
        Map<String,Object> H400 = new HashMap<>();
        Map<String,Object> H500 = new HashMap<>();
        Map<String,Object> MacH500 = new HashMap<>();

        queryMap.put("related", "Y");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H400MetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH400MainData", queryMap);
        if(H400MetaDataList == null) {
            H400MetaDataList = new ArrayList<>();
        }
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H500MetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH500MainData", queryMap);
        if(H500MetaDataList == null) {
            H500MetaDataList = new ArrayList<>();
        }
        // 20221009新增摊余成本债基
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> MacH500MetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH500MacMainData", queryMap);
        if(H500MetaDataList == null) {
            H500MetaDataList = new ArrayList<>();
        }
        queryMap.remove("related");

        H400.put("list", H400MetaDataList);
        H400.put("count", H400MetaDataList.size());
        H500.put("list", H500MetaDataList);
        H500.put("count", H500MetaDataList.size());
        MacH500.put("list", MacH500MetaDataList);
        MacH500.put("count", MacH500MetaDataList.size());
        related.put("H400", H400);
        related.put("H500", H500);
        related.put("MacH500", MacH500);
        //========process dataMap for related view end========

        result.put("main", main);
        result.put("interestRatePeriod", interestRatePeriod);
        result.put("related", related);
        return result;
    }

    /**
     * 处理sheet页H400的数据
     * @author Dai Zong 2017年12月13日
     *
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getH400Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();

        Map<String, Object> main = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> mainMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH400MainData", queryMap);
        if(mainMetaDataList == null) {
            mainMetaDataList = new ArrayList<>();
        }
        main.put("list", mainMetaDataList);
        main.put("count", mainMetaDataList.size());
        main.put("noteFlag", mainMetaDataList.stream().filter(item -> {return "期货".equals(item.get("type"));}).count()==0 ? "N" : "Y");
        result.put("main", main);

        // 20221014irene新增摊余成本债基
        Map<String, Object> macMain = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> MacmainMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH400MacMainData", queryMap);
        if(MacmainMetaDataList == null) {
            MacmainMetaDataList = new ArrayList<>();
        }
        macMain.put("list", MacmainMetaDataList);
        macMain.put("count", MacmainMetaDataList.size());
        result.put("macMain", macMain);

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> metaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH400OptionData", queryMap);
        if(metaDataList == null) {
            metaDataList = new ArrayList<>();
        }

        result.put("list", metaDataList);
        result.put("count", metaDataList.size());
        return result;
    }

    /**
     * 处理sheet页H500的数据
     * @author Dai Zong 2017年12月13日
     *
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getH500Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();

        Map<String, Object> main = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> mainMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH500MainData", queryMap);
        if(mainMetaDataList == null) {
            mainMetaDataList = new ArrayList<>();
        }
        main.put("list", mainMetaDataList);
        main.put("count", mainMetaDataList.size());
        result.put("main", main);

        // 20221008irene新增摊余成本债基
        Map<String, Object> macMain = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> macMainMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH500MacMainData", queryMap);
        if(mainMetaDataList == null) {
            mainMetaDataList = new ArrayList<>();
        }
        macMain.put("list", macMainMetaDataList);
        macMain.put("count", macMainMetaDataList.size());
        result.put("macMain", macMain);

        Map<String, Object> rate = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<String> rateDataList = (List<String>)this.dao.findForList("HExportMapper.selectrateData", queryMap);
        if(rateDataList == null) {
            rateDataList = new ArrayList<>();
        }
        rate.put("list", rateDataList);
        result.put("rate", rate);

        Map<String,Object> interestRatePeriod = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<String> periodMetaDataList = (List<String>)this.dao.findForList("HExportMapper.selectH300InterestRatePeriodsData", queryMap);
        if(periodMetaDataList == null) {
            periodMetaDataList = new ArrayList<>();
        }
        while(periodMetaDataList.size() < 8) {
            periodMetaDataList.add(StringUtils.EMPTY);
        }
        interestRatePeriod.put("list", periodMetaDataList);
        interestRatePeriod.put("count", periodMetaDataList.size());

        @SuppressWarnings("unchecked")
        Map<String,Object> diviatonMetaData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectH500DiviatonData", queryMap);
        if(diviatonMetaData == null) {
            diviatonMetaData = new HashMap<>();
        }

        Map<String, Object> lastQueryMap = this.createBaseQueryMap(fundId, this.getLastPeriodStr(periodStr));
        @SuppressWarnings("unchecked")
        Map<String,Object> lastDiviatonMetaData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectH500DiviatonData", lastQueryMap);
        if(lastDiviatonMetaData == null) {
        	lastDiviatonMetaData = new HashMap<>();
        }
        diviatonMetaData.put("lastDiviatonEy", lastDiviatonMetaData.get("diviatonEy"));

        result.put("diviaton", diviatonMetaData);
        result.put("interestRatePeriod", interestRatePeriod);
        return result;
    }

    /**
     * 根据本期的PeriodStr获取上期的PeriodStr
     * @param periodStr 本期PeriodStr
     * @return 上期PeriodStr
     */
    private String getLastPeriodStr(String periodStr) {
    	return String.valueOf(Integer.parseInt(periodStr.substring(0, 4)) - 1) + periodStr.substring(4);
    }

    /**
     * 处理sheet页H800的数据
     * @author Dai Zong 2017年12月14日
     *
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getH800Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();

        //========process dataMap for main view begin========
        Map<String, Object> main = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> mainMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH800MainData", queryMap);
        if(mainMetaDataList == null) {
            mainMetaDataList = new ArrayList<>();
        }
        main.put("list", mainMetaDataList);
        main.put("count", mainMetaDataList.size());
        //========process dataMap for main view end========

        //========process dataMap for H800 view begin========
        Map<String, Object> TMPL = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> TMPLTestMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH800TMPLData", queryMap);
        if(TMPLTestMetaDataList == null) {
            TMPLTestMetaDataList = new ArrayList<>();
        }
        TMPL.put("list", TMPLTestMetaDataList);
        TMPL.put("count", TMPLTestMetaDataList.size());
        //========process dataMap for interestTest view end========

        //========process dataMap for interestTest view begin========
        Map<String, Object> interestTest = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> interestTestMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH800InterestTestData", queryMap);
        if(interestTestMetaDataList == null) {
            interestTestMetaDataList = new ArrayList<>();
        }
        interestTest.put("list", interestTestMetaDataList);
        interestTest.put("count", interestTestMetaDataList.size());
        //========process dataMap for interestTest view end========

        Map<String, Object> interestTestOTC = new HashMap<>();
        List<Map<String, Object>> interestTestOTCMetaDataList = (List<Map<String, Object>>)this.dao.findForList("HExportMapper.selectH800InterestTestOTCData", queryMap);
        if (interestTestOTCMetaDataList == null) {
            interestTestOTCMetaDataList = new ArrayList<>(); }
        interestTestOTC.put("list", interestTestOTCMetaDataList);
        interestTestOTC.put("count", Integer.valueOf(interestTestOTCMetaDataList.size()));
        Map<String, Object> interestTestEX = new HashMap<>();
        List<Map<String, Object>> interestTestEXMetaDataList = (List<Map<String, Object>>)this.dao.findForList("HExportMapper.selectH800InterestTestEXData", queryMap);
        if (interestTestEXMetaDataList == null){
            interestTestEXMetaDataList = new ArrayList<>(); }
        interestTestEX.put("list", interestTestEXMetaDataList);
        interestTestEX.put("count", Integer.valueOf(interestTestEXMetaDataList.size()));
        Map<String, Object> hp100h800 = new HashMap<>();
        List<Map<String, Object>> hp100h800DataList = (List<Map<String, Object>>)this.dao.findForList("HExportMapper.selectHP100H800Data", queryMap);
        if (hp100h800DataList == null){
            hp100h800DataList = new ArrayList<>(); }
        hp100h800.put("list", hp100h800DataList);
        hp100h800.put("count", Integer.valueOf(hp100h800DataList.size()));
        Map<String, Object> trxcalendarData = new HashMap<>();
        List<Map<String, Object>> trxcalendarDataList = (List<Map<String, Object>>)this.dao.findForList("HExportMapper.selecttrxcalendarData", queryMap);
        if (trxcalendarDataList == null){
            trxcalendarDataList = new ArrayList<>(); }
        trxcalendarData.put("list", trxcalendarDataList);
        trxcalendarData.put("count", Integer.valueOf(trxcalendarDataList.size()));

        //========process dataMap for intestSummary view end========
        @SuppressWarnings("unchecked")
        Map<String,Object> intestSummaryMetaData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectH800IntestSummaryData", queryMap);
        if(intestSummaryMetaData == null) {
            intestSummaryMetaData = new HashMap<>();
        }
        //========process dataMap for intestSummary view end========

        //========process dataMap for interestDetail view begin========
        Map<String, Object> interestDetail = new HashMap<String,Object>();
        List<Map<String, Object>> headLists = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> interestDetailMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH800InterestDetailData", queryMap);
        if(interestDetailMetaDataList == null) {
            interestDetailMetaDataList = new ArrayList<>();
        }
        List<String> headIds = interestTestMetaDataList.stream().map(item -> {
            return String.valueOf(item.get("headId"));
        }).distinct().collect(Collectors.toList());
        Map<Object, List<Map<String, Object>>> groups = interestDetailMetaDataList.stream().collect(Collectors.groupingBy(item -> {
            Map<String,Object> map = (Map<String,Object>)item;
            return map.get("headId");
        }));
        for(String headId : headIds) {
            List<Map<String, Object>> tempList = groups.get(headId);
            if(CollectionUtils.isNotEmpty(tempList)) {
                Map<String, Object> tempMap = new HashMap<String,Object>();
                tempMap.put("list", tempList);
                tempMap.put("count", tempList.size());
                headLists.add(tempMap);
            }
        }
        interestDetail.put("heads", headLists);
        interestDetail.put("headsCount", headLists.size());
        //========process dataMap for interestDetail view end========

        //========process dataMap for intRiskPeriod view begin========
        Map<String, Object> intRiskPeriod = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<String> intRiskPeriodMetaDataList = (List<String>)this.dao.findForList("HExportMapper.selectH800IntRiskPeriodData", queryMap);
        if(intRiskPeriodMetaDataList == null) {
            intRiskPeriodMetaDataList = new ArrayList<>();
        }
        intRiskPeriod.put("list", intRiskPeriodMetaDataList);
        intRiskPeriod.put("count", intRiskPeriodMetaDataList.size());
        //========process dataMap for intRiskPeriod view end========

        result.put("main", main);
        result.put("interestTest", interestTest);
        result.put("intestSummary", intestSummaryMetaData);
        result.put("interestDetail", interestDetail);
        result.put("intRiskPeriod", intRiskPeriod);
        result.put("interestTestOTC", interestTestOTC);
        result.put("interestTestEX", interestTestEX);
        result.put("trxcalendarData", trxcalendarData);
        result.put("hp100h800", hp100h800);
        result.put("TMPL", TMPL);
        return result;
    }

    /**
     * 处理sheet页H10000的数据
     * @author Dai Zong 2017年12月15日
     *
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getH10000Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> lastQueryMap = new HashMap<String,Object>();
        queryMap.forEach((k,v) -> {
            lastQueryMap.put(k, v);
        });
        lastQueryMap.put("period", (Integer.parseInt(String.valueOf(lastQueryMap.get("period")).substring(0, 4)) - 1) + "1231");
        Map<String, Object> result = new HashMap<String,Object>();

        //========process dataMap for note view begin========
        @SuppressWarnings("unchecked")
        Map<String,Object> noteMetaData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectHNoteAData", queryMap);
        if(noteMetaData == null) {
            noteMetaData = new HashMap<>();
        }
        //========process dataMap for note view end========

        //========process dataMap for TFA view begin========
        Map<String, Object> TFA = new HashMap<String,Object>();
        Map<String, Object> exchange = new HashMap<String,Object>();
        Map<String, Object> bank = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> tfaMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000TFAData", queryMap);
        if(tfaMetaDataList == null) {
            tfaMetaDataList = new ArrayList<>();
        }
        for(Map<String,Object> map : tfaMetaDataList) {
            if("交易所".equals(map.get("subItem"))) {
                exchange = map;
            }else if("银行间".equals(map.get("subItem"))) {
                bank = map;
            }
        }
        TFA.put("exchange", exchange);
        TFA.put("bank", bank);

        Object dataSumCheckForWP = this.dao.findForObject("HExportMapper.checkIfH10000TFAHasDataForReport", queryMap);
        TFA.put("dataSumCheck", dataSumCheckForWP == null ? 0d : dataSumCheckForWP);

//        2018-12-18 偏离度合计改为直接由Excel公式计算,废弃此处代码
//        Object diviatonEyCurrent = this.dao.findForObject("HExportMapper.selectH10000TFADiviatonSumData", queryMap);
//        Object diviatonEyLast = this.dao.findForObject("HExportMapper.selectH10000TFADiviatonSumData", lastQueryMap);
//        TFA.put("diviatonEyCurrent", diviatonEyCurrent);
//        TFA.put("diviatonEyLast", diviatonEyLast);

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H10000NotBondDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000TFANotBondDataForReport", queryMap);
        if(H10000NotBondDataList == null) {
            H10000NotBondDataList = new ArrayList<>();
        }
        Map<String,Object> assetBackedSecuritiesInvestment = new HashMap<>();
        for(Map<String,Object> map : H10000NotBondDataList) {
            if("资产支持证券投资".equals(map.get("item"))){
            	assetBackedSecuritiesInvestment = map;
            }
        }
        TFA.put("assetBackedSecuritiesInvestment", assetBackedSecuritiesInvestment);
        //========process dataMap for TFA view end========

        //========process dataMap for derivative view begin========
        Map<String, Object> derivative = new HashMap<String,Object>();
        Map<String, Object> item1 = new HashMap<String,Object>();
        Map<String, Object> item2 = new HashMap<String,Object>();
        Map<String, Object> item3 = new HashMap<String,Object>();
        Map<String, Object> item4 = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> derivativeMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000DerivativeData", queryMap);
        if(derivativeMetaDataList == null) {
            derivativeMetaDataList = new ArrayList<>();
        }
        for(Map<String,Object> map : derivativeMetaDataList) {
            if("利率衍生工具".equals(map.get("item"))) {
                item1 = map;
            }else if("货币衍生工具".equals(map.get("item"))) {
                item2 = map;
            }else if("权益衍生工具".equals(map.get("item"))) {
                item3 = map;
            }else if("其他衍生工具".equals(map.get("item"))) {
                item4 = map;
            }
        }

        //chenhy,20231221,修改衍生工具取数逻辑
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> interestList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000InterestDerivativeData", queryMap);
        if(interestList == null) {
            interestList = new ArrayList<>();
        }
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> currencyList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000CurrencyDerivativeData", queryMap);
        if(currencyList == null) {
            currencyList = new ArrayList<>();
        }
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> equityList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000EquityDerivativeData", queryMap);
        if(equityList == null) {
            equityList = new ArrayList<>();
        }
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> otherList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000OtherDerivativeData", queryMap);
        if(otherList == null) {
            otherList = new ArrayList<>();
        }
        derivative.put("item1", item1);
        derivative.put("item2", item2);
        derivative.put("item3", item3);
        derivative.put("item4", item4);
        derivative.put("derivativeMetaDataList", derivativeMetaDataList);
        derivative.put("interestCount", interestList.size());
        derivative.put("currencyCount", currencyList.size());
        derivative.put("equityCount", equityList.size());
        derivative.put("otherCount", otherList.size());
        //========process dataMap for derivative view end========

        //========process dataMap for futures view begin========
        Map<String, Object> futures = new HashMap<String,Object>();
        item1 = new HashMap<String,Object>();
        item2 = new HashMap<String,Object>();
        item3 = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> futuresMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000FuturesData", queryMap);
        if(futuresMetaDataList == null) {
            futuresMetaDataList = new ArrayList<>();
        }
        futures.put("list", futuresMetaDataList);
        futures.put("count", futuresMetaDataList.size());

        for(Map<String,Object> map : futuresMetaDataList) {
            if("股指期货合约".equals(map.get("item"))) {
                item1 = map;
            }else if("国债期货合约".equals(map.get("item"))) {
                item2 = map;
            }else if("黄金现货延期交收合约".equals(map.get("item"))) {
                item3 = map;
            }
        }
        futures.put("item1", item1);
        futures.put("item2", item2);
        futures.put("item3", item3);
        //========process dataMap for futures view end========

        //========process dataMap for rmcfs view begin========
		//---chenhy,20220902,判断新老基金----
		Map<String, Object> note = new HashMap<>();
        String note3Flag = "N";

        @SuppressWarnings("unchecked")
        Map<String,Object> fundDateInfo = (Map<String,Object>)this.dao.findForObject("TExportMapper.selectFundDateInfo", queryMap);
        // chenhy,20240223,新增基金和产品的区分
        Map<String, Object> dateInfo = this.reportService.getDateInfo(periodStr, (Date)fundDateInfo.get("dateFrom"), (Date)fundDateInfo.get("dateTo"),(Date)fundDateInfo.get("dateTransform"),(String)fundDateInfo.get("fundType"));
        if("合同生效日".equals(dateInfo.get("CURRENT_INIT_SOURCE"))) {
            note3Flag = "Y";
        }
		result.put("note3Flag", note3Flag);

        Map<String, Object> rmcfs = new HashMap<String,Object>();
        item1 = new HashMap<String,Object>();
        item2 = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> rmcfsMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000RmcfsData", queryMap);
        if(rmcfsMetaDataList == null) {
            rmcfsMetaDataList = new ArrayList<>();
        }
        for(Map<String,Object> map : rmcfsMetaDataList) {
            if("交易所市场".equals(map.get("item"))) {
                item1 = map;
            }else if("银行间市场".equals(map.get("item"))) {
                item2 = map;
            }
        }
        rmcfs.put("item1", item1);
        rmcfs.put("item2", item2);
		rmcfs.put("count", rmcfsMetaDataList.size());
		Object rmcfsdataSumCheck = this.dao.findForObject("HExportMapper.checkIfH10000RmcfsHasDataForReport", queryMap);
        rmcfs.put("rmcfsdataSumCheck", rmcfsdataSumCheck == null ? 0d : rmcfsdataSumCheck);
        //========process dataMap for rmcfs view end========

        //========process dataMap for interestDetail view begin========
        Map<String, Object> interestDetail = new HashMap<String,Object>();
        Map<String, Object> current = new HashMap<String,Object>();
        Map<String, Object> last = new HashMap<String,Object>();

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> currentInterestDetailMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000InterestDetailData", queryMap);
        if(currentInterestDetailMetaDataList == null) {
            currentInterestDetailMetaDataList = new ArrayList<>();
        }
        current.put("list", currentInterestDetailMetaDataList);
        current.put("count", currentInterestDetailMetaDataList.size());

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> lastInterestDetailMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000InterestDetailData", lastQueryMap);
        if(lastInterestDetailMetaDataList == null) {
            lastInterestDetailMetaDataList = new ArrayList<>();
        }

        SimpleDateFormat sdfin = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfout = new SimpleDateFormat("yyyy年MM月dd日");
        String returnDays = currentInterestDetailMetaDataList.stream().map(item -> {
            return String.valueOf(item.get("returnDay"));
        }).filter(item -> {
            return !"null".equals(item) && !StringUtils.EMPTY.equals(item);
        }).map(item -> {
            try {
                return sdfout.format(sdfin.parse(item));
            } catch (ParseException e) {
                return StringUtils.EMPTY;
            }
        }).distinct().collect(Collectors.joining("、"));

        last.put("list", lastInterestDetailMetaDataList);
        last.put("count", lastInterestDetailMetaDataList.size());

        interestDetail.put("current", current);
        interestDetail.put("last", last);
        interestDetail.put("returnDays", returnDays);
        //========process dataMap for interestDetail view end========

        //========process dataMap for threeLevel view begin========
        //========20220629新增========
        Map<String,Object> H10000 = new HashMap<>();
        Map<String,Object> threeLevel = new HashMap<>();

        @SuppressWarnings("unchecked")
        Map<String,Object> H10000ThreeLevelCurrentData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectH10000ThreeLevelData", queryMap);
        if(H10000ThreeLevelCurrentData == null) {
            H10000ThreeLevelCurrentData = new HashMap<>();
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> H10000ThreeLevelLastData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectH10000ThreeLevelData", lastQueryMap);
        if(H10000ThreeLevelLastData == null) {
            H10000ThreeLevelLastData = new HashMap<>();
        }
        threeLevel.put("current", H10000ThreeLevelCurrentData);
        threeLevel.put("last", H10000ThreeLevelLastData);

        //========process dataMap for threeLevel view end========

        //--------------------↓H10000.three_level_measurement_of_unobservable_input_values↓--------------------
        Map<String, Object> threeLevelMeasure0fUnobservableInput = new HashMap<>();

        Object weightedAvg = this.dao.findForObject("HExportMapper.selecteylomdthreeLevelWeightValues", queryMap);
        Object weightedAvgLast = this.dao.findForObject("HExportMapper.selecteylomdthreeLevelWeightValuesLast", lastQueryMap);

        threeLevelMeasure0fUnobservableInput.put("weightedAvg", weightedAvg);
        threeLevelMeasure0fUnobservableInput.put("weightedAvgLast", weightedAvgLast);


        //--------------------↑H10000.three_level_measurement_of_unobservable_input_values↑--------------------

        //=======20220620新增MAC========
        //========process dataMap for MAC view begin========
        Map<String, Object> MAC = new HashMap<String,Object>();
        Map<String, Object> macexchange = new HashMap<String,Object>();
        Map<String, Object> macbank = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> macMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000MACData", queryMap);
        if(macMetaDataList == null) {
            macMetaDataList = new ArrayList<>();
        }
        for(Map<String,Object> map : macMetaDataList) {
            if("交易所".equals(map.get("subItem"))) {
                macexchange = map;
            }else if("银行间".equals(map.get("subItem"))) {
                macbank = map;
            }
        }
        MAC.put("exchange", macexchange);
        MAC.put("bank", macbank);


        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H10000MACNotBondDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000MacABSDataForReport", queryMap);
        if(H10000MACNotBondDataList == null) {
            H10000MACNotBondDataList = new ArrayList<>();
        }
        Map<String,Object> ABS = new HashMap<>();
        Map<String,Object> other = new HashMap<>();
        for(Map<String,Object> map : H10000MACNotBondDataList) {
            if("资产支持证券投资".equals(map.get("item"))){
            	ABS = map;
            }else if("其他".equals(map.get("item"))) {
                other = map;
            }
        }
        MAC.put("ABS", ABS);
        MAC.put("other", other);

        Object macdataSumCheck = this.dao.findForObject("HExportMapper.checkIfH10000MACHasDataForReport", queryMap);
        MAC.put("macdataSumCheck", macdataSumCheck == null ? 0d : macdataSumCheck);
        //========process dataMap for MAC view end========
        //=======20220620新增MACBad========
        //========process dataMap for MAC view begin========
        Map<String, Object> MACBad = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> MacBadDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000MACBad", queryMap);
        if(MacBadDataList == null) {
            MacBadDataList = new ArrayList<>();
        }
        Map<String,Object> temp = new HashMap<>();
        for(Map<String,Object> map : MacBadDataList) {
            temp.put(String.valueOf(map.get("item")), map);
        }
        MACBad.put("item1", temp.get("期初余额")==null?new HashMap<String,Object>():temp.get("期初余额"));
        MACBad.put("item2", temp.get("本期从其他阶段转入")==null?new HashMap<String,Object>():temp.get("本期从其他阶段转入"));
        MACBad.put("item3", temp.get("本期转出至其他阶段")==null?new HashMap<String,Object>():temp.get("本期转出至其他阶段"));
        MACBad.put("item4", temp.get("本期新增")==null?new HashMap<String,Object>():temp.get("本期新增"));
        MACBad.put("item5", temp.get("本期转回")==null?new HashMap<String,Object>():temp.get("本期转回"));
        MACBad.put("item6", temp.get("其他变动")==null?new HashMap<String,Object>():temp.get("其他变动"));
        MACBad.put("item7", temp.get("期末余额")==null?new HashMap<String,Object>():temp.get("期末余额"));

        //========process dataMap for MACBad view end========

        //========process dataMap for threelevelchange view begin========
        //========20220629新增========
        Map<String, Object> threelevelchange = new HashMap<>();
        Map<String,Object> BOND = new HashMap<>();
        Map<String,Object> STOCK = new HashMap<>();
        Map<String,Object> REPO = new HashMap<>();
        Map<String,Object> WARRANT = new HashMap<>();
        Map<String,Object> FUND = new HashMap<>();
        Map<String,Object> TOTAL = new HashMap<>();
        item1 = new HashMap<>();
        item2 = new HashMap<>();
        item3 = new HashMap<>();
        item4 = new HashMap<>();
        Map<String,Object> item5 = new HashMap<>();
        Map<String,Object> item6 = new HashMap<>();
        Map<String,Object> item7 = new HashMap<>();
        Map<String,Object> item8 = new HashMap<>();
        Map<String,Object> item9 = new HashMap<>();
        Map<String,Object> item10 = new HashMap<>();

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H10000ThreeLevelChangeDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000ThreeLevelChangeData", queryMap);
        if(H10000ThreeLevelChangeDataList == null) {
            H10000ThreeLevelChangeDataList = new ArrayList<>();
        }
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H10000ThreeLevelChangeDataTypeHasDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.checkIfH10000ThreeLevelChangeHasDataForWP", queryMap);
        if(H10000ThreeLevelChangeDataTypeHasDataList == null) {
            H10000ThreeLevelChangeDataTypeHasDataList = new ArrayList<>();
        }
        //20220628 irenewu修改
        for(Map<String,Object> map : H10000ThreeLevelChangeDataTypeHasDataList) {
            if("BOND".equals(map.get("TYPE"))) {
                item1 = map;
            }else if("STOCK".equals(map.get("TYPE"))){
                item2 = map;
            }else if("REPO".equals(map.get("TYPE"))){
                item3 = map;
            }else if("WARRANT".equals(map.get("TYPE"))){
                item4 = map;
            }else if("FUND".equals(map.get("TYPE"))){
                item5 = map;
            }else if("TOTAL".equals(map.get("TYPE"))){
                item6 = map;
            }
        }
        BOND.put("sum",item1);
        STOCK.put("sum", item2);
        REPO.put("sum", item3);
        WARRANT.put("sum", item4);
        FUND.put("sum", item5);
        TOTAL.put("sum", item6);


        //20220628 irenewu修改

        item1 = new HashMap<>();
        item2 = new HashMap<>();
        item3 = new HashMap<>();
        item4 = new HashMap<>();
        item5 = new HashMap<>();
        item6 = new HashMap<>();
        item7 = new HashMap<>();
        item8 = new HashMap<>();
        item9 = new HashMap<>();
        item10 = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H10000ThreeLevelChangeBondDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000ThreeLevelChangeBondData", queryMap);
        if(H10000ThreeLevelChangeBondDataList == null) {
            H10000ThreeLevelChangeBondDataList = new ArrayList<>();
        }
        for(Map<String,Object> map : H10000ThreeLevelChangeBondDataList) {
            if("1".equals(map.get("SORT")) || "期初余额".equals(map.get("ITEM"))) {
                item1 = map;
            }else if("2".equals(map.get("SORT"))|| "当期购买".equals(map.get("ITEM"))){
                item2 = map;
            }else if("3".equals(map.get("SORT"))|| "当期出售/结算".equals(map.get("ITEM"))){
                item3 = map;
            }else if("4".equals(map.get("SORT"))|| "转入第三层次".equals(map.get("ITEM"))){
                item4 = map;
            }else if("5".equals(map.get("SORT"))|| "转出第三层次".equals(map.get("ITEM"))){
                item5 = map;
            }else if("6".equals(map.get("SORT"))|| "当期利得或损失总额".equals(map.get("ITEM"))){
                item6 = map;
            }else if("7".equals(map.get("SORT"))|| "其中：计入损益的利得或损失".equals(map.get("ITEM"))){
                item7 = map;
            }else if("8".equals(map.get("SORT"))|| "计入其他综合收益的利得或损失".equals(map.get("ITEM"))){
                item8 = map;
            }else if("9".equals(map.get("SORT"))|| "期末余额".equals(map.get("ITEM"))){
                item9 = map;
            }else if("10".equals(map.get("SORT"))|| "期末仍持有的第三层次金融资产计入本期损益的未实现利得或损失的变动——公允价值变动损益".equals(map.get("ITEM"))){
                item10 = map;
            }
        }
        BOND.put("item1",item1);
        BOND.put("item2",item2);
        BOND.put("item3",item3);
        BOND.put("item4",item4);
        BOND.put("item5",item5);
        BOND.put("item6",item6);
        BOND.put("item7",item7);
        BOND.put("item8",item8);
        BOND.put("item9",item9);
        BOND.put("item10",item10);

        item1 = new HashMap<>();
        item2 = new HashMap<>();
        item3 = new HashMap<>();
        item4 = new HashMap<>();
        item5 = new HashMap<>();
        item6 = new HashMap<>();
        item7 = new HashMap<>();
        item8 = new HashMap<>();
        item9 = new HashMap<>();
        item10 = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H10000ThreeLevelChangeStockDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000ThreeLevelChangeStockData", queryMap);
        if(H10000ThreeLevelChangeStockDataList == null) {
            H10000ThreeLevelChangeStockDataList = new ArrayList<>();
        }
        for(Map<String,Object> map : H10000ThreeLevelChangeStockDataList) {
            if("1".equals(map.get("SORT")) || "期初余额".equals(map.get("ITEM"))) {
                item1 = map;
            }else if("2".equals(map.get("SORT"))|| "当期购买".equals(map.get("ITEM"))){
                item2 = map;
            }else if("3".equals(map.get("SORT"))|| "当期出售/结算".equals(map.get("ITEM"))){
                item3 = map;
            }else if("4".equals(map.get("SORT"))|| "转入第三层次".equals(map.get("ITEM"))){
                item4 = map;
            }else if("5".equals(map.get("SORT"))|| "转出第三层次".equals(map.get("ITEM"))){
                item5 = map;
            }else if("6".equals(map.get("SORT"))|| "当期利得或损失总额".equals(map.get("ITEM"))){
                item6 = map;
            }else if("7".equals(map.get("SORT"))|| "其中：计入损益的利得或损失".equals(map.get("ITEM"))){
                item7 = map;
            }else if("8".equals(map.get("SORT"))|| "计入其他综合收益的利得或损失".equals(map.get("ITEM"))){
                item8 = map;
            }else if("9".equals(map.get("SORT"))|| "期末余额".equals(map.get("ITEM"))){
                item9 = map;
            }else if("10".equals(map.get("SORT"))|| "期末仍持有的第三层次金融资产计入本期损益的未实现利得或损失的变动——公允价值变动损益".equals(map.get("ITEM"))){
                item10 = map;
            }
        }
        STOCK.put("item1",item1);
        STOCK.put("item2",item2);
        STOCK.put("item3",item3);
        STOCK.put("item4",item4);
        STOCK.put("item5",item5);
        STOCK.put("item6",item6);
        STOCK.put("item7",item7);
        STOCK.put("item8",item8);
        STOCK.put("item9",item9);
        STOCK.put("item10",item10);

        item1 = new HashMap<>();
        item2 = new HashMap<>();
        item3 = new HashMap<>();
        item4 = new HashMap<>();
        item5 = new HashMap<>();
        item6 = new HashMap<>();
        item7 = new HashMap<>();
        item8 = new HashMap<>();
        item9 = new HashMap<>();
        item10 = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H10000ThreeLevelChangeRepoDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000ThreeLevelChangeRepoData", queryMap);
        if(H10000ThreeLevelChangeRepoDataList == null) {
            H10000ThreeLevelChangeRepoDataList = new ArrayList<>();
        }
        for(Map<String,Object> map : H10000ThreeLevelChangeRepoDataList) {
            if("1".equals(map.get("SORT")) || "期初余额".equals(map.get("ITEM"))) {
                item1 = map;
            }else if("2".equals(map.get("SORT"))|| "当期购买".equals(map.get("ITEM"))){
                item2 = map;
            }else if("3".equals(map.get("SORT"))|| "当期出售/结算".equals(map.get("ITEM"))){
                item3 = map;
            }else if("4".equals(map.get("SORT"))|| "转入第三层次".equals(map.get("ITEM"))){
                item4 = map;
            }else if("5".equals(map.get("SORT"))|| "转出第三层次".equals(map.get("ITEM"))){
                item5 = map;
            }else if("6".equals(map.get("SORT"))|| "当期利得或损失总额".equals(map.get("ITEM"))){
                item6 = map;
            }else if("7".equals(map.get("SORT"))|| "其中：计入损益的利得或损失".equals(map.get("ITEM"))){
                item7 = map;
            }else if("8".equals(map.get("SORT"))|| "计入其他综合收益的利得或损失".equals(map.get("ITEM"))){
                item8 = map;
            }else if("9".equals(map.get("SORT"))|| "期末余额".equals(map.get("ITEM"))){
                item9 = map;
            }else if("10".equals(map.get("SORT"))|| "期末仍持有的第三层次金融资产计入本期损益的未实现利得或损失的变动——公允价值变动损益".equals(map.get("ITEM"))){
                item10 = map;
            }
        }
        REPO.put("item1",item1);
        REPO.put("item2",item2);
        REPO.put("item3",item3);
        REPO.put("item4",item4);
        REPO.put("item5",item5);
        REPO.put("item6",item6);
        REPO.put("item7",item7);
        REPO.put("item8",item8);
        REPO.put("item9",item9);
        REPO.put("item10",item10);

        item1 = new HashMap<>();
        item2 = new HashMap<>();
        item3 = new HashMap<>();
        item4 = new HashMap<>();
        item5 = new HashMap<>();
        item6 = new HashMap<>();
        item7 = new HashMap<>();
        item8 = new HashMap<>();
        item9 = new HashMap<>();
        item10 = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H10000ThreeLevelChangeWarrantDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000ThreeLevelChangeWarrantData", queryMap);
        if(H10000ThreeLevelChangeWarrantDataList == null) {
            H10000ThreeLevelChangeWarrantDataList = new ArrayList<>();
        }
        for(Map<String,Object> map : H10000ThreeLevelChangeWarrantDataList) {
            if("1".equals(map.get("SORT")) || "期初余额".equals(map.get("ITEM"))) {
                item1 = map;
            }else if("2".equals(map.get("SORT"))|| "当期购买".equals(map.get("ITEM"))){
                item2 = map;
            }else if("3".equals(map.get("SORT"))|| "当期出售/结算".equals(map.get("ITEM"))){
                item3 = map;
            }else if("4".equals(map.get("SORT"))|| "转入第三层次".equals(map.get("ITEM"))){
                item4 = map;
            }else if("5".equals(map.get("SORT"))|| "转出第三层次".equals(map.get("ITEM"))){
                item5 = map;
            }else if("6".equals(map.get("SORT"))|| "当期利得或损失总额".equals(map.get("ITEM"))){
                item6 = map;
            }else if("7".equals(map.get("SORT"))|| "其中：计入损益的利得或损失".equals(map.get("ITEM"))){
                item7 = map;
            }else if("8".equals(map.get("SORT"))|| "计入其他综合收益的利得或损失".equals(map.get("ITEM"))){
                item8 = map;
            }else if("9".equals(map.get("SORT"))|| "期末余额".equals(map.get("ITEM"))){
                item9 = map;
            }else if("10".equals(map.get("SORT"))|| "期末仍持有的第三层次金融资产计入本期损益的未实现利得或损失的变动——公允价值变动损益".equals(map.get("ITEM"))){
                item10 = map;
            }
        }
        WARRANT.put("item1",item1);
        WARRANT.put("item2",item2);
        WARRANT.put("item3",item3);
        WARRANT.put("item4",item4);
        WARRANT.put("item5",item5);
        WARRANT.put("item6",item6);
        WARRANT.put("item7",item7);
        WARRANT.put("item8",item8);
        WARRANT.put("item9",item9);
        WARRANT.put("item10",item10);

        item1 = new HashMap<>();
        item2 = new HashMap<>();
        item3 = new HashMap<>();
        item4 = new HashMap<>();
        item5 = new HashMap<>();
        item6 = new HashMap<>();
        item7 = new HashMap<>();
        item8 = new HashMap<>();
        item9 = new HashMap<>();
        item10 = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H10000ThreeLevelChangeFundDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000ThreeLevelChangeFundData", queryMap);
        if(H10000ThreeLevelChangeFundDataList == null) {
            H10000ThreeLevelChangeFundDataList = new ArrayList<>();
        }
        for(Map<String,Object> map : H10000ThreeLevelChangeFundDataList) {
            if("1".equals(map.get("SORT")) || "期初余额".equals(map.get("ITEM"))) {
                item1 = map;
            }else if("2".equals(map.get("SORT"))|| "当期购买".equals(map.get("ITEM"))){
                item2 = map;
            }else if("3".equals(map.get("SORT"))|| "当期出售/结算".equals(map.get("ITEM"))){
                item3 = map;
            }else if("4".equals(map.get("SORT"))|| "转入第三层次".equals(map.get("ITEM"))){
                item4 = map;
            }else if("5".equals(map.get("SORT"))|| "转出第三层次".equals(map.get("ITEM"))){
                item5 = map;
            }else if("6".equals(map.get("SORT"))|| "当期利得或损失总额".equals(map.get("ITEM"))){
                item6 = map;
            }else if("7".equals(map.get("SORT"))|| "其中：计入损益的利得或损失".equals(map.get("ITEM"))){
                item7 = map;
            }else if("8".equals(map.get("SORT"))|| "计入其他综合收益的利得或损失".equals(map.get("ITEM"))){
                item8 = map;
            }else if("9".equals(map.get("SORT"))|| "期末余额".equals(map.get("ITEM"))){
                item9 = map;
            }else if("10".equals(map.get("SORT"))|| "期末仍持有的第三层次金融资产计入本期损益的未实现利得或损失的变动——公允价值变动损益".equals(map.get("ITEM"))){
                item10 = map;
            }
        }
        FUND.put("item1",item1);
        FUND.put("item2",item2);
        FUND.put("item3",item3);
        FUND.put("item4",item4);
        FUND.put("item5",item5);
        FUND.put("item6",item6);
        FUND.put("item7",item7);
        FUND.put("item8",item8);
        FUND.put("item9",item9);
        FUND.put("item10",item10);

        item1 = new HashMap<>();
        item2 = new HashMap<>();
        item3 = new HashMap<>();
        item4 = new HashMap<>();
        item5 = new HashMap<>();
        item6 = new HashMap<>();
        item7 = new HashMap<>();
        item8 = new HashMap<>();
        item9 = new HashMap<>();
        item10 = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> H10000ThreeLevelChangeTotalDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH10000ThreeLevelChangeTotalData", queryMap);
        if(H10000ThreeLevelChangeTotalDataList == null) {
            H10000ThreeLevelChangeTotalDataList = new ArrayList<>();
        }
        for(Map<String,Object> map : H10000ThreeLevelChangeTotalDataList) {
            if("1".equals(map.get("SORT")) || "期初余额".equals(map.get("ITEM"))) {
                item1 = map;
            }else if("2".equals(map.get("SORT"))|| "当期购买".equals(map.get("ITEM"))){
                item2 = map;
            }else if("3".equals(map.get("SORT"))|| "当期出售/结算".equals(map.get("ITEM"))){
                item3 = map;
            }else if("4".equals(map.get("SORT"))|| "转入第三层次".equals(map.get("ITEM"))){
                item4 = map;
            }else if("5".equals(map.get("SORT"))|| "转出第三层次".equals(map.get("ITEM"))){
                item5 = map;
            }else if("6".equals(map.get("SORT"))|| "当期利得或损失总额".equals(map.get("ITEM"))){
                item6 = map;
            }else if("7".equals(map.get("SORT"))|| "其中：计入损益的利得或损失".equals(map.get("ITEM"))){
                item7 = map;
            }else if("8".equals(map.get("SORT"))|| "计入其他综合收益的利得或损失".equals(map.get("ITEM"))){
                item8 = map;
            }else if("9".equals(map.get("SORT"))|| "期末余额".equals(map.get("ITEM"))){
                item9 = map;
            }else if("10".equals(map.get("SORT"))|| "期末仍持有的第三层次金融资产计入本期损益的未实现利得或损失的变动——公允价值变动损益".equals(map.get("ITEM"))){
                item10 = map;
            }
        }
        TOTAL.put("item1",item1);
        TOTAL.put("item2",item2);
        TOTAL.put("item3",item3);
        TOTAL.put("item4",item4);
        TOTAL.put("item5",item5);
        TOTAL.put("item6",item6);
        TOTAL.put("item7",item7);
        TOTAL.put("item8",item8);
        TOTAL.put("item9",item9);
        TOTAL.put("item10",item10);

        @SuppressWarnings("unchecked")
        Map<String,Object> H10000ThreeLevelChangeSumData = (Map<String,Object>)this.dao.findForObject("HExportMapper.selectH10000ThreeLevelChangeSumDataForReport", queryMap);
        if(H10000ThreeLevelChangeSumData == null) {
            H10000ThreeLevelChangeSumData = new HashMap<>();
        }

        threelevelchange.put("list", H10000ThreeLevelChangeDataList);
        threelevelchange.put("count", H10000ThreeLevelChangeDataList.size());
        threelevelchange.put("sum", H10000ThreeLevelChangeSumData);
        threelevelchange.put("BOND",BOND);
        threelevelchange.put("STOCK", STOCK);
        threelevelchange.put("REPO", REPO);
        threelevelchange.put("WARRANT", WARRANT);
        threelevelchange.put("FUND", FUND);
        threelevelchange.put("TOTAL", TOTAL);
        Object dataSumCheck = this.dao.findForObject("HExportMapper.checkIfH10000ThreeLevelChangeHasDataForReport", queryMap);
        threelevelchange.put("dataSumCheck", dataSumCheck == null ? 0d : dataSumCheck);
        //========process dataMap for threeLevelchange view end========


        result.put("note", noteMetaData);
        result.put("TFA", TFA);
        result.put("derivative", derivative);
        result.put("futures", futures);
        result.put("rmcfs", rmcfs);
        result.put("interestDetail", interestDetail);
        result.put("MAC", MAC);  //20220620新增mac债权投资
        result.put("MACBad", MACBad);  //20220620新增mac债权投资
        result.put("threeLevel", threeLevel);//20220629新增三层次
        result.put("threelevelchange", threelevelchange);//20220629新增三层次变动
        result.put("threeLevelMeasure0fUnobservableInput", threeLevelMeasure0fUnobservableInput);//20240227 新增
        return result;
    }

    /**
     * 处理sheet页H11000的数据
     * @author Dai Zong 2017年12月16日
     *
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getH11000Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();
        Map<String, Object> lastQueryMap = this.createBaseQueryMap(fundId, this.getLastPeriodStr(periodStr));

        //========process dataMap for addition view begin========
        // Map<String, Object> additian = new HashMap<String,Object>();
        // @SuppressWarnings("unchecked")
        // List<Map<String,Object>> additianMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH11000AdditianData", queryMap);
        // if(additianMetaDataList == null) {
        //     additianMetaDataList = new ArrayList<>();
        // }
        // additian.put("list", additianMetaDataList);
        // additian.put("count", additianMetaDataList.size());

        // yury, 20200831, H11000拆分流通受限股票和债券
        Map<String, Object> additian = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> additianMetaDataStockList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH11000AdditianDataStock", queryMap);
        if(additianMetaDataStockList == null) {
            additianMetaDataStockList = new ArrayList<>();
        }
        additian.put("stocklist", additianMetaDataStockList);
        additian.put("stockcount", additianMetaDataStockList.size());

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> additianMetaDataBondList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH11000AdditianDataBond", queryMap);
        if(additianMetaDataBondList == null) {
            additianMetaDataBondList = new ArrayList<>();
        }
        additian.put("bondlist", additianMetaDataBondList);
        additian.put("bondcount", additianMetaDataBondList.size());

        //========process dataMap for addition view end========

        //========process dataMap for suspension view begin========
        Map<String, Object> suspension = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> suspensionMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH11000SuspensionData", queryMap);
        if(suspensionMetaDataList == null) {
            suspensionMetaDataList = new ArrayList<>();
        }
        suspension.put("list", suspensionMetaDataList);
        suspension.put("count", suspensionMetaDataList.size());
        //========process dataMap for suspension view end========

        //========process dataMap for saleIn view begin========
        Map<String, Object> saleIn = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> saleInMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectH11000SaleInData", queryMap);
        if(saleInMetaDataList == null) {
            saleInMetaDataList = new ArrayList<>();
        }
        saleIn.put("list", saleInMetaDataList);
        saleIn.put("count", saleInMetaDataList.size());
        //========process dataMap for saleIn view end========

        //========process dataMap for noteDates view begin========
        @SuppressWarnings("unchecked")
        List<String> noteDataList = (List<String>)this.dao.findForList("HExportMapper.selectH11000NoteData", queryMap);
        if(noteDataList == null) {
            noteDataList = new ArrayList<>();
        }
        SimpleDateFormat sdfin = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfout = new SimpleDateFormat("yyyy年MM月dd日");
        String noteDates = noteDataList.stream().filter(item -> {
            return item != null  && !StringUtils.EMPTY.equals(item);
        }).map(item -> {
            try {
                return sdfout.format(sdfin.parse(item));
            } catch (ParseException e) {
                return StringUtils.EMPTY;
            }
        }).distinct().collect(Collectors.joining("、"));
        //========process dataMap for noteDates view end========
        //--------------------selectIRefinancingData↓--------------------
        Map<String, Object> IRefinancingData = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> IRefinancingDataList = (List<Map<String,Object>>)this.dao.findForList("HExportMapper.selectIRefinancingData", queryMap);
        if(IRefinancingDataList == null) {
            IRefinancingDataList = new ArrayList<>();
        }

        IRefinancingData.put("list", IRefinancingDataList);
        IRefinancingData.put("Count", IRefinancingDataList.size());
        //--------------------selectIRefinancingData↑--------------------

        result.put("additian", additian);
        result.put("suspension", suspension);
        result.put("saleIn", saleIn);
        result.put("noteDates", noteDates);
        result.put("IRefinancingData", IRefinancingData);
        return result;
    }

}
