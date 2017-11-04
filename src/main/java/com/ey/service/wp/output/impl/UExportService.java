package com.ey.service.wp.output.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.ey.service.wp.output.UExportManager;
import com.ey.util.fileexport.Constants;
import com.ey.util.fileexport.FileExportUtils;
import com.ey.util.fileexport.FreeMarkerUtils;

/**
 * @name UExportService
 * @description 底稿输出服务--U
 * @author Dai Zong	2017年11月1日
 */
@Service("uExportService")
public class UExportService extends BaseExportService implements UExportManager {
    
    /**
     * 生成文件内容
     * @author Dai Zong 2017年11月1日
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
        
        dataMap.put("U", this.getUData(fundId, period));
        dataMap.put("U300", this.getU300Data(fundId, period));
//        dataMap.put("E400", this.getE400Data(fundId, period));
//        dataMap.put("E410", this.getE410Data(fundId, period));
//        dataMap.put("E41X", this.getE41XData(fundId, period));
//        dataMap.put("E500", this.getE500Data(fundId, period));
//        dataMap.put("E600", this.getE600Data(fundId, period));

        return FreeMarkerUtils.processTemplateToString(dataMap, Constants.EXPORT_TEMPLATE_FOLDER_PATH, Constants.EXPORT_TEMPLATE_FILE_NAME_U);
    }

    @Override
    public boolean doExport(HttpServletRequest request, HttpServletResponse response, String fundId, Long period) throws Exception {
        String xmlStr = this.generateFileContent(fundId, period);
        FileExportUtils.writeFileToHttpResponse(request, response, Constants.EXPORT_AIM_FILE_NAME_U, xmlStr);
        return true;
    }

    @Override
    public boolean doExport(String folederName, String fileName, String fundId, Long period) throws Exception {
        String xmlStr = this.generateFileContent(fundId, period);
        FileExportUtils.writeFileToDisk(folederName, fileName, xmlStr);
        return true;
    }
    
    /**
     * 处理sheet页U的数据
     * @author Dai Zong 2017年11月2日
     * 
     * @param fundId
     * @param period
     * @return
     * @throws Exception
     */
    private Map<String,Object> getUData(String fundId, Long period) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, period);
        Map<String, Object> result = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> UMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectUData", queryMap);
        if(UMetaDataList == null) {
            UMetaDataList = new ArrayList<>(); 
        }
        
        Map<String,Object> M6011 = new HashMap<>();
        M6011.put("S1", new HashMap<String,Object>());
        M6011.put("S2", new HashMap<String,Object>());
        M6011.put("S3", new HashMap<String,Object>());
        M6011.put("S4", new HashMap<String,Object>());
        M6011.put("S5", new HashMap<String,Object>());
        
        Map<String,Object> M6111 = new HashMap<>();
        M6111.put("S1", new HashMap<String,Object>());
        M6111.put("S2", new HashMap<String,Object>());
        M6111.put("S3", new HashMap<String,Object>());
        M6111.put("S4", new HashMap<String,Object>());
        M6111.put("S5", new HashMap<String,Object>());
        M6111.put("S6", new HashMap<String,Object>());
        
        result.put("KM6101", new HashMap<String,Object>());
        result.put("KM6302", new HashMap<String,Object>());
        result.put("KM6403", new HashMap<String,Object>());
        result.put("KM6404", new HashMap<String,Object>());
        result.put("KM6406", new HashMap<String,Object>());
        result.put("KM6407", new HashMap<String,Object>());
        result.put("KM6411", new HashMap<String,Object>());
        result.put("KM6605", new HashMap<String,Object>());
        
        for(Map<String,Object> map : UMetaDataList) {
            if("6011".equals(map.get("accountNum"))) {
                if("存款利息收入".equals(map.get("item"))) {
                    M6011.put("S1", map);
                }else if("债券利息收入".equals(map.get("item"))) {
                    M6011.put("S2", map);
                }else if("资产支持证券利息收入".equals(map.get("item"))) {
                    M6011.put("S3", map);                
                }else if("买入返售金融资产收入".equals(map.get("item"))) {
                    M6011.put("S4", map);
                }else if("其他利息收入".equals(map.get("item"))) {
                    M6011.put("S5", map);
                }
            }else if("6111".equals(map.get("accountNum"))) {
                if("股票投资收益".equals(map.get("item"))) {
                    M6111.put("S1", map);
                }else if("基金投资收益".equals(map.get("item"))) {
                    M6111.put("S2", map);
                }else if("债券投资收益".equals(map.get("item"))) {
                    M6111.put("S3", map);                
                }else if("资产支持证券投资收益".equals(map.get("item"))) {
                    M6111.put("S4", map);
                }else if("衍生工具收益".equals(map.get("item"))) {
                    M6111.put("S5", map);
                }else if("股利收益".equals(map.get("item"))) {
                    M6111.put("S6", map);
                }
            }else {
                result.put("KM" + map.get("accountNum"), map);
            }
        }
        
        result.put("KM6011", M6011);
        result.put("KM6111", M6111);
        
        return result;
    }
    
    /**
     * 处理sheet页U300的数据
     * @author Dai Zong 2017年11月4日
     * 
     * @param fundId
     * @param period
     * @return
     * @throws Exception
     */
    private Map<String,Object> getU300Data(String fundId, Long period) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, period);
        Map<String, Object> result = new HashMap<String,Object>();
        
        Map<String, Object> main = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U300MainMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU300MainData", queryMap);
        if(U300MainMetaDataList == null) {
            U300MainMetaDataList = new ArrayList<>(); 
        }
        Map<String,Object> temp = new HashMap<>();
        for(Map<String,Object> map : U300MainMetaDataList) {
            temp.put(String.valueOf(map.get("item")), map);
        }
        main.put("S1", temp.get("股票投资收益")==null?new HashMap<String,Object>():temp.get("股票投资收益"));
        main.put("S2", temp.get("债券投资收益")==null?new HashMap<String,Object>():temp.get("债券投资收益"));
        main.put("S3", temp.get("资产支持性证券投资收益")==null?new HashMap<String,Object>():temp.get("资产支持性证券投资收益"));
        main.put("S4", temp.get("基金投资收益")==null?new HashMap<String,Object>():temp.get("基金投资收益"));
        main.put("S5", temp.get("贵金属投资收益")==null?new HashMap<String,Object>():temp.get("贵金属投资收益"));
        main.put("S6", temp.get("衍生工具收益")==null?new HashMap<String,Object>():temp.get("衍生工具收益"));
        main.put("S7", temp.get("股利收益")==null?new HashMap<String,Object>():temp.get("股利收益"));
        main.put("S8", temp.get("存款利息收入")==null?new HashMap<String,Object>():temp.get("存款利息收入"));
        main.put("S9", temp.get("债券利息收入")==null?new HashMap<String,Object>():temp.get("债券利息收入"));
        main.put("S10", temp.get("资产支持证券利息收入")==null?new HashMap<String,Object>():temp.get("资产支持证券利息收入"));
        main.put("S11", temp.get("买入返售证券收入")==null?new HashMap<String,Object>():temp.get("买入返售证券收入"));
        main.put("S12", temp.get("其他利息收入")==null?new HashMap<String,Object>():temp.get("其他利息收入"));
        
        Map<String, Object> dividend = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U300DividendMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU300DividendData", queryMap);
        if(U300DividendMetaDataList == null) {
            U300DividendMetaDataList = new ArrayList<>(); 
        }
        temp = new HashMap<>();
        for(Map<String,Object> map : U300DividendMetaDataList) {
            temp.put(String.valueOf(map.get("item")), map);
        }
        dividend.put("S1", temp.get("股票")==null?new HashMap<String,Object>():temp.get("股票"));
        dividend.put("S2", temp.get("基金")==null?new HashMap<String,Object>():temp.get("基金"));
        
        Map<String, Object> interest = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U300InterestMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU300InterestData", queryMap);
        if(U300InterestMetaDataList == null) {
            U300InterestMetaDataList = new ArrayList<>(); 
        }
        temp = new HashMap<>();
        for(Map<String,Object> map : U300InterestMetaDataList) {
            temp.put(String.valueOf(map.get("item")), map);
        }
        interest.put("S1", temp.get("活期存款利息收入")==null?new HashMap<String,Object>():temp.get("活期存款利息收入"));
        interest.put("S2", temp.get("定期存款利息收入")==null?new HashMap<String,Object>():temp.get("定期存款利息收入"));
        interest.put("S3", temp.get("其他存款利息收入")==null?new HashMap<String,Object>():temp.get("其他存款利息收入"));
        interest.put("S4", temp.get("结算备付金利息收入")==null?new HashMap<String,Object>():temp.get("结算备付金利息收入"));
        interest.put("S5", temp.get("其他")==null?new HashMap<String,Object>():temp.get("其他"));
        
        result.put("main", main);
        result.put("dividend", dividend);
        result.put("interest", interest);
        return result;
    }

}
