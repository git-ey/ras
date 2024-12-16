package com.ey.service.wp.output.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import com.ey.service.wp.output.HSumExportManager;
import com.ey.util.fileexport.Constants;
import com.ey.util.fileexport.FileExportUtils;
import com.ey.util.fileexport.FreeMarkerUtils;

/**
 * @name HSumExportService
 * @description 底稿输出服务--H_SUM
 * @author Dai Zong	2017年12月6日
 */
@Service("hSumExportService")
public class HSumExportService extends BaseExportService implements HSumExportManager{
    
    private static ThreadLocal<SimpleDateFormat> localSimpleDateFormat = new ThreadLocal<SimpleDateFormat>();

    /**
     * 生成文件内容
     * @author Dai Zong 2017年12月6日
     * 
     * @param firmCode
     * @param periodStr
     * @param companyInfo
     * @return
     * @throws Exception
     */
    private String generateFileContent(String firmCode, String periodStr, Map<String, String> companyInfo) throws Exception {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        
        Long period = Long.parseLong(periodStr.substring(0, 4));
        Long month = Long.parseLong(periodStr.substring(4, 6));
        Long day = Long.parseLong(periodStr.substring(6, 8));
        
        dataMap.put("period", period);
        dataMap.put("month", month);
        dataMap.put("day", day);
        dataMap.put("companyInfo", companyInfo);
        
        dataMap.put("H00", this.getH00Data(firmCode, periodStr));
        dataMap.put("H10", this.getH10Data(firmCode, periodStr));
        dataMap.put("H11", this.getH11Data(firmCode, periodStr));
        dataMap.put("H12", this.getH12Data(firmCode, periodStr));
        dataMap.put("H20", this.getH20Data(firmCode, periodStr));
        dataMap.put("H21", this.getH21Data(firmCode, periodStr));
        dataMap.put("H22", this.getH22Data(firmCode, periodStr));
        dataMap.put("H30", this.getH30Data(firmCode, periodStr));
        dataMap.put("H31", this.getH31Data(firmCode, periodStr));
        dataMap.put("H32", this.getH32Data(firmCode, periodStr));
        dataMap.put("H40", this.getH40Data(firmCode, periodStr));
        dataMap.put("H50", this.getH50Data(firmCode, periodStr));
        
        return FreeMarkerUtils.processTemplateToString(dataMap, Constants.EXPORT_TEMPLATE_FOLDER_PATH, Constants.EXPORT_TEMPLATE_FILE_NAME_H_SUM);
    }
    
    /**
     * 获取小于指定日期的最近的闰日
     * @author Dai Zong 2017年12月8日
     * 
     * @param date 指定日期
     * @return 最近的闰日[以yyyy-MM-dd表示]
     */
    private String getRecentLeap(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        while(!((year%4 == 0 && year%100 != 0) || year%400 == 0)) {
            year--;
        }
        calendar.set(year, 1, 29, 0, 0, 0);
        return year + "-02-29";
    }
    
    @Override
    protected Map<String, Object> createBaseQueryMap(String firmCode, String periodStr) {
        Map<String, Object> res = new HashMap<String,Object>();
        res.put("firmCode", firmCode);
        res.put("period", periodStr);
        return res;
    }
    
    /**
     * 根据公司Code获取公司信息
     * @author Dai Zong 2017年12月6日
     * 
     * @param firmCode 公司Code
     * @return 公司信息
     * @throws Exception 公司Code无效
     */
    private Map<String,String> selectCompanyInfo(String firmCode) throws Exception{
        Map<String, Object> query = new HashMap<String,Object>();
        query.put("companyCode", firmCode);
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> resMapList = (List<Map<String,Object>>)this.dao.findForList("MgrcompanyMapper.selectCompanyInfo", query);
        if(CollectionUtils.isEmpty(resMapList) || resMapList.size() != 1) {
            throw new Exception("管理公司Code " + firmCode + " 无效");
        }
        Map<String, String> res = new HashMap<String,String>();
        resMapList.get(0).forEach((k,v) -> {
            res.put(k, String.valueOf(v));
        });
        return res;
    }
    
