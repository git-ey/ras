package com.ey.service.wp.output.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
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
        
        dataMap.put("E", this.getEData(fundId, periodStr));
        dataMap.put("E300", this.getE300Data(fundId, periodStr));
        dataMap.put("E400", this.getE400Data(fundId, periodStr));
        dataMap.put("E410", this.getE410Data(fundId, periodStr));
        dataMap.put("E41X", this.getE41XData(fundId, periodStr));
        dataMap.put("E500", this.getE500Data(fundId, periodStr));
        dataMap.put("E600", this.getE600Data(fundId, periodStr));

        return FreeMarkerUtils.processTemplateToString(dataMap, Constants.EXPORT_TEMPLATE_FOLDER_PATH, Constants.EXPORT_TEMPLATE_FILE_NAME_E);
    }

	@Override
    public boolean doExport(HttpServletRequest request, HttpServletResponse response, String fundId, String periodStr) throws Exception {
	    Map<String, String> fundInfo = this.selectFundInfo(fundId);
        fundInfo.put("periodStr", periodStr);
	    String xmlStr = this.generateFileContent(fundId, periodStr, fundInfo);
        FileExportUtils.writeFileToHttpResponse(request, response, FreeMarkerUtils.simpleReplace(Constants.EXPORT_AIM_FILE_NAME_E, fundInfo), xmlStr);
        return true;
    }
	
	@Override
	public boolean doExport(String folederName, String fileName, String fundId, String periodStr) throws Exception{
	    Map<String, String> fundInfo = this.selectFundInfo(fundId);
        fundInfo.put("periodStr", periodStr);
	    String xmlStr = this.generateFileContent(fundId, periodStr, fundInfo);
        FileExportUtils.writeFileToDisk(folederName, FreeMarkerUtils.simpleReplace(fileName, fundInfo), xmlStr);
	    return true;
	}
	
	/**
     * 处理sheet页E的数据
     * @author Dai Zong 2017年9月24日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getEData(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
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
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getE300Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
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
            Map<String,Object> map = item;
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
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getE400Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
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
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getE410Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
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
        Long period = Long.parseLong(periodStr.substring(0, 4));
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
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getE41XData(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
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
        
        Map<Object, List<Map<String, Object>>> collection = E41XDetailMetaDataList.stream().collect(Collectors.groupingBy(item -> {
            Map<String,Object> map = item;
            return map.get("trxDate");
        }));
        
        Set<Entry<Object,List<Map<String,Object>>>> entrySet = collection.entrySet();
        AtomicInteger i = new AtomicInteger(1);
        entrySet.stream().sorted((A,B) -> {
            Date d1 = (Date)A.getKey();
            Date d2 = (Date)B.getKey();
            return Long.compare(d1.getTime(), d2.getTime());
        }).forEach(entry -> {
            Map<String,Object> temp = new HashMap<>();
            temp.put("trxDate", entry.getKey());
            temp.put("sheetNum", "E41"+(i.getAndIncrement()));
            List<Map<String,Object>> list = entry.getValue();
            if(list == null) {
                list = new ArrayList<>();
            }
            temp.put("list", list);
            temp.put("count", list.size());
            
            sheetList.add(temp);
        });
//        for(Entry<Object,List<Map<String,Object>>> entry: entrySet) {
//            Map<String,Object> temp = new HashMap<>();
//            temp.put("trxDate", entry.getKey());
//            temp.put("sheetNum", "E41"+(i++));
//            List<Map<String,Object>> list = entry.getValue();
//            if(list == null) {
//                list = new ArrayList<>();
//            }
//            temp.put("list", list);
//            temp.put("count", list.size());
//            
//            sheetList.add(temp);
//        }
//        sheetList = sheetList.stream().sorted((A,B) -> {
//            Date d1 = (Date)A.get("trxDate");
//            Date d2 = (Date)B.get("trxDate");
//            return Long.compare(d1.getTime(), d2.getTime());
//        }).collect(Collectors.toList());
        
        
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
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getE500Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();
        
        Map<String, Object> sh = new HashMap<String,Object>();
        Map<String, Object> sz = new HashMap<String,Object>();
        Map<String, Object> bank = new HashMap<String,Object>();
        Map<String, Object> other = new HashMap<String,Object>();
        List<Map<String, Object>> otherList = new ArrayList<>();
        Integer etfCount = 0;
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> E500MetaDataList = (List<Map<String,Object>>)this.dao.findForList("EExportMapper.selectE500Data", queryMap);
        if(E500MetaDataList == null) {
            E500MetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        for(Map<String,Object> map : E500MetaDataList) {
            if("上交所".equals(map.get("detailName"))) {
                sh = map;
            }else if("深交所".equals(map.get("detailName"))) {
                sz = map;
            }else if("银行间".equals(map.get("detailName"))) {
                bank = map;
            }else if("ETF现金差额".equals(map.get("detailName"))) {
                result.put("etf", map);
                etfCount = 1;
            }else {
                otherList.add(map);
            }
        }
        
        other.put("list", otherList);
        other.put("count", otherList.size());
        
        result.put("sh", sh);
        result.put("sz", sz);
        result.put("bank", bank);
        result.put("other", other);
        result.put("etfCount", etfCount);
        
        return result;
    }
    
    /**
     * 处理sheet页E600的数据
     * @author Dai Zong 2017年9月30日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getE600Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();
        
        List<Map<String,Object>> stockList =  new ArrayList<>();
        List<Map<String,Object>> fundList =  new ArrayList<>();
        Map<String, Object> stock = new HashMap<String,Object>();
        Map<String, Object> fund = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> E600MetaDataList = (List<Map<String,Object>>)this.dao.findForList("EExportMapper.selectE600Data", queryMap);
        if(E600MetaDataList == null) {
            E600MetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        for(Map<String,Object> map : E600MetaDataList) {
            if("股票".equals(map.get("item"))) {
                stockList.add(map);
            }else if("基金".equals(map.get("item"))) {
                fundList.add(map);
            }
        }
        
        stock.put("list", stockList);
        stock.put("count", stockList.size());
        
        fund.put("list", fundList);
        fund.put("count", fundList.size());
        
        result.put("stock", stock);
        result.put("fund", fund);
        result.put("totalCount", E600MetaDataList.size());
        
        return result;
    }
}