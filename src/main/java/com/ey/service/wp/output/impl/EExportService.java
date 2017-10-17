package com.ey.service.wp.output.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.ey.service.wp.output.EExportManager;
import com.ey.util.fileexport.Constants;
import com.ey.util.fileexport.FileExportUtils;
import com.ey.util.fileexport.FreeMarkerUtils;

/**
 * @name EExportService
 * @description 底稿输出服务--E
 * @author Dai Zong	2017年9月24日
 */
@Service("eExportService")
public class EExportService extends BaseExportService implements EExportManager{

	@Override
    public boolean doExport(HttpServletRequest request, HttpServletResponse response, String fundId, Long period) throws Exception {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        
        dataMap.put("period", period);
        dataMap.put("fundId", fundId);
        
        dataMap.put("E", this.getEData(fundId, period));
        dataMap.put("E300", this.getE300Data(fundId, period));
        dataMap.put("E400", this.getE400Data(fundId, period));
        dataMap.put("E410", this.getE410Data(fundId, period));
        dataMap.put("E41X", this.getE41XData(fundId, period));
        dataMap.put("E500", this.getE500Data(fundId, period));
        dataMap.put("E600", this.getE600Data(fundId, period));

        String xmlStr = FreeMarkerUtils.processTemplateToString(dataMap, Constants.EXPORT_TEMPLATE_FOLDER_PATH, Constants.EXPORT_TEMPLATE_FILE_NAME_E);
        FileExportUtils.writeFileToHttpResponse(request, response, Constants.EXPORT_AIM_FILE_NAME_E, xmlStr);
        
        return true;
    }
	
