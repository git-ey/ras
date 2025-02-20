package com.ey.service.wp.output.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.ey.service.wp.output.PExportManager;
import com.ey.util.fileexport.Constants;
import com.ey.util.fileexport.FileExportUtils;
import com.ey.util.fileexport.FreeMarkerUtils;

/**
 * @name PExportService
 * @description 底稿输出服务--P
 * @author Dai Zong	2017年9月19日
 */
@Service("pExportService")
public class PExportService extends BaseExportService implements PExportManager{

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
        Map<String, Object> dataMap = new HashMap<>();

        Long period = Long.parseLong(periodStr.substring(0, 4));
        Long month = Long.parseLong(periodStr.substring(4, 6));
        Long day = Long.parseLong(periodStr.substring(6, 8));

        dataMap.put("period", period);
        dataMap.put("month", month);
        dataMap.put("day", day);
        dataMap.put("fundInfo", fundInfo);
        dataMap.put("extraFundInfo", this.getExtraFundInfo(fundId, periodStr));

        dataMap.put("P", this.getPData(fundId, periodStr));
        dataMap.put("P300", this.getP300Data(fundId, periodStr));
        dataMap.put("P400", this.getP400Data(fundId, periodStr));
        dataMap.put("P500", this.getP500Data(fundId, periodStr));
        dataMap.put("P600", this.getP600Data(fundId, periodStr));
        dataMap.put("P800", this.getP800Data(fundId, periodStr));
        dataMap.put("P810", this.getP810Data(fundId, periodStr));
        dataMap.put("P900", this.getP900Data(fundId, periodStr));
        dataMap.put("P10000", this.getP10000Data(fundId, periodStr));