	@Override
    public boolean doExport(HttpServletRequest request, HttpServletResponse response, String fundId, String periodStr) throws Exception {
	    /**
	     * 如果输入了fundInfo,则根据fundInfo反查到FirmCode之后处理。
	     */
	    Map<String, String> fundInfo = this.selectFundInfo(fundId);
        return this.doExport(fundInfo.get("firmCode"), periodStr, request, response);
    }
	
	@Override
    public boolean doExport(String folederName, String fileName, String fundId, String periodStr) throws Exception {
	    /**
         * 如果输入了fundInfo,则根据fundInfo反查到FirmCode之后处理。
         */
	    Map<String, String> fundInfo = this.selectFundInfo(fundId);
        return this.doExport(folederName, fileName, fundInfo.get("firmCode"), periodStr);
    }

    @Override
    public boolean doExport(String firmCode, String periodStr, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, String> companyInfo = this.selectCompanyInfo(firmCode);
        companyInfo.put("periodStr", periodStr);
        String xmlStr = this.generateFileContent(firmCode, periodStr, companyInfo);
        FileExportUtils.writeFileToHttpResponse(request, response, FreeMarkerUtils.simpleReplace(Constants.EXPORT_AIM_FILE_NAME_H_SUM, companyInfo), xmlStr);
        return true;
    }

    @Override
    public boolean doExport(String folederName, Object fileName, String firmCode, String periodStr) throws Exception {
        Map<String, String> companyInfo = this.selectCompanyInfo(firmCode);
        companyInfo.put("periodStr", periodStr);
        String xmlStr = this.generateFileContent(firmCode, periodStr, companyInfo);
        FileExportUtils.writeFileToDisk(folederName, FreeMarkerUtils.simpleReplace(String.valueOf(fileName), companyInfo), xmlStr);
        return true;
    }
    
