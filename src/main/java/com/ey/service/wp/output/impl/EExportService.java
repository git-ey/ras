package com.ey.service.wp.output.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        dataMap.put("E", this.getEData(fundId, periodStr));
        dataMap.put("E300", this.getE300Data(fundId, periodStr));
        dataMap.put("E310", this.getE310Data(fundId, periodStr));
        dataMap.put("E400", this.getE400Data(fundId, periodStr));
        dataMap.put("E500", this.getE500Data(fundId, periodStr));
        dataMap.put("E600", this.getE600Data(fundId, periodStr));

        return FreeMarkerUtils.processTemplateToString(dataMap,templatePath, Constants.EXPORT_TEMPLATE_FILE_NAME_E);
    }

	@Override
    public boolean doExport(HttpServletRequest request, HttpServletResponse response, String fundId, String periodStr, String templatePath) throws Exception {
	    Map<String, String> fundInfo = this.selectFundInfo(fundId);
        fundInfo.put("periodStr", periodStr);
	    String xmlStr = this.generateFileContent(fundId, periodStr, fundInfo, templatePath);
        FileExportUtils.writeFileToHttpResponse(request, response, FreeMarkerUtils.simpleReplace(Constants.EXPORT_AIM_FILE_NAME_E, fundInfo), xmlStr);
        return true;
    }

	@Override
	public boolean doExport(String folederName, String fileName, String fundId, String periodStr, String templatePath) throws Exception{
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

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> E300MainMetaDataList = (List<Map<String,Object>>)this.dao.findForList("EExportMapper.selectE300MainData", queryMap);
        if(E300MainMetaDataList == null) {
            E300MainMetaDataList = new ArrayList<Map<String,Object>>();
        }
        Map<String,Map<String,Object>> temp = new HashMap<>();
        for(Map<String,Object> map : E300MainMetaDataList) {
            temp.put(String.valueOf(map.get("item")), map);
        }

        main.put("I10", this.computeE300DiscObject(temp, "应收活期存款利息"));
        main.put("I20", this.computeE300DiscObject(temp, "应收定期存款利息"));
        main.put("I30", this.computeE300DiscObject(temp, "应收其他存款利息"));
        main.put("I40", this.computeE300DiscObject(temp, "应收结算备付金利息"));
        main.put("I50", this.computeE300DiscObject(temp, "应收债券利息"));
        main.put("I55", this.computeE300DiscObject(temp, "应收资产支持证券利息"));
        main.put("I60", this.computeE300DiscObject(temp, "应收买入返售证券利息"));
        main.put("I70", this.computeE300DiscObject(temp, "应收申购款利息"));
        main.put("I80", this.computeE300DiscObject(temp, "应收黄金合约拆借孳息"));
        main.put("I85", this.computeE300DiscObject(temp, "应收出借证券利息")); // YURY，20200831，新增证券出借业务
        main.put("I90", this.computeE300DiscObject(temp, "其他"));

        result.put("main", main);
        //========process dataMap for main view end========

        //========process dataMap for disc view end========
        Map<String, Object> disc = new HashMap<String,Object>();

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> E300DiscMetaDataList = (List<Map<String,Object>>)this.dao.findForList("EExportMapper.selectE300DiscData", queryMap);
        if(E300DiscMetaDataList == null) {
            E300DiscMetaDataList = new ArrayList<Map<String,Object>>();
        }

        temp = new HashMap<>();
        for(Map<String,Object> map : E300DiscMetaDataList) {
            temp.put(String.valueOf(map.get("item")), map);
        }

        List<Map<String,Object>> discList = new ArrayList<Map<String,Object>>();

        discList.add(this.computeE300DiscObject(temp, "应收活期存款利息"));
        discList.add(this.computeE300DiscObject(temp, "应收定期存款利息"));
        discList.add(this.computeE300DiscObject(temp, "应收其他存款利息"));
        discList.add(this.computeE300DiscObject(temp, "应收结算备付金利息"));
        discList.add(this.computeE300DiscObject(temp, "应收债券利息"));
        discList.add(this.computeE300DiscObject(temp, "应收资产支持证券利息"));
        discList.add(this.computeE300DiscObject(temp, "应收买入返售证券利息"));
        discList.add(this.computeE300DiscObject(temp, "应收申购款利息"));
        discList.add(this.computeE300DiscObject(temp, "应收黄金合约拆借孳息"));
        discList.add(this.computeE300DiscObject(temp, "应收出借证券利息")); // YURY，20200831，新增证券出借业务
        discList.add(this.computeE300DiscObject(temp, "其他"));

        disc.put("list", discList);
        disc.put("count", discList.size());

        result.put("disc", disc);
        //========process dataMap for disc view end========

        return result;
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
     * 处理sheet页E310的数据
     * @author Dai Zong 2018年12月12日
     *
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getE310Data(String fundId, String periodStr) throws Exception{
    	Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> E310MetaDataList = (List<Map<String,Object>>)this.dao.findForList("EExportMapper.selectE310Data", queryMap);
        if(E310MetaDataList == null) {
        	E310MetaDataList = new ArrayList<Map<String,Object>>();
        }

        result.put("list", E310MetaDataList);
        result.put("count", E310MetaDataList.size());

        return result;
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