        return FreeMarkerUtils.processTemplateToStrUseAbsPath(dataMap, templatePath, Constants.EXPORT_TEMPLATE_FILE_NAME_P);
    }

	@Override
    public boolean doExport(HttpServletRequest request, HttpServletResponse response, String fundId, String periodStr, String templatePath) throws Exception {
	    Map<String, String> fundInfo = this.selectFundInfo(fundId);
        fundInfo.put("periodStr", periodStr);
        String xmlStr = this.generateFileContent(fundId, periodStr, fundInfo, templatePath);
        FileExportUtils.writeFileToHttpResponse(request, response, FreeMarkerUtils.simpleReplace(Constants.EXPORT_AIM_FILE_NAME_P, fundInfo), xmlStr);
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
     * 处理sheet页P的数据
     * @author Dai Zong 2017年9月19日
     *
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getPData(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<>();

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> PMetaDataList = (List<Map<String,Object>>)this.dao.findForList("PExportMapper.selectPData", queryMap);
        if(PMetaDataList == null) {
            PMetaDataList = new ArrayList<Map<String,Object>>();
        }

        Map<String,Object> temp = new HashMap<>();
        for(Map<String,Object> map : PMetaDataList) {
            if("3003".equals(String.valueOf(map.get("accountNum")))) {
                if("可退替代款".equals(String.valueOf(map.get("revealItem")))) {
                    temp.put(String.valueOf(map.get("accountNum")) + "c", map);
                }else if ("应退替代款".equals(String.valueOf(map.get("revealItem")))) {
                    temp.put(String.valueOf(map.get("accountNum")) + "s", map);
                }
            }else {
                temp.put(String.valueOf(map.get("accountNum")), map);
            }
        }

        result.put("KM2241", temp.get("2241")==null?new HashMap<>():temp.get("2241"));
        result.put("KM2501", temp.get("2501")==null?new HashMap<>():temp.get("2501"));
        result.put("KM2204", temp.get("2204")==null?new HashMap<>():temp.get("2204"));
        result.put("KM2209", temp.get("2209")==null?new HashMap<>():temp.get("2209"));
        result.put("KM2202", temp.get("2202")==null?new HashMap<>():temp.get("2202"));
        result.put("KM3003Can", temp.get("3003c")==null?new HashMap<>():temp.get("3003c"));
        result.put("KM3003Should", temp.get("3003s")==null?new HashMap<>():temp.get("3003s"));

        String P3003Flag = (String)this.dao.findForObject("PExportMapper.selectP3003Flag", queryMap);
        result.put("P3003Flag", P3003Flag);

        return result;
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
        Map<String,Object> fundInfo = (Map<String,Object>)this.dao.findForObject("PExportMapper.selectExtraFundInfo", queryMap);
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
     * 处理sheet页P300的数据
     * @author Dai Zong 2017年9月19日
     *
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getP300Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<>();

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> P300MetaDataList = (List<Map<String,Object>>)this.dao.findForList("PExportMapper.selectP300Data", queryMap);
        if(P300MetaDataList == null) {
            P300MetaDataList = new ArrayList<Map<String,Object>>();
        }

        for(Map<String, Object> map : P300MetaDataList) {
            if("应付深圳交易所席位保证金".equals(String.valueOf(map.get("detailName")))
                    &&(Double.parseDouble(String.valueOf(map.get("drAmount"))) != 0D || Double.parseDouble(String.valueOf(map.get("crAmount"))) != 0D)) {
                map.put("hasNote", "Y");
            }else {
                map.put("hasNote", "N");
            }
        }

        result.put("list", P300MetaDataList);
        result.put("count", P300MetaDataList.size());

        return result;
    }

    /**
     * 处理sheet页P400的数据
     * @author Dai Zong 2017年9月21日
     *
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getP400Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<>();

        //========process dataMap for main view begin========
        Map<String, Object> main = new HashMap<>();

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> P400MainMetaDataList = (List<Map<String,Object>>)this.dao.findForList("PExportMapper.selectP400MainData", queryMap);
        if(P400MainMetaDataList == null) {
            P400MainMetaDataList = new ArrayList<Map<String,Object>>();
        }

        for(Map<String, Object> map : P400MainMetaDataList) {
            if("审计费".equals(map.get("detailName")) || "信息披露费".equals(map.get("detailName"))) {
                map.put("hasV", "Y");
            }else {
                map.put("hasV", "N");
            }
        }

        main.put("list", P400MainMetaDataList);
        main.put("count", P400MainMetaDataList.size());

        result.put("main", main);
        //========process dataMap for main view end========

        //========process dataMap for summary view begin========
//        Map<String, Object> summary = new HashMap<>();
//
//        Map<String, Object> annualFee4Listing = new HashMap<>();
//        Map<String, Object> auditFee = new HashMap<>();
//        Map<String, Object> subtotal = new HashMap<>();
//
//        @SuppressWarnings("unchecked")
//        List<Map<String,Object>> P400SummaryMetaDataList = (List<Map<String,Object>>)this.dao.findForList("PExportMapper.selectP400SummaryData", queryMap);
//        if(P400SummaryMetaDataList == null) {
//            P400SummaryMetaDataList = new ArrayList<Map<String,Object>>();
//        }
//
//        for(Map<String, Object> map : P400SummaryMetaDataList) {
//            if("上市年费".equals(map.get("item"))) {
//                annualFee4Listing = map;
//            }else if("审计费用".equals(map.get("item"))) {
//                auditFee = map;
//            }else if ("信息披露费".equals(map.get("item"))) {
//                subtotal = map;
//            }
//        }
//
//        summary.put("annualFee4Listing", annualFee4Listing);
//        summary.put("auditFee", auditFee);
//        summary.put("subtotal", subtotal);
//
//        result.put("summary", summary);
        //========process dataMap for summary view end========
        //========process dataMap for detail view end========
//        Map<String, Object> detail = new HashMap<>();
//
//
//        @SuppressWarnings("unchecked")
//        List<Map<String,Object>> P400DetailMetaDataList = (List<Map<String,Object>>)this.dao.findForList("PExportMapper.selectP400DetailData", queryMap);
//        if(P400DetailMetaDataList == null) {
//            P400DetailMetaDataList = new ArrayList<Map<String,Object>>();
//        }
//
//        detail.put("list", P400DetailMetaDataList);
//        detail.put("count", P400DetailMetaDataList.size());
//
//        result.put("detail", detail);
        //========process dataMap for detail view end========

        return result;
    }

    /**
     * 处理sheet页P500的数据
     * @author Dai Zong 2017年9月19日
     *
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getP500Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<>();

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> P500MetaDataList = (List<Map<String,Object>>)this.dao.findForList("PExportMapper.selectP500Data", queryMap);
        if(P500MetaDataList == null) {
            P500MetaDataList = new ArrayList<Map<String,Object>>();
        }

        result.put("list", P500MetaDataList);
        result.put("count", P500MetaDataList.size());

        return result;
    }

    /**
     * 处理sheet页P600的数据
     * @author Dai Zong 2017年9月19日
     *
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getP600Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<>();
        //========process dataMap for main view begin========
        Map<String, Object> main = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> P600MainMetaDataList = (List<Map<String,Object>>)this.dao.findForList("PExportMapper.selectP600Data", queryMap);
        if(P600MainMetaDataList == null) {
            P600MainMetaDataList = new ArrayList<Map<String,Object>>();
        }
        main.put("list", P600MainMetaDataList);
        main.put("count", P600MainMetaDataList.size());
        //========process dataMap for main view end========

        //========process dataMap for test view begin========
        Map<String, Object> test = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> P600TestMetaDataList = (List<Map<String,Object>>)this.dao.findForList("PExportMapper.selectP600TestData", queryMap);
        if(P600TestMetaDataList == null) {
            P600TestMetaDataList = new ArrayList<Map<String,Object>>();
        }
        test.put("list", P600TestMetaDataList);
        test.put("count", P600TestMetaDataList.size());
        //========process dataMap for test view end========

        Map<String, Object> testOTC = new HashMap<>();
        List<Map<String, Object>> P600TestOTCMetaDataList = (List<Map<String, Object>>)this.dao.findForList("PExportMapper.selectP600TestOTCData", queryMap);
        if (P600TestOTCMetaDataList == null) {
            P600TestOTCMetaDataList =new ArrayList<Map<String,Object>>();
        }
        testOTC.put("list", P600TestOTCMetaDataList);
        testOTC.put("count", Integer.valueOf(P600TestOTCMetaDataList.size()));
        Map<String, Object> testEX = new HashMap<>();
        List<Map<String, Object>> P600TestEXMetaDataList = (List<Map<String, Object>>)this.dao.findForList("PExportMapper.selectP600TestEXData", queryMap);
        if (P600TestEXMetaDataList == null) {
            P600TestEXMetaDataList = new ArrayList<Map<String,Object>>();
        }
        testEX.put("list", P600TestEXMetaDataList);
        testEX.put("count", Integer.valueOf(P600TestEXMetaDataList.size()));
        Map<String, Object> hp100p600 = new HashMap<>();
        List<Map<String, Object>> hp100p600DataList = (List<Map<String, Object>>)this.dao.findForList("PExportMapper.selectHP100P600Data", queryMap);
        if (hp100p600DataList == null){
            hp100p600DataList = new ArrayList<Map<String,Object>>();
        }
        hp100p600.put("list", hp100p600DataList);
        hp100p600.put("count", Integer.valueOf(hp100p600DataList.size()));

        //========process dataMap for summary view begin========
        @SuppressWarnings("unchecked")
        Map<String,Object> P600TestSummaryMetaData = (Map<String,Object>)this.dao.findForObject("PExportMapper.selectP600TestSummaryData", queryMap);
        if(P600TestSummaryMetaData == null) {
            P600TestSummaryMetaData = new HashMap<>();
        }
        //========process dataMap for summary view end========
        Map<String, Object> trxcalendarData = new HashMap<>();
        List<Map<String, Object>> trxcalendarDataList = (List<Map<String, Object>>)this.dao.findForList("HExportMapper.selecttrxcalendarData", queryMap);
        if (trxcalendarDataList == null){
            trxcalendarDataList = new ArrayList<Map<String,Object>>();
        }
        trxcalendarData.put("list", trxcalendarDataList);
        trxcalendarData.put("count", Integer.valueOf(trxcalendarDataList.size()));

        //========process dataMap for testDetail view begin========
        Map<String, Object> testDetail = new HashMap<>();
        Map<String, Object> note2 = new HashMap<>();
        Map<String, Object> note3 = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> P600TestDetailMetaDataList = (List<Map<String,Object>>)this.dao.findForList("PExportMapper.selectP600TestDetailData", queryMap);
        if(P600TestDetailMetaDataList == null) {
            P600TestDetailMetaDataList = new ArrayList<Map<String,Object>>();
        }
        Map<String, Map<String, List<Map<String, Object>>>> groups = P600TestDetailMetaDataList.stream().collect(Collectors.groupingBy(item -> {
            Map<String,Object> map = item;
            return String.valueOf(map.get("byOutBond"));
        }, Collectors.groupingBy(item -> {
            Map<String,Object> map = item;
            return String.valueOf(map.get("headerId"));
        })));
        List<String> headerIds = P600TestDetailMetaDataList.stream().map(item -> {
            return String.valueOf(item.get("headerId"));
        }).distinct().collect(Collectors.toList());
        Map<String, List<Map<String, Object>>> note2map = groups.get("Note 2")==null ? new HashMap<>() : groups.get("Note 2");
        Map<String, List<Map<String, Object>>> note3map = groups.get("Note 3")==null ? new HashMap<>() : groups.get("Note 3");
        List<Map<String,Object>> note2HeadList = new ArrayList<>();
        Integer noteTotalLineCount = 0;
        for(String headerId : headerIds) {
            if (note2map.get(headerId) != null) {
                Map<String, Object> tempMap = new HashMap<>();
                List<Map<String, Object>> tempList = note2map.get(headerId);
                tempMap.put("list", tempList);
                tempMap.put("count", tempList.size());
                noteTotalLineCount += tempList.size();
                note2HeadList.add(tempMap);
            }
        }
        List<Map<String,Object>> note3HeadList = new ArrayList<>();
        for(String headerId : headerIds) {
            if(note3map.get(headerId) != null) {
                Map<String,Object> tempMap = new HashMap<>();

                List<Map<String,Object>> tempList = note3map.get(headerId);
                tempMap.put("list", tempList);
                tempMap.put("count", tempList.size());
                noteTotalLineCount += tempList.size();
                note3HeadList.add(tempMap);
            }
        }
        note2.put("heads", note2HeadList);
        note2.put("headCount", note2HeadList.size());
        note3.put("heads", note3HeadList);
        note3.put("headCount", note3HeadList.size());
        Integer constantCount = 18;
        if(note2HeadList.size() == 0) {
            constantCount -= 2 ;
        }
        if(note3HeadList.size() == 0) {
            constantCount -= 2 ;
        }
        testDetail.put("note2", note2);
        testDetail.put("note3", note3);
        testDetail.put("noteTotalHeadCount", note2HeadList.size() + note3HeadList.size());
        testDetail.put("noteTotalLineCount", noteTotalLineCount);
        testDetail.put("constantCount", constantCount);
        //========process dataMap for testDetail view end========

        //========process dataMap for exposurePeriod view begin========
        Map<String, Object> exposurePeriod = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> P600ExposurePeriodMetaDataList = (List<Map<String,Object>>)this.dao.findForList("PExportMapper.selectP600ExposurePeriodData", queryMap);
        if(P600ExposurePeriodMetaDataList == null) {
            P600ExposurePeriodMetaDataList = new ArrayList<Map<String,Object>>();
        }
        exposurePeriod.put("list", P600ExposurePeriodMetaDataList);
        exposurePeriod.put("count", P600ExposurePeriodMetaDataList.size());
        //========process dataMap for exposurePeriod view end========

        result.put("main", main);
        result.put("test", test);
        result.put("testOTC", testOTC);
        result.put("testEX", testEX);
        result.put("hp100p600", hp100p600);
        result.put("summary", P600TestSummaryMetaData);
        result.put("testDetail", testDetail);
        result.put("exposurePeriod", exposurePeriod);
        result.put("trxcalendarData", trxcalendarData);
        return result;
    }


    /**
     * 处理sheet页P800的数据
     * @author Dai Zong 2017年9月14日
     *
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getP800Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();

        //========process dataMap for main view begin========
        Map<String, Object> main = new HashMap<String,Object>();

        Map<String, Object> commission = new HashMap<String,Object>();
        Map<String, Object> tradeCosts = new HashMap<String,Object>();

        List<Map<String, Object>> commissionList = new ArrayList<Map<String, Object>>();
        Map<String, Object> bondSettlement = new HashMap<String,Object>();
        Map<String, Object> repurchaseSettlement = new HashMap<String,Object>();
        Map<String, Object> bondTrade = new HashMap<String,Object>();
        Map<String, Object> repurchaseTrade = new HashMap<String,Object>();

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> P800MainMetaDataList = (List<Map<String,Object>>)this.dao.findForList("PExportMapper.selectP800MainData", queryMap);
        if(P800MainMetaDataList == null) {
            P800MainMetaDataList = new ArrayList<Map<String,Object>>();
        }

        for(Map<String, Object> map : P800MainMetaDataList){
            if("应付佣金".equals(map.get("type"))) {
                commissionList.add(map);
            }
            else if("银行间交易费用".equals(map.get("type"))) {
                switch (String.valueOf(map.get("tradeDetailName"))) {
                case "债券结算手续费":
                    bondSettlement = map;
                    break;
                case "回购结算手续费":
                    repurchaseSettlement = map;
                    break;
                case "债券交易手续费":
                    bondTrade = map;
                    break;
                case "回购交易手续费":
                    repurchaseTrade = map;
                    break;
                default:
                    break;
                }
            }
        }

        commission.put("list", commissionList);
        commission.put("count", commissionList.size());

        tradeCosts.put("bondSettlement", bondSettlement);
        tradeCosts.put("repurchaseSettlement", repurchaseSettlement);
        tradeCosts.put("bondTrade", bondTrade);
        tradeCosts.put("repurchaseTrade", repurchaseTrade);

        main.put("commission", commission);
        main.put("tradeCosts", tradeCosts);

        result.put("main", main);
        //========process dataMap for main view end========

        //========process dataMap for related view begin========
        Map<String, Object> related = new HashMap<String,Object>();

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> P800RelatedMetaDataList = (List<Map<String,Object>>)this.dao.findForList("PExportMapper.selectP800RelatedData", queryMap);
        if(P800RelatedMetaDataList == null) {
            P800RelatedMetaDataList = new ArrayList<Map<String,Object>>();
        }

        related.put("list", P800RelatedMetaDataList);
        related.put("count", P800RelatedMetaDataList.size());

        result.put("related", related);
        //========process dataMap for related view begin========
        return result;
    }

    /**
     * 处理sheet页P810的数据
     * @author Dai Zong 2017年9月17日
     *
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getP810Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();

        Map<String, Object> main = new HashMap<String,Object>();
        Map<String, Object> related = new HashMap<String,Object>();
        Map<String, Object> commission = new HashMap<String,Object>();
        Map<String, Object> commissionComparable = new HashMap<String,Object>();

        //========process dataMap for main view begin========
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> P810MetaDataList = (List<Map<String,Object>>)this.dao.findForList("PExportMapper.selectP810Data", queryMap);
        if(P810MetaDataList == null) {
            P810MetaDataList = new ArrayList<Map<String,Object>>();
        }

        main.put("list", P810MetaDataList);
        main.put("count", P810MetaDataList.size());
        //========process dataMap for main view end========

        //========process dataMap for related view begin========
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> P810RelatedMetaDataList = (List<Map<String,Object>>)this.dao.findForList("PExportMapper.selectP810RelatedData", queryMap);
        if(P810RelatedMetaDataList == null) {
            P810RelatedMetaDataList = new ArrayList<Map<String,Object>>();
        }

        related.put("list", P810RelatedMetaDataList);
        related.put("count", P810RelatedMetaDataList.size());
        //========process dataMap for related view end========

        //========process dataMap for related view begin========
        @SuppressWarnings("unchecked")
        Map<String,Object> P810CommissionMetaDataList = (Map<String,Object>)this.dao.findForObject("PExportMapper.selectP810CommissionData", queryMap);
        if(P810CommissionMetaDataList == null) {
            P810CommissionMetaDataList = new HashMap<>();
        }

        //========process dataMap for related view end========

        //========process dataMap for related view begin========
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> P810CommissionComparableMetaDataList = (List<Map<String,Object>>)this.dao.findForList("PExportMapper.selectP810CommissionComparableData", queryMap);
        if(P810CommissionComparableMetaDataList == null) {
            P810CommissionComparableMetaDataList = new ArrayList<Map<String,Object>>();
        }

        commissionComparable.put("list", P810CommissionComparableMetaDataList);
        commissionComparable.put("count", P810CommissionComparableMetaDataList.size());
        //========process dataMap for related view end========

        result.put("main", main);
        result.put("related", related);
        result.put("commission", P810CommissionMetaDataList);
        result.put("commissionComparable", commissionComparable);

        return result;
    }



    /**
     * 处理sheet页P900的数据
     * @author Dai Zong 2017年9月19日
     *
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getP900Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<>();

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> P900MetaDataList = (List<Map<String,Object>>)this.dao.findForList("PExportMapper.selectP900Data", queryMap);
        if(P900MetaDataList == null) {
            P900MetaDataList = new ArrayList<Map<String,Object>>();
        }

        result.put("list", P900MetaDataList);
        result.put("count", P900MetaDataList.size());

        return result;
    }

    /**
     * 处理sheet页P10000的数据
     * @author Dai Zong 2017年9月19日
     *
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getP10000Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<>();

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> P10000MetaDataList = (List<Map<String,Object>>)this.dao.findForList("PExportMapper.selectP10000Data", queryMap);
        if(P10000MetaDataList == null) {
            P10000MetaDataList = new ArrayList<Map<String,Object>>();
        }

        result.put("list", P10000MetaDataList);
        result.put("count", P10000MetaDataList.size());

        return result;
    }
}
