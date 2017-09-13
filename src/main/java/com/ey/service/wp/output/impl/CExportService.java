package com.ey.service.wp.output.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import com.ey.service.wp.output.CExportManager;
import com.ey.util.fileexport.Constants;
import com.ey.util.fileexport.FileExportUtils;
import com.ey.util.fileexport.FreeMarkerUtils;

/**
 * @name CExportService
 * @description 底稿输出服务--C
 * @author Dai Zong	2017年8月26日
 */
@Service("cExportService")
public class CExportService extends BaseExportService implements CExportManager{

    @Override
    public boolean doExport(HttpServletRequest request, HttpServletResponse response, String fundId, Long period) throws Exception {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        
        dataMap.put("period", period);
        dataMap.put("fundId", fundId);
        
        dataMap.put("C", this.getCData(fundId, period));
        dataMap.put("C300", this.getC300Data(fundId, period));
        dataMap.put("C400", this.getC400Data(fundId, period));
        dataMap.put("C10000", this.getC10000Data(fundId, period));
        
        String xmlStr = FreeMarkerUtils.processTemplateToString(dataMap, Constants.EXPORT_TEMPLATE_FOLDER_PATH, Constants.EXPORT_TEMPLATE_FILE_NAME_C);
        FileExportUtils.writeFileToHttpResponse(request, response, Constants.EXPORT_AIM_FILE_NAME_C, xmlStr);
        
        return true;
    }
	
    /**
     * 处理sheet页C的数据
     * @author Dai Zong 2017年8月27日
     * 
     * @param fundId
     * @param period
     * @return
     * @throws Exception
     */
    private Map<String,Object> getCData(String fundId, Long period) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, period);
        Map<String, Object> result = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> resMapList = (List<Map<String,Object>>)this.dao.findForList("CExportMapper.selectCData", queryMap);
        if(CollectionUtils.isEmpty(resMapList)) {return result;}
        
        for(Map<String, Object> resMap : resMapList) {
            if(resMap == null || resMap.get("accountNum") == null) {continue;}
            result.put("KM" + (String) resMap.get("accountNum"), resMap);
        }
        
        return result;
    }
    
    /**
     * 处理sheet页C300的数据
     * @author Dai Zong 2017年8月27日
     * 
     * @param fundId
     * @param period
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private Map<String,Object> getC300Data(String fundId, Long period) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, period);
        Map<String, Object> result = new HashMap<String,Object>();
        
        Map<String, Object> mainMap = new HashMap<String,Object>();
        Map<String, Object> intRiskPeriodMap = new HashMap<String,Object>();
        
        //========process dataMap for main view begin========
        List<Map<String,Object>> demandDepositsList = new ArrayList<>();//one part of KM1002
        List<Map<String,Object>> timeDepositsList = new ArrayList<>();// another part of KM1002
        List<Map<String,Object>> KM1021List = new ArrayList<>();
        List<Map<String,Object>> KM1031List = new ArrayList<>();
        int demandDepositsListCount = 0;
        int timeDepositsListCount = 0;
        int KM1021ListCount = 0;
        int KM1031ListCount = 0;
        List<Map<String,Object>> mainData = (List<Map<String,Object>>)this.dao.findForList("CExportMapper.selectC300MainData", queryMap);
        if(CollectionUtils.isEmpty(mainData)) {
            mainData = new ArrayList<Map<String,Object>>();
        }
        // classification
        for(Map<String,Object> map : mainData) {
            if("1002".equals(map.get("accountNum")) && "活期".equals(map.get("type"))) {
                demandDepositsList.add(map);
            }else if("1002".equals(map.get("accountNum")) && "定期".equals(map.get("type"))) {
                timeDepositsList.add(map);
            }else if("1021".equals(map.get("accountNum"))) {
                KM1021List.add(map);
            }else if("1031".equals(map.get("accountNum"))) {
                KM1031List.add(map);
            }
        }
        // calculate count
        demandDepositsListCount = demandDepositsList.size();
        timeDepositsListCount = timeDepositsList.size();
        KM1021ListCount = KM1021List.size();
        KM1031ListCount = KM1031List.size();
        // store
        Map<String,Object> KM1002Map = new HashMap<>();
        KM1002Map.put("demandDepositsList", demandDepositsList);
        KM1002Map.put("demandDepositsCount", demandDepositsListCount);
        KM1002Map.put("timeDepositsList", timeDepositsList);
        KM1002Map.put("timeDepositsCount", timeDepositsListCount);
        
        Map<String,Object> KM1021Map = new HashMap<>();
        KM1021Map.put("list", KM1021List);
        KM1021Map.put("count", KM1021ListCount);
        
        Map<String,Object> KM1031Map = new HashMap<>();
        KM1031Map.put("list", KM1031List);
        KM1031Map.put("count", KM1031ListCount);
        
        mainMap.put("KM1002", KM1002Map);
        mainMap.put("KM1021", KM1021Map);
        mainMap.put("KM1031", KM1031Map);
        //========process dataMap for main view end========
        
        //========process dataMap for related view begin========
        Map<String,Object> RelatedData = new HashMap<>();
        List<Map<String,Object>> RelatedMetaData = (List<Map<String,Object>>)this.dao.findForList("CExportMapper.selectC300RelatedData", queryMap);
        List<Map<String,Object>> RdemandDepositsList = new ArrayList<>();
        List<Map<String,Object>> RTimeDepositsList = new ArrayList<>();
        List<Map<String,Object>> RKM1021List = new ArrayList<>();
        List<Map<String,Object>> RKM1031List = new ArrayList<>();
        for(Map<String,Object> map : RelatedMetaData) {
            if("活期".equals(map.get("depositType"))) {
                RdemandDepositsList.add(map);
            }else if("定期".equals(map.get("depositType"))) {
                RTimeDepositsList.add(map);
            }else if("清算备付金".equals(map.get("depositType"))) {
                RKM1021List.add(map);
            }else if("存出保证金".equals(map.get("depositType"))) {
                RKM1031List.add(map);
            }
        }
        int RdemandDepositsCount = RdemandDepositsList.size();
        int RTimeDepositsCount = RTimeDepositsList.size();
        int RKM1021Count = RKM1021List.size();
        int RKM1031Count = RKM1031List.size();
        
        RelatedData.put("demandDepositsList", RdemandDepositsList);
        RelatedData.put("demandDepositsCount", RdemandDepositsCount);
        RelatedData.put("timeDepositsList", RTimeDepositsList);
        RelatedData.put("timeDepositsCount", RTimeDepositsCount);
        RelatedData.put("KM1021", RKM1021List);
        RelatedData.put("KM1021Count", RKM1021Count);
        RelatedData.put("KM1031", RKM1031List);
        RelatedData.put("KM1031Count", RKM1031Count);
        //========process dataMap for related view end========
        
        //========process dataMap for intRistPeriod view begin========
        List<String> intRistPeriods = (List<String>)this.dao.findForList("CExportMapper.selectC300IntRiskPeriods", queryMap);
        List<Map<String,Object>> timeDepositsDataList = (List<Map<String,Object>>)this.dao.findForList("CExportMapper.selectC300IntRiskTimeDepositsData", queryMap);
        List<Double> timeDepositsData = new ArrayList<>();
        intRistPeriods = intRistPeriods==null?new ArrayList<String>():intRistPeriods;
        timeDepositsDataList = timeDepositsDataList==null?new ArrayList<Map<String,Object>>():timeDepositsDataList;
        Map<String,Object> temp = new HashMap<>();
        
        //对于定期存款取C400数据，按【FUND_ID】【PERIOD】【PERIOD_LEFT】字段汇总【AMOUNT】写入对应的利率风险敞口，不计息类型默认为0
        temp.put("intRiskPeriod", "不计息");
        temp.put("amount", 0D);
        
        timeDepositsDataList.add(temp);
        int noInterestColIndex = intRistPeriods.size() - 1;
        //find no-interest col's index
        for(int i=0;i<intRistPeriods.size();i++) {
            if("不计息".equals(intRistPeriods.get(i))) {
                noInterestColIndex = i;
                break;
            }
        }
        //map timeDepositsData to right position
        for(String periodName : intRistPeriods) {
           for(Map<String,Object> map : timeDepositsDataList) {
               if(periodName.equals(map.get("intRiskPeriod"))) {
                   Double amount = Double.parseDouble(String.valueOf(map.get("amount")));
                   timeDepositsData.add(amount);
                   break;
               }
           }
        }
        
        intRiskPeriodMap.put("intRistPeriods", intRistPeriods);
        intRiskPeriodMap.put("noInterestColIndex", noInterestColIndex);
        intRiskPeriodMap.put("timeDepositsData", timeDepositsData);
        intRiskPeriodMap.put("intRistPeriodsCount", intRistPeriods.size());
        intRiskPeriodMap.put("timeDepositsCount", timeDepositsData.size());
        //========process dataMap for intRistPeriod view end========
        
        result.put("main", mainMap);
        result.put("related", RelatedData);
        result.put("intRiskPeriod", intRiskPeriodMap);
        return result;
    }
    
    /**
     * 处理sheet页C400的数据
     * @author Dai Zong 2017年8月30日
     * 
     * @param fundId
     * @param period
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private Map<String,Object> getC400Data(String fundId, Long period) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, period);
        Map<String,Object> result = new HashMap<>();
        
        Map<String,Object> mainData = new HashMap<>();
        Map<String,Object> bankData = new HashMap<>();
        Map<String,Object> termData = new HashMap<>();
        //========process dataMap for main view begin========
        List<Map<String,Object>> mainMetaData = (List<Map<String,Object>>)this.dao.findForList("CExportMapper.selectC400MainData", queryMap);
        if(mainMetaData == null) {
            mainMetaData = new ArrayList<Map<String,Object>>();
        }
        int mainListCount = mainMetaData.size();
        mainData.put("list", mainMetaData);
        mainData.put("count", mainListCount);
        //========process dataMap for main view end========
        //========process dataMap for bank view begin========
        List<Map<String,Object>> bankList = (List<Map<String,Object>>)this.dao.findForList("CExportMapper.selectC400BankData", queryMap);
        if(bankList == null) {
            bankList = new ArrayList<Map<String,Object>>();
        }
        bankData.put("list", bankList);
        bankData.put("count", bankList.size());
        //========process dataMap for bank view end========
        //========process dataMap for term view begin========
        List<String> termList = (List<String>)this.dao.findForList("CExportMapper.selectC400TermData", queryMap);
        if(termList == null) {
            termList = new ArrayList<String>();
        }
        termData.put("list", termList);
        termData.put("count", termList.size());
        //========process dataMap for term view end========
        result.put("main", mainData);
        result.put("groupByBank", bankData);
        result.put("groupByTerm", termData);
        return result;
    }
    
    @SuppressWarnings("unchecked")
    private Map<String,Object> getC10000Data(String fundId, Long period) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, period);
        Map<String,Object> result = new HashMap<>();
        
        List<Map<String, Object>> headList = new ArrayList<>();
        List<Map<String, Object>> detailList = new ArrayList<>();
        //========process dataMap for main view begin========
        List<Map<String,Object>> metaData = (List<Map<String,Object>>)this.dao.findForList("CExportMapper.selectC10000MainData", queryMap);
        if(metaData == null) {metaData = new ArrayList<>();}
        for(Map<String,Object> map : metaData) {
            if("Y".equals(map.get("detailFlag"))) {
                detailList.add(map);
            }else {
                headList.add(map);
            }
        }
        result.put("headList", headList);
        result.put("headListCount", headList.size());
        result.put("detailList", detailList);
        result.put("detailListCount", detailList.size());
        //========process dataMap for main view end========
        return result;
    }
}