package com.ey.service.wp.output.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.ey.service.system.report.ReportManager;
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

        dataMap.put("T", this.getTData(fundId, periodStr));
        dataMap.put("T300", this.getT300Data(fundId, periodStr));
        dataMap.put("T310", this.getT310Data(fundId, periodStr));
        dataMap.put("T400", this.getT400Data(fundId, periodStr));
        dataMap.put("T500", this.getT500Data(fundId, periodStr));
        dataMap.put("T11000", this.getT11000Data(fundId, periodStr));

        return FreeMarkerUtils.processTemplateToString(dataMap, templatePath, Constants.EXPORT_TEMPLATE_FILE_NAME_T);
    }

    @Override
    public boolean doExport(HttpServletRequest request, HttpServletResponse response, String fundId, String periodStr, String templatePath) throws Exception {
        Map<String, String> fundInfo = this.selectFundInfo(fundId);
        fundInfo.put("periodStr", periodStr);
        String xmlStr = this.generateFileContent(fundId, periodStr, fundInfo, templatePath);
        FileExportUtils.writeFileToHttpResponse(request, response, FreeMarkerUtils.simpleReplace(Constants.EXPORT_AIM_FILE_NAME_T, fundInfo), xmlStr);
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
        //处理T10000需要用到的数据
        Map<String, Object> dataFor10000 = new HashMap<>();
        List<String> levelNames = mainMetaDataList.stream().map(item -> {
            return String.valueOf(item.get("level"));
        }).distinct().sorted(LEVEL_COMPARATOR).collect(Collectors.toList());
        List<Map<String,Object>> begin = new ArrayList<>();
        List<Map<String,Object>> cr = new ArrayList<>();
        List<Map<String,Object>> dr = new ArrayList<>();
        Map<String,Map<String,Object>> tempMap = new HashMap<>();
        for(Map<String,Object> map : mainMetaDataList) {
            tempMap.put(String.valueOf(map.get("level")), map);
        }
        for(String levelName : levelNames) {
            Map<String, Object> map = tempMap.get(levelName);
            if(map == null) {
                map = new HashMap<>();
            }
            Map<String,Object> beginMap = new HashMap<>();
            Map<String,Object> crMap = new HashMap<>();
            Map<String,Object> drMap = new HashMap<>();
            beginMap.put("unit", map.get("beginUnits"));
            beginMap.put("amount", map.get("beginBalance"));
            crMap.put("unit", map.get("crUnits"));
            crMap.put("amount", map.get("crAmount"));
            drMap.put("unit", map.get("drUnits"));
            drMap.put("amount", map.get("drAmount"));
            begin.add(beginMap);
            cr.add(crMap);
            dr.add(drMap);
        }
        dataFor10000.put("levelNames", levelNames);
        dataFor10000.put("levelsCount", levelNames.size());
        dataFor10000.put("begin", begin);
        dataFor10000.put("cr", cr);
        dataFor10000.put("dr", dr);
        main.put("dataFor10000", dataFor10000);
        //========process dataMap for main view end========

        //========process dataMap for note view begin========
        Map<String, Object> note = new HashMap<>();
        Map<String, Object> note1 = new HashMap<>();
        Map<String, Object> note2 = new HashMap<>();
        List<Map<String,Object>> note1ItemList = new ArrayList<>();
        List<Map<String,Object>> note2ItemList = new ArrayList<>();
        String note3Flag = "N";

        @SuppressWarnings("unchecked")
        Map<String,Object> fundDateInfo = (Map<String,Object>)this.dao.findForObject("TExportMapper.selectFundDateInfo", queryMap);
        // chenhy,20240223,新增基金和产品的区分
        Map<String, Object> dateInfo = this.reportService.getDateInfo(periodStr, (Date)fundDateInfo.get("dateFrom"), (Date)fundDateInfo.get("dateTo"), (Date)fundDateInfo.get("dateTransform"),(String)fundDateInfo.get("fundType"));
        if("合同生效日".equals(dateInfo.get("CURRENT_INIT_SOURCE"))) {
            note3Flag = "Y";
        }

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
        note.put("note3Flag", note3Flag);
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
                container.put("item", levelMaps.size()==0 ? null : levelMaps.get(0).get("item"));
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
        }).distinct().sorted(LEVEL_COMPARATOR).collect(Collectors.toList());
        Map<String, List<Map<String, Object>>> groups = metaDataList.stream().collect(Collectors.groupingBy(item -> {
            Map<String, Object> map = (Map<String, Object>)item;
            return String.valueOf(map.get("level"));
        }));
        List<Object> levelMaps = new ArrayList<>();
        int accDivCount = 0; // chenhy,231214,增加累计分红次数
        for(String level : levelNames) {
            List<Object> tempList = new ArrayList<>();
            Map<String,Object> tempMap = new HashMap<>();
            if(groups.get(level) != null) {
                tempList.addAll(groups.get(level));
            }

            accDivCount = accDivCount + tempList.size();// chenhy,231214,增加累计分红次数

            tempMap.put("list", tempList);
            tempMap.put("count", tempList.size());
            tempMap.put("levelName", level);
            tempMap.put("accDivCount", accDivCount);
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
        }).distinct().sorted(LEVEL_COMPARATOR).collect(Collectors.toList());
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

    /**
     * 处理sheet页T500的数据
     * @author Dai Zong 2017年12月05日
     *
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getT500Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<>();
        //========process dataMap for main view begin========
        String oldPeriodStr = StringUtils.EMPTY;
        try {
            oldPeriodStr = String.valueOf(Long.parseLong(periodStr.substring(0, 4))-1)+"1231";
        }catch (Exception e) {
            oldPeriodStr = String.valueOf(Calendar.getInstance().get(Calendar.YEAR)-1)+"1231";
        }
        Map<String, Object> oldQueryMap = this.createBaseQueryMap(fundId, oldPeriodStr);

        Map<String, Object> main = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> mainMetaDataList = (List<Map<String,Object>>)this.dao.findForList("TExportMapper.selectT500MainData", queryMap);
        if(mainMetaDataList == null) {
            mainMetaDataList = new ArrayList<>();
        }
        Map<String,Map<String,Object>> mainContainer = new HashMap<>();
        for(Map<String,Object> map : mainMetaDataList) {
            mainContainer.put(String.valueOf(map.get("type")), map);
        }
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> mainOldMetaDataList = (List<Map<String,Object>>)this.dao.findForList("TExportMapper.selectT500MainData", oldQueryMap);
        if(mainOldMetaDataList == null) {
            mainOldMetaDataList = new ArrayList<>();
        }
        Map<String,Map<String,Object>> mainOldContainer = new HashMap<>();
        for(Map<String,Object> map : mainOldMetaDataList) {
            mainOldContainer.put(String.valueOf(map.get("type")), map);
        }
        for(int i=1 ; i<=12 ; i++) {
            String tag = "attr" + i;
            Map<String,Object> temp = new HashMap<>();
            if(mainContainer.get("实收基金") != null) {
                temp.put("SS", mainContainer.get("实收基金").get(tag));
            }
            if(mainContainer.get("未分配利润") != null) {
                temp.put("WFP", mainContainer.get("未分配利润").get(tag));
            }
            if(mainContainer.get("净资产合计") != null) {
                temp.put("SYZ", mainContainer.get("净资产合计").get(tag));
            }
            if(mainOldContainer.get("实收基金") != null) {
                temp.put("SSOLD", mainOldContainer.get("实收基金").get(tag));
            }
            if(mainOldContainer.get("未分配利润") != null) {
                temp.put("WFPOLD", mainOldContainer.get("未分配利润").get(tag));
            }
            if(mainOldContainer.get("净资产合计") != null) {
                temp.put("SYZOLD", mainOldContainer.get("净资产合计").get(tag));
            }
            main.put(tag, temp);
        }
        //========process dataMap for main view end========
        //========process dataMap for n_test view begin========
        Map<String, Object> n_test = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> ntestMetaDataList = (List<Map<String,Object>>)this.dao.findForList("TExportMapper.selectT500NTestData", queryMap);
        if(ntestMetaDataList == null) {
            ntestMetaDataList = new ArrayList<>();
        }
        n_test.put("list", ntestMetaDataList);
        n_test.put("count", ntestMetaDataList.size());
        //========process dataMap for n_test view end========
        //========process dataMap for f_test view begin========
        Map<String, Object> f_test = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> ftestMetaDataList = (List<Map<String,Object>>)this.dao.findForList("TExportMapper.selectT500FTestData", queryMap);
        if(ftestMetaDataList == null) {
            ftestMetaDataList = new ArrayList<>();
        }
        Map<String, Object> mother = new HashMap<>();
        Map<String, Object> steady = new HashMap<>();
        Map<String, Object> enterprise = new HashMap<>();
        for(Map<String,Object> map : ftestMetaDataList) {
            if("母基金".equals(map.get("item"))) {
                mother = map;
            }else if("稳健型".equals(map.get("item"))) {
                steady = map;
            }else if("进取型".equals(map.get("item"))) {
                enterprise = map;
            }
        }
        f_test.put("mother", mother);
        f_test.put("steady", steady);
        f_test.put("enterprise", enterprise);
        //========process dataMap for f_test view end========
        result.put("main", main);
        result.put("n_test", n_test);
        result.put("f_test", f_test);
        return result;
    }

    /**
     * 处理sheet页T11000D的数据
     * @author Dai Zong 2017年12月10日
     *
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getT11000Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<>();
        //========process dataMap for P4104 view begin========
        Map<String, Object> P4104 = new HashMap<>();
        List<Map<String, Object>> levels = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> p4104MetaDataList = (List<Map<String,Object>>)this.dao.findForList("TExportMapper.selectT11000P4104Data", queryMap);
        if(p4104MetaDataList == null) {
            p4104MetaDataList = new ArrayList<>();
        }
        List<String> levelNames = p4104MetaDataList.stream().map(item -> {
            return String.valueOf(item.get("level"));
        }).distinct().sorted(LEVEL_COMPARATOR).collect(Collectors.toList());
        Map<String, Map<String, List<Map<String, Object>>>> groups = p4104MetaDataList.stream().collect(Collectors.groupingBy(item -> {
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
            for(int i=1 ; i<=7 ; i++) {
                Map<String,Object> temp = new HashMap<>();
                String tag = "attr" + i;
                temp.put("realized", CollectionUtils.isEmpty(innerMap.get("已实现"))?null:innerMap.get("已实现").get(0).get(tag));
                temp.put("unrealized", CollectionUtils.isEmpty(innerMap.get("未实现"))?null:innerMap.get("未实现").get(0).get(tag));
                level.put(tag, temp);
            }
            levels.add(level);
        }

        P4104.put("levels", levels);
        P4104.put("levelCount", levels.size());
        //========process dataMap for P4104 view end========

        //========process dataMap for profitDist view begin========
        Map<String, Object> profitDist = new HashMap<>();
        List<Map<String,Object>> profitDistList = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> profitDistMetaDataList = (List<Map<String,Object>>)this.dao.findForList("TExportMapper.selectT11000ProfitDistData", queryMap);
        if(profitDistMetaDataList == null) {
            profitDistMetaDataList = new ArrayList<>();
        }
        List<String> levelNames2 = profitDistMetaDataList.stream().map(item -> {
            return String.valueOf(item.get("level"));
        }).distinct().sorted(LEVEL_COMPARATOR).collect(Collectors.toList());
        Map<String,Map<String,Object>> temp = new HashMap<>();
        for(Map<String,Object> map : profitDistMetaDataList) {
            temp.put(String.valueOf(map.get("level")) ,map);
        }
        for(String levelName : levelNames2) {
            Map<String, Object> map = temp.get(levelName);
            profitDistList.add(map==null ? new HashMap<>() : map);
        }
        profitDist.put("list", profitDistList);
        profitDist.put("count", profitDistList.size());
        //========process dataMap for profitDist view end========

        //========process dataMap for main view begin========
        Map<String, Object> main = new HashMap<>();
        List<Map<String, Object>> mainList = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> mainMetaDataList = (List<Map<String,Object>>)this.dao.findForList("TExportMapper.selectT11000MainData", queryMap);
        if(mainMetaDataList == null) {
            mainMetaDataList = new ArrayList<>();
        }
        Map<String, List<Map<String, Object>>> groups2 = mainMetaDataList.stream().collect(Collectors.groupingBy(item -> {
            Map<String,Object> map = (Map<String,Object>)item;
            return String.valueOf(map.get("level"));
        }));
        List<String> levelNames3 = mainMetaDataList.stream().map(item -> {
            return String.valueOf(item.get("level"));
        }).distinct().sorted(LEVEL_COMPARATOR).collect(Collectors.toList());
        for(String levelName : levelNames3) {
            Map<String, Object> temp1 = new HashMap<>();
            List<Map<String, Object>> list = groups2.get(levelName);
            if(list == null) {
                list  = new ArrayList<>();
            }
            temp1.put("levelName", levelName);
            temp1.put("list", list);
            temp1.put("count", list.size());
            mainList.add(temp1);
        }
        main.put("levels", mainList);
        main.put("levelCount", mainList.size());
        //========process dataMap for main view end========
        result.put("P4104", P4104);
        result.put("profitDist", profitDist);
        result.put("main", main);
        return result;
    }

}
