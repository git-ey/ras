package com.ey.service.wp.output.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
import com.ey.service.wp.output.ReportExportManager;
import com.ey.util.DocUtil;
import com.ey.util.PageData;
import com.ey.util.fileexport.Constants;
import com.ey.util.fileexport.FileExportUtils;
import com.ey.util.fileexport.FreeMarkerUtils;

import freemarker.template.TemplateException;

/**
 * @name GExportService
 * @description 底稿输出服务--Report
 * @author andy.chen 2017年9月7日
 */
@Service("reportExportService")
public class ReportExportService implements ReportExportManager {
    /**
     * dao
     */
    @Resource(name = "daoSupport")
    protected DaoSupport dao;
    
    /**
     * 构建基本查询Map
     * @author Dai Zong 2017年9月12日
     * 
     * @param pd
     * @return
     */
    protected Map<String,Object> createBaseQueryMap(PageData pd){
        String fundId = pd.getString("FUND_ID");
        String periodStr = pd.getString("PEROID");
        Map<String, Object> res = new HashMap<String,Object>();
        res.put("fundId", fundId);
        res.put("period", periodStr);
        return res;
    }
    
    /**
     * 根据基金ID获取基金信息
     * @author Dai Zong 2017年11月8日
     * 
     * @param fundId 基金ID
     * @return 基金信息
     * @throws Exception 基金ID无效
     */
    protected Map<String,String> selectFundInfo(String fundId) throws Exception{
        Map<String, Object> query = new HashMap<String,Object>();
        query.put("fundId", fundId);
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> resMapList = (List<Map<String,Object>>)this.dao.findForList("FundMapper.selectFundInfo", query);
        if(CollectionUtils.isEmpty(resMapList) || resMapList.size() != 1) {
            throw new Exception("基金ID " + fundId + " 无效");
        }
        Map<String, String> res = new HashMap<String,String>();
        resMapList.get(0).forEach((k,v) -> {
            res.put(k, String.valueOf(v));
        });
        return res;
    }

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
    private String generateFileContent(Map<String,Object> exportParam, Map<String, String> fundInfo) throws Exception {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        Map<String, Object> content = new HashMap<String, Object>();
        
        String fundId = String.valueOf(exportParam.get("FUND_ID"));
        String periodStr = String.valueOf(exportParam.get("PEROID"));
        Long period = Long.parseLong(periodStr.substring(0, 4));
        Long month = Long.parseLong(periodStr.substring(4, 6));
        Long day = Long.parseLong(periodStr.substring(6, 8));
        
        exportParam.put("period", period);
        exportParam.put("month", month);
        exportParam.put("day", day);
        exportParam.put("fundId", fundId);
        this.processParts(exportParam, content);
        
        exportParam.forEach((k,v) -> {
            dataMap.put(k, v);
        });
        dataMap.put("content", content);

        return FreeMarkerUtils.processTemplateToString(dataMap, Constants.EXPORT_TEMPLATE_FOLDER_PATH, Constants.EXPORT_TEMPLATE_FILE_NAME_REPORT);
    }
    
    @Override
    public boolean doExport(HttpServletRequest request, HttpServletResponse response, Map<String,Object> exportParam) throws Exception {
        String fundId = String.valueOf(exportParam.get("FUND_ID"));
        String periodStr = String.valueOf(exportParam.get("PEROID"));
        Map<String, String> fundInfo = this.selectFundInfo(fundId);
        fundInfo.put("periodStr", periodStr);
        String fileStr = this.generateFileContent(exportParam, fundInfo);
        FileExportUtils.writeFileToHttpResponse(request, response, FreeMarkerUtils.simpleReplace(Constants.EXPORT_AIM_FILE_NAME_REPORT, fundInfo), fileStr);
        return true;
    }

    @Override
    public boolean doExport(String folederName, String fileName, Map<String,Object> exportParam) throws Exception {
        String fundId = String.valueOf(exportParam.get("FUND_ID"));
        String periodStr = String.valueOf(exportParam.get("PEROID"));
        Map<String, String> fundInfo = this.selectFundInfo(fundId);
        fundInfo.put("periodStr", periodStr);
        String fileStr = this.generateFileContent(exportParam, fundInfo);
        FileExportUtils.writeFileToDisk(folederName, FreeMarkerUtils.simpleReplace(fileName, fundInfo), fileStr);
        return true;
    }
    
    private void processParts(Map<String, Object> exportParam, Map<String,Object> content) throws Exception{
        @SuppressWarnings("unchecked")
        Map<String,Object> partName = (Map<String,Object>)exportParam.get("partName");
        content.put("P1", this.processP1(exportParam, partName));
        content.put("P2", this.processP2(exportParam, partName));
    }
    
    private String processP1(Map<String,Object> exportParam, Map<String,Object> partName) throws IOException, TemplateException{
        return FreeMarkerUtils.processTemplateToStrUseAbsPath(exportParam, String.valueOf(exportParam.get("reportTempRootPath")), String.valueOf(partName.get("P1")));
    }
    
    private String processP2(Map<String,Object> exportParam, Map<String,Object> partName) throws IOException, TemplateException{
        return DocUtil.getXml2003Content(String.valueOf(exportParam.get("reportTempRootPath")) + String.valueOf(partName.get("P2")), "<w:body><wx:sect><wx:sub-section>(.*)</wx:sub-section>", 1);
    }
}