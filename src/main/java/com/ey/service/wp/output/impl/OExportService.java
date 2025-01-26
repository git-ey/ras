package com.ey.service.wp.output.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.ey.service.wp.output.OExportManager;
import com.ey.util.fileexport.Constants;
import com.ey.util.fileexport.FileExportUtils;
import com.ey.util.fileexport.FreeMarkerUtils;

@Service("oExportService")
public class OExportService extends BaseExportService implements OExportManager {

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
//        dataMap.put("extraFundInfo", this.getExtraFundInfo(fundId, periodStr));

        dataMap.put("O", this.getOData(fundId, periodStr));
        dataMap.put("O300", this.getO300Data(fundId, periodStr));
        dataMap.put("O310", this.getO310Data(fundId, periodStr));
        dataMap.put("O311", this.getO311Data(fundId, periodStr));

        return FreeMarkerUtils.processTemplateToString(dataMap, templatePath, Constants.EXPORT_TEMPLATE_FILE_NAME_O);
    }

    @Override
    public boolean doExport(HttpServletRequest request, HttpServletResponse response, String fundId, String periodStr, String templatePath) throws Exception {
        Map<String, String> fundInfo = this.selectFundInfo(fundId);
        fundInfo.put("periodStr", periodStr);
        String xmlStr = this.generateFileContent(fundId, periodStr, fundInfo, templatePath);
        FileExportUtils.writeFileToHttpResponse(request, response, FreeMarkerUtils.simpleReplace(Constants.EXPORT_AIM_FILE_NAME_O, fundInfo), xmlStr);
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
     * 处理sheet页O的数据
     * @author Dai Zong 2018年11月4日
     *
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getOData(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> resMapList = (List<Map<String,Object>>)this.dao.findForList("OExportMapper.selectOData", queryMap);
        if(resMapList == null) {
            resMapList = new ArrayList<>();
        }

        for(Map<String, Object> resMap : resMapList) {
            if(resMap == null || resMap.get("accountNum") == null) {continue;}
            result.put("KM" + (String) resMap.get("accountNum"), resMap);
        }

        if(result.get("KM2221") == null) {
            result.put("KM2221", new HashMap<>());
        }

        return result;
    }

    /**
     * 处理sheet页O300的数据
     * @author Dai Zong 2018年11月4日
     *
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getO300Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<>();

        result.put("YJZZS_JRSPZR_SX", new HashMap<>());
        result.put("YJZZS_DKFW_SX", new HashMap<>());
        result.put("YJZZS_YJZZS_SX", new HashMap<>());
        result.put("YJFJS_YJCJFFJ_SX", new HashMap<>());
        result.put("YJFJS_YJJYFJ_SX", new HashMap<>());
        result.put("YJFJS_YJDFJYFJ_SX", new HashMap<>());
        result.put("YJZZS_JRSPZR_WSX", new HashMap<>());
        result.put("YJZZS_DKFW_WSX", new HashMap<>());
        result.put("YJFJS_YJJYFJ_WSX", new HashMap<>());
        result.put("YJFJS_YJDFJYFJ_WSX", new HashMap<>());
        result.put("YJFJS_YJCJFFJ_WSX", new HashMap<>());
        result.put("OTHER", new HashMap<>());

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> resMapList = (List<Map<String,Object>>)this.dao.findForList("OExportMapper.selectO300Data", queryMap);
        if(resMapList == null) {
            resMapList = new ArrayList<>();
        }

        for(Map<String, Object> resMap : resMapList) {
            if(resMap == null) {continue;}
            if("其他".equals(resMap.get("type"))) {
                result.put("OTHER", resMap);
            }else if("应交增值税".equals(resMap.get("type"))) {
                if("未实现".equals(resMap.get("realizedFlag"))) {
                    if("金融商品转让".equals(resMap.get("subtype"))) {
                        result.put("YJZZS_JRSPZR_WSX", resMap);
                    }else if("贷款服务".equals(resMap.get("subtype"))) {
                        result.put("YJZZS_DKFW_WSX", resMap);
                    }
                }else {
                    if("金融商品转让".equals(resMap.get("subtype"))) {
                        result.put("YJZZS_JRSPZR_SX", resMap);
                    }else if("贷款服务".equals(resMap.get("subtype"))) {
                        result.put("YJZZS_DKFW_SX", resMap);
                    }else if("应交增值税".equals(resMap.get("subtype"))) {
                        result.put("YJZZS_YJZZS_SX", resMap);
                    }
                }
            }else if("应交附加税".equals(resMap.get("type"))) {
                if("未实现".equals(resMap.get("realizedFlag"))) {
                    if("应交城建费附加".equals(resMap.get("subtype"))) {
                        result.put("YJFJS_YJCJFFJ_WSX", resMap);
                    }else if("应交教育费附加".equals(resMap.get("subtype"))) {
                        result.put("YJFJS_YJJYFJ_WSX", resMap);
                    }else if("应交地方教育费附加".equals(resMap.get("subtype"))) {
                        result.put("YJFJS_YJDFJYFJ_WSX", resMap);
                    }
                }else {
                    if("应交城建费附加".equals(resMap.get("subtype"))) {
                        result.put("YJFJS_YJCJFFJ_SX", resMap);
                    }else if("应交教育费附加".equals(resMap.get("subtype"))) {
                        result.put("YJFJS_YJJYFJ_SX", resMap);
                    }else if("应交地方教育费附加".equals(resMap.get("subtype"))) {
                        result.put("YJFJS_YJDFJYFJ_SX", resMap);
                    }else if("应交附加税-中转".equals(resMap.get("subtype"))) {
                        result.put("YJFJS_YJFJSZZ_SX", resMap);
                    }
                }
            }
        }

        return result;
    }

    /**
     * 处理sheet页O310Vat的数据
     * @author Dai Zong 2018年11月4日
     *
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private Map<String,Object> getO310Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();


        //========process dataMap for detail view begin========
        Map<String,Object> detail = new HashMap<>();

        Map<String,Object> item1 = new HashMap<>();

        item1.put("JJZR", new HashMap<>());
        item1.put("GJSZR", new HashMap<>());
        item1.put("QZZR", new HashMap<>());
        item1.put("QHZR", new HashMap<>());

        List<Map<String,Object>> resMapList = (List<Map<String,Object>>)this.dao.findForList("OExportMapper.selectO310DetailData", queryMap);
        if(resMapList == null) {
            resMapList = new ArrayList<>();
        }

        for(Map<String, Object> resMap : resMapList) {
            if("金融商品转让".equals(resMap.get("item"))) {
                if("基金投资收益".equals(resMap.get("subItem"))) {
                    item1.put("JJZR", resMap);
                }else if("贵金属投资收益".equals(resMap.get("subItem"))) {
                    item1.put("GJSZR", resMap);
                }else if("权证损益".equals(resMap.get("subItem"))) {
                    item1.put("QZZR", resMap);
                }else if("衍生工具收益".equals(resMap.get("subItem"))) {
                    item1.put("QHZR", resMap);
                }
            }
        }

        detail.put("JRSPZR", item1);

        result.put("detail", detail);
        //========process dataMap for detail view end========

        //========process dataMap for summary view begin========
        Map<String,Object> summary = new HashMap<>();

        item1 = new HashMap<>();
        Map<String,Object> item2 = new HashMap<>();

        item1.put("XJ", new HashMap<>());
        item2.put("YSLXSR", new HashMap<>());

        resMapList = (List<Map<String,Object>>)this.dao.findForList("OExportMapper.selectO310SummaryData", queryMap);
        if(resMapList == null) {
            resMapList = new ArrayList<>();
        }

        for(Map<String, Object> resMap : resMapList) {
            if("金融商品转让".equals(resMap.get("item"))) {
                if("小计".equals(resMap.get("subItem"))) {
                    item1.put("XJ", resMap);
                }
            }else if("贷款服务".equals(resMap.get("item"))) {
                if("应税利息收入".equals(resMap.get("subItem"))) {
                    item2.put("YSLXSR", resMap);
                }
            }
        }

        summary.put("JRSPZR", item1);
        summary.put("DKFW", item2);

        result.put("summary", summary);
        //========process dataMap for summary view end========

        //========process dataMap for extra view begin========
        Map<String,Object> extra = new HashMap<>();

        extra.put("YJCJFFJ", new HashMap<>());
        extra.put("YJJYFFJ", new HashMap<>());
        extra.put("YJDFJYFFJ", new HashMap<>());

        resMapList = (List<Map<String,Object>>)this.dao.findForList("OExportMapper.selectO310ExtraData", queryMap);
        if(resMapList == null) {
            resMapList = new ArrayList<>();
        }
        for(Map<String, Object> resMap : resMapList) {
            if("应交城建费附加".equals(resMap.get("item"))) {
                extra.put("YJCJFFJ", resMap);
            }else if("应交教育费附加".equals(resMap.get("item"))) {
                extra.put("YJJYFFJ", resMap);
            }else if("应交地方教育费附加".equals(resMap.get("item"))) {
                extra.put("YJDFJYFFJ", resMap);
            }
        }

        result.put("extra", extra);
        //========process dataMap for extra view end========

        return result;
    }

    /**
     * 处理sheet页O311的数据
     * @author Dai Zong 2018年11月4日
     *
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private Map<String,Object> getO311Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();

        //========process dataMap for summary view begin========
        Map<String,Object> summary = (Map<String,Object>)this.dao.findForObject("OExportMapper.selectO311SummaryData", queryMap);
        if(summary == null) {
            summary = new HashMap<>();
        }

        result.put("summary", summary);
        //========process dataMap for summary view end========

        //========process dataMap for detail view begin========
        List<Map<String,Object>> detailList = (List<Map<String,Object>>)this.dao.findForList("OExportMapper.selectO311DetailData", queryMap);
        if(detailList == null) {
            detailList = new ArrayList<>();
        }
        result.put("detailList", detailList);
        result.put("detailListCount", detailList.size());
        //========process dataMap for detail view end========

        return result;
    }

}
