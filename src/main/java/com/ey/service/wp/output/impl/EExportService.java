package com.ey.service.wp.output.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
//        dataMap.put("G10000", this.getG10000Data(fundId, period));

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
}