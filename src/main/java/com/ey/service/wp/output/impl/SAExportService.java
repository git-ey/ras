package com.ey.service.wp.output.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import com.ey.service.wp.output.SAExportManager;
import com.ey.util.fileexport.Constants;
import com.ey.util.fileexport.FileExportUtils;
import com.ey.util.fileexport.FreeMarkerUtils;

/**
 * @name HSumExportService
 * @description 底稿输出服务--SA
 * @author Dai Zong	2018年11月11日
 */
@Service("saExportService")
public class SAExportService extends BaseExportService implements SAExportManager{

//    private static ThreadLocal<SimpleDateFormat> localSimpleDateFormat = new ThreadLocal<SimpleDateFormat>();

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
    private String generateFileContent(String firmCode, String periodStr, Map<String, String> companyInfo, String templatePath) throws Exception {
        Map<String, Object> dataMap = new HashMap<String, Object>();

        Long period = Long.parseLong(periodStr.substring(0, 4));
        Long month = Long.parseLong(periodStr.substring(4, 6));
        Long day = Long.parseLong(periodStr.substring(6, 8));

        dataMap.put("period", period);
        dataMap.put("month", month);
        dataMap.put("day", day);
        dataMap.put("companyInfo", companyInfo);

        Map<String, Object> queryMap = this.createBaseQueryMap(firmCode, periodStr);
        List<Map<String,Object>> lraSummaryDataList = (List<Map<String,Object>>)this.dao.findForList("SAExportMapper.selectLRASummaryData", queryMap);
        if(lraSummaryDataList == null) {
            lraSummaryDataList = new ArrayList<>();
        }
        dataMap.put("lraSummaryDataList", lraSummaryDataList);
        dataMap.put("lraSummaryDataCount", lraSummaryDataList.size());

        queryMap.put("item", "递延所得税资产");
        @SuppressWarnings("unchecked")
        Map<String, Object> DYSDSZCList= (Map<String, Object>)this.dao.findForObject("SAExportMapper.selectSASummaryData", queryMap);
        dataMap.put("DYSDSZC", DYSDSZCList.get("amount"));

        queryMap.put("item", "短期借款");
        @SuppressWarnings("unchecked")
        Map<String, Object> DQJKList= (Map<String, Object>)this.dao.findForObject("SAExportMapper.selectSASummaryData", queryMap);
        dataMap.put("DQJK", DQJKList.get("amount"));

        queryMap.put("item", "交易性金融负债");
        @SuppressWarnings("unchecked")
        Map<String, Object> JYXJRFZList= (Map<String, Object>)this.dao.findForObject("SAExportMapper.selectSASummaryData", queryMap);
        dataMap.put("JYXJRFZ", DYSDSZCList.get("amount"));

        queryMap.put("item", "应付投资顾问费");
        @SuppressWarnings("unchecked")
        Map<String, Object> YFTZGWFList= (Map<String, Object>)this.dao.findForObject("SAExportMapper.selectSASummaryData", queryMap);
        dataMap.put("YFTZGWF", YFTZGWFList.get("amount"));

        queryMap.put("item", "递延所得税负债");
        @SuppressWarnings("unchecked")
        Map<String, Object> DYSDSFZList= (Map<String, Object>)this.dao.findForObject("SAExportMapper.selectSASummaryData", queryMap);
        dataMap.put("DYSDSFZ", DYSDSFZList.get("amount"));

        Map<String, Object> firmQueryParam = this.createBaseQueryMap(firmCode, periodStr);
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> fundInfos = (List<Map<String, Object>>) this.dao.findForList("SAExportMapper.selectSAFundInfos", firmQueryParam);
        if(fundInfos == null) {
            fundInfos = new ArrayList<>();
        }
        fundInfos = this.generateDetailData(firmCode, periodStr, fundInfos);
        dataMap.put("fundInfos", fundInfos);
        dataMap.put("fundInfosCount", fundInfos.size());


        return FreeMarkerUtils.processTemplateToString(dataMap, templatePath, Constants.EXPORT_TEMPLATE_FILE_NAME_SA);
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
    public boolean doExport(HttpServletRequest request, HttpServletResponse response, String fundId, String periodStr, String templatePath) throws Exception {
	    /**
	     * 如果输入了fundInfo,则根据fundInfo反查到FirmCode之后处理。
	     */
	    Map<String, String> fundInfo = this.selectFundInfo(fundId);
        return this.doExport(fundInfo.get("firmCode"), periodStr, request, response, templatePath);
    }

	@Override
    public boolean doExport(String folederName, String fileName, String fundId, String periodStr, String templatePath) throws Exception {
	    /**
         * 如果输入了fundInfo,则根据fundInfo反查到FirmCode之后处理。
         */
	    Map<String, String> fundInfo = this.selectFundInfo(fundId);
        return this.doExport(folederName, fileName, fundInfo.get("firmCode"), periodStr, templatePath);
    }

    @Override
    public boolean doExport(String firmCode, String periodStr, HttpServletRequest request, HttpServletResponse response, String templatePath) throws Exception {
        Map<String, String> companyInfo = this.selectCompanyInfo(firmCode);
        companyInfo.put("periodStr", periodStr);
        String xmlStr = this.generateFileContent(firmCode, periodStr, companyInfo, templatePath);
        FileExportUtils.writeFileToHttpResponse(request, response, FreeMarkerUtils.simpleReplace(Constants.EXPORT_AIM_FILE_NAME_SA, companyInfo), xmlStr);
        return true;
    }

    @Override
    public boolean doExport(String folederName, Object fileName, String firmCode, String periodStr, String templatePath) throws Exception {
        Map<String, String> companyInfo = this.selectCompanyInfo(firmCode);
        companyInfo.put("periodStr", periodStr);
        String xmlStr = this.generateFileContent(firmCode, periodStr, companyInfo, templatePath);
        FileExportUtils.writeFileToDisk(folederName, FreeMarkerUtils.simpleReplace(String.valueOf(fileName), companyInfo), xmlStr);
        return true;
    }

    /**
     * 生成Detail的数据
     * @author Dai Zong 2018年11月11日
     *
     * @param firmCode
     * @param periodStr
     * @param fundInfos 旗下基金信息列表
     * @return 包含Detail的数据的旗下基金信息列表
     * @throws Exception
     */
    private List<Map<String,Object>> generateDetailData(String firmCode, String periodStr, List<Map<String,Object>> fundInfos) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(firmCode, periodStr);
        if(fundInfos == null) {
            fundInfos = new ArrayList<>(1);
        }

        for(Map<String,Object> fundInfo : fundInfos) {
            if(fundInfo == null) {
                fundInfo = new HashMap<>();
            }
            queryMap.put("fundId", fundInfo.get("fundId"));
            @SuppressWarnings("unchecked")
            List<Map<String,Object>> fundDetailDatas = (List<Map<String,Object>>)this.dao.findForList("SAExportMapper.selectSADetailData", queryMap);
            if(fundDetailDatas == null) {
                fundDetailDatas = new ArrayList<>();
            }
            Map<String,Map<String,Object>> seqMap = new HashMap<>(fundInfo.size());
            for(Map<String,Object> fundDetailData : fundDetailDatas) {
                Object seq = fundDetailData.get("seq");
                if(seq == null) {
                    continue;
                }
                seqMap.put(String.valueOf(seq), fundDetailData);
            }
            List<Map<String,Object>> processedFundDetailDatas = new ArrayList<>(62);
            for(int i=1 ; i<=62 ; i++) {
                Map<String, Object> fundDetailData = seqMap.get(String.valueOf(i));
                if(fundDetailData == null) {
                    fundDetailData = new HashMap<>();
                    fundDetailData.put("seq", String.valueOf(i));
                }
                processedFundDetailDatas.add(fundDetailData);
            }
            fundInfo.put("fundDetailDatas", processedFundDetailDatas);
            fundInfo.put("fundDetailDatasCount", processedFundDetailDatas.size());
        }
        return fundInfos;
    }

}