	/**
     * 处理sheet页E的数据
     * @author Dai Zong 2017年9月24日
     * 
     * @param fundId
     * @param period
     * @return
     * @throws Exception
     */
    private Map<String,Object> getEData(String fundId, Long period) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, period);
        Map<String, Object> result = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> EMetaDataList = (List<Map<String,Object>>)this.dao.findForList("EExportMapper.selectEData", queryMap);
        if(EMetaDataList == null) {
            EMetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        Map<String,Object> temp = new HashMap<>();
        for(Map<String,Object> map : EMetaDataList) {
            temp.put(String.valueOf(map.get("accountNum")), map);
        }
        
        result.put("KM1204", temp.get("1204")==null?new HashMap<String,Object>():temp.get("1204"));
        result.put("KM1207", temp.get("1207")==null?new HashMap<String,Object>():temp.get("1207"));
        result.put("KM3003", temp.get("3003")==null?new HashMap<String,Object>():temp.get("3003"));
        result.put("KM1203", temp.get("1203")==null?new HashMap<String,Object>():temp.get("1203"));
        
        return result;
    }
    
    /**
     * 处理sheet页E300的数据
     * @author Dai Zong 2017年9月24日
     * 
     * @param fundId
     * @param period
     * @return
     * @throws Exception
     */
    private Map<String,Object> getE300Data(String fundId, Long period) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, period);
        Map<String, Object> result = new HashMap<String,Object>();
        
        //========process dataMap for main view begin========
        Map<String, Object> main = new HashMap<String,Object>();
        
        List<Map<String, Object>> totalList = new ArrayList<Map<String,Object>>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> E300MainMetaDataList = (List<Map<String,Object>>)this.dao.findForList("EExportMapper.selectE300MainData", queryMap);
        if(E300MainMetaDataList == null) {
            E300MainMetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        Map<String, List<Map<String, Object>>> groupByItemRes = E300MainMetaDataList.parallelStream().collect(Collectors.groupingBy(item -> {
            Map<String,Object> map = (Map<String,Object>) item;
            return String.valueOf(map.get("item"));
        }));
        
        totalList.add(this.computeE300MainObject(groupByItemRes, "应收活期存款利息", "应收活期存款利息"));
        totalList.add(this.computeE300MainObject(groupByItemRes, "应收定期存款利息", "应收定期存款利息"));
        totalList.add(this.computeE300MainObject(groupByItemRes, "应收其他存款利息", "应收其他存款利息"));
        totalList.add(this.computeE300MainObject(groupByItemRes, "应收结算备付金利息", "应收结算备付金利息"));
        totalList.add(this.computeE300MainObject(groupByItemRes, "应收债券利息", "应收债券利息"));
        totalList.add(this.computeE300MainObject(groupByItemRes, "应收资产支持证券利息", "应收债券利息"));
        totalList.add(this.computeE300MainObject(groupByItemRes, "应收买入返售利息", "应收买入返售证券利息"));
        totalList.add(this.computeE300MainObject(groupByItemRes, "应收申购款利息", "应收申购款利息"));
        totalList.add(this.computeE300MainObject(groupByItemRes, "应收黄金合约拆借孳息", "应收黄金合约拆借孳息"));
        totalList.add(this.computeE300MainObject(groupByItemRes, "应收保证金利息", "其他"));

        main.put("totalList", totalList);
        main.put("totalCount", E300MainMetaDataList.size());
        
        result.put("main", main);
        //========process dataMap for main view end========
        
        //========process dataMap for disc view end========
        Map<String, Object> disc = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> E300DiscMetaDataList = (List<Map<String,Object>>)this.dao.findForList("EExportMapper.selectE300DiscData", queryMap);
        if(E300DiscMetaDataList == null) {
            E300DiscMetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        Map<String,Map<String,Object>> temp = new HashMap<>();
        for(Map<String,Object> map : E300DiscMetaDataList) {
            temp.put(String.valueOf(map.get("item")), map);
        }
        
        List<Map<String,Object>> discList = new ArrayList<Map<String,Object>>();

        discList.add(this.computeE300DiscObject(temp, "应收活期存款利息"));
        discList.add(this.computeE300DiscObject(temp, "应收定期存款利息"));
        discList.add(this.computeE300DiscObject(temp, "应收其他存款利息"));
        discList.add(this.computeE300DiscObject(temp, "应收结算备付金利息"));
        discList.add(this.computeE300DiscObject(temp, "应收债券利息"));
        discList.add(this.computeE300DiscObject(temp, "应收买入返售证券利息"));
        discList.add(this.computeE300DiscObject(temp, "应收申购款利息"));
        discList.add(this.computeE300DiscObject(temp, "应收黄金合约拆借孳息"));
        discList.add(this.computeE300DiscObject(temp, "其他"));
        
        disc.put("list", discList);
        disc.put("count", discList.size());
        
        result.put("disc", disc);
        //========process dataMap for disc view end========
        
        return result;
    }
    
    /**
     * 计算E300 main的主要数据
     * @author Dai Zong 2017年9月24日
     * 
     * @param groupByItemRes
     * @param itemName
     * @param discType
     * @return
     */
    private Map<String,Object> computeE300MainObject(Map<String, List<Map<String, Object>>> groupByItemRes, String itemName, String discType) {
        Map<String,Object> item = new HashMap<>();
        
        if (null == groupByItemRes.get(itemName)) {
            item.put("item", itemName);
            item.put("discType", discType);
            item.put("list", new ArrayList<Map<String, Object>>());
            item.put("count", 0);
        } else {
            List<Map<String, Object>> list = groupByItemRes.get(itemName);

            item.put("item", itemName);
            item.put("discType", discType);
            item.put("list", list);
            item.put("count", list.size());
        }
        
        return item;
    }
    
    /**
     * 计算E300 disc的主要数据
     * @author Dai Zong 2017年9月24日
     * 
     * @param temp
     * @param itemName
     * @return
     */
    private Map<String,Object> computeE300DiscObject(Map<String,Map<String,Object>> temp, String itemName) {
        Map<String, Object> map = temp.get(itemName);
        
        if(map==null) {
            map = new HashMap<String,Object>();
            map.put("item", itemName);
        }
        
        return map;
    }
    
    /**
     * 处理sheet页E400的数据
     * @author Dai Zong 2017年9月26日
     * 
     * @param fundId
     * @param period
     * @return
     * @throws Exception
     */
    private Map<String,Object> getE400Data(String fundId, Long period) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, period);
        Map<String, Object> result = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> E400MetaDataList = (List<Map<String,Object>>)this.dao.findForList("EExportMapper.selectE400Data", queryMap);
        if(E400MetaDataList == null) {
            E400MetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        result.put("list", E400MetaDataList);
        result.put("count", E400MetaDataList.size());
        
        return result;
    }
    
    /**
     * 处理sheet页E410的数据
     * @author Dai Zong 2017年9月27日
     * 
     * @param fundId
     * @param period
     * @return
     * @throws Exception
     */
    private Map<String,Object> getE410Data(String fundId, Long period) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, period);
        Map<String, Object> result = new HashMap<String,Object>();
        
        //========process dataMap for rule view begin========
        Map<String, Object> rule = new HashMap<String,Object>();
        
        Map<String, Object> debitDay = new HashMap<String,Object>();
        Map<String, Object> confirmDay = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> E410RuleMetaDataList = (List<Map<String,Object>>)this.dao.findForList("EExportMapper.selectE410RuleData", queryMap);
        if(E410RuleMetaDataList == null) {
            E410RuleMetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        for(Map<String,Object> map : E410RuleMetaDataList) {
            if("划款日".equals(map.get("type"))) {
                debitDay = map;
            }else if("确认日".equals(map.get("type"))) {
                confirmDay = map;
            }
        }
        
        rule.put("debitDay", debitDay);
        rule.put("confirmDay", confirmDay);
        
        result.put("rule", rule);
        //========process dataMap for rule view end========
        
        //========process dataMap for trxDay view begin========
        Map<String, Object> trxDay = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> E410TrxDayMetaDataList = (List<Map<String,Object>>)this.dao.findForList("EExportMapper.selectE410TrxDayData", queryMap);
        if(E410TrxDayMetaDataList == null) {
            E410TrxDayMetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        trxDay.put("list", E410TrxDayMetaDataList);
        trxDay.put("count", E410TrxDayMetaDataList.size());
        
        result.put("trxDay", trxDay);
        //========process dataMap for trxDay view end========
        
        //========process dataMap for Summary view begin========
        Map<String, Object> summary = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> E410SummaryMetaDataList = (List<Map<String,Object>>)this.dao.findForList("EExportMapper.selectE410SummaryData", queryMap);
        if(E410SummaryMetaDataList == null) {
            E410SummaryMetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        int splitPoint = 0;
        for(Map<String,Object> map : E410SummaryMetaDataList) {
            int year = Integer.parseInt(String.valueOf(map.get("trxDate")).substring(0, 4));
            if(year > period) {
                splitPoint++;
            }
        }
        
        summary.put("list", E410SummaryMetaDataList);
        summary.put("count", E410SummaryMetaDataList.size());
        summary.put("splitPoint", splitPoint);
        
        result.put("summary", summary);
        //========process dataMap for Summary view end========
        
        //========process dataMap for apArTest view begin========
        Map<String, Object> apArTest = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> E410ApArTestMetaDataList = (List<Map<String,Object>>)this.dao.findForList("EExportMapper.selectE410ApArTestData", queryMap);
        if(E410ApArTestMetaDataList == null) {
            E410ApArTestMetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        Map<String,Map<String,Object>> temp = new HashMap<>();
        E410ApArTestMetaDataList.forEach(map -> {
            temp.put(String.valueOf(map.get("item")), map);
        });
        
        Map<String, Object> attr1 = this.computeE410ApArTestObject(temp, "申购款");
        Map<String, Object> attr2 = this.computeE410ApArTestObject(temp, "转入款");
        Map<String, Object> attr3 = this.computeE410ApArTestObject(temp, "赎回款");
        Map<String, Object> attr4 = this.computeE410ApArTestObject(temp, "转出款");
        Map<String, Object> attr5 = this.computeE410ApArTestObject(temp, "赎回费");
        Map<String, Object> attr6 = this.computeE410ApArTestObject(temp, "转换费");
        Map<String, Object> attr7 = this.computeE410ApArTestObject(temp, "后端申购费");
        
        LocalDate begin = LocalDate.of(1900, 1, 1);
        LocalDate end = LocalDate.of(period.intValue(), 12, 31);
        
        apArTest.put("attr1", attr1);
        apArTest.put("attr2", attr2);
        apArTest.put("attr3", attr3);
        apArTest.put("attr4", attr4);
        apArTest.put("attr5", attr5);
        apArTest.put("attr6", attr6);
        apArTest.put("attr7", attr7);
        
        apArTest.put("periodLastDayNum", end.toEpochDay()-begin.toEpochDay()+2);
        
        result.put("apArTest", apArTest);
        //========process dataMap for apArTest view end========
        
        //========process dataMap for periodAfterTest view begin========
        Map<String, Object> periodAfterTest = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> E410PeriodAfterTestMetaDataList = (List<Map<String,Object>>)this.dao.findForList("EExportMapper.selectE410PeriodAfterTestData", queryMap);
        if(E410PeriodAfterTestMetaDataList == null) {
            E410PeriodAfterTestMetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        periodAfterTest.put("list", E410PeriodAfterTestMetaDataList);
        periodAfterTest.put("count", E410PeriodAfterTestMetaDataList.size());
        
        result.put("periodAfterTest", periodAfterTest);
        //========process dataMap for periodAfterTest view end========
        
        return result;
    }
    
    /**
     * 计算E300 ApArTest的主要数据
     * @author Dai Zong 2017年9月27日
     * 
     * @param temp
     * @param itemName
     * @return
     */
    private Map<String,Object> computeE410ApArTestObject(Map<String,Map<String,Object>> temp, String itemName) {
        Map<String, Object> map = temp.get(itemName);
        
        if(map==null) {
            map = new HashMap<String,Object>();
            map.put("item", itemName);
        }
        
        return map;
    }
    
    /**
     * 处理sheet页E41X的数据
     * @author Dai Zong 2017年9月29日
     * 
     * @param fundId
     * @param period
     * @return
     * @throws Exception
     */
    private Map<String,Object> getE41XData(String fundId, Long period) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, period);
        Map<String, Object> result = new HashMap<String,Object>();
        
        List<Map<String,Object>> sheetList = new ArrayList<>();
        Map<String,Object> E41XdealerMetaData = null;
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> E41XdealerMetaDataList = (List<Map<String,Object>>) this.dao.findForList("EExportMapper.selectE41XDealerFlagData", queryMap);
        if(CollectionUtils.isEmpty(E41XdealerMetaDataList)) {
            E41XdealerMetaData = new HashMap<>();
        }else {
            E41XdealerMetaData = E41XdealerMetaDataList.get(0);
        }
        
        String dealerFlag = StringUtils.EMPTY;
        if(E41XdealerMetaData.get("dealerFlag") == null) {
            dealerFlag = "N";
        }else {
            dealerFlag = String.valueOf(E41XdealerMetaData.get("dealerFlag"));
        }
        Object dealerName = E41XdealerMetaData.get("dealerName");
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> E41XDetailMetaDataList = (List<Map<String,Object>>)this.dao.findForList("EExportMapper.selectE41XDetailData", queryMap);
        if(E41XDetailMetaDataList == null) {
            E41XDetailMetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        Map<Object, List<Map<String, Object>>> collection = E41XDetailMetaDataList.parallelStream().collect(Collectors.groupingBy(item -> {
            Map<String,Object> map = (Map<String,Object>) item;
            return map.get("trxDate");
        }));
        
        Set<Entry<Object,List<Map<String,Object>>>> entrySet = collection.entrySet();
        int i = 1;
        for(Entry<Object,List<Map<String,Object>>> entry: entrySet) {
            Map<String,Object> temp = new HashMap<>();
            temp.put("trxDate", entry.getKey());
            temp.put("sheetNum", "E41"+(i++));
            List<Map<String,Object>> list = entry.getValue();
            if(list == null) {
                list = new ArrayList<>();
            }
            temp.put("list", list);
            temp.put("count", list.size());
            
            sheetList.add(temp);
        }
        
        
        result.put("dealerFlag", dealerFlag);
        result.put("dealerName", dealerName);
        result.put("sheetList", sheetList);
        result.put("sheetCount", sheetList.size());
        
        return result;
    }
    
    /**
     * 处理sheet页E500的数据
     * @author Dai Zong 2017年9月26日
     * 
     * @param fundId
     * @param period
     * @return
     * @throws Exception
     */
    private Map<String,Object> getE500Data(String fundId, Long period) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, period);
        Map<String, Object> result = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> E500MetaDataList = (List<Map<String,Object>>)this.dao.findForList("EExportMapper.selectE500Data", queryMap);
        if(E500MetaDataList == null) {
            E500MetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        result.put("list", E500MetaDataList);
        result.put("count", E500MetaDataList.size());
        
        return result;
    }
    
    /**
     * 处理sheet页E600的数据
     * @author Dai Zong 2017年9月30日
     * 
     * @param fundId
     * @param period
     * @return
     * @throws Exception
     */
    private Map<String,Object> getE600Data(String fundId, Long period) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, period);
        Map<String, Object> result = new HashMap<String,Object>();
        
        List<Map<String,Object>> itemList =  new ArrayList<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> E600MetaDataList = (List<Map<String,Object>>)this.dao.findForList("EExportMapper.selectE600Data", queryMap);
        if(E600MetaDataList == null) {
            E600MetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        Map<Object, List<Map<String, Object>>> collect = E600MetaDataList.parallelStream().collect(Collectors.groupingBy(item -> {
            Map<String,Object> map = (Map<String,Object>) item;
            return map.get("item");
        }));
        
        collect.forEach((k,v) -> {
            Map<String,Object> temp = new HashMap<>();
            temp.put("item", k);
            if(v == null) {
                v = new ArrayList<>();
                v.add(new HashMap<>());
            }
            temp.put("detailList", v);
            temp.put("detailCount", v.size());
            
            itemList.add(temp);
        });
        
        Integer totalCount = 0;
        for(Map<String,Object> map : itemList) {
            totalCount += Integer.parseInt(String.valueOf(map.get("detailCount")));
        }
        
        result.put("itemList", itemList);
        result.put("itemCount", itemList.size());
        result.put("totalCount", totalCount+3*itemList.size());
        
        return result;
    }
}