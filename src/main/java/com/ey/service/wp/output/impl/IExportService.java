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

import com.ey.service.wp.output.IExportManager;
import com.ey.util.fileexport.Constants;
import com.ey.util.fileexport.FileExportUtils;
import com.ey.util.fileexport.FreeMarkerUtils;

/**
 * @name IExportService
 * @description 底稿输出服务--I
 * @author Dai Zong	2017年12月11日
 */
@Service("iExportService")
public class IExportService extends BaseExportService implements IExportManager{

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
        
        dataMap.put("I", this.getIData(fundId, periodStr));
        
        return FreeMarkerUtils.processTemplateToString(dataMap, Constants.EXPORT_TEMPLATE_FOLDER_PATH, Constants.EXPORT_TEMPLATE_FILE_NAME_I);
    }
    
	@Override
    public boolean doExport(HttpServletRequest request, HttpServletResponse response, String fundId, String periodStr) throws Exception {
	    Map<String, String> fundInfo = this.selectFundInfo(fundId);
        fundInfo.put("periodStr", periodStr);
        String xmlStr = this.generateFileContent(fundId, periodStr, fundInfo);
        FileExportUtils.writeFileToHttpResponse(request, response, FreeMarkerUtils.simpleReplace(Constants.EXPORT_AIM_FILE_NAME_I, fundInfo), xmlStr);
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
     * 处理sheet页I的数据
     * @author Dai Zong 2017年12月16日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getIData(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();
        
        //========process dataMap for fundRelatedParty view begin========
        Map<String, Object> fundRelatedParty = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> fundRelatedPartyMetaDataList = (List<Map<String,Object>>)this.dao.findForList("IExportMapper.selectIFundRelatedPartyData", queryMap);
        if(fundRelatedPartyMetaDataList == null) {
            fundRelatedPartyMetaDataList = new ArrayList<>();
        }
        fundRelatedParty.put("list", fundRelatedPartyMetaDataList);
        fundRelatedParty.put("count", fundRelatedPartyMetaDataList.size());
        //========process dataMap for fundRelatedParty view end========
        
        //========process dataMap for rp view begin========
        Map<String, Object> rp = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> rpMetaDataList = (List<Map<String,Object>>)this.dao.findForList("IExportMapper.selectIRpData", queryMap);
        if(rpMetaDataList == null) {
            rpMetaDataList = new ArrayList<>();
        }
        rp.put("list", rpMetaDataList);
        rp.put("count", rpMetaDataList.size());
        //========process dataMap for rp view end========
        
        //========process dataMap for transaction view begin========
        Map<String, Object> transaction = new HashMap<String,Object>();
        Map<String, Object> stock = new HashMap<String,Object>();
        Map<String, Object> bond = new HashMap<String,Object>();
        Map<String, Object> warrant = new HashMap<String,Object>();
        Map<String, Object> repo = new HashMap<String,Object>();
        Map<String, Object> fund = new HashMap<String,Object>();
        
        List<Map<String, Object>> tempList = null;
        tempList = this.selectITransactionDataList(queryMap, "STOCK");
        stock.put("list", tempList);
        stock.put("count", tempList.size());
        tempList = this.selectITransactionDataList(queryMap, "BOND");
        bond.put("list", tempList);
        bond.put("count", tempList.size());
        tempList = this.selectITransactionDataList(queryMap, "WARRANT");
        warrant.put("list", tempList);
        warrant.put("count", tempList.size());
        tempList = this.selectITransactionDataList(queryMap, "REPO");
        repo.put("list", tempList);
        repo.put("count", tempList.size());
        tempList = this.selectITransactionDataList(queryMap, "FUND");
        fund.put("list", tempList);
        fund.put("count", tempList.size());
        
        transaction.put("stock", stock);
        transaction.put("bond", bond);
        transaction.put("warrant", warrant);
        transaction.put("repo", repo);
        transaction.put("fund", fund);
        
        Map<String, Object> related = new HashMap<String,Object>();
        Map<String, Object> current = new HashMap<String,Object>();
        Map<String, Object> last = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> n500RelatedCurrentMetaDataList = (List<Map<String,Object>>)this.dao.findForList("IExportMapper.selectN500RelatedData", queryMap);
        if(n500RelatedCurrentMetaDataList == null) {
            n500RelatedCurrentMetaDataList = new ArrayList<>();
        }
        current.put("list", n500RelatedCurrentMetaDataList);
        current.put("count", n500RelatedCurrentMetaDataList.size());
        Map<String,Object> lastQueryMap = new HashMap<>();
        queryMap.forEach((k,v) -> {
            lastQueryMap.put(k, v);
        });
        lastQueryMap.put("period", (Integer.parseInt(String.valueOf(lastQueryMap.get("period")).substring(0, 4)) - 1) + "1231");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> n500RelatedLastMetaDataList = (List<Map<String,Object>>)this.dao.findForList("IExportMapper.selectN500RelatedData", queryMap);
        if(n500RelatedLastMetaDataList == null) {
            n500RelatedLastMetaDataList = new ArrayList<>();
        }
        last.put("list", n500RelatedLastMetaDataList);
        last.put("count", n500RelatedLastMetaDataList.size());
        related.put("current", current);
        related.put("last", last);
        transaction.put("related", related);
        //========process dataMap for transaction view end========
        
        //========process dataMap for manageFee view begin========
        Map<String, Object> manageFee = new HashMap<String,Object>();
        Map<String, Object> item1 = new HashMap<String,Object>();
        Map<String, Object> item2 = new HashMap<String,Object>();
        Map<String, Object> item3 = new HashMap<String,Object>();
        Map<String, Object> item4 = new HashMap<String,Object>();
        Map<String, Object> item5 = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> manageFeeMetaDataList = (List<Map<String,Object>>)this.dao.findForList("IExportMapper.selectIManageFeeData", queryMap);
        if(manageFeeMetaDataList == null) {
            manageFeeMetaDataList = new ArrayList<>();
        }
        for(Map<String, Object> map : manageFeeMetaDataList) {
            if("MANAGE".equals(map.get("tpye"))) {
                if("当期发生的基金应支付的管理费".equals(map.get("item"))) {
                    item1 = map;
                }else  if("其中：支付销售机构的客户维护费".equals(map.get("item"))) {
                    item2 = map;
                }else  if("期末未支付管理费余额".equals(map.get("item"))) {
                    item3 = map;
                }
            }else if("TRUSTEE".equals(map.get("tpye"))) {
                if("当期发生的基金应支付的托管费".equals(map.get("item"))) {
                    item4 = map;
                }else  if("期末未支付托管费余额".equals(map.get("item"))) {
                    item5 = map;
                }
            }
        }
        manageFee.put("item1", item1);
        manageFee.put("item2", item2);
        manageFee.put("item3", item3);
        manageFee.put("item4", item4);
        manageFee.put("item5", item5);
        //========process dataMap for manageFee view end========
        
        //========process dataMap for salesFee view end========
        Map<String, Object> salesFee = new HashMap<String,Object>();
        List<Map<String, Object>> salesFeeResultList = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> salesFeeMetaDataList = (List<Map<String,Object>>)this.dao.findForList("IExportMapper.selectISalesFeeData", queryMap);
        if(salesFeeMetaDataList == null) {
            salesFeeMetaDataList = new ArrayList<>();
        }
        List<String> levelNames = salesFeeMetaDataList.stream().map(item -> {
            return String.valueOf(item.get("level"));
        }).distinct().collect(Collectors.toList());
        Map<String, Map<String, List<Map<String, Object>>>> groups = salesFeeMetaDataList.stream().collect(Collectors.groupingBy(item -> {
            Map<String, Object> map = (Map<String, Object>)item;
            return String.valueOf(map.get("partyShortName"));
        }, Collectors.groupingBy(item -> {
            Map<String, Object> map = (Map<String, Object>)item;
            return String.valueOf(map.get("level"));
        })));
        for(Entry<String, Map<String, List<Map<String, Object>>>> entry : groups.entrySet()) {
            Map<String, Object> middleMap = new HashMap<String,Object>();
            List<Map<String,Object>> levelDataList = new ArrayList<>();
            
            String partyShortName = entry.getKey();
            Map<String, List<Map<String, Object>>> levelMap = entry.getValue();
            
            for(String levelName : levelNames) {
                List<Map<String, Object>> oneLevelList = levelMap.get(levelName);
                if(CollectionUtils.isNotEmpty(oneLevelList)) {
                    levelDataList.add(oneLevelList.get(0));
                }
            }
            
            Map<String, Object> one = null;
            if(CollectionUtils.isNotEmpty(levelDataList)) {
                one = levelDataList.get(0);
            }else {
                one = new HashMap<>();
            }
            
            middleMap.put("partyShortName", partyShortName);
            middleMap.put("leves", levelDataList);
            middleMap.put("count", levelDataList.size());
            middleMap.put("salesCommisionBal", one.get("salesCommisionBal"));
            middleMap.put("salesCommisionBalLast", one.get("salesCommisionBalLast"));
            salesFeeResultList.add(middleMap);
        }
        salesFee.put("levelNames", levelNames);
        salesFee.put("levelCount", levelNames.size());
        salesFee.put("list", salesFeeResultList);
        salesFee.put("count", salesFeeResultList.size());
        //========process dataMap for salesFee view end========
        
        //========process dataMap for bankThx view begin========
        Map<String, Object> bankThx = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> bankThxMetaDataList = (List<Map<String,Object>>)this.dao.findForList("IExportMapper.selectIBankThxData", queryMap);
        if(bankThxMetaDataList == null) {
            bankThxMetaDataList = new ArrayList<>();
        }
        bankThx.put("list", bankThxMetaDataList);
        bankThx.put("count", bankThxMetaDataList.size());
        //========process dataMap for bankThx view end========
        
        result.put("fundRelatedParty", fundRelatedParty);
        result.put("rp", rp);
        result.put("transaction", transaction);
        result.put("manageFee", manageFee);
        result.put("salesFee", salesFee);
        result.put("bankThx", bankThx);
        return result;
    }
    
    /**
     * 查询I表交易信息
     * @author Dai Zong 2017年12月16日
     * 
     * @param queryMap 基本查询Map
     * @param type 交易类型
     * @return I表交易信息
     * @throws Exception
     */
    private List<Map<String,Object>> selectITransactionDataList(Map<String, Object> queryMap, String type) throws Exception{
        queryMap.put("type", type);
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> metaDataList = (List<Map<String,Object>>)this.dao.findForList("IExportMapper.selectITransactionData", queryMap);
        if(metaDataList == null) {
            metaDataList = new ArrayList<>();
        }
        queryMap.remove("type");
        return metaDataList;
    }
    
}