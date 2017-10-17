package com.ey.service.wp.output.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.ey.service.wp.output.NExportManager;
import com.ey.util.fileexport.Constants;
import com.ey.util.fileexport.FileExportUtils;
import com.ey.util.fileexport.FreeMarkerUtils;

/**
 * @name NExportService
 * @description 底稿输出服务--N
 * @author Dai Zong	2017年9月11日
 */
@Service("nExportService")
public class NExportService extends BaseExportService implements NExportManager{

    /**
     * 生成文件内容
     * @author Dai Zong 2017年10月17日
     * 
     * @param fundId
     * @param period
     * @return
     * @throws Exception
     */
    private String generateFileContent(String fundId, Long period) throws Exception {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        
        dataMap.put("period", period);
        dataMap.put("fundId", fundId);
        
        dataMap.put("N", this.getNData(fundId, period));
        dataMap.put("N300", this.getN300Data(fundId, period));
        dataMap.put("N400", this.getN400Data(fundId, period));
        dataMap.put("N500", this.getN500Data(fundId, period));
        dataMap.put("N510", this.getN510Data(fundId, period));
        dataMap.put("N600", this.getN600Data(fundId, period));
        dataMap.put("N700", this.getN700Data(fundId, period));
        dataMap.put("N800", this.getN800Data(fundId, period));
        dataMap.put("N10000", this.getN10000Data(fundId, period));
        
        return FreeMarkerUtils.processTemplateToString(dataMap, Constants.EXPORT_TEMPLATE_FOLDER_PATH, Constants.EXPORT_TEMPLATE_FILE_NAME_N);
    }
    
	@Override
    public boolean doExport(HttpServletRequest request, HttpServletResponse response, String fundId, Long period) throws Exception {
        String xmlStr = this.generateFileContent(fundId, period);
        FileExportUtils.writeFileToHttpResponse(request, response, Constants.EXPORT_AIM_FILE_NAME_N, xmlStr);
        return true;
    }
	
	@Override
    public boolean doExport(String folederName, String fileName, String fundId, Long period) throws Exception {
	    String xmlStr = this.generateFileContent(fundId, period);
        FileExportUtils.writeFileToDisk(folederName, fileName, xmlStr);
        return true;
    }
	
	/**
     * 处理sheet页N的数据
     * @author Dai Zong 2017年9月12日
     * 
     * @param fundId
     * @param period
     * @return
     * @throws Exception
     */
    private Map<String,Object> getNData(String fundId, Long period) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, period);
        Map<String, Object> result = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> NMetaDataList = (List<Map<String,Object>>)this.dao.findForList("NExportMapper.selectNData", queryMap);
        if(NMetaDataList == null) {
            NMetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        Map<String,Object> temp = new HashMap<>();
        NMetaDataList.forEach(map -> {
            temp.put(String.valueOf(map.get("accountNum")), map);
        });
        
        result.put("KM2203", temp.get("2203")==null?new HashMap<String,Object>():temp.get("2203"));
        result.put("KM2206", temp.get("2206")==null?new HashMap<String,Object>():temp.get("2206"));
        result.put("KM2207", temp.get("2207")==null?new HashMap<String,Object>():temp.get("2207"));
        result.put("KM2209", temp.get("2209")==null?new HashMap<String,Object>():temp.get("2209"));
        result.put("KM3003", temp.get("3003")==null?new HashMap<String,Object>():temp.get("3003"));
        result.put("KM2208", temp.get("2208")==null?new HashMap<String,Object>():temp.get("2208"));
        result.put("KM2221", temp.get("2221")==null?new HashMap<String,Object>():temp.get("2221"));
        result.put("KM2231", temp.get("2231")==null?new HashMap<String,Object>():temp.get("2231"));
        result.put("KM2232", temp.get("2232")==null?new HashMap<String,Object>():temp.get("2232"));
        
        return result;
    }
    
    /**
     * 处理sheet页N300的数据
     * @author Dai Zong 2017年9月13日
     * 
     * @param fundId
     * @param period
     * @return
     * @throws Exception
     */
    private Map<String,Object> getN300Data(String fundId, Long period) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, period);
        Map<String, Object> result = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> N300MetaDataList = (List<Map<String,Object>>)this.dao.findForList("NExportMapper.selectN300Data", queryMap);
        if(N300MetaDataList == null) {
            N300MetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        result.put("list", N300MetaDataList);
        result.put("count", N300MetaDataList.size());
        
        return result;
    }
    
    /**
     * 处理sheet页N400的数据
     * @author Dai Zong 2017年9月14日
     * 
     * @param fundId
     * @param period
     * @return
     * @throws Exception
     */
    private Map<String,Object> getN400Data(String fundId, Long period) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, period);
        Map<String, Object> result = new HashMap<String,Object>();
        
        Map<String, Object> KM2206 = new HashMap<String,Object>();
        Map<String, Object> KM2207 = new HashMap<String,Object>();
        Map<String, Object> KM2208 = new HashMap<String,Object>();
        //Prevent null pointer
        result.put("KM2206", KM2206);
        result.put("KM2207", KM2207);
        result.put("KM2208", KM2208);
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> N400MetaDataList = (List<Map<String,Object>>)this.dao.findForList("NExportMapper.selectN400Data", queryMap);
        if(N400MetaDataList == null) {
            N400MetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        for(Map<String,Object> map : N400MetaDataList) {
            result.put("KM" + String.valueOf(map.get("accountNum")), map);
        }
        
        return result;
    }
    
    /**
     * 处理sheet页N400的数据
     * @author Dai Zong 2017年9月14日
     * 
     * @param fundId
     * @param period
     * @return
     * @throws Exception
     */
    private Map<String,Object> getN500Data(String fundId, Long period) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, period);
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
        List<Map<String,Object>> N500MainMetaDataList = (List<Map<String,Object>>)this.dao.findForList("NExportMapper.selectN500MainData", queryMap);
        if(N500MainMetaDataList == null) {
            N500MainMetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        for(Map<String, Object> map : N500MainMetaDataList){
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
        List<Map<String,Object>> N500RelatedMetaDataList = (List<Map<String,Object>>)this.dao.findForList("NExportMapper.selectN500RelatedData", queryMap);
        if(N500RelatedMetaDataList == null) {
            N500RelatedMetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        related.put("list", N500RelatedMetaDataList);
        related.put("count", N500RelatedMetaDataList.size());
        
        result.put("related", related);
        //========process dataMap for related view begin========
        return result;
    }
    
    /**
     * 处理sheet页N510的数据
     * @author Dai Zong 2017年9月17日
     * 
     * @param fundId
     * @param period
     * @return
     * @throws Exception
     */
    private Map<String,Object> getN510Data(String fundId, Long period) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, period);
        Map<String, Object> result = new HashMap<String,Object>();
        
        Map<String, Object> main = new HashMap<String,Object>();
        Map<String, Object> related = new HashMap<String,Object>();
        
        //========process dataMap for main view begin========
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> N510MetaDataList = (List<Map<String,Object>>)this.dao.findForList("NExportMapper.selectN510Data", queryMap);
        if(N510MetaDataList == null) {
            N510MetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        main.put("list", N510MetaDataList);
        main.put("count", N510MetaDataList.size());
        //========process dataMap for main view end========
        
        //========process dataMap for related view begin========
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> N510RelatedMetaDataList = (List<Map<String,Object>>)this.dao.findForList("NExportMapper.selectN510RelatedData", queryMap);
        if(N510RelatedMetaDataList == null) {
            N510RelatedMetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        related.put("list", N510RelatedMetaDataList);
        related.put("count", N510RelatedMetaDataList.size());
        //========process dataMap for related view end========
        
        result.put("main", main);
        result.put("related", related);
        
        return result;
    }
    
    /**
     * 处理sheet页N600的数据
     * @author Dai Zong 2017年9月17日
     * 
     * @param fundId
     * @param period
     * @return
     * @throws Exception
     */
    private Map<String,Object> getN600Data(String fundId, Long period) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, period);
        Map<String, Object> result = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> N600MetaDataList = (List<Map<String,Object>>)this.dao.findForList("NExportMapper.selectN600Data", queryMap);
        if(N600MetaDataList == null) {
            N600MetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        result.put("list", N600MetaDataList);
        result.put("count", N600MetaDataList.size());
        
        return result;
    }
    
    /**
     * 处理sheet页N700的数据
     * @author Dai Zong 2017年9月17日
     * 
     * @param fundId
     * @param period
     * @return
     * @throws Exception
     */
    private Map<String,Object> getN700Data(String fundId, Long period) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, period);
        Map<String, Object> result = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> N700MetaDataList = (List<Map<String,Object>>)this.dao.findForList("NExportMapper.selectN700Data", queryMap);
        if(N700MetaDataList == null) {
            N700MetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        result.put("list", N700MetaDataList);
        result.put("count", N700MetaDataList.size());
        
        return result;
    }
    
    /**
     * 处理sheet页N800的数据
     * @author Dai Zong 2017年9月17日
     * 
     * @param fundId
     * @param period
     * @return
     * @throws Exception
     */
    private Map<String,Object> getN800Data(String fundId, Long period) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, period);
        Map<String, Object> result = new HashMap<String,Object>();
        
        //========process dataMap for main view begin========
        Map<String, Object> main = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> N800MainMetaDataList = (List<Map<String,Object>>)this.dao.findForList("NExportMapper.selectN800MainData", queryMap);
        if(N800MainMetaDataList == null) {
            N800MainMetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        main.put("list", N800MainMetaDataList);
        main.put("count", N800MainMetaDataList.size());
        
        result.put("main", main);
        //========process dataMap for main view end========
        
        //========process dataMap for note view begin========
        Map<String, Object> note = new HashMap<String,Object>();
        
        Map<String, Object> levels = new HashMap<String,Object>();
        
        List<Object> item1 = new ArrayList<Object>();
        List<Object> item2 = new ArrayList<Object>();
        List<Object> item3 = new ArrayList<Object>();
        List<Object> item4 = new ArrayList<Object>();
        List<Object> item5 = new ArrayList<Object>();
        List<Object> item6 = new ArrayList<Object>();
        
        @SuppressWarnings("unchecked")
        List<String> N800NoteLevels = (List<String>)this.dao.findForList("NExportMapper.selectN800NoteLevels", queryMap);
        if(N800NoteLevels == null) {
            N800NoteLevels = new ArrayList<String>(); 
        }
        
        levels.put("list", N800NoteLevels);
        levels.put("count", N800NoteLevels.size());
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> N800NoteMetaDataList = (List<Map<String,Object>>)this.dao.findForList("NExportMapper.selectN800NoteData", queryMap);
        if(N800NoteMetaDataList == null) {
            N800NoteMetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        Map<String, Map<String, Object>> temp = new HashMap<>();
        for(Map<String,Object> map : N800NoteMetaDataList) {
            temp.put(String.valueOf(map.get("level")), map);
        }
        
        for(String level : N800NoteLevels) {
            Map<String, Object> map = temp.get(level);
            item1.add(map.get("item1"));
            item2.add(map.get("item2"));
            item3.add(map.get("item3"));
            item4.add(map.get("item4"));
            item5.add(map.get("item5"));
            item6.add(map.get("item6"));
        }
        
        note.put("levels", levels);
        note.put("item1", item1);
        note.put("item2", item2);
        note.put("item3", item3);
        note.put("item4", item4);
        note.put("item5", item5);
        note.put("item6", item6);
        
        result.put("note", note);
        //========process dataMap for note view end========
        return result;
    }
    
    /**
     * 处理sheet页N10000的数据
     * @author Dai Zong 2017年9月17日
     * 
     * @param fundId
     * @param period
     * @return
     * @throws Exception
     */
    private Map<String,Object> getN10000Data(String fundId, Long period) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, period);
        Map<String, Object> result = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> N10000MetaDataList = (List<Map<String,Object>>)this.dao.findForList("NExportMapper.selectN10000Data", queryMap);
        if(N10000MetaDataList == null) {
            N10000MetaDataList = new ArrayList<Map<String,Object>>(); 
        }
        
        result.put("list", N10000MetaDataList);
        result.put("count", N10000MetaDataList.size());
        
        return result;
    }
}