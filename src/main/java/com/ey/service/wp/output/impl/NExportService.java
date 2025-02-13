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

        dataMap.put("N", this.getNData(fundId, periodStr));
        dataMap.put("N300", this.getN300Data(fundId, periodStr));
        dataMap.put("N400", this.getN400Data(fundId, periodStr));
        dataMap.put("N600", this.getN600Data(fundId, periodStr));
        dataMap.put("N800", this.getN800Data(fundId, periodStr));
        dataMap.put("N10000", this.getN10000Data(fundId, periodStr));

        return FreeMarkerUtils.processTemplateToString(dataMap, templatePath, Constants.EXPORT_TEMPLATE_FILE_NAME_N);
    }

	@Override
    public boolean doExport(HttpServletRequest request, HttpServletResponse response, String fundId, String periodStr, String templatePath) throws Exception {
	    Map<String, String> fundInfo = this.selectFundInfo(fundId);
        fundInfo.put("periodStr", periodStr);
        String xmlStr = this.generateFileContent(fundId, periodStr, fundInfo, templatePath);
        FileExportUtils.writeFileToHttpResponse(request, response, FreeMarkerUtils.simpleReplace(Constants.EXPORT_AIM_FILE_NAME_N, fundInfo), xmlStr);
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
     * 处理sheet页N的数据
     * @author Dai Zong 2017年9月12日
     *
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getNData(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
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
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getN300Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
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
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getN400Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();
        Map<String, Object> rate = new HashMap<String,Object>();
        Map<String, Object> title = new HashMap<>();

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

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> N400RateMetaDataList = (List<Map<String,Object>>)this.dao.findForList("NExportMapper.selectN400RateData", queryMap);
        if(N400RateMetaDataList == null) {
            N400RateMetaDataList = new ArrayList<Map<String,Object>>();
        }

        rate.put("list", N400RateMetaDataList);
        rate.put("count", N400RateMetaDataList.size());

        @SuppressWarnings("unchecked")
        List<String> N400RateTitleMetaDataList = (List<String>)this.dao.findForList("NExportMapper.selectN400RateTitleData", queryMap);
        if(N400RateTitleMetaDataList == null) {
            N400RateTitleMetaDataList = new ArrayList<>();
        }

        title.put("list", N400RateTitleMetaDataList);
        title.put("count", N400RateTitleMetaDataList.size());


        result.put("title", title);
        result.put("rate", rate);

        return result;
    }

    /**
     * 处理sheet页N600的数据
     * @author Dai Zong 2017年9月17日
     *
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getN600Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();

        Map<String, Object> sh = new HashMap<String,Object>();
        Map<String, Object> sz = new HashMap<String,Object>();
        Map<String, Object> bank = new HashMap<String,Object>();
        Map<String, Object> other = new HashMap<String,Object>();
        List<Map<String, Object>> otherList = new ArrayList<>();
        Integer etfCount = 0;

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> N600MetaDataList = (List<Map<String,Object>>)this.dao.findForList("NExportMapper.selectN600Data", queryMap);
        if(N600MetaDataList == null) {
            N600MetaDataList = new ArrayList<Map<String,Object>>();
        }

        for(Map<String,Object> map : N600MetaDataList) {
            if("上交所".equals(map.get("name"))) {
                sh = map;
            }else if("深交所".equals(map.get("name"))) {
                sz = map;
            }else if("银行间".equals(map.get("name"))) {
                bank = map;
            }else if("ETF现金差额".equals(map.get("name"))) {
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
        result.put("shCount", sh.size());
        result.put("szCount", sz.size());
        result.put("bankCount", bank.size());
        result.put("etfCount", etfCount);
        result.put("count", N600MetaDataList.size());

        return result;
    }

    // /**
    //  * 处理sheet页N700的数据
    //  * @author Dai Zong 2017年9月17日
    //  *
    //  * @param fundId
    //  * @param periodStr
    //  * @return
    //  * @throws Exception
    //  */
    // private Map<String,Object> getN700Data(String fundId, String periodStr) throws Exception{
    //     Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
    //     Map<String, Object> result = new HashMap<String,Object>();

    //     @SuppressWarnings("unchecked")
    //     List<Map<String,Object>> N700MetaDataList = (List<Map<String,Object>>)this.dao.findForList("NExportMapper.selectN700Data", queryMap);
    //     if(N700MetaDataList == null) {
    //         N700MetaDataList = new ArrayList<Map<String,Object>>();
    //     }

    //     result.put("list", N700MetaDataList);
    //     result.put("count", N700MetaDataList.size());

    //     return result;
    // }

    /**
     * 处理sheet页N800的数据
     * @author Dai Zong 2017年9月17日
     *
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getN800Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
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

        String MFFlag = (String)this.dao.findForObject("NExportMapper.selectMFFlag", queryMap);
        result.put("MFFlag", MFFlag);

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
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getN10000Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
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