    /**
     * 处理sheet页H00的数据
     * @author irene 20231205
     * 
     * @param firmCode
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getH00Data(String firmCode, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(firmCode, periodStr);
        Map<String, Object> result = new HashMap<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> metaDataList = (List<Map<String,Object>>)this.dao.findForList("HSumExportMapper.selectH00Data", queryMap);
        if(metaDataList == null) {
            metaDataList = new ArrayList<>();
        }
        
        result.put("list", metaDataList);
        result.put("count", metaDataList.size());
        return result;
    }
    
    /**
     * 处理sheet页H10的数据
     * @author Dai Zong 2017年12月06日
     * 
     * @param firmCode
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getH10Data(String firmCode, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(firmCode, periodStr);
        Map<String, Object> result = new HashMap<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> metaDataList = (List<Map<String,Object>>)this.dao.findForList("HSumExportMapper.selectH10Data", queryMap);
        if(metaDataList == null) {
            metaDataList = new ArrayList<>();
        }
        
        result.put("list", metaDataList);
        result.put("count", metaDataList.size());
        return result;
    }
    
    /**
     * 处理sheet页H11的数据
     * @author Dai Zong 2017年12月08日
     * 
     * @param firmCode
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getH11Data(String firmCode, String periodStr) throws Exception{
        Map<String, Object> result = new HashMap<>();
        
        //查询头
        Map<String, Object> queryMap = this.createBaseQueryMap(firmCode, periodStr);
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> headMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HSumExportMapper.selectH11HeadData", queryMap);
        if(headMetaDataList == null) {
            headMetaDataList = new ArrayList<>();
        }
        
        Integer offset = 0;
        Integer preLineCout = 0;
        for(Map<String,Object> headMap : headMetaDataList) {
            Map<String,Object> lines = new HashMap<>();
            Map<String,Object> titles = new HashMap<>();
            //查询行
            queryMap.put("stockCode", headMap.get("stockCode"));
            @SuppressWarnings("unchecked")
            List<Map<String,Object>> lineMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HSumExportMapper.selectH11LineData", queryMap);
            if(lineMetaDataList == null) {
                lineMetaDataList = new ArrayList<>();
            }
            //查询行上应有的明细TITLE
            @SuppressWarnings("unchecked")
            List<Map<String,Object>> detailTitleMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HSumExportMapper.selectH11TestTitleData", queryMap);
            if(detailTitleMetaDataList == null) {
                detailTitleMetaDataList = new ArrayList<>();
            }
            //查询明细
            for(Map<String,Object> lineMap : lineMetaDataList) {
                Map<String,Object> details = new HashMap<>();
                List<Map<String,Object>> detailList = new ArrayList<>();
                
                queryMap.put("valuationDate", lineMap.get("valuationDate"));
                @SuppressWarnings("unchecked")
                List<Map<String,Object>> detailMetaDataList = (List<Map<String,Object>>)this.dao.findForList("HSumExportMapper.selectH11TestData", queryMap);
                if(detailMetaDataList == null) {
                    detailMetaDataList = new ArrayList<>();
                }
                Map<String,Map<String,Object>> temp = new HashMap<>();
                for(Map<String,Object> map : detailMetaDataList) {
                    temp.put(String.valueOf(map.get("fundId")), map);
                }
                for(Map<String,Object> titleMap : detailTitleMetaDataList) {
                    Map<String,Object> detail = temp.get(String.valueOf(titleMap.get("fundId")));
                    if(detail == null) {
                        detail = new HashMap<>();
                    }
                    detailList.add(detail);
                }
                details.put("list", detailList);
                details.put("count", detailList.size());
                lineMap.put("details", details);
                
            }
            lines.put("list", lineMetaDataList);
            lines.put("count", lineMetaDataList.size());
            titles.put("list", detailTitleMetaDataList);
            titles.put("count", detailTitleMetaDataList.size());
            //生成偏移量
            offset += preLineCout;
            preLineCout = lineMetaDataList.size();
            headMap.put("offset", offset);
            headMap.put("lines", lines);
            headMap.put("titles", titles);
        }
        
        result.put("list", headMetaDataList);
        result.put("count", headMetaDataList.size());
        return result;
    }
    
    /**
     * 处理sheet页H12的数据
     * @author Dai Zong 2017年12月07日
     * 
     * @param firmCode
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getH12Data(String firmCode, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(firmCode, periodStr);
        Map<String, Object> result = new HashMap<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> metaDataList = (List<Map<String,Object>>)this.dao.findForList("HSumExportMapper.selectH12Data", queryMap);
        if(metaDataList == null) {
            metaDataList = new ArrayList<>();
        }
        
        result.put("list", metaDataList);
        result.put("count", metaDataList.size());
        return result;
    }
    
    /**
     * 处理sheet页H20的数据
     * @author Dai Zong 2017年12月07日
     * 
     * @param firmCode
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getH20Data(String firmCode, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(firmCode, periodStr);
        Map<String, Object> result = new HashMap<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> metaDataList = (List<Map<String,Object>>)this.dao.findForList("HSumExportMapper.selectH20Data", queryMap);
        if(metaDataList == null) {
            metaDataList = new ArrayList<>();
        }
        
        result.put("list", metaDataList);
        result.put("count", metaDataList.size());
        return result;
    }
    
    /**
     * 处理sheet页H21的数据
     * @author Dai Zong 2017年12月07日
     * 
     * @param firmCode
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getH21Data(String firmCode, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(firmCode, periodStr);
        Map<String, Object> result = new HashMap<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> metaDataList = (List<Map<String,Object>>)this.dao.findForList("HSumExportMapper.selectH21Data", queryMap);
        if(metaDataList == null) {
            metaDataList = new ArrayList<>();
        }
        
        result.put("list", metaDataList);
        result.put("count", metaDataList.size());
        return result;
    }
    
    /**
     * 处理sheet页H22的数据
     * @author Irene20231205
     * 
     * @param firmCode
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getH22Data(String firmCode, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(firmCode, periodStr);
        Map<String, Object> result = new HashMap<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> metaDataList = (List<Map<String,Object>>)this.dao.findForList("HSumExportMapper.selectH22Data", queryMap);
        if(metaDataList == null) {
            metaDataList = new ArrayList<>();
        }
        
        result.put("list", metaDataList);
        result.put("count", metaDataList.size());
        return result;
    }
    
    /**
     * 处理sheet页H30的数据
     * @author Dai Zong 2017年12月08日
     * 
     * @param firmCode
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getH30Data(String firmCode, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(firmCode, periodStr);
        Map<String, Object> result = new HashMap<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> metaDataList = (List<Map<String,Object>>)this.dao.findForList("HSumExportMapper.selectH30Data", queryMap);
        if(metaDataList == null) {
            metaDataList = new ArrayList<>();
        }
        
        SimpleDateFormat sdf = localSimpleDateFormat.get();  
        if (sdf == null) {  
            sdf = new SimpleDateFormat("yyyyMMdd");  
            localSimpleDateFormat.set(sdf);  
        }  
        String recentLeap = null;
        try {
            recentLeap = this.getRecentLeap(sdf.parse(periodStr))+"T00:00:00.000";
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        result.put("list", metaDataList);
        result.put("count", metaDataList.size());
        result.put("recentLeap", recentLeap);
        return result;
    }
    
    /**
     * 处理sheet页H31的数据
     * @author Dai Zong 2017年12月08日
     * 
     * @param firmCode
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getH31Data(String firmCode, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(firmCode, periodStr);
        Map<String, Object> result = new HashMap<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> metaDataList = (List<Map<String,Object>>)this.dao.findForList("HSumExportMapper.selectH31Data", queryMap);
        if(metaDataList == null) {
            metaDataList = new ArrayList<>();
        }
        
        SimpleDateFormat sdf = localSimpleDateFormat.get();  
        if (sdf == null) {  
            sdf = new SimpleDateFormat("yyyyMMdd");  
            localSimpleDateFormat.set(sdf);  
        }  
        String recentLeap = null;
        try {
            recentLeap = this.getRecentLeap(sdf.parse(periodStr))+"T00:00:00.000";
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        result.put("list", metaDataList);
        result.put("count", metaDataList.size());
        result.put("recentLeap", recentLeap);
        return result;
    }
    /**
     * 处理sheet页H32的数据
     * @author IRENE 20231205
     * 
     * @param firmCode
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getH32Data(String firmCode, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(firmCode, periodStr);
        Map<String, Object> result = new HashMap<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> metaDataList = (List<Map<String,Object>>)this.dao.findForList("HSumExportMapper.selectH32Data", queryMap);
        if(metaDataList == null) {
            metaDataList = new ArrayList<>();
        }
        
        SimpleDateFormat sdf = localSimpleDateFormat.get();  
        if (sdf == null) {  
            sdf = new SimpleDateFormat("yyyyMMdd");  
            localSimpleDateFormat.set(sdf);  
        }  
        String recentLeap = null;
        try {
            recentLeap = this.getRecentLeap(sdf.parse(periodStr))+"T00:00:00.000";
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        result.put("list", metaDataList);
        result.put("count", metaDataList.size());
        result.put("recentLeap", recentLeap);
        return result;
    }
    
    /**
     * 处理sheet页H40的数据
     * @author Dai Zong 2017年12月08日
     * 
     * @param firmCode
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getH40Data(String firmCode, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(firmCode, periodStr);
        Map<String, Object> result = new HashMap<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> metaDataList = (List<Map<String,Object>>)this.dao.findForList("HSumExportMapper.selectH40Data", queryMap);
        if(metaDataList == null) {
            metaDataList = new ArrayList<>();
        }
        
        result.put("list", metaDataList);
        result.put("count", metaDataList.size());
        return result;
    }
    
    /**
     * 处理sheet页H50的数据
     * @author Dai Zong 2017年12月08日
     * 
     * @param firmCode
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getH50Data(String firmCode, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(firmCode, periodStr);
        Map<String, Object> result = new HashMap<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> metaDataList = (List<Map<String,Object>>)this.dao.findForList("HSumExportMapper.selectH50Data", queryMap);
        if(metaDataList == null) {
            metaDataList = new ArrayList<>();
        }
        
        result.put("list", metaDataList);
        result.put("count", metaDataList.size());
        return result;
    }
    
}