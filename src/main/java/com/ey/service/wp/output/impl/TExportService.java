package com.ey.service.wp.output.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import com.ey.service.wp.output.TExportManager;
import com.ey.util.fileexport.Constants;
import com.ey.util.fileexport.FileExportUtils;
import com.ey.util.fileexport.FreeMarkerUtils;

/**
 * @name TExportService
 * @description 底稿输出服务--T
 * @author Dai Zong	2017年11月21日
 */
@Service("tExportService")
public class TExportService extends BaseExportService implements TExportManager{
    
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
        dataMap.put("extraFundInfo", this.getExtraFundInfo(fundId, periodStr));
        
        dataMap.put("T", this.getTData(fundId, periodStr));
        dataMap.put("T300", this.getT300Data(fundId, periodStr));
        dataMap.put("T310", this.getT310Data(fundId, periodStr));
        dataMap.put("T400", this.getT400Data(fundId, periodStr));
//        dataMap.put("E41X", this.getE41XData(fundId, periodStr));
//        dataMap.put("E500", this.getE500Data(fundId, periodStr));
//        dataMap.put("E600", this.getE600Data(fundId, periodStr));

        return FreeMarkerUtils.processTemplateToString(dataMap, Constants.EXPORT_TEMPLATE_FOLDER_PATH, Constants.EXPORT_TEMPLATE_FILE_NAME_T);
    }

    @Override
    public boolean doExport(HttpServletRequest request, HttpServletResponse response, String fundId, String periodStr) throws Exception {
        Map<String, String> fundInfo = this.selectFundInfo(fundId);
        fundInfo.put("periodStr", periodStr);
        String xmlStr = this.generateFileContent(fundId, periodStr, fundInfo);
        FileExportUtils.writeFileToHttpResponse(request, response, FreeMarkerUtils.simpleReplace(Constants.EXPORT_AIM_FILE_NAME_T, fundInfo), xmlStr);
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
     * 处理sheet页T的数据
     * @author Dai Zong 2017年12月02日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getTData(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> KM4001 = new HashMap<>();
        Map<String, Object> KM4104 = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> metaDataList = (List<Map<String,Object>>)this.dao.findForList("TExportMapper.selectTData", queryMap);
        if(metaDataList == null) {
            metaDataList = new ArrayList<>();
        }
        Map<String,Map<String,Object>> temp = new HashMap<>();
        metaDataList.forEach(map -> {
            temp.put(String.valueOf(map.get("accountNum")), map);
        });
        for(Entry<String, Map<String,Object>> entry : temp.entrySet()) {
            String key = entry.getKey();
            if("4001".equals(key)) {
                KM4001 = entry.getValue();
            }else if("4104".equals(key)) {
                KM4104 = entry.getValue();
            }
        }
        
        result.put("KM4001", KM4001);
        result.put("KM4104", KM4104);
        return result;
    }

    /**
     * 处理sheet页T的数据
     * @author Dai Zong 2017年12月02日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getT300Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<>();
        
        //========process dataMap for main view begin========
        Map<String, Object> main = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> mainMetaDataList = (List<Map<String,Object>>)this.dao.findForList("TExportMapper.selectT300MainData", queryMap);
        if(mainMetaDataList == null) {
            mainMetaDataList = new ArrayList<>();
        }
        main.put("list", mainMetaDataList);
        main.put("count", mainMetaDataList.size());
        //========process dataMap for main view end========
        
        //========process dataMap for note view begin========
        Map<String, Object> note = new HashMap<>();
        Map<String, Object> note1 = new HashMap<>();
        Map<String, Object> note2 = new HashMap<>();
        List<Map<String,Object>> note1ItemList = new ArrayList<>();
        List<Map<String,Object>> note2ItemList = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> noteMetaDataList = (List<Map<String,Object>>)this.dao.findForList("TExportMapper.selectT300NoteData", queryMap);
        if(noteMetaDataList == null) {
            noteMetaDataList = new ArrayList<>();
        }
        for(Map<String,Object> map : noteMetaDataList) {
            if("NOTE1".equals(map.get("note"))) {
                note1 = map;
            }else if ("NOTE2".equals(map.get("note"))) {
                note2 = map;
            }
        }
        for(int i=0 ; i<mainMetaDataList.size() ; i++) {
            Map<String,Object> temp1 = new HashMap<>();
            Map<String,Object> temp2 = new HashMap<>();
            String level = String.valueOf(mainMetaDataList.get(i).get("level"));
            //level info : note1 -> item 3~6
            temp1.put("level", level);
            temp1.put("value", note1.get("item" + (i + 3)));
            note1ItemList.add(temp1);
            //level info : note2 -> item 5~8
            temp2.put("level", level);
            temp2.put("value", note2.get("item" + (i + 5)));
            note2ItemList.add(temp2);
            
        }
        note1.put("levelDataList", note1ItemList);
        note1.put("levelDataCount", note1ItemList.size());
        note2.put("levelDataList", note2ItemList);
        note2.put("levelDataCount", note2ItemList.size());
        note.put("note1", note1);
        note.put("note2", note2);
        //========process dataMap for note view end========
        
        //========process dataMap for raise view begin========
        Map<String, Object> raise = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> raiseMetaDataList = (List<Map<String,Object>>)this.dao.findForList("TExportMapper.selectT300RaiseData", queryMap);
        if(raiseMetaDataList == null) {
            raiseMetaDataList = new ArrayList<>();
        }
        raise.put("list", raiseMetaDataList);
        raise.put("count", raiseMetaDataList.size());
        //========process dataMap for raise view end========
        
        //========process dataMap for adj view begin========
        Map<String, Object> adj = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> adjMetaDataList = (List<Map<String,Object>>)this.dao.findForList("TExportMapper.selectT300AdjData", queryMap);
        if(CollectionUtils.isEmpty(adjMetaDataList)) {
            adj.put("adjFlag", "N");
        }else {
            adj.put("adjFlag", "Y");
            List<String> levels = new ArrayList<>();
            List<String> items = new ArrayList<>();
            for(Map<String,Object> map : adjMetaDataList) {
                String level = String.valueOf(map.get("level"));
                String item = String.valueOf(map.get("sort"));
                if(!levels.contains(level)) {
                    levels.add(level);
                }
                if(!items.contains(item)) {
                    items.add(item);
                }
            }
            Map<String, Map<String, List<Map<String, Object>>>> groups = adjMetaDataList.stream().collect(Collectors.groupingBy(item -> {
                Map<String,Object> map = (Map<String,Object>) item;
                return String.valueOf(map.get("sort"));
            }, Collectors.groupingBy(item -> {
                Map<String,Object> map = (Map<String,Object>) item;
                return String.valueOf(map.get("level"));
            })));
            
            List<Map<String,Object>> itemMaps = new ArrayList<>();
            for(String item : items) {
                Map<String, List<Map<String, Object>>> outMap = groups.get(item);
                if(outMap == null) {
                    outMap = new HashMap<>();
                }
                Map<String,Object> container = new HashMap<>();
                List<Map<String,Object>> levelMaps = new ArrayList<>();
                for(String level : levels) {
                    List<Map<String, Object>> innerMap = outMap.get(level);
                    if(innerMap != null) {
                        levelMaps.addAll(innerMap);
                    }
                }
                container.put("levels", levelMaps);
                container.put("levelCount", levelMaps.size());
                itemMaps.add(container);
            }
            adj.put("items", itemMaps);
            adj.put("itemCount", itemMaps.size());
            adj.put("levels", levels);
        }
        //========process dataMap for adj view end========
        
        result.put("main", main);
        result.put("note", note);
        result.put("raise", raise);
        result.put("adj", adj);
        return result;
    }
    
    /**
     * 处理sheet页T310的数据
     * @author Dai Zong 2017年12月04日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getT310Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> metaDataList = (List<Map<String,Object>>)this.dao.findForList("TExportMapper.selectT310Data", queryMap);
        if(metaDataList == null) {
            metaDataList = new ArrayList<>();
        }
        List<String> levelNames = metaDataList.stream().map(item -> {
            return String.valueOf(item.get("level"));
        }).distinct().collect(Collectors.toList());
        Map<String, List<Map<String, Object>>> groups = metaDataList.stream().collect(Collectors.groupingBy(item -> {
            Map<String, Object> map = (Map<String, Object>)item;
            return String.valueOf(map.get("level"));
        }));
        List<Object> levelMaps = new ArrayList<>();
        for(String level : levelNames) {
            List<Object> tempList = new ArrayList<>();
            Map<String,Object> tempMap = new HashMap<>();
            if(groups.get(level) != null) {
                tempList.addAll(groups.get(level));
            }
            tempMap.put("list", tempList);
            tempMap.put("count", tempList.size());
            tempMap.put("levelName", level);
            levelMaps.add(tempMap);
        }
        
        result.put("levels", levelMaps);
        result.put("levelCount", levelMaps.size());
        return result;
    }
    
    /**
     * 处理sheet页T400的数据
     * @author Dai Zong 2017年12月04日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getT400Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> levels = new ArrayList<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> metaDataList = (List<Map<String,Object>>)this.dao.findForList("TExportMapper.selectT400Data", queryMap);
        if(metaDataList == null) {
            metaDataList = new ArrayList<>();
        }
        
        List<String> levelNames = metaDataList.stream().map(item -> {
            return String.valueOf(item.get("level"));
        }).distinct().collect(Collectors.toList());
        Map<String, Map<String, List<Map<String, Object>>>> groups = metaDataList.stream().collect(Collectors.groupingBy(item -> {
            Map<String,Object> map = (Map<String,Object>)item;
            return String.valueOf(map.get("level"));
        }, Collectors.groupingBy(item -> {
            Map<String,Object> map = (Map<String,Object>)item;
            return String.valueOf(map.get("type"));
        })));
        for(String levelName : levelNames) {
            Map<String, List<Map<String, Object>>> innerMap = groups.get(levelName);
            Map<String,Object> level = new HashMap<>();
            level.put("levelName",  levelName);
            if(innerMap == null) {
                innerMap = new HashMap<>();
            }
            for(int i=1 ; i<=14 ; i++) {
                Map<String,Object> temp = new HashMap<>();
                String tag = "attr" + i;
                temp.put("realized", CollectionUtils.isEmpty(innerMap.get("已实现"))?null:innerMap.get("已实现").get(0).get(tag));
                temp.put("unrealized", CollectionUtils.isEmpty(innerMap.get("未实现"))?null:innerMap.get("未实现").get(0).get(tag));
                level.put(tag, temp);
            }
            levels.add(level);
        }
        
        result.put("levels", levels);
        result.put("levelCount", levels.size());
        return result;
    }
    
}
