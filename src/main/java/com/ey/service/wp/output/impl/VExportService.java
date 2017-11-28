package com.ey.service.wp.output.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.ey.service.wp.output.VExportManager;
import com.ey.util.fileexport.Constants;
import com.ey.util.fileexport.FileExportUtils;
import com.ey.util.fileexport.FreeMarkerUtils;

/**
 * @name VExportService
 * @description 底稿输出服务--V
 * @author Dai Zong	2017年11月21日
 */
@Service("vExportService")
public class VExportService extends BaseExportService implements VExportManager{
    
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
    private String generateFileContent(String fundId, String periodStr, Map<String, String> fundInfo) throws Exception {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        
        Long period = Long.parseLong(periodStr.substring(0, 4));
        Long month = Long.parseLong(periodStr.substring(4, 6));
        Long day = Long.parseLong(periodStr.substring(6, 8));
        
        dataMap.put("period", period);
        dataMap.put("month", month);
        dataMap.put("day", day);
        dataMap.put("fundInfo", fundInfo);
        
        dataMap.put("V300", this.getV300Data(fundId, periodStr));
        dataMap.put("V400", this.getV400Data(fundId, periodStr));
//        dataMap.put("E410", this.getE410Data(fundId, periodStr));
//        dataMap.put("E41X", this.getE41XData(fundId, periodStr));
//        dataMap.put("E500", this.getE500Data(fundId, periodStr));
//        dataMap.put("E600", this.getE600Data(fundId, periodStr));

        return FreeMarkerUtils.processTemplateToString(dataMap, Constants.EXPORT_TEMPLATE_FOLDER_PATH, Constants.EXPORT_TEMPLATE_FILE_NAME_V);
    }

    @Override
    public boolean doExport(HttpServletRequest request, HttpServletResponse response, String fundId, String periodStr) throws Exception {
        Map<String, String> fundInfo = this.selectFundInfo(fundId);
        fundInfo.put("periodStr", periodStr);
        String xmlStr = this.generateFileContent(fundId, periodStr, fundInfo);
        FileExportUtils.writeFileToHttpResponse(request, response, FreeMarkerUtils.simpleReplace(Constants.EXPORT_AIM_FILE_NAME_V, fundInfo), xmlStr);
        return true;
    }

    @Override
    public boolean doExport(String folederName, String fileName, String fundId, String periodStr) throws Exception {
        Map<String, String> fundInfo = this.selectFundInfo(fundId);
        fundInfo.put("periodStr", periodStr);
        String xmlStr = this.generateFileContent(fundId, periodStr, fundInfo);
        FileExportUtils.writeFileToDisk(folederName, FreeMarkerUtils.simpleReplace(fileName, fundInfo), xmlStr);
        return true;
    }
    
    /**
     * 处理sheet页V300的数据
     * @author Dai Zong 2017年11月27日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getV300Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<String> V300IntRistPeriodsDataList = (List<String>)this.dao.findForList("VExportMapper.selectV300IntRiskPeriodData", queryMap);
        if(V300IntRistPeriodsDataList == null) {
            V300IntRistPeriodsDataList = new ArrayList<>(); 
        }
        
        List<Integer> emptyList = new ArrayList<>();
        for(int i=0 ; i<V300IntRistPeriodsDataList.size(); i++) {
            emptyList.add(0);
        }
        
        Map<String, Object> detail = new HashMap<String,Object>();
        
        Map<String, Object> attr1 = new HashMap<String, Object>();
        attr1.put("list", emptyList);
        attr1.put("count", 0);
        Map<String, Object> attr2 = new HashMap<String, Object>();
        attr2.put("list", emptyList);
        attr2.put("count", 0);
        Map<String, Object> attr3 = new HashMap<String, Object>();
        attr3.put("list", emptyList);
        attr3.put("count", 0);
        Map<String, Object> attr4 = new HashMap<String, Object>();
        attr4.put("list", emptyList);
        attr4.put("count", 0);
        Map<String, Object> attr5 = new HashMap<String, Object>();
        attr5.put("list", emptyList);
        attr5.put("count", 0);
        Map<String, Object> attr6 = new HashMap<String, Object>();
        attr6.put("list", emptyList);
        attr6.put("count", 0);
        Map<String, Object> attr7 = new HashMap<String, Object>();
        attr7.put("list", emptyList);
        attr7.put("count", 0);
        Map<String, Object> attr8 = new HashMap<String, Object>();
        attr8.put("list", emptyList);
        attr8.put("count", 0);
        Map<String, Object> attr9 = new HashMap<String, Object>();
        attr9.put("list", emptyList);
        attr9.put("count", 0);
        Map<String, Object> attr10 = new HashMap<String, Object>();
        attr10.put("list", emptyList);
        attr10.put("count", 0);
        Map<String, Object> attr11 = new HashMap<String, Object>();
        attr11.put("list", emptyList);
        attr11.put("count", 0);
        Map<String, Object> attr12 = new HashMap<String, Object>();
        attr12.put("list", emptyList);
        attr12.put("count", 0);
        Map<String, Object> attr13 = new HashMap<String, Object>();
        attr13.put("list", emptyList);
        attr13.put("count", 0);
        Map<String, Object> attr14 = new HashMap<String, Object>();
        attr14.put("list", emptyList);
        attr14.put("count", 0);
        Map<String, Object> attr15 = new HashMap<String, Object>();
        attr15.put("list", emptyList);
        attr15.put("count", 0);
        Map<String, Object> attr16 = new HashMap<String, Object>();
        attr16.put("list", emptyList);
        attr16.put("count", 0);
        Map<String, Object> attr17 = new HashMap<String, Object>();
        attr17.put("list", emptyList);
        attr17.put("count", 0);
        Map<String, Object> attr18 = new HashMap<String, Object>();
        attr18.put("list", emptyList);
        attr18.put("count", 0);
        Map<String, Object> attr19 = new HashMap<String, Object>();
        attr19.put("list", emptyList);
        attr19.put("count", 0);
        Map<String, Object> attr20 = new HashMap<String, Object>();
        attr20.put("list", emptyList);
        attr20.put("count", 0);
        Map<String, Object> attr21 = new HashMap<String, Object>();
        attr21.put("list", emptyList);
        attr21.put("count", 0);
        Map<String, Object> attr22 = new HashMap<String, Object>();
        attr22.put("list", emptyList);
        attr22.put("count", 0);
        Map<String, Object> attr23 = new HashMap<String, Object>();
        attr23.put("list", emptyList);
        attr23.put("count", 0);
        Map<String, Object> attr24 = new HashMap<String, Object>();
        attr24.put("list", emptyList);
        attr24.put("count", 0);
        Map<String, Object> attr25 = new HashMap<String, Object>();
        attr25.put("list", emptyList);
        attr25.put("count", 0);
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> V300MetaDataList = (List<Map<String,Object>>)this.dao.findForList("VExportMapper.selectV300Data", queryMap);
        if(V300MetaDataList == null) {
            V300MetaDataList = new ArrayList<>(); 
        }
        
        Map<String, List<Map<String, Object>>> groups = V300MetaDataList.parallelStream().collect(Collectors.groupingBy(item -> {
            return String.valueOf(item.get("type"));
        }));
        
        final List<String> V300IntRistPeriodsDataListFinal = V300IntRistPeriodsDataList;
        groups.forEach((type,list) -> {
            Map<String,Map<String,Object>> tempMap = new HashMap<>();
            for(Map<String,Object> map : list) {
                tempMap.put(String.valueOf(map.get("intRiskPeriod")), map);
            }
            List<Map<String,Object>> tempList = new ArrayList<>();
            for(String period :V300IntRistPeriodsDataListFinal) {
                if(tempMap.get(period) != null) {
                   tempList.add(tempMap.get(period));
                }else {
                    tempList.add(new HashMap<>());
                }
            }
            list = tempList;
            int count = list.size();
            switch (type) {
                case "银行存款":
                    attr1.put("list", list);
                    attr1.put("count", count);
                    break;
                case "结算备付金":
                    attr2.put("list", list);
                    attr2.put("count", count);
                    break;
                case "存出保证金":
                    attr3.put("list", list);
                    attr3.put("count", count);
                    break;
                case "交易性金融资产":
                    attr4.put("list", list);
                    attr4.put("count", count);
                    break;
                case "衍生金融资产":
                    attr5.put("list", list);
                    attr5.put("count", count);
                    break;
                case "买入返售金融资产":
                    attr6.put("list", list);
                    attr6.put("count", count);
                    break;
                case "应收证券清算款":
                    attr7.put("list", list);
                    attr7.put("count", count);
                    break;
                case "应收利息":
                    attr8.put("list", list);
                    attr8.put("count", count);
                    break;
                case "应收股利":
                    attr9.put("list", list);
                    attr9.put("count", count);
                    break;
                case "应收申购款":
                    attr10.put("list", list);
                    attr10.put("count", count);
                    break;
                case "其他资产":
                    attr11.put("list", list);
                    attr11.put("count", count);
                    break;
                case "短期借款":
                    attr12.put("list", list);
                    attr12.put("count", count);
                    break;
                case "交易性金融负债":
                    attr13.put("list", list);
                    attr13.put("count", count);
                    break;
                case "衍生金融负债":
                    attr14.put("list", list);
                    attr14.put("count", count);
                    break;
                case "卖出回购金融资产款":
                    attr15.put("list", list);
                    attr15.put("count", count);
                    break;
                case "应付证券清算款":
                    attr16.put("list", list);
                    attr16.put("count", count);
                    break;
                case "应付赎回款":
                    attr17.put("list", list);
                    attr17.put("count", count);
                    break;
                case "应付管理人报酬":
                    attr18.put("list", list);
                    attr18.put("count", count);
                    break;
                case "应付托管费":
                    attr19.put("list", list);
                    attr19.put("count", count);
                    break;
                case "应付销售服务费":
                    attr20.put("list", list);
                    attr20.put("count", count);
                    break;
                case "应付交易费用":
                    attr21.put("list", list);
                    attr21.put("count", count);
                    break;
                case "应付税费":
                    attr22.put("list", list);
                    attr22.put("count", count);
                    break;
                case "应付利息":
                    attr23.put("list", list);
                    attr23.put("count", count);
                    break;
                case "应付利润":
                    attr24.put("list", list);
                    attr24.put("count", count);
                    break;
                case "其他负债":
                    attr25.put("list", list);
                    attr25.put("count", count);
                    break;
                default:
                    break;
            }
        });
        
        detail.put("attr1", attr1);
        detail.put("attr2", attr2);
        detail.put("attr3", attr3);
        detail.put("attr4", attr4);
        detail.put("attr5", attr5);
        detail.put("attr6", attr6);
        detail.put("attr7", attr7);
        detail.put("attr8", attr8);
        detail.put("attr9", attr9);
        detail.put("attr10", attr10);
        detail.put("attr11", attr11);
        detail.put("attr12", attr12);
        detail.put("attr13", attr13);
        detail.put("attr14", attr14);
        detail.put("attr15", attr15);
        detail.put("attr16", attr16);
        detail.put("attr17", attr17);
        detail.put("attr18", attr18);
        detail.put("attr19", attr19);
        detail.put("attr20", attr20);
        detail.put("attr21", attr21);
        detail.put("attr22", attr22);
        detail.put("attr23", attr23);
        detail.put("attr24", attr24);
        detail.put("attr25", attr25);
        
        result.put("intRistPeriods", V300IntRistPeriodsDataList);
        result.put("intRistPeriodsCount", V300IntRistPeriodsDataList.size());
        result.put("detail", detail);
        
        return result;
    }
    
    /**
     * 处理sheet页V400的数据
     * @author Dai Zong 2017年11月28日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getV400Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();
        
        Map<String,Object> fundInfo = new HashMap<>();
        
        @SuppressWarnings("unchecked")
        Map<String,Object> V400FundInfoMetaData = (Map<String,Object>)this.dao.findForObject("VExportMapper.selectV400FundInfoData", queryMap);
        if(V400FundInfoMetaData != null) {
            fundInfo = V400FundInfoMetaData;
        }
        
        @SuppressWarnings("unchecked")
        List<String> V400HypothesisDataList = (List<String>)this.dao.findForList("VExportMapper.selectV400HypothesisData", queryMap);
        if(V400HypothesisDataList == null) {
            V400HypothesisDataList = new ArrayList<>(); 
        }
        
        @SuppressWarnings("unchecked")
        List<String> V400DetailMetaDataList = (List<String>)this.dao.findForList("VExportMapper.selectV400DetailData", queryMap);
        if(V400DetailMetaDataList == null) {
            V400DetailMetaDataList = new ArrayList<>(); 
        }
        
        @SuppressWarnings("unchecked")
        Map<String,Object> V400SummaryData = (Map<String,Object>)this.dao.findForObject("VExportMapper.selectV400SummaryData", queryMap);
        if(V400SummaryData == null) {
            V400SummaryData = new HashMap<>(); 
        }
        
        result.put("fundInfo", fundInfo);
        result.put("hypothesis", V400HypothesisDataList);
        result.put("hypothesisCount", V400HypothesisDataList.size());
        result.put("detailList", V400DetailMetaDataList);
        result.put("detailCount", V400DetailMetaDataList.size());
        result.put("summary", V400SummaryData);
        
        return result;
    }

}
