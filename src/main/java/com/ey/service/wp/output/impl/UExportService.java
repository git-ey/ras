package com.ey.service.wp.output.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
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
        
        dataMap.put("U", this.getUData(fundId, periodStr));
        dataMap.put("U300", this.getU300Data(fundId, periodStr));
        dataMap.put("U320", this.getU320Data(fundId, periodStr));
        dataMap.put("U400", this.getU400Data(fundId, periodStr));
        dataMap.put("U500", this.getU500Data(fundId, periodStr));
        dataMap.put("U600", this.getU600Data(fundId, periodStr));
        dataMap.put("U10000", this.getU10000Data(fundId, periodStr));

        return FreeMarkerUtils.processTemplateToString(dataMap, Constants.EXPORT_TEMPLATE_FOLDER_PATH, Constants.EXPORT_TEMPLATE_FILE_NAME_U);
    }

    @Override
    public boolean doExport(HttpServletRequest request, HttpServletResponse response, String fundId, String periodStr) throws Exception {
        Map<String, String> fundInfo = this.selectFundInfo(fundId);
        fundInfo.put("periodStr", periodStr);
        String xmlStr = this.generateFileContent(fundId, periodStr, fundInfo);
        FileExportUtils.writeFileToHttpResponse(request, response, FreeMarkerUtils.simpleReplace(Constants.EXPORT_AIM_FILE_NAME_U, fundInfo), xmlStr);
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
     * 处理sheet页U的数据
     * @author Dai Zong 2017年11月2日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getUData(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
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
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getU300Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
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
    
    /**
     * 处理sheet页U320的数据
     * @author Dai Zong 2017年11月7日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getU320Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U320MetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU320Data", queryMap);
        if(U320MetaDataList == null) {
            U320MetaDataList = new ArrayList<>(); 
        }
        
        result.put("list", U320MetaDataList);
        result.put("count", U320MetaDataList.size());
        return result;
    }

    /**
     * 处理sheet页U400的数据
     * @author Dai Zong 2017年11月4日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getU400Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U400MetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU400Data", queryMap);
        if(U400MetaDataList == null) {
            U400MetaDataList = new ArrayList<>(); 
        }
        
        Map<String,Object> temp = new HashMap<>();
        for(Map<String,Object> map : U400MetaDataList) {
            temp.put(String.valueOf(map.get("item")), map);
        }
        result.put("S1", temp.get("赎回费收入")==null?new HashMap<String,Object>():temp.get("赎回费收入"));
        result.put("S2", temp.get("转换费收入")==null?new HashMap<String,Object>():temp.get("转换费收入"));
        result.put("S3", temp.get("印花税返还收入")==null?new HashMap<String,Object>():temp.get("印花税返还收入"));
        result.put("S4", temp.get("其他收入")==null?new HashMap<String,Object>():temp.get("其他收入"));
        
        return result;
    }
    
    /**
     * 处理sheet页U500的数据
     * @author Dai Zong 2017年11月4日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getU500Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();
        
        Map<String, Object> main = new HashMap<String,Object>();
        Map<String, Object> trade = new HashMap<String,Object>();
        Map<String, Object> bank = new HashMap<String,Object>();
        Map<String, Object> KM6407 = new HashMap<String,Object>();
        Map<String, Object> KM6411 = new HashMap<String,Object>();
        
        trade.put("S1", new HashMap<String,Object>());
        trade.put("S2", new HashMap<String,Object>());
        trade.put("S3", new HashMap<String,Object>());
        trade.put("S4", new HashMap<String,Object>());
        trade.put("S5", new HashMap<String,Object>());
        bank.put("S1", new HashMap<String,Object>());
        bank.put("S2", new HashMap<String,Object>());
        KM6411.put("S1", new HashMap<String,Object>());
        KM6411.put("S2", new HashMap<String,Object>());
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U500MainMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU500MainData", queryMap);
        if(U500MainMetaDataList == null) {
            U500MainMetaDataList = new ArrayList<>(); 
        }
        
        for(Map<String,Object> map : U500MainMetaDataList) {
            if("6407".equals(map.get("accountNum"))) {
                if("交易所".equals(map.get("type"))) {
                    if("股票交易费用".equals(map.get("item"))) {
                        trade.put("S1", map);
                    }else if("基金交易费用".equals(map.get("item"))) {
                        trade.put("S2", map);
                    }else if("债券交易费用".equals(map.get("item"))) {
                        trade.put("S3", map);
                    }else if("期货交易费用".equals(map.get("item"))) {
                        trade.put("S4", map);
                    }else if("资产支持证券交易费用".equals(map.get("item"))) {
                        trade.put("S5", map);
                    }
                }else if("银行间".equals(map.get("type"))) {
                    if("结算服务费".equals(map.get("item"))) {
                        bank.put("S1", map);
                    }else if("交易手续费".equals(map.get("item"))) {
                        bank.put("S2", map);
                    }
                }
            }else if("6411".equals(map.get("accountNum"))) {
                if("卖出回购证券支出".equals(map.get("item"))) {
                    KM6411.put("S1", map);
                }else if("银行借款利息支出".equals(map.get("item"))) {
                    KM6411.put("S2", map);
                }
            }
        }
        
        KM6407.put("trade", trade);
        KM6407.put("bank", bank);
        main.put("KM6407", KM6407);
        main.put("KM6411", KM6411);
        
        Map<String, Object> trxFee = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U500TrxFeeMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU500TrxFeeData", queryMap);
        if(U500TrxFeeMetaDataList == null) {
            U500TrxFeeMetaDataList = new ArrayList<>(); 
        }
        Map<String,Object> temp = new HashMap<>();
        for(Map<String,Object> map : U500TrxFeeMetaDataList) {
            temp.put(String.valueOf(map.get("market")), map);
        }
        
        trxFee.put("SH", temp.get("上交所") ==null?new HashMap<String,Object>():temp.get("上交所"));
        trxFee.put("SZ", temp.get("深交所") ==null?new HashMap<String,Object>():temp.get("深交所"));
        
        Map<String, Object> test = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U500TestMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU500TestData", queryMap);
        if(CollectionUtils.isNotEmpty(U500TestMetaDataList)) {
            test.put("commission", U500TestMetaDataList.get(0).get("commission"));
            test.put("perClient", U500TestMetaDataList.get(0).get("perClient"));
        }
        
        result.put("main", main);
        result.put("trxFee", trxFee);
        result.put("test", test);
        return result;
    }
    
    /**
     * 处理sheet页U600的数据
     * @author Dai Zong 2017年11月4日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getU600Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U600MetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU600Data", queryMap);
        if(U600MetaDataList == null) {
            U600MetaDataList = new ArrayList<>(); 
        }
        
        Map<String,Object> temp = new HashMap<>();
        for(Map<String,Object> map : U600MetaDataList) {
            temp.put(String.valueOf(map.get("item")), map);
        }
        
        result.put("S1", temp.get("审计费")==null?new HashMap<String,Object>():temp.get("审计费"));
        result.put("S2", temp.get("信息披露费")==null?new HashMap<String,Object>():temp.get("信息披露费"));
        result.put("S3", temp.get("上市年费")==null?new HashMap<String,Object>():temp.get("上市年费"));
        result.put("S4", temp.get("分红手续费")==null?new HashMap<String,Object>():temp.get("分红手续费"));
        result.put("S5", temp.get("指数使用费")==null?new HashMap<String,Object>():temp.get("指数使用费"));
        result.put("S6", temp.get("银行划款费用")==null?new HashMap<String,Object>():temp.get("银行划款费用"));
        result.put("S7", temp.get("账户维护费")==null?new HashMap<String,Object>():temp.get("账户维护费"));
        result.put("S8", temp.get("交易费用")==null?new HashMap<String,Object>():temp.get("交易费用"));
        result.put("S9", temp.get("回购手续费")==null?new HashMap<String,Object>():temp.get("回购手续费"));
        result.put("S10", temp.get("其他")==null?new HashMap<String,Object>():temp.get("其他"));

        return result;
    }
    
    /**
     * 处理sheet页U10000的数据
     * @author Dai Zong 2017年11月5日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getU10000Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();
        
        Map<String, Object> interest = new HashMap<String,Object>();
        interest.put("S1", new HashMap<String,Object>());
        interest.put("S2", new HashMap<String,Object>());
        interest.put("S3", new HashMap<String,Object>());
        interest.put("S4", new HashMap<String,Object>());
        interest.put("S5", new HashMap<String,Object>());
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000InterestMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000InterestData", queryMap);
        if(U10000InterestMetaDataList == null) {
            U10000InterestMetaDataList = new ArrayList<>(); 
        }
        for(Map<String,Object> map : U10000InterestMetaDataList) {
            if("活期存款利息收入".equals(map.get("item"))) {
                interest.put("S1", map);
            }else if("定期存款利息收入".equals(map.get("item"))) {
                interest.put("S2", map);
            }else if("其他存款利息收入".equals(map.get("item"))) {
                interest.put("S3", map);
            }else if("结算备付金利息收入".equals(map.get("item"))) {
                interest.put("S4", map);
            }else if("其他".equals(map.get("item"))) {
                interest.put("S5", map);
            }
        }
        result.put("interest", interest);
        
        Map<String, Object> dividend = new HashMap<String,Object>();
        dividend.put("S1", new HashMap<String,Object>());
        dividend.put("S2", new HashMap<String,Object>());
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000DividendMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000DividendData", queryMap);
        if(U10000DividendMetaDataList == null) {
            U10000DividendMetaDataList = new ArrayList<>(); 
        }
        for(Map<String,Object> map : U10000DividendMetaDataList) {
            if("股票投资产生的股利收益".equals(map.get("item"))) {
                dividend.put("S1", map);
            }else if("基金投资产生的股利收益".equals(map.get("item"))) {
                dividend.put("S2", map);
            }
        }
        result.put("dividend", dividend);
        
        Map<String, Object> other_r = new HashMap<String,Object>();
        other_r.put("S1", new HashMap<String,Object>());
        other_r.put("S2", new HashMap<String,Object>());
        other_r.put("S3", new HashMap<String,Object>());
        other_r.put("S4", new HashMap<String,Object>());
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000OtherRMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000OtherRData", queryMap);
        if(U10000OtherRMetaDataList == null) {
            U10000OtherRMetaDataList = new ArrayList<>(); 
        }
        for(Map<String,Object> map : U10000OtherRMetaDataList) {
            if("基金赎回费收入".equals(map.get("item"))) {
                other_r.put("S1", map);
            }else if("基金转换费收入".equals(map.get("item"))) {
                other_r.put("S2", map);
            }else if("印花税返还".equals(map.get("item"))) {
                other_r.put("S3", map);
            }else if("其他".equals(map.get("item"))) {
                other_r.put("S4", map);
            }
        }
        result.put("other_r", other_r);
        
        Map<String, Object> trxFee = new HashMap<String,Object>();
        trxFee.put("S1", new HashMap<String,Object>());
        trxFee.put("S2", new HashMap<String,Object>());
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000TrxFeeMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000TrxFeeData", queryMap);
        if(U10000TrxFeeMetaDataList == null) {
            U10000TrxFeeMetaDataList = new ArrayList<>(); 
        }
        for(Map<String,Object> map : U10000TrxFeeMetaDataList) {
            if("交易所市场交易费用".equals(map.get("item"))) {
                trxFee.put("S1", map);
            }else if("银行间市场交易费用".equals(map.get("item"))) {
                trxFee.put("S2", map);
            }
        }
        result.put("trxFee", trxFee);
        
        Map<String, Object> other_c = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000OtherCMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000OtherCData", queryMap);
        if(U10000OtherCMetaDataList == null) {
            U10000OtherCMetaDataList = new ArrayList<>(); 
        }
        other_c.put("list", U10000OtherCMetaDataList);
        other_c.put("count", U10000OtherCMetaDataList.size());
        result.put("other_c", other_c);
        
        return result;
    }
    
}
