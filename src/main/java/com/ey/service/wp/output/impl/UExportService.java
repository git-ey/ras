package com.ey.service.wp.output.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import com.ey.service.system.report.ReportManager;
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

    @Resource(name = "reportService")
    private ReportManager reportService;
   
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
        Map<String, Object> dataMap = new HashMap<>();
        
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
        dataMap.put("U600Test", this.getU600TestData(fundId, periodStr));
        dataMap.put("U800", this.getU800Data(fundId, periodStr));
        dataMap.put("U900", this.getU900Data(fundId, periodStr));
        dataMap.put("U1000", this.getU1000Data(fundId, periodStr));
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
        Map<String, Object> result = new HashMap<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> UMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectUData", queryMap);
        if(UMetaDataList == null) {
            UMetaDataList = new ArrayList<>(); 
        }
        
        Map<String,Object> M6011 = new HashMap<>();
        M6011.put("S1", new HashMap<>());
        M6011.put("S2", new HashMap<>());
        M6011.put("S3", new HashMap<>());
        M6011.put("S4", new HashMap<>());
        M6011.put("S45", new HashMap<>()); // yury,20200901,U Lead新增证券出借业务
        M6011.put("S5", new HashMap<>());
        
        Map<String,Object> M6111 = new HashMap<>();
        M6111.put("S1", new HashMap<>());
        M6111.put("S2", new HashMap<>());
        M6111.put("S3", new HashMap<>());
        M6111.put("S4", new HashMap<>());
        M6111.put("S5", new HashMap<>());
        M6111.put("S6", new HashMap<>());
        M6111.put("S7", new HashMap<>());
        M6111.put("S8", new HashMap<>()); // linnea,20220920,U I9新增
        
        result.put("KM6101", new HashMap<>());
        result.put("KM6302", new HashMap<>());
        result.put("KM6403", new HashMap<>());
        result.put("KM6404", new HashMap<>());
        result.put("KM6406", new HashMap<>());
        result.put("KM6408", new HashMap<>());// linnea,20220920,U I9新增
        result.put("KM6411", new HashMap<>());
        result.put("KM6802", new HashMap<>());
        result.put("KM6605", new HashMap<>());
        result.put("KM6702", new HashMap<>());// linnea,20220920,U I9新增

        // yury,20200901,U Lead新增证券出借业务
        for(Map<String,Object> map : UMetaDataList) {
            if("6011".equals(map.get("accountNum"))){
                switch ((String)map.get("item")){
                    case "存款利息收入":
                        M6011.put("S1", map);
                        break;
                    case "债券利息收入":
                        M6011.put("S2", map);
                        break;
                    case "资产支持证券利息收入":
                        M6011.put("S3", map);
                        break;
                    case "买入返售金融资产收入":
                        M6011.put("S4", map);
                        break;
                    case "证券出借利息收入":
                        M6011.put("S45", map);
                        break;
                    case "其他利息收入":
                        M6011.put("S5", map);
                        break;
                    }
            }else if ("6111".equals(map.get("accountNum"))){
                switch ((String)map.get("item")){
                    case "股票投资收益":
                        M6111.put("S1", map);
                        break;
                    case "基金投资收益":
                        M6111.put("S2", map);
                        break;
                    case "债券投资收益":
                        M6111.put("S3", map);
                        break;
                    case "资产支持证券投资收益":
                        M6111.put("S4", map);
                        break;
                    case "贵金属投资收益":
                        M6111.put("S5", map);
                        break;
                    case "衍生工具收益":
                        M6111.put("S6", map);
                        break;
                    case "股利收益":
                        M6111.put("S7", map);
                        break;
                    case "其他投资收益":
                        M6111.put("S8", map); // linnea,20220920,U I9新增
                        break;
                }
            }else {
                result.put("KM" + map.get("accountNum"), map);
            }

        // for(Map<String,Object> map : UMetaDataList) {
        //     if("6011".equals(map.get("accountNum"))) {
        //         if("存款利息收入".equals(map.get("item"))) {
        //             M6011.put("S1", map);
        //         }else if("债券利息收入".equals(map.get("item"))) {
        //             M6011.put("S2", map);
        //         }else if("资产支持证券利息收入".equals(map.get("item"))) {
        //             M6011.put("S3", map);                
        //         }else if("买入返售金融资产收入".equals(map.get("item"))) {
        //             M6011.put("S4", map);
        //         }else if("其他利息收入".equals(map.get("item"))) {
        //             M6011.put("S5", map);
        //         }
        //     }else if("6111".equals(map.get("accountNum"))) {
        //         if("股票投资收益".equals(map.get("item"))) {
        //             M6111.put("S1", map);
        //         }else if("基金投资收益".equals(map.get("item"))) {
        //             M6111.put("S2", map);
        //         }else if("债券投资收益".equals(map.get("item"))) {
        //             M6111.put("S3", map);
        //         }else if("资产支持证券投资收益".equals(map.get("item"))) {
        //             M6111.put("S4", map);
        //         }else if("贵金属投资收益".equals(map.get("item"))) {
        //             M6111.put("S5", map);
        //         }else if("衍生工具收益".equals(map.get("item"))) {
        //             M6111.put("S6", map);
        //         }else if("股利收益".equals(map.get("item"))) {
        //             M6111.put("S7", map);
        //         }
        //     }else {
        //         result.put("KM" + map.get("accountNum"), map);
        //     }
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
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> main = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U300MainMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU300MainData", queryMap);
        if(U300MainMetaDataList == null) {
            U300MainMetaDataList = new ArrayList<>(); 
        }
        Map<String,Object> temp = new HashMap<>();
        // for(Map<String,Object> map : U300MainMetaDataList) {
        //     temp.put(String.valueOf(map.get("item")), map);
        // }
        // main.put("S1", temp.get("股票投资收益")==null?new HashMap<>():temp.get("股票投资收益"));
        // main.put("S2", temp.get("债券投资收益")==null?new HashMap<>():temp.get("债券投资收益"));
        // main.put("S3", temp.get("资产支持证券投资收益")==null?new HashMap<>():temp.get("资产支持证券投资收益"));
        // main.put("S4", temp.get("基金投资收益")==null?new HashMap<>():temp.get("基金投资收益"));
        // main.put("S5", temp.get("贵金属投资收益")==null?new HashMap<>():temp.get("贵金属投资收益"));
        // main.put("S6", temp.get("衍生工具收益")==null?new HashMap<>():temp.get("衍生工具收益"));
        // main.put("S7", temp.get("股利收益")==null?new HashMap<>():temp.get("股利收益"));
        // main.put("S8", temp.get("存款利息收入")==null?new HashMap<>():temp.get("存款利息收入"));
        // main.put("S9", temp.get("债券利息收入")==null?new HashMap<>():temp.get("债券利息收入"));
        // main.put("S10", temp.get("资产支持证券利息收入")==null?new HashMap<>():temp.get("资产支持证券利息收入"));
        // main.put("S11", temp.get("买入返售金融资产收入")==null?new HashMap<>():temp.get("买入返售金融资产收入"));
        // main.put("S12", temp.get("其他利息收入")==null?new HashMap<>():temp.get("其他利息收入"));
        // main.put("S13", temp.get("其他投资收益")==null?new HashMap<>():temp.get("其他投资收益"));// linnea,20220920,U I9新增
        
        // linnea,20220920,U I9新增
        main.put("S1", new HashMap<>());
        main.put("S2", new HashMap<>());
        main.put("S21", new HashMap<>());
        main.put("S22", new HashMap<>());
        main.put("S3", new HashMap<>());
        main.put("S31", new HashMap<>());
        main.put("S32", new HashMap<>());
        main.put("S4", new HashMap<>());
        main.put("S5", new HashMap<>());
        main.put("S6", new HashMap<>());
        main.put("S7", new HashMap<>());
        main.put("S8", new HashMap<>());
        main.put("S9", new HashMap<>());
        main.put("S10", new HashMap<>());
        main.put("S11", new HashMap<>());
        main.put("S12", new HashMap<>());
        main.put("S13", new HashMap<>());
        
        for(Map<String,Object> map : U300MainMetaDataList) {
            switch ((String)map.get("item")){
                case "股票投资收益":
                    main.put("S1", map);
                    break;
                case "债券投资收益":
                    if("其中：差价收入".equals(map.get("item2"))) {
                        main.put("S21", map);
                    }else if("其中：利息收入".equals(map.get("item2"))) {
                        main.put("S22", map);
                    }else{
                        main.put("S2", map);
                    }
                    break;
                case "资产支持证券投资收益":
                    if("其中：差价收入".equals(map.get("item2"))) {
                        main.put("S31", map);
                    }else if("其中：利息收入".equals(map.get("item2"))) {
                        main.put("S32", map);
                    }else{
                        main.put("S3", map);
                    }
                    break;
                case "基金投资收益":
                    main.put("S4", map);
                    break;
                case "贵金属投资收益":
                    main.put("S5", map);
                    break;
                case "衍生工具收益":
                    main.put("S6", map);
                    break;
                case "股利收益":
                    main.put("S7", map);
                    break;
                case "存款利息收入":
                    main.put("S8", map);
                    break;
                case "债券利息收入":
                    main.put("S9", map);
                    break;
                case "资产支持证券利息收入":
                    main.put("S10", map);
                    break;
                case "买入返售金融资产收入":
                    main.put("S11", map);
                    break;
                case "其他利息收入":
                    main.put("S12", map);
                    break;
                case "其他投资收益":
                    main.put("S13", map);
                    break;
            }
        }


        Map<String, Object> C410 = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> C410DataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectC410Data", queryMap);
        if(C410DataList == null) {
            C410DataList = new ArrayList<>(); 
        }
        C410.put("list", C410DataList);
        C410.put("count", C410DataList.size());


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
        dividend.put("S1", temp.get("股票")==null?new HashMap<>():temp.get("股票"));
        dividend.put("S15", temp.get("证券出借")==null?new HashMap<>():temp.get("证券出借")); // yury,20200911,U300新增证券出借业务
        dividend.put("S2", temp.get("基金")==null?new HashMap<>():temp.get("基金"));
        
        Map<String, Object> interest = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U300InterestMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU300InterestData", queryMap);
        if(U300InterestMetaDataList == null) {
            U300InterestMetaDataList = new ArrayList<>(); 
        }

        interest.put("S2", new HashMap<>());
        temp = new HashMap<>();
        for(Map<String,Object> map : U300InterestMetaDataList) {
            temp.put(String.valueOf(map.get("item")), map);
        }
        interest.put("S1", temp.get("活期存款利息收入")==null?new HashMap<>():temp.get("活期存款利息收入"));
        interest.put("S2", temp.get("定期存款利息收入")==null?new HashMap<>():temp.get("定期存款利息收入"));
        interest.put("S3", temp.get("其他存款利息收入")==null?new HashMap<>():temp.get("其他存款利息收入"));
        interest.put("S4", temp.get("结算备付金利息收入")==null?new HashMap<>():temp.get("结算备付金利息收入"));
        interest.put("S5", temp.get("其他")==null?new HashMap<>():temp.get("其他"));
        
        result.put("main", main);
        result.put("C410", C410);
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
        Map<String, Object> result = new HashMap<>();
        
        String finSys = (String)this.dao.findForObject("UExportMapper.selectFinSys", queryMap);
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U320MetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU320Data", queryMap);
        if(U320MetaDataList == null) {
            U320MetaDataList = new ArrayList<>(); 
        }
        
        result.put("finSys", finSys);
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
        Map<String, Object> result = new HashMap<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U400MetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU400Data", queryMap);
        if(U400MetaDataList == null) {
            U400MetaDataList = new ArrayList<>(); 
        }
        
        Map<String,Object> temp = new HashMap<>();
        for(Map<String,Object> map : U400MetaDataList) {
            temp.put(String.valueOf(map.get("item")), map);
        }
        result.put("S1", temp.get("基金赎回费收入")==null?new HashMap<>():temp.get("基金赎回费收入"));
        result.put("S2", temp.get("基金转换费收入")==null?new HashMap<>():temp.get("基金转换费收入"));
        result.put("S3", temp.get("印花税返还收入")==null?new HashMap<>():temp.get("印花税返还收入"));
        result.put("S5", temp.get("销售服务费返还")==null?new HashMap<>():temp.get("销售服务费返还"));
        result.put("S4", temp.get("其他")==null?new HashMap<>():temp.get("其他"));
        
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
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> main = new HashMap<>();
        Map<String, Object> KM6111 = new HashMap<>();
        Map<String, Object> KM6411 = new HashMap<>();
        
        KM6111.put("S1", new HashMap<>());
        KM6111.put("S2", new HashMap<>());
        KM6111.put("S3", new HashMap<>());
        KM6111.put("S4", new HashMap<>());
        KM6111.put("S5", new HashMap<>());
        KM6111.put("S6", new HashMap<>());
        KM6411.put("S1", new HashMap<>());
        KM6411.put("S2", new HashMap<>());

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U500MainMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU500MainData", queryMap);
        if(U500MainMetaDataList == null) {
            U500MainMetaDataList = new ArrayList<>(); 
        }
        
        for(Map<String,Object> map : U500MainMetaDataList) {
            if("6111".equals(map.get("accountNum"))){
                switch ((String)map.get("subType")){
                    case "股票交易费用":
                        KM6111.put("S1", map);
                        break;
                    case "债券交易费用":
                        KM6111.put("S2", map);
                        break;
                    case "资产支持证券交易费用":
                        KM6111.put("S3", map);
                        break;
                    case "基金交易费用":
                        KM6111.put("S4", map);
                        break;
                    case "衍生工具交易费用":
                        KM6111.put("S5", map);
                        break;
                    case "贵金属交易费用":
                        KM6111.put("S6", map);
                        break;
                    }
            }else if("6411".equals(map.get("accountNum"))) {
                if("卖出回购证券支出".equals(map.get("item"))) {
                    KM6411.put("S1", map);
                }else if("银行借款利息支出".equals(map.get("item"))) {
                    KM6411.put("S2", map);
                }
            }
        }
            
        main.put("KM6111", KM6111);
        main.put("KM6411", KM6411);
        
        Map<String, Object> trxFee = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U500TrxFeeMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU500TrxFeeData", queryMap);
        if(U500TrxFeeMetaDataList == null) {
            U500TrxFeeMetaDataList = new ArrayList<>(); 
        }
        Map<String,Object> temp = new HashMap<>();
        for(Map<String,Object> map : U500TrxFeeMetaDataList) {
            temp.put(String.valueOf(map.get("market")), map);
        }
        
        trxFee.put("SH", temp.get("上交所") ==null?new HashMap<>():temp.get("上交所"));
        trxFee.put("SZ", temp.get("深交所") ==null?new HashMap<>():temp.get("深交所"));
        // trxFee.put("SH", temp.get("上海证券交易所") ==null?new HashMap<>():temp.get("上海证券交易所"));
        // trxFee.put("SZ", temp.get("深圳证券交易所") ==null?new HashMap<>():temp.get("深圳证券交易所"));
        // yury，20201112，U500交易费用测试或新股配售修改
        trxFee.put("SHH", temp.get("沪港通") ==null?new HashMap<>():temp.get("沪港通"));
        trxFee.put("SZH", temp.get("深港通") ==null?new HashMap<>():temp.get("深港通"));
        
        // chenhy，20231206，修改U330为浮动行
        List<Map<String,Object>> U500TrxFeeMetaDataSHList = new ArrayList<>();
        List<Map<String,Object>> U500TrxFeeMetaDataSZList = new ArrayList<>();
        List<Map<String,Object>> U500TrxFeeMetaDataBJList = new ArrayList<>();
        List<Map<String,Object>> U500TrxFeeMetaDataHKList = new ArrayList<>();

        int U500TrxFeeMetaDataSHListCount = 0;
        int U500TrxFeeMetaDataSZListCount = 0;
        int U500TrxFeeMetaDataBJListCount = 0;
        int U500TrxFeeMetaDataHKListCount = 0;
        
        for(Map<String,Object> map : U500TrxFeeMetaDataList) {
            if("上交所".equals(map.get("market"))) {
                U500TrxFeeMetaDataSHList.add(map);
            }else if("深交所".equals(map.get("market"))) {
                U500TrxFeeMetaDataSZList.add(map);
            }else if("北交所".equals(map.get("market"))) {
                U500TrxFeeMetaDataBJList.add(map);
            }else if("沪港通".equals(map.get("market")) || "深港通".equals(map.get("market")) || "港股通".equals(map.get("market"))) {
                U500TrxFeeMetaDataHKList.add(map);
            }
        }
		
        U500TrxFeeMetaDataSHListCount = U500TrxFeeMetaDataSHList.size();
        U500TrxFeeMetaDataSZListCount = U500TrxFeeMetaDataSZList.size();
        U500TrxFeeMetaDataBJListCount = U500TrxFeeMetaDataBJList.size();
        U500TrxFeeMetaDataHKListCount = U500TrxFeeMetaDataHKList.size();

        // chenhy，20231212，增加北交所和港股通的判断条件
        Object dataCheckForBJ = this.dao.findForObject("UExportMapper.checkIfBJHasDataForU330", queryMap);
        Object dataCheckForHK = this.dao.findForObject("UExportMapper.checkIfHKHasDataForU330", queryMap);
        trxFee.put("dataCheckForBJ", dataCheckForBJ == null ? 0d : dataCheckForBJ);
        trxFee.put("dataCheckForHK", dataCheckForHK == null ? 0d : dataCheckForHK);

        trxFee.put("U500TrxFeeMetaDataSHList", U500TrxFeeMetaDataSHList);
        trxFee.put("U500TrxFeeMetaDataSHListCount", U500TrxFeeMetaDataSHListCount);
        trxFee.put("U500TrxFeeMetaDataSZList", U500TrxFeeMetaDataSZList);
        trxFee.put("U500TrxFeeMetaDataSZListCount", U500TrxFeeMetaDataSZListCount);
        trxFee.put("U500TrxFeeMetaDataBJList", U500TrxFeeMetaDataBJList);
        trxFee.put("U500TrxFeeMetaDataBJListCount", U500TrxFeeMetaDataBJListCount);
        trxFee.put("U500TrxFeeMetaDataHKList", U500TrxFeeMetaDataHKList);
        trxFee.put("U500TrxFeeMetaDataHKListCount", U500TrxFeeMetaDataHKListCount);


        Map<String, Object> test = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U500TestMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU500TestData", queryMap);
        if(CollectionUtils.isNotEmpty(U500TestMetaDataList)) {
            test.put("commission", U500TestMetaDataList.get(0).get("commission"));
            test.put("perClient", U500TestMetaDataList.get(0).get("perClient"));
        }
        
        // yury,20201112,U500交易费用测试或新股配售修改
        Map<String, Object> iposubscribe = new HashMap<>();
        iposubscribe.put("iposubscribeamount", 0);
        iposubscribe.put("commissionRate", 0.005);
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U500ipo_subscribe_amount = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU500iposubscrive", queryMap);
        if (U500ipo_subscribe_amount.size() != 0){
            iposubscribe.put("iposubscribeamount", U500ipo_subscribe_amount.get(0).get("iposubscribeamount") == null? 0: U500ipo_subscribe_amount.get(0).get("iposubscribeamount"));
            iposubscribe.put("commissionRate", U500ipo_subscribe_amount.get(0).get("commissionRate") == null? 0.005: U500ipo_subscribe_amount.get(0).get("commissionRate"));
        }

        result.put("iposubscribe", iposubscribe);
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
        Map<String, Object> result = new HashMap<>();
        
        List<Object> list = new ArrayList<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U600MetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU600Data", queryMap);
        if(U600MetaDataList == null) {
            U600MetaDataList = new ArrayList<>(); 
        }
        
        Map<String,Object> temp = new HashMap<>();
        for(Map<String,Object> map : U600MetaDataList) {
            temp.put(String.valueOf(map.get("item")), map);
        }
        
        result.put("S1", temp.get("审计费用")==null?new HashMap<>():temp.get("审计费用"));
        result.put("S2", temp.get("信息披露费")==null?new HashMap<>():temp.get("信息披露费"));
        
        temp.remove("审计费用");
        temp.remove("信息披露费");
        
        temp.forEach((k,v) -> {
            list.add(v);
        });
        
        result.put("list", list);
        result.put("count", list.size());
        
        String testFlag = "N";
        if(temp.get("指数使用费") != null) {
            testFlag = "Y";
        }
        result.put("testFlag", testFlag);
        
//        result.put("S3", temp.get("上市年费")==null?new HashMap<>():temp.get("上市年费"));
//        result.put("S4", temp.get("分红手续费")==null?new HashMap<>():temp.get("分红手续费"));
//        result.put("S5", temp.get("指数使用费")==null?new HashMap<>():temp.get("指数使用费"));
//        result.put("S6", temp.get("银行划款费用")==null?new HashMap<>():temp.get("银行划款费用"));
//        result.put("S7", temp.get("账户维护费")==null?new HashMap<>():temp.get("账户维护费"));
//        result.put("S8", temp.get("交易费用")==null?new HashMap<>():temp.get("交易费用"));
//        result.put("S9", temp.get("回购手续费")==null?new HashMap<>():temp.get("回购手续费"));
//        result.put("S10", temp.get("其他")==null?new HashMap<>():temp.get("其他"));

        return result;
    }
    
    /**
     * 处理sheet页U600Test的数据
     * @author Dai Zong 2017年11月24日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getU600TestData(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<>();
        
        @SuppressWarnings("unchecked")
        Map<String,Object> U600TestDetailData = (Map<String,Object>)this.dao.findForObject("UExportMapper.selectU600TestData", queryMap);
        if(U600TestDetailData == null) {
            U600TestDetailData = new HashMap<>();
        }
        
        @SuppressWarnings("unchecked")
        Map<String,Object> fundIndexfeeRate = (Map<String,Object>)this.dao.findForObject("UExportMapper.selectU600IndexFeeRateData", queryMap);
        if(fundIndexfeeRate == null) {
            fundIndexfeeRate = new HashMap<>();
        }
        
        result.put("detail", U600TestDetailData);
        result.put("fundIndexfeeRate", fundIndexfeeRate);
        
        return result;
    }
    
    /**
     * 处理sheet页U800的数据
     * @author Dai Zong 2018年12月23日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getU800Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> CJFFJ = new HashMap<>(); // 城建费附加
        Map<String, Object> JYFJ = new HashMap<>(); // 教育附加
        Map<String, Object> DFJYFJ = new HashMap<>(); // 地方教育附加
        Map<String, Object> CJFFJ_WSX = new HashMap<>(); // 城建费附加_未实现
        Map<String, Object> JYFJ_WSX = new HashMap<>(); // 教育附加_未实现
        Map<String, Object> DFJYFJ_WSX = new HashMap<>(); // 地方教育附加_未实现
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U800Data = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU800Data", queryMap);
        if(U800Data == null) {
        	U800Data = Collections.emptyList();
        }
        
        for(Map<String,Object> item : U800Data) {
        	if("城建费附加".equals(item.get("itemName"))) {
        		CJFFJ = item;
        	} else if("教育费附加".equals(item.get("itemName"))) {
        		JYFJ = item;
        	} else if("地方教育费附加".equals(item.get("itemName"))) {
        		DFJYFJ = item;
        	} else if("城建费附加_未实现".equals(item.get("itemName"))) {
        		CJFFJ_WSX = item;
        	} else if("教育费附加_未实现".equals(item.get("itemName"))) {
        		JYFJ_WSX = item;
        	} else if("地方教育费附加_未实现".equals(item.get("itemName"))) {
        		DFJYFJ_WSX = item;
        	}
        }
        
        result.put("CJFFJ", CJFFJ);
        result.put("JYFJ", JYFJ);
        result.put("DFJYFJ", DFJYFJ);
        result.put("CJFFJ_WSX", CJFFJ_WSX);
        result.put("JYFJ_WSX", JYFJ_WSX);
        result.put("DFJYFJ_WSX", DFJYFJ_WSX);
        
        return result;
    }
    
    /**
     * 处理sheet页U900的数据
     * @author Dai Zong 2018年12月23日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getU900Data(String fundId, String periodStr) throws Exception{
    	Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
    	Map<String, Object> result = new HashMap<>();
    	
    	Map<String, Object> I1010 = new HashMap<>();// ——股票投资
    	Map<String, Object> I1020 = new HashMap<>();// ——债券投资
    	Map<String, Object> I1030 = new HashMap<>();// ——资产支持证券投资
    	Map<String, Object> I1040 = new HashMap<>();// ——基金投资
    	Map<String, Object> I1050 = new HashMap<>();// ——贵金属投资
    	Map<String, Object> I1060 = new HashMap<>();// ——其他
    	Map<String, Object> I2010 = new HashMap<>();// ——权证投资
    	Map<String, Object> I3000 = new HashMap<>();// 3.其他
    	Map<String, Object> I3010 = new HashMap<>();// 减：应税金融商品公允价值变动产生的预估增值税
    	// 2.衍生工具里除权证投资外其他的数据需要动态显示
    	// since 2019-01-26
    	// author dai zong
    	List<Map<String,Object>> I20List = new ArrayList<>();
    	
    	@SuppressWarnings("unchecked")
    	List<Map<String,Object>> U900Data = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU900Data", queryMap);
    	if(U900Data == null) {
    		U900Data = Collections.emptyList();
    	}
    	
    	for(Map<String,Object> item : U900Data) {
    	    Object itemName = item.get("item");
    		if("——股票投资".equals(itemName)) {
    			I1010 = item;
    		} else if("——债券投资".equals(itemName)) {
    			I1020 = item;
    		} else if("——资产支持证券投资".equals(itemName)) {
    			I1030 = item;
    		} else if("——基金投资".equals(itemName)) {
    			I1040 = item;
    		} else if("——贵金属投资".equals(itemName)) {
    			I1050 = item;
    		} else if("——其他".equals(itemName)) {
    			I1060 = item;
    		} else if("——权证投资".equals(itemName)) {
    			I2010 = item;
    		} else if("3.其他".equals(itemName)) {
                I3000 = item;
            } else if("减：应税金融商品公允价值变动产生的预估增值税".equals(itemName)) {
    			I3010 = item;
    		} else if(item.get("sort") != null && ((Integer)item.get("sort")) >= 21 && ((Integer)item.get("sort")) <= 29){
    		    // 2.衍生工具里除权证投资外其他的数据需要动态显示(21 <= sort <= 29)
    	        // since 2019-01-26
    	        // author dai zong
    		    I20List.add(item);
    		}
    	}
    	
    	result.put("I1010", I1010);
    	result.put("I1020", I1020);
    	result.put("I1030", I1030);
    	result.put("I1040", I1040);
    	result.put("I1050", I1050);
    	result.put("I1060", I1060);
    	result.put("I2010", I2010);
    	result.put("I3000", I3000);
    	result.put("I3010", I3010);
    	result.put("I20List", I20List);
    	result.put("I20ListCount", I20List.size());
    	
    	return result;
    }
    
     /**
     * 处理sheet页U1000的数据
     * @author linnea 2022年09月23日
     * 
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getU1000Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> main = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U1000MainMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU1000MainData", queryMap);
        if(U1000MainMetaDataList == null) {
            U1000MainMetaDataList = new ArrayList<>(); 
        }
        Map<String,Object> temp = new HashMap<>();
        for(Map<String,Object> map : U1000MainMetaDataList) {
            temp.put(String.valueOf(map.get("item")), map);
        }
        main.put("S1", temp.get("银行存款")==null?new HashMap<>():temp.get("银行存款"));
        main.put("S2", temp.get("结算备付金")==null?new HashMap<>():temp.get("结算备付金"));
        main.put("S3", temp.get("存出保证金")==null?new HashMap<>():temp.get("存出保证金"));
        main.put("S4", temp.get("应收申购款")==null?new HashMap<>():temp.get("应收申购款"));
        main.put("S5", temp.get("应收清算款")==null?new HashMap<>():temp.get("应收清算款"));
        main.put("S6", temp.get("买入返售金融资产")==null?new HashMap<>():temp.get("买入返售金融资产"));
        main.put("S7", temp.get("债权投资")==null?new HashMap<>():temp.get("债权投资"));

        Map<String, Object> U1000Test = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U1000TestDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU1000TestData", queryMap);
        if(U1000TestDataList == null) {
            U1000TestDataList = new ArrayList<>(); 
        }
        temp = new HashMap<>();
        for(Map<String,Object> map : U1000TestDataList) {
            temp.put(String.valueOf(map.get("item")), map);
        }
        U1000Test.put("S1", temp.get("银行存款")==null?new HashMap<>():temp.get("银行存款"));
        U1000Test.put("S2", temp.get("结算备付金")==null?new HashMap<>():temp.get("结算备付金"));
        U1000Test.put("S3", temp.get("存出保证金")==null?new HashMap<>():temp.get("存出保证金"));
        U1000Test.put("S4", temp.get("应收申购款")==null?new HashMap<>():temp.get("应收申购款"));
        U1000Test.put("S5", temp.get("应收清算款")==null?new HashMap<>():temp.get("应收清算款"));
        U1000Test.put("S6", temp.get("买入返售金融资产")==null?new HashMap<>():temp.get("买入返售金融资产"));
        U1000Test.put("S7", temp.get("债权投资")==null?new HashMap<>():temp.get("债权投资"));
        
        result.put("U1000Test", U1000Test);
        result.put("main", main);
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
        Map<String, Object> result = new HashMap<>();

        @SuppressWarnings("unchecked")
        Map<String,Object> fundControl = (Map<String,Object>)this.dao.findForObject("ReportMapper.selectFundControlData", queryMap);
        if(fundControl == null) {
            fundControl = new HashMap<>(); 
        }
        result.put("fundControl", fundControl);

        @SuppressWarnings("unchecked")
        Map<String, Object> ETF_MAP = (Map<String, Object>)this.dao.findForObject("FundMapper.selectETFFlag", queryMap);
        String ETF = "N";
        if("Y".equals(ETF_MAP.get("ETF")) || "Y".equals(ETF_MAP.get("ETF_CONNECTION"))) {
            ETF = "Y";
        }
        
        result.put("ETF", ETF);

        @SuppressWarnings("unchecked")
        Map<String, Object> FOF_MAP = (Map<String, Object>)this.dao.findForObject("FundMapper.selectFOFFlag", queryMap);
        String FOF = "N";
        if("Y".equals(FOF_MAP.get("FOF")) || "Y".equals(FOF_MAP.get("FOF_CONNECTION"))) {
            FOF = "Y";
        }
        
        result.put("FOF", FOF);
		
        @SuppressWarnings("unchecked")
        Map<String, Object> AC_MAP = (Map<String, Object>)this.dao.findForObject("FundMapper.selectACFlag", queryMap);
        String AC = "N";
        if("Y".equals(AC_MAP.get("AC"))) {
            AC = "Y";
        }
        
        result.put("AC", AC);
		
        @SuppressWarnings("unchecked")
        Map<String, Object> MF_MAP = (Map<String, Object>)this.dao.findForObject("FundMapper.selectMFFlag", queryMap);
        String MF = "N";
        if("Y".equals(MF_MAP.get("MF"))) {
            MF = "Y";
        }
        
        result.put("MF", MF);
        
        Map<String, Object> interest = new HashMap<>();
        interest.put("S1", new HashMap<>());
        interest.put("S2", new HashMap<>());
        interest.put("S3", new HashMap<>());
        interest.put("S4", new HashMap<>());
        interest.put("S5", new HashMap<>());
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
        
        
        // Map<String, Object> stocks = new HashMap<>();
        Map<String, Object> STOCKS = new HashMap<>();
        Map<String, Object> STOCKS_BS = new HashMap<>();
        Map<String, Object> STOCKS_R = new HashMap<>();
        Map<String, Object> STOCKS_P = new HashMap<>();
        Map<String, Object> STOCKS_L = new HashMap<>();
        STOCKS.put("S1", new HashMap<>());
        STOCKS.put("S2", new HashMap<>());
        STOCKS.put("S3", new HashMap<>());
        STOCKS.put("S35", new HashMap<>()); // yury, 20200902, U10000新增7.4.7.12.5证券出借业务
        STOCKS_BS.put("S4", new HashMap<>());
        STOCKS_BS.put("S5", new HashMap<>());
        STOCKS_BS.put("S55", new HashMap<>()); // chenhy, 20220704, U10000新增7.4.7.14.2交易费用
        STOCKS_R.put("S6", new HashMap<>());
        STOCKS_R.put("S7", new HashMap<>());
        STOCKS_R.put("S8", new HashMap<>());
        STOCKS_R.put("S85", new HashMap<>()); // chenhy, 20220704, U10000新增7.4.7.14.3交易费用
        STOCKS_P.put("S9", new HashMap<>());
        STOCKS_P.put("S10", new HashMap<>());
        STOCKS_P.put("S11", new HashMap<>());
        STOCKS_P.put("S115", new HashMap<>()); // chenhy, 20220704, U10000新增7.4.7.14.4交易费用
        STOCKS_P.put("S12", new HashMap<>());
        STOCKS_L.put("S13", new HashMap<>()); // yury, 20200902, U10000新增7.4.7.12.5证券出借业务
        STOCKS_L.put("S14", new HashMap<>());
        STOCKS_L.put("S15", new HashMap<>());
        STOCKS_L.put("S155", new HashMap<>()); // chenhy, 20220704, U10000新增7.4.7.14.5交易费用

        // 计算标题序号
        int TitleCount = 9;
        // 判断大标题是否需要输出标记
        String StockBTFlag = "N";
        int StockBTCount = TitleCount;
        if("N".equals(fundControl.get("stockAll")) && "N".equals(fundControl.get("stockBs")) && "N".equals(fundControl.get("stockR")) && "N".equals(fundControl.get("stockP")) && "N".equals(fundControl.get("stockL"))) {
            StockBTFlag = "N";
        } else {
            StockBTFlag = "Y";
            TitleCount = TitleCount + 1;
            StockBTCount = TitleCount;
        }

        // queryMap.put("type", "STOCKS");
        // @SuppressWarnings("unchecked")
        // List<Map<String,Object>> U10000StocksMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryMap);
        // if(U10000StocksMetaDataList == null) {
        //     U10000StocksMetaDataList = new ArrayList<>(); 
        // }
        queryMap.put("type", "STOCKS_BS");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000StocksBsDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryMap);
        if(U10000StocksBsDataList == null) {
            U10000StocksBsDataList = new ArrayList<>(); 
        }

        for(Map<String,Object> map : U10000StocksBsDataList) {
            //S4--S55[公共]
            if ("卖出股票成交总额".equals(map.get("item")) && "STOCKS_BS".equals(map.get("type"))) {
                STOCKS_BS.put("S4", map);
            } else if ("减：卖出股票成本总额".equals(map.get("item")) && "STOCKS_BS".equals(map.get("type"))) {
                STOCKS_BS.put("S5", map);
            } else if ("减：交易费用".equals(map.get("item")) && "STOCKS_BS".equals(map.get("type"))) {
                STOCKS_BS.put("S55", map);
            }
        }

        queryMap.put("type", "STOCKS");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000StocksSummaryMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportSummaryData", queryMap);
        if(U10000StocksSummaryMetaDataList == null) {
            U10000StocksSummaryMetaDataList = new ArrayList<>(); 
        }

        if(U10000StocksSummaryMetaDataList.size() != 0) {
            @SuppressWarnings("unchecked")
            Map<String,Object> U10000StocksSummarySumData = (Map<String,Object>)this.dao.findForObject("UExportMapper.selectU10000ImportSummarySumDataForReport", queryMap);
            if(U10000StocksSummarySumData.get("amountCurrent") == null && U10000StocksSummarySumData.get("amountLast") == null) {
                U10000StocksSummaryMetaDataList = new ArrayList<>();
            } else {
                    for(Map<String,Object> map : U10000StocksSummaryMetaDataList) {
                        if ("股票投资收益——买卖股票差价收入".equals(map.get("item")) && "STOCKS".equals(map.get("type"))) {
                            STOCKS.put("S1", map);
                        } else if ("股票投资收益——赎回差价收入".equals(map.get("item")) && "STOCKS".equals(map.get("type"))) {
                            STOCKS.put("S2", map);
                        } else if ("股票投资收益——申购差价收入".equals(map.get("item")) && "STOCKS".equals(map.get("type"))) {
                            STOCKS.put("S3", map);
                        // yury，20200901，U10000新增证券出借业务
                        } else if ("股票投资收益——证券出借差价收入".equals(map.get("item")) && "STOCKS".equals(map.get("type"))) {
                            STOCKS.put("S35", map);
                        }
                    }
            }
        }
        
        // U10000StocksMetaDataList.addAll(U10000StocksSummaryMetaDataList);

        
        queryMap.put("type", "STOCKS_R");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000StocksRDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryMap);
        if(U10000StocksRDataList == null) {
            U10000StocksRDataList = new ArrayList<>(); 
        }
        for(Map<String,Object> map : U10000StocksRDataList) {
            if ("赎回基金份额对价总额".equals(map.get("item")) && "STOCKS_R".equals(map.get("type"))) {
                STOCKS_R.put("S6", map);
            } else if ("减：现金支付赎回款总额".equals(map.get("item")) && "STOCKS_R".equals(map.get("type"))) {
                STOCKS_R.put("S7", map);
            } else if ("减：赎回股票成本总额".equals(map.get("item")) && "STOCKS_R".equals(map.get("type"))) {
                STOCKS_R.put("S8", map);
            } else if ("减：交易费用".equals(map.get("item")) && "STOCKS_R".equals(map.get("type"))) {
                STOCKS_R.put("S85", map);
            }
        }

        String StockRFlag = "N";
        if("T".equals(fundControl.get("stockR"))) {
            if(U10000StocksRDataList.size() != 0){
                StockRFlag = "Y";
            } else {
                StockRFlag = "N";
            }
        } else if ("Y".equals(fundControl.get("stockR"))){
            StockRFlag = "Y";
        } else {
            StockRFlag = "N";
        }

        queryMap.put("type", "STOCKS_P");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000StocksPDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryMap);
        if(U10000StocksPDataList == null) {
            U10000StocksPDataList = new ArrayList<>(); 
        }

        List<Map<String,Object>> StocksPList = new ArrayList<>();
        for(Map<String,Object> map : U10000StocksPDataList) {
            if ("申购基金份额总额".equals(map.get("item")) && "STOCKS_P".equals(map.get("type"))) {
                STOCKS_P.put("S9", map);
            } else if ("减：现金支付申购款总额".equals(map.get("item")) && "STOCKS_P".equals(map.get("type"))) {
                STOCKS_P.put("S10", map);
            } else if ("减：申购股票成本总额".equals(map.get("item")) && "STOCKS_P".equals(map.get("type"))) {
                STOCKS_P.put("S11", map);
            } else if ("减：交易费用".equals(map.get("item")) && "STOCKS_P".equals(map.get("type"))) {
                STOCKS_P.put("S115", map);
            } else {
                if ("申购差价收入".equals(map.get("item"))){
                    continue;
                }else{
                    StocksPList.add(map);
                }
            }
        }

        String StockPFlag = "N";
        if("T".equals(fundControl.get("stockP"))) {
            if(U10000StocksPDataList.size() != 0){
                StockPFlag = "Y";
            } else {
                StockPFlag = "N";
            }
        } else if ("Y".equals(fundControl.get("stockP"))){
            StockPFlag = "Y";
        } else {
            StockPFlag = "N";
        }

        queryMap.put("type", "STOCKS_L");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000StocksLDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryMap);
        if(U10000StocksLDataList == null) {
            U10000StocksLDataList = new ArrayList<>(); 
        }

        for(Map<String,Object> map : U10000StocksLDataList) {
            if ("出借证券现金清偿总额".equals(map.get("item")) && "STOCKS_L".equals(map.get("type"))) {
                STOCKS_L.put("S13", map);
            } else if ("减：出借证券成本总额".equals(map.get("item")) && "STOCKS_L".equals(map.get("type"))) {
                STOCKS_L.put("S14", map);
            } else if ("减：应收证券出借利息".equals(map.get("item")) && "STOCKS_L".equals(map.get("type"))) {
                STOCKS_L.put("S15", map);
            } else if ("减：交易费用".equals(map.get("item")) && "STOCKS_L".equals(map.get("type"))) {
                STOCKS_L.put("S155", map);
            }
        }

        String StockLFlag = "N";
        if("T".equals(fundControl.get("stockL"))) {
            if(U10000StocksLDataList.size() != 0){
                StockLFlag = "Y";
            } else {
                StockLFlag = "N";
            }
        } else if ("Y".equals(fundControl.get("stockL"))){
            StockLFlag = "Y";
        } else {
            StockLFlag = "N";
        }

        // ALL表特殊逻辑  Y时必定输出；N时必定不输出；T时看情况：当STOCK_R/P/L均为零时，不输出标题及内容
        String StockFlag = "N";
        if("T".equals(fundControl.get("stockAll"))) {
            if(U10000StocksSummaryMetaDataList.size() == 0 || U10000StocksLDataList.size()+U10000StocksPDataList.size()+U10000StocksRDataList.size() == 0){
                StockFlag = "N";
            } else {
                StockFlag = "Y";
            }
        } else if ("Y".equals(fundControl.get("stockAll"))){
            StockFlag = "Y";
        } else {
            StockFlag = "N";
        }

        // BS表特殊逻辑  Y时必定输出；N时必定不输出；T时看情况：当STOCK_R/P/L均为零时，不输出标题，任意一项有内容时，输出本项标题和内容
        // BS表特殊文本输出逻辑 当29.0表对应项为Y时，本期（及上期）均无数字；当29表对应项为T时，本期（及上期）均无数字 且 STOCK_L本期或上期有数字
        String StockBsFlag = "N";
        if("T".equals(fundControl.get("stockBs"))) {
            if(U10000StocksBsDataList.size() != 0 || U10000StocksLDataList.size()+U10000StocksPDataList.size()+U10000StocksRDataList.size() != 0){
                StockBsFlag = "Y";
            } else {
                StockBsFlag = "N";
            }
        } else if ("N".equals(fundControl.get("stockBs"))){
            StockBsFlag = "N";
        } else {
            StockBsFlag = "Y";
        }

        // 判断小标题是否需要输出标记
        String StockLTFlag = "N";
        StockLTFlag = StockBsFlag + StockFlag + StockRFlag + StockPFlag + StockLFlag;
        StockLTFlag = StockLTFlag.replace("N","");
        if("Y".equals(StockLTFlag) || "".equals(StockLTFlag)) {
            StockLTFlag = "N";
        } else {
            StockLTFlag = "Y";
        }

        // 判断各个小标题的值
        int StockLTCount = 0;
        int StockCount = 1;
        int StockBsCount = 2;
        int StockRCount = 3;
        int StockPCount = 4;
        int StockLCount = 5;
        if (StockLTFlag == "Y"){
           if (StockFlag == "Y"){
            StockLTCount = StockLTCount + 1;
            StockCount = StockLTCount;
           }
           if (StockBsFlag == "Y"){
            StockLTCount = StockLTCount + 1;
            StockBsCount = StockLTCount;
           }
           if (StockRFlag == "Y"){
            StockLTCount = StockLTCount + 1;
            StockRCount = StockLTCount;
           }
           if (StockPFlag == "Y"){
            StockLTCount = StockLTCount + 1;
            StockPCount = StockLTCount;
           }
           if (StockLFlag == "Y"){
            StockLTCount = StockLTCount + 1;
            StockLCount = StockLTCount;
           }
        }

        // control位置计算
        int controlNum = 0;
        int StockRControl = 0;
        int StockPControl = 0;
        int StockLControl = 0;
        if (StockBsFlag == "Y" && U10000StocksBsDataList.size() != 0){
            controlNum = 10;
        } else if (StockBsFlag == "Y" && U10000StocksBsDataList.size() == 0){
            controlNum = 4;
        } else {
            controlNum = 0;
        }
        StockRControl = controlNum + 9 + 6;
        
        if (StockRFlag == "Y" && U10000StocksRDataList.size() != 0){
            controlNum = controlNum + 11;
        } else if (StockRFlag == "Y" && U10000StocksRDataList.size() == 0){
            controlNum = controlNum + 4;
        } else {
            controlNum = controlNum + StocksPList.size();
        }
        StockPControl = controlNum + StocksPList.size() + 9 + 5;

        if (StockRFlag == "Y" && U10000StocksPDataList.size() != 0){
            controlNum = controlNum + StocksPList.size() + 11;
        } else if (StockRFlag == "Y" && U10000StocksPDataList.size() == 0){
            controlNum = controlNum + 4;
        } else {
            controlNum = controlNum;
        }
        StockLControl = controlNum + 9 + 4;

        result.put("StockBTCount", StockBTCount);
        result.put("StockCount", StockCount);
        result.put("StockBsCount", StockBsCount);
        result.put("StockRCount", StockRCount);
        result.put("StockPCount", StockPCount);
        result.put("StockLCount", StockLCount);

        result.put("StockBsFlag", StockBsFlag);
        result.put("StockFlag", StockFlag);
        result.put("StockRFlag", StockRFlag);
        result.put("StockPFlag", StockPFlag);
        result.put("StockLFlag", StockLFlag);
        result.put("StockBTFlag", StockBTFlag);
        result.put("StockLTFlag", StockLTFlag);

        result.put("StockBsNum", U10000StocksBsDataList.size());
        result.put("StockNum", U10000StocksSummaryMetaDataList.size());
        result.put("StockRNum", U10000StocksRDataList.size());
        result.put("StockPNum", U10000StocksPDataList.size());
        result.put("StockLNum", U10000StocksLDataList.size());

        result.put("STOCKS_BS", STOCKS_BS);
        result.put("STOCKS", STOCKS);
        result.put("STOCKS_R", STOCKS_R);
        result.put("STOCKS_P", STOCKS_P);
        result.put("STOCKS_L", STOCKS_L);

        result.put("StocksPList", StocksPList);
        result.put("StocksPListCount", StocksPList.size());
        result.put("StockRControl", StockRControl);
        result.put("StockPControl", StockPControl);
        result.put("StockLControl", StockLControl);
        
        Map<String, Object> fund = new HashMap<>();
        fund.put("S1", new HashMap<>());
        fund.put("S2", new HashMap<>());
        fund.put("S3", new HashMap<>());  // chenhy, 20220704, U10000新增买卖基金差价收入应缴纳增值税额
        fund.put("S4", new HashMap<>());  // chenhy, 20220704, U10000新增交易费用
        queryMap.put("type", "FUND");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000FundMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryMap);
        if(U10000FundMetaDataList == null) {
            U10000FundMetaDataList = new ArrayList<>(); 
        }

        for(Map<String,Object> map : U10000FundMetaDataList) {
            if ("卖出/赎回基金成交总额".equals(map.get("item")) && "FUND".equals(map.get("type"))) {
                fund.put("S1", map);
            } else if ("减：卖出/赎回基金成本总额".equals(map.get("item")) && "FUND".equals(map.get("type"))) {
                fund.put("S2", map);
            } else if ("减：买卖基金差价收入应缴纳增值税额".equals(map.get("item")) && "FUND".equals(map.get("type"))) {
                fund.put("S3", map);
            } else if ("减：交易费用".equals(map.get("item")) && "FUND".equals(map.get("type"))) {
                fund.put("S4", map);
            }
        }

        // 判断是否需要输出标题
        String fundFlag = "N";
        int fundBTCount = TitleCount;
        if("T".equals(fundControl.get("fundBs"))) {
            if(U10000FundMetaDataList.size() != 0){
                fundFlag = "Y";
            } else {
                fundFlag = "N";
            }
        } else if ("N".equals(fundControl.get("fundBs"))){
            fundFlag = "N";
        } else {
            fundFlag = "Y";
        }

        if (fundFlag == "Y"){
            TitleCount = TitleCount + 1;
            fundBTCount = TitleCount;
        }

        result.put("fundFlag", fundFlag);
        result.put("fundBTCount", fundBTCount);
        result.put("fundNum", U10000FundMetaDataList.size());
        result.put("fund", fund);
        
        Map<String, Object> BOND = new HashMap<>();
        Map<String, Object> BOND_BS = new HashMap<>();
        Map<String, Object> BOND_R = new HashMap<>();
        Map<String, Object> BOND_P = new HashMap<>();
        BOND.put("S1", new HashMap<>());
        BOND.put("S2", new HashMap<>());
        BOND.put("S3", new HashMap<>());
        BOND.put("S16", new HashMap<>());
        BOND_BS.put("S4", new HashMap<>());
        BOND_BS.put("S5", new HashMap<>());
        BOND_BS.put("S6", new HashMap<>());
        BOND_BS.put("S65", new HashMap<>());   // chenhy, 20220704, U10000新增7.4.7.16.2交易费用
        BOND_R.put("S7", new HashMap<>());
        BOND_R.put("S8", new HashMap<>());
        BOND_R.put("S9", new HashMap<>());
        BOND_R.put("S10", new HashMap<>());
        BOND_R.put("S105", new HashMap<>());   // chenhy, 20220704, U10000新增7.4.7.16.3交易费用
        BOND_P.put("S11", new HashMap<>());
        BOND_P.put("S12", new HashMap<>());
        BOND_P.put("S13", new HashMap<>());
        BOND_P.put("S14", new HashMap<>());
        BOND_P.put("S145", new HashMap<>());  // chenhy, 20220704, U10000新增7.4.7.16.5交易费用
        BOND_P.put("S15", new HashMap<>());

        // 判断大标题是否需要输出标记
        String BondBTFlag = "N";
        int BondBTCount = TitleCount;
        if("N".equals(fundControl.get("bondAll")) && "N".equals(fundControl.get("bondBs")) && "N".equals(fundControl.get("bondR")) && "N".equals(fundControl.get("bondP"))) {
            BondBTFlag = "N";
        } else {
            BondBTFlag = "Y";
            TitleCount = TitleCount + 1;
            BondBTCount = TitleCount;
        }
        
        queryMap.put("type", "BOND_BS");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000BondBsMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryMap);
        if(U10000BondBsMetaDataList == null) {
            U10000BondBsMetaDataList = new ArrayList<>(); 
        }

        for(Map<String,Object> map : U10000BondBsMetaDataList) {
            if ("卖出债券（债转股及债券到期兑付）成交总额".equals(map.get("item")) && "BOND_BS".equals(map.get("type"))) {
                BOND_BS.put("S4", map);
            } else if ("减：卖出债券（债转股及债券到期兑付）成本总额".equals(map.get("item")) && "BOND_BS".equals(map.get("type"))) {
                BOND_BS.put("S5", map);
            } else if ("减：应计利息总额".equals(map.get("item")) && "BOND_BS".equals(map.get("type"))) {
                BOND_BS.put("S6", map);
            } else if ("减：交易费用".equals(map.get("item")) && "BOND_BS".equals(map.get("type"))) {
                BOND_BS.put("S65", map);
            }
        }

        String BondBsFlag = "N";
        if("T".equals(fundControl.get("bondBs"))) {
            if(U10000BondBsMetaDataList.size() != 0){
                BondBsFlag = "Y";
            } else {
                BondBsFlag = "N";
            }
        } else if ("Y".equals(fundControl.get("bondBs"))){
            BondBsFlag = "Y";
        } else {
            BondBsFlag = "N";
        }


        queryMap.put("type", "BOND");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000BondSummaryMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportSummaryData", queryMap);
        if(U10000BondSummaryMetaDataList == null) {
            U10000BondSummaryMetaDataList = new ArrayList<>(); 
        }
        // U10000BondMetaDataList.addAll(U10000BondSummaryMetaDataList);

        if(U10000BondSummaryMetaDataList.size() != 0) {
            @SuppressWarnings("unchecked")
            Map<String,Object> U10000BondSummarySumData = (Map<String,Object>)this.dao.findForObject("UExportMapper.selectU10000ImportSummarySumDataForReport", queryMap);
            if(U10000BondSummarySumData.get("amountCurrent") == null && U10000BondSummarySumData.get("amountLast") == null) {
                U10000BondSummaryMetaDataList = new ArrayList<>();
            } else {
                    for(Map<String,Object> map : U10000BondSummaryMetaDataList) {
                        //S1--S3
                        if ("债券投资收益——买卖债券（债转股及债券到期兑付）差价收入".equals(map.get("item")) && "BOND".equals(map.get("type"))) {
                            BOND.put("S1", map);
                        } else if ("债券投资收益——利息收入".equals(map.get("item")) && "BOND".equals(map.get("type"))) {
                            BOND.put("S16", map);
                        } else if ("债券投资收益——赎回差价收入".equals(map.get("item")) && "BOND".equals(map.get("type"))) {
                            BOND.put("S2", map);
                        } else if ("债券投资收益——申购差价收入".equals(map.get("item")) && "BOND".equals(map.get("type"))) {
                            BOND.put("S3", map);
                        }
                    }
            }
        }

        String BondFlag = "N";
        if("T".equals(fundControl.get("bondAll"))) {
            if(U10000BondSummaryMetaDataList.size() != 0){
                BondFlag = "Y";
            } else {
                BondFlag = "N";
            }
        } else if ("Y".equals(fundControl.get("bondAll"))){
            BondFlag = "Y";
        } else {
            BondFlag = "N";
        }

        queryMap.put("type", "BOND_R");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000BondRMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryMap);
        if(U10000BondRMetaDataList == null) {
            U10000BondRMetaDataList = new ArrayList<>(); 
        }

        for(Map<String,Object> map : U10000BondRMetaDataList) {
            //S1--S3
            if ("赎回基金份额对价总额".equals(map.get("item")) && "BOND_R".equals(map.get("type"))) {
                BOND_R.put("S7", map);
            } else if ("减：现金支付赎回款总额".equals(map.get("item")) && "BOND_R".equals(map.get("type"))) {
                BOND_R.put("S8", map);
            } else if ("减：赎回债券成本总额".equals(map.get("item")) && "BOND_R".equals(map.get("type"))) {
                BOND_R.put("S9", map);
            } else if ("减：赎回债券应收利息总额".equals(map.get("item")) && "BOND_R".equals(map.get("type"))) {
                BOND_R.put("S10", map);
            } else if ("减：交易费用".equals(map.get("item")) && "BOND_R".equals(map.get("type"))) {
                BOND_R.put("S105", map);
            }
        }

        String BondRFlag = "N";
        if("T".equals(fundControl.get("bondR"))) {
            if(U10000BondRMetaDataList.size() != 0){
                BondRFlag = "Y";
            } else {
                BondRFlag = "N";
            }
        } else if ("Y".equals(fundControl.get("bondR"))){
            BondRFlag = "Y";
        } else {
            BondRFlag = "N";
        }

        queryMap.put("type", "BOND_P");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000BondPMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryMap);
        if(U10000BondPMetaDataList == null) {
            U10000BondPMetaDataList = new ArrayList<>(); 
        }
        
        List<Map<String,Object>> BondPList = new ArrayList<>();
        for(Map<String,Object> map : U10000BondPMetaDataList) {
            //S1--S3
            if ("申购基金份额对价总额".equals(map.get("item")) && "BOND_P".equals(map.get("type"))) {
                BOND_P.put("S11", map);
            } else if ("减：现金支付申购款总额".equals(map.get("item")) && "BOND_P".equals(map.get("type"))) {
                BOND_P.put("S12", map);
            } else if ("减：申购债券成本总额".equals(map.get("item")) && "BOND_P".equals(map.get("type"))) {
                BOND_P.put("S13", map);
            } else if ("减：申购债券应收利息总额".equals(map.get("item")) && "BOND_P".equals(map.get("type"))) {
                BOND_P.put("S14", map);
            } else if ("减：交易费用".equals(map.get("item")) && "BOND_P".equals(map.get("type"))) {
                BOND_P.put("S145", map);
            } else {
                if ("申购差价收入".equals(map.get("item"))){
                    continue;
                }else{
                    BondPList.add(map);
                }
            }
        }

        String BondPFlag = "N";
        if("T".equals(fundControl.get("bondP"))) {
            if(U10000BondPMetaDataList.size() != 0){
                BondPFlag = "Y";
            } else {
                BondPFlag = "N";
            }
        } else if ("Y".equals(fundControl.get("bondP"))){
            BondPFlag = "Y";
        } else {
            BondPFlag = "N";
        }

        // 判断小标题是否需要输出标记
        String BondLTFlag = "N";
        BondLTFlag = BondBsFlag + BondFlag + BondRFlag + BondPFlag;
        BondLTFlag = BondLTFlag.replace("N","");
        if("Y".equals(BondLTFlag) || "".equals(BondLTFlag)) {
            BondLTFlag = "N";
        } else {
            BondLTFlag = "Y";
        }

        // 判断各个小标题的值
        int BondLTCount = 0;
        int BondCount = 1;
        int BondBsCount = 2;
        int BondRCount = 3;
        int BondPCount = 4;
        if (BondLTFlag == "Y"){
           if (BondFlag == "Y"){
            BondLTCount = BondLTCount + 1;
            BondCount = BondLTCount;
           }
           if (BondBsFlag == "Y"){
            BondLTCount = BondLTCount + 1;
            BondBsCount = BondLTCount;
           }
           if (BondRFlag == "Y"){
            BondLTCount = BondLTCount + 1;
            BondRCount = BondLTCount;
           }
           if (BondPFlag == "Y"){
            BondLTCount = BondLTCount + 1;
            BondPCount = BondLTCount;
           }
        }

        // control位置计算
        controlNum = 0;
        int BondRControl = 0;
        int BondPControl = 0;
        if (BondBsFlag == "Y" && U10000BondBsMetaDataList.size() != 0){
            controlNum = 11;
        } else if (BondBsFlag == "Y" && U10000BondBsMetaDataList.size() == 0){
            controlNum = 4;
        } else {
            controlNum = 0;
        }
        BondRControl = controlNum + 10 + 5;

        if (BondRFlag == "Y" && U10000BondRMetaDataList.size() != 0){
            controlNum = controlNum + 12;
        } else if (BondRFlag == "Y" && U10000BondRMetaDataList.size() == 0){
            controlNum = controlNum + 4;
        } else {
            controlNum = controlNum;
        }
        BondPControl = controlNum + BondPList.size() + 10 + 4;

        result.put("BondBTCount", BondBTCount);
        result.put("BondCount", BondCount);
        result.put("BondBsCount", BondBsCount);
        result.put("BondRCount", BondRCount);
        result.put("BondPCount", BondPCount);

        result.put("BondBTFlag", BondBTFlag);
        result.put("BondLTFlag", BondLTFlag);
        result.put("BondBsFlag", BondBsFlag);
        result.put("BondFlag", BondFlag);
        result.put("BondRFlag", BondRFlag);
        result.put("BondPFlag", BondPFlag);

        result.put("BondNum", U10000BondSummaryMetaDataList.size());
        result.put("BondBsNum", U10000BondBsMetaDataList.size());
        result.put("BondRNum", U10000BondRMetaDataList.size());
        result.put("BondPNum", U10000BondPMetaDataList.size());

        result.put("BOND", BOND);
        result.put("BOND_BS", BOND_BS);
        result.put("BOND_R", BOND_R);
        result.put("BOND_P", BOND_P);

        result.put("BondPList", BondPList);
        result.put("BondPListCount", BondPList.size());
        result.put("BondRControl", BondRControl);
        result.put("BondPControl", BondPControl);
        
        Map<String, Object> ABS = new HashMap<>();
        Map<String, Object> ABS_BS = new HashMap<>();
        Map<String, Object> ABS_R = new HashMap<>();
        Map<String, Object> ABS_P = new HashMap<>();
        ABS.put("S1", new HashMap<>());
        ABS.put("S15", new HashMap<>());
        ABS.put("S2", new HashMap<>());
        ABS.put("S3", new HashMap<>());
        ABS_BS.put("S4", new HashMap<>());   // chenhy, 20220704, U10000新增7.4.7.17.2交易费用
        ABS_BS.put("S5", new HashMap<>()); 
        ABS_BS.put("S6", new HashMap<>()); 
        ABS_BS.put("S65", new HashMap<>()); 
        ABS_R.put("S7", new HashMap<>()); 
        ABS_R.put("S8", new HashMap<>()); 
        ABS_R.put("S9", new HashMap<>()); 
        ABS_R.put("S10", new HashMap<>()); 
        ABS_R.put("S105", new HashMap<>()); 
        ABS_P.put("S11", new HashMap<>()); 
        ABS_P.put("S12", new HashMap<>()); 
        ABS_P.put("S13", new HashMap<>());
        ABS_P.put("S14", new HashMap<>());
        ABS_P.put("S145", new HashMap<>());

        // 判断大标题是否需要输出标记
        String AbsBTFlag = "N";     
        int AbsBTCount = TitleCount;
        if("N".equals(fundControl.get("ABSAll")) && "N".equals(fundControl.get("ABSBS")) && "N".equals(fundControl.get("ABSR")) && "N".equals(fundControl.get("ABSP"))) {
            AbsBTFlag = "N";
        } else {
            AbsBTFlag = "Y";
            TitleCount = TitleCount + 1;
            AbsBTCount = TitleCount;
        }
        
        queryMap.put("type", "ABS_BS");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000AbsBsMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryMap);
        if(U10000AbsBsMetaDataList == null) {
            U10000AbsBsMetaDataList = new ArrayList<>(); 
        }

        for(Map<String,Object> map : U10000AbsBsMetaDataList) {
            if ("卖出资产支持证券成交总额".equals(map.get("item")) && "ABS_BS".equals(map.get("type"))) {
                ABS_BS.put("S4", map);
            } else if ("减：卖出资产支持证券成本总额".equals(map.get("item")) && "ABS_BS".equals(map.get("type"))) {
                ABS_BS.put("S5", map);
            } else if ("减：应计利息总额".equals(map.get("item")) && "ABS_BS".equals(map.get("type"))) {
                ABS_BS.put("S6", map);
            } else if ("减：交易费用".equals(map.get("item")) && "ABS_BS".equals(map.get("type"))) {
                ABS_BS.put("S65", map);
            }
        }

        String AbsBsFlag = "N";
        if("T".equals(fundControl.get("ABSBS"))) {
            if(U10000AbsBsMetaDataList.size() != 0){
                AbsBsFlag = "Y";
            } else {
                AbsBsFlag = "N";
            }
        } else if ("Y".equals(fundControl.get("ABSBS"))){
            AbsBsFlag = "Y";
        } else {
            AbsBsFlag = "N";
        }

        queryMap.put("type", "ABS");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000AbsSummaryMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportSummaryData", queryMap);
        if(U10000AbsSummaryMetaDataList == null) {
            U10000AbsSummaryMetaDataList = new ArrayList<>(); 
        }
        // U10000AbsMetaDataList.addAll(U10000AbsSummaryMetaDataList);

        if(U10000AbsSummaryMetaDataList.size() != 0) {
            @SuppressWarnings("unchecked")
            Map<String,Object> U10000AbsSummarySumData = (Map<String,Object>)this.dao.findForObject("UExportMapper.selectU10000ImportSummarySumDataForReport", queryMap);
            if(U10000AbsSummarySumData.get("amountCurrent") == null && U10000AbsSummarySumData.get("amountLast") == null) {
                U10000AbsSummaryMetaDataList = new ArrayList<>();
            } else {
                    for(Map<String,Object> map : U10000AbsSummaryMetaDataList) {
                        //S1--S3
                        if ("资产支持证券投资收益——买卖资产支持证券差价收入".equals(map.get("item")) && "ABS".equals(map.get("type"))) {
                            ABS.put("S1", map);
                        } else if ("资产支持证券投资收益——利息收入".equals(map.get("item")) && "ABS".equals(map.get("type"))) {
                            ABS.put("S15", map);
                        } else if ("资产支持证券投资收益——赎回差价收入".equals(map.get("item")) && "ABS".equals(map.get("type"))) {
                            ABS.put("S2", map);
                        } else if ("资产支持证券投资收益——申购差价收入".equals(map.get("item")) && "ABS".equals(map.get("type"))) {
                            ABS.put("S3", map);
                        }
                    }
            }
        }

        String AbsFlag = "N";
        if("T".equals(fundControl.get("ABSAll"))) {
            if(U10000AbsSummaryMetaDataList.size() != 0){
                AbsFlag = "Y";
            } else {
                AbsFlag = "N";
            }
        } else if ("Y".equals(fundControl.get("ABSAll"))){
            AbsFlag = "Y";
        } else {
            AbsFlag = "N";
        }

        queryMap.put("type", "ABS_R");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000AbsRMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryMap);
        if(U10000AbsRMetaDataList == null) {
            U10000AbsRMetaDataList = new ArrayList<>(); 
        }

        for(Map<String,Object> map : U10000AbsRMetaDataList) {
            if ("赎回基金份额对价总额".equals(map.get("item")) && "ABS_R".equals(map.get("type"))) {
                ABS_R.put("S7", map);
            } else if ("减：现金支付赎回款总额".equals(map.get("item")) && "ABS_R".equals(map.get("type"))) {
                ABS_R.put("S8", map);
            } else if ("减：赎回资产支持证券成本总额".equals(map.get("item")) && "ABS_R".equals(map.get("type"))) {
                ABS_R.put("S9", map);
            } else if ("减：赎回资产支持证券应计利息总额".equals(map.get("item")) && "ABS_R".equals(map.get("type"))) {
                ABS_R.put("S10", map);
            } else if ("减：交易费用".equals(map.get("item")) && "ABS_R".equals(map.get("type"))) {
                ABS_R.put("S105", map);
            }
        }

        String AbsRFlag = "N";
        if("T".equals(fundControl.get("ABSR"))) {
            if(U10000AbsRMetaDataList.size() != 0){
                AbsRFlag = "Y";
            } else {
                AbsRFlag = "N";
            }
        } else if ("Y".equals(fundControl.get("ABSR"))){
            AbsRFlag = "Y";
        } else {
            AbsRFlag = "N";
        }

        queryMap.put("type", "ABS_P");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000AbsPMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryMap);
        if(U10000AbsPMetaDataList == null) {
            U10000AbsPMetaDataList = new ArrayList<>(); 
        }

        List<Map<String,Object>> ABS15List = new ArrayList<>();
        for(Map<String,Object> map : U10000AbsPMetaDataList) {
            if ("ABS_P".equals(map.get("type"))){
                if ("申购基金份额对价总额".equals(map.get("item"))){
                    ABS_P.put("S11", map);
                } else if ("减：现金支付申购款总额".equals(map.get("item"))){
                    ABS_P.put("S12", map);
                } else if ("减：申购资产支持证券成本总额".equals(map.get("item"))){
                    ABS_P.put("S13", map);
                } else if ("减：申购资产支持证券应计利息总额".equals(map.get("item"))){
                    ABS_P.put("S14", map);
                } else if ("减：交易费用".equals(map.get("item"))){
                    ABS_P.put("S145", map);
                } else{
                    if ("申购差价收入".equals(map.get("item"))){
                        continue;
                    }else{
                        ABS15List.add(map);
                    }
                }
            }
        }

        String AbsPFlag = "N";
        if("T".equals(fundControl.get("ABSP"))) {
            if(U10000AbsPMetaDataList.size() != 0){
                AbsPFlag = "Y";
            } else {
                AbsPFlag = "N";
            }
        } else if ("Y".equals(fundControl.get("ABSP"))){
            AbsPFlag = "Y";
        } else {
            AbsPFlag = "N";
        }

        // 判断小标题是否需要输出标记
        String AbsLTFlag = "N";
        AbsLTFlag = AbsBsFlag + AbsFlag + AbsRFlag + AbsPFlag;
        AbsLTFlag = AbsLTFlag.replace("N","");
        if("Y".equals(AbsLTFlag) || "".equals(AbsLTFlag)) {
            AbsLTFlag = "N";
        } else {
            AbsLTFlag = "Y";
        }

        // 判断各个小标题的值
        int AbsLTCount = 0;
        int AbsCount = 1;
        int AbsBsCount = 2;
        int AbsRCount = 3;
        int AbsPCount = 4;
        if (AbsLTFlag == "Y"){
           if (AbsFlag == "Y"){
            AbsLTCount = AbsLTCount + 1;
            AbsCount = AbsLTCount;
           }
           if (AbsBsFlag == "Y"){
            AbsLTCount = AbsLTCount + 1;
            AbsBsCount = AbsLTCount;
           }
           if (AbsRFlag == "Y"){
            AbsLTCount = AbsLTCount + 1;
            AbsRCount = AbsLTCount;
           }
           if (AbsPFlag == "Y"){
            AbsLTCount = AbsLTCount + 1;
            AbsPCount = AbsLTCount;
           }
        }

        // control位置计算
        controlNum = 0;
        int AbsRControl = 0;
        int AbsPControl = 0;
        if (AbsBsFlag == "Y" && U10000AbsBsMetaDataList.size() != 0){
            controlNum = 11;
        } else if (AbsBsFlag == "Y" && U10000AbsBsMetaDataList.size() == 0){
            controlNum = 4;
        } else {
            controlNum = 0;
        }
        AbsRControl = controlNum + 10 + 5;

        if (AbsRFlag == "Y" && U10000AbsRMetaDataList.size() != 0){
            controlNum = controlNum + 12;
        } else if (AbsRFlag == "Y" && U10000AbsRMetaDataList.size() == 0){
            controlNum = controlNum + 4;
        } else {
            controlNum = controlNum;
        }
        AbsPControl = controlNum + ABS15List.size() + 10 + 4;

        result.put("AbsBTCount", AbsBTCount);
        result.put("AbsCount", AbsCount);
        result.put("AbsBsCount", AbsBsCount);
        result.put("AbsRCount", AbsRCount);
        result.put("AbsPCount", AbsPCount);

        result.put("AbsBTFlag", AbsBTFlag);
        result.put("AbsLTFlag", AbsLTFlag);
        result.put("AbsBsFlag", AbsBsFlag);
        result.put("AbsFlag", AbsFlag);
        result.put("AbsRFlag", AbsRFlag);
        result.put("AbsPFlag", AbsPFlag);

        result.put("AbsBsNum", U10000AbsBsMetaDataList.size());
        result.put("AbsNum", U10000AbsSummaryMetaDataList.size());
        result.put("AbsRNum", U10000AbsRMetaDataList.size());
        result.put("AbsPNum", U10000AbsPMetaDataList.size());

        result.put("ABS", ABS);
        result.put("ABS_BS", ABS_BS);
        result.put("ABS_R", ABS_R);
        result.put("ABS_P", ABS_P);

        result.put("ABS15List", ABS15List);
    	result.put("ABS15ListCount", ABS15List.size());
        result.put("AbsRControl", AbsRControl);
        result.put("AbsPControl", AbsPControl);
        
        Map<String, Object> GOLD = new HashMap<>();
        Map<String, Object> GOLD_BS = new HashMap<>();
        Map<String, Object> GOLD_R = new HashMap<>();
        Map<String, Object> GOLD_P = new HashMap<>();
        GOLD.put("S1", new HashMap<>());
        GOLD.put("S2", new HashMap<>());
        GOLD.put("S3", new HashMap<>());
        GOLD_BS.put("S4", new HashMap<>());
        GOLD_BS.put("S5", new HashMap<>());
        GOLD_BS.put("S5_1", new HashMap<>());
        GOLD_BS.put("S13", new HashMap<>());
        GOLD_R.put("S6", new HashMap<>());
        GOLD_R.put("S7", new HashMap<>());
        GOLD_R.put("S8", new HashMap<>());
        GOLD_R.put("S14", new HashMap<>());
        GOLD_P.put("S9", new HashMap<>());
        GOLD_P.put("S10", new HashMap<>());
        GOLD_P.put("S11", new HashMap<>());
        GOLD_P.put("S12", new HashMap<>());
        
        // 判断大标题是否需要输出标记
        String GoldBTFlag = "N";
        int GoldBTCount = TitleCount;
        if("N".equals(fundControl.get("goldAll")) && "N".equals(fundControl.get("goldBs")) && "N".equals(fundControl.get("goldR")) && "N".equals(fundControl.get("goldP"))) {
            GoldBTFlag = "N";
        } else {
            GoldBTFlag = "Y";
            TitleCount = TitleCount + 1;
            GoldBTCount = TitleCount;
        }

        queryMap.put("type", "GOLD_BS");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000GoldBsMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryMap);
        if(U10000GoldBsMetaDataList == null) {
            U10000GoldBsMetaDataList = new ArrayList<>(); 
        }

        for(Map<String,Object> map : U10000GoldBsMetaDataList) {
            if ("卖出贵金属成交总额".equals(map.get("item")) && "GOLD_BS".equals(map.get("type"))) {
                GOLD_BS.put("S4", map);
            } else if ("减：卖出贵金属成本总额".equals(map.get("item")) && "GOLD_BS".equals(map.get("type"))) {
                GOLD_BS.put("S5", map);
            }else if ("减：买卖贵金属差价收入应缴纳增值税额".equals(map.get("item")) && "GOLD_BS".equals(map.get("type"))) {
                GOLD_BS.put("S5_1", map);
            }else if ("减：交易费用".equals(map.get("item")) && "GOLD_BS".equals(map.get("type"))) {
                GOLD_BS.put("S13", map);
            }
        }

        String GoldBsFlag = "N";
        if("T".equals(fundControl.get("goldBs"))) {
            if(U10000GoldBsMetaDataList.size() != 0){
                GoldBsFlag = "Y";
            } else {
                GoldBsFlag = "N";
            }
        } else if ("Y".equals(fundControl.get("goldBs"))){
            GoldBsFlag = "Y";
        } else {
            GoldBsFlag = "N";
        }

        queryMap.put("type", "GOLD");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000GoldSummaryMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportSummaryData", queryMap);
        if(U10000GoldSummaryMetaDataList == null) {
            U10000GoldSummaryMetaDataList = new ArrayList<>(); 
        }
        // U10000GoldMetaDataList.addAll(U10000GoldSummaryMetaDataList);
        
        if(U10000GoldSummaryMetaDataList.size() != 0) {
            @SuppressWarnings("unchecked")
            Map<String,Object> U10000GoldSummarySumData = (Map<String,Object>)this.dao.findForObject("UExportMapper.selectU10000ImportSummarySumDataForReport", queryMap);
            if(U10000GoldSummarySumData.get("amountCurrent") == null && U10000GoldSummarySumData.get("amountLast") == null) {
                U10000GoldSummaryMetaDataList = new ArrayList<>();
            } else {
                    for(Map<String,Object> map : U10000GoldSummaryMetaDataList) {
                        //S1--S3
                        if ("贵金属投资收益——买卖贵金属差价收入".equals(map.get("item")) && "GOLD".equals(map.get("type"))) {
                            GOLD.put("S1", map);
                        } else if ("贵金属投资收益——赎回差价收入".equals(map.get("item")) && "GOLD".equals(map.get("type"))) {
                            GOLD.put("S2", map);
                        } else if ("贵金属投资收益——申购差价收入".equals(map.get("item")) && "GOLD".equals(map.get("type"))) {
                            GOLD.put("S3", map);
                        }
                    }
            }
        }

        String GoldFlag = "N";
        if("T".equals(fundControl.get("goldAll"))) {
            if(U10000GoldSummaryMetaDataList.size() != 0){
                GoldFlag = "Y";
            } else {
                GoldFlag = "N";
            }
        } else if ("Y".equals(fundControl.get("goldAll"))){
            GoldFlag = "Y";
        } else {
            GoldFlag = "N";
        }

        queryMap.put("type", "GOLD_R");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000GoldRMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryMap);
        if(U10000GoldRMetaDataList == null) {
            U10000GoldRMetaDataList = new ArrayList<>(); 
        }

        for(Map<String,Object> map : U10000GoldRMetaDataList) {
            if ("赎回贵金属份额对价总额".equals(map.get("item")) && "GOLD_R".equals(map.get("type"))) {
                GOLD_R.put("S6", map);
            } else if ("减：现金支付赎回款总额".equals(map.get("item")) && "GOLD_R".equals(map.get("type"))) {
                GOLD_R.put("S7", map);
            } else if ("减：赎回贵金属成本总额".equals(map.get("item")) && "GOLD_R".equals(map.get("type"))) {
                GOLD_R.put("S8", map);
            } else if ("减：交易费用".equals(map.get("item")) && "GOLD_R".equals(map.get("type"))) {
                GOLD_R.put("S14", map);
            }
        }

        String GoldRFlag = "N";
        if("T".equals(fundControl.get("goldR"))) {
            if(U10000GoldRMetaDataList.size() != 0){
                GoldRFlag = "Y";
            } else {
                GoldRFlag = "N";
            }
        } else if ("Y".equals(fundControl.get("goldR"))){
            GoldRFlag = "Y";
        } else {
            GoldRFlag = "N";
        }

        queryMap.put("type", "GOLD_P");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000GoldPMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryMap);
        if(U10000GoldPMetaDataList == null) {
            U10000GoldPMetaDataList = new ArrayList<>(); 
        }

        List<Map<String,Object>> GoldPList = new ArrayList<>();
        for(Map<String,Object> map : U10000GoldPMetaDataList) {
            //S1--S3
            if ("申购贵金属份额总额".equals(map.get("item")) && "GOLD_P".equals(map.get("type"))) {
                GOLD_P.put("S9", map);
            } else if ("减：现金支付申购款总额".equals(map.get("item")) && "GOLD_P".equals(map.get("type"))) {
                GOLD_P.put("S10", map);
            } else if ("减：申购贵金属成本总额".equals(map.get("item")) && "GOLD_P".equals(map.get("type"))) {
                GOLD_P.put("S11", map);
            } else if ("其他".equals(map.get("item")) && "GOLD_P".equals(map.get("type"))) {
                GOLD_P.put("S12", map);
            } else{
                if ("申购差价收入".equals(map.get("item"))){
                    continue;
                }else{
                    GoldPList.add(map);
                }
            }
        }

        String GoldPFlag = "N";
        if("T".equals(fundControl.get("goldP"))) {
            if(U10000GoldPMetaDataList.size() != 0){
                GoldPFlag = "Y";
            } else {
                GoldPFlag = "N";
            }
        } else if ("Y".equals(fundControl.get("goldP"))){
            GoldPFlag = "Y";
        } else {
            GoldPFlag = "N";
        }

        // 判断小标题是否需要输出标记
        String GoldLTFlag = "N";
        GoldLTFlag = GoldBsFlag + GoldFlag + GoldRFlag + GoldPFlag;
        GoldLTFlag = GoldLTFlag.replace("N","");
        if("Y".equals(GoldLTFlag) || "".equals(GoldLTFlag)) {
            GoldLTFlag = "N";
        } else {
            GoldLTFlag = "Y";
        }

        // 判断各个小标题的值
        int GoldLTCount = 0;
        int GoldCount = 1;
        int GoldBsCount = 2;
        int GoldRCount = 3;
        int GoldPCount = 4;
        if (AbsLTFlag == "Y"){
           if (GoldFlag == "Y"){
            GoldLTCount = GoldLTCount + 1;
            GoldCount = GoldLTCount;
           }
           if (GoldBsFlag == "Y"){
            GoldLTCount = GoldLTCount + 1;
            GoldBsCount = GoldLTCount;
           }
           if (GoldRFlag == "Y"){
            GoldLTCount = GoldLTCount + 1;
            GoldRCount = GoldLTCount;
           }
           if (GoldPFlag == "Y"){
            GoldLTCount = GoldLTCount + 1;
            GoldPCount = GoldLTCount;
           }
        }

        // control位置计算
        controlNum = 0;
        int GoldRControl = 0;
        int GoldPControl = 0;
        if (GoldBsFlag == "Y" && U10000GoldBsMetaDataList.size() != 0){
            controlNum = 11;
        } else if (GoldBsFlag == "Y" && U10000GoldBsMetaDataList.size() == 0){
            controlNum = 4;
        } else {
            controlNum = 0;
        }
        GoldRControl = controlNum + 9 + 5;

        if (AbsRFlag == "Y" && U10000GoldRMetaDataList.size() != 0){
            controlNum = controlNum + 11;
        } else if (AbsRFlag == "Y" && U10000GoldRMetaDataList.size() == 0){
            controlNum = controlNum + 4;
        } else {
            controlNum = controlNum;
        }
        GoldPControl = controlNum + 9 + 4 + GoldPList.size();

        result.put("GoldBTCount", GoldBTCount);
        result.put("GoldCount", GoldCount);
        result.put("GoldBsCount", GoldBsCount);
        result.put("GoldRCount", GoldRCount);
        result.put("GoldPCount", GoldPCount);

        result.put("GoldBTFlag", GoldBTFlag);
        result.put("GoldLTFlag", GoldLTFlag);
        result.put("GoldBsFlag", GoldBsFlag);
        result.put("GoldFlag", GoldFlag);
        result.put("GoldRFlag", GoldRFlag);
        result.put("GoldPFlag", GoldPFlag);

        result.put("GoldBsNum", U10000GoldBsMetaDataList.size());
        result.put("GoldNum", U10000GoldSummaryMetaDataList.size());
        result.put("GoldRNum", U10000GoldRMetaDataList.size());
        result.put("GoldPNum", U10000GoldPMetaDataList.size());

        result.put("GOLD", GOLD);
        result.put("GOLD_BS", GOLD_BS);
        result.put("GOLD_R", GOLD_R);
        result.put("GOLD_P", GOLD_P);

        result.put("GoldPList", GoldPList);
        result.put("GoldPListCount", GoldPList.size());
        result.put("GoldRControl", GoldRControl);
        result.put("GoldPControl", GoldPControl);
        
        Map<String, Object> di = new HashMap<>();
        di.put("S1", new HashMap<>());
        di.put("S2", new HashMap<>());
        di.put("S3", new HashMap<>());
        queryMap.put("type", "DI_WARRANT");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000DiWarrantMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryMap);
        if(U10000DiWarrantMetaDataList == null) {
            U10000DiWarrantMetaDataList = new ArrayList<>(); 
        }
        queryMap.put("type", "DI_OTHER");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000DiOtherMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryMap);
        if(U10000DiOtherMetaDataList == null) {
            U10000DiOtherMetaDataList = new ArrayList<>(); 
        }
        List<Map<String,Object>> U10000DiMetaDataList = new ArrayList<>();
        U10000DiMetaDataList.addAll(U10000DiWarrantMetaDataList);
        U10000DiMetaDataList.addAll(U10000DiOtherMetaDataList);
        int diCount = U10000DiMetaDataList.size();
        if(diCount != 0) {
            for(Map<String,Object> map : U10000DiMetaDataList) {
                if ("卖出权证成交金额".equals(map.get("item"))) {
                    di.put("S1", map);
                } else if ("减：卖出权证成本总额".equals(map.get("item"))) {
                    di.put("S2", map);
                } else if ("减：买卖权证差价收入应缴纳增值税额".equals(map.get("item"))) {
                    di.put("S3", map);
                }
            }
        }

        diCount = TitleCount;
        if (diCount > 0){
            TitleCount = TitleCount + 1;
            diCount = TitleCount;
        }

        result.put("diCount", diCount);
        result.put("di_count", U10000DiMetaDataList.size());
        result.put("di", di);
        
        result.put("di_other_list", U10000DiOtherMetaDataList);
        result.put("di_other_count", U10000DiOtherMetaDataList.size());
        
        Map<String, Object> dividend = new HashMap<>();
        dividend.put("S1", new HashMap<>());
        dividend.put("S15", new HashMap<>()); // yury，20200915，U10000股利收益新增证券出借业务
        dividend.put("S2", new HashMap<>());
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000DividendMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000DividendData", queryMap);
        if(U10000DividendMetaDataList == null) {
            U10000DividendMetaDataList = new ArrayList<>(); 
        }
        for(Map<String,Object> map : U10000DividendMetaDataList) {
            if("10".equals(String.valueOf(map.get("sort")))) {
                dividend.put("S1", map);
            }else if("15".equals(String.valueOf(map.get("sort")))) { // yury，20200915，U10000股利收益新增证券出借业务
                dividend.put("S15", map);
            }else if("20".equals(String.valueOf(map.get("sort")))) {
                dividend.put("S2", map);
            }
        }
        result.put("dividend", dividend);
		
		// -----------chenhy，20220704，U10000新增信用减值损失--------------
		   //---chenhy,20220902,判断新老基金----
		Map<String, Object> note = new HashMap<>();
        String note3Flag = "N";
        
        @SuppressWarnings("unchecked")
        Map<String,Object> fundDateInfo = (Map<String,Object>)this.dao.findForObject("TExportMapper.selectFundDateInfo", queryMap);
        // chenhy,20240223,新增基金和产品的区分
        Map<String, Object> dateInfo = this.reportService.getDateInfo(periodStr, (Date)fundDateInfo.get("dateFrom"), (Date)fundDateInfo.get("dateTo"), (Date)fundDateInfo.get("dateTransform"),(String)fundDateInfo.get("fundType"));
        if("合同生效日".equals(dateInfo.get("CURRENT_INIT_SOURCE"))) {
            note3Flag = "Y";
        }
		result.put("note3Flag", note3Flag);

		Map<String, Object> credit = new HashMap<>();
        credit.put("S1", new HashMap<>());
        credit.put("S2", new HashMap<>());
        credit.put("S3", new HashMap<>());
		credit.put("S4", new HashMap<>());
		credit.put("S5", new HashMap<>());
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000CreditMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000CreditData", queryMap);
        if(U10000CreditMetaDataList == null) {
            U10000CreditMetaDataList = new ArrayList<>(); 
        }

        for(Map<String,Object> map : U10000CreditMetaDataList) {
            if("银行存款".equals(map.get("item"))) {
                credit.put("S1", map);
            }else if("买入返售金融资产".equals(map.get("item"))) {
                credit.put("S2", map);
            }else if("债权投资".equals(map.get("item"))) {
                credit.put("S3", map);
			}else if("其他债权投资".equals(map.get("item"))) {
                credit.put("S4", map);
			}else if("其他".equals(map.get("item"))) {
                credit.put("S5", map);
			}
		}
        credit.put("count", U10000CreditMetaDataList.size());
        result.put("credit", credit);
        
        Map<String, Object> other_r = new HashMap<>();
        other_r.put("S1", new HashMap<>());
        other_r.put("S2", new HashMap<>());
        List<Map<String,Object>> otherRL = new ArrayList<>();
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
            }else {
                otherRL.add(map);
            }
        }
        other_r.put("otherRL", otherRL);
        other_r.put("otherRL_count", otherRL.size());
        result.put("other_r", other_r);
        
        Map<String, Object> trxFee = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000TrxFeeMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000TrxFeeData", queryMap);
        if(U10000TrxFeeMetaDataList == null) {
            U10000TrxFeeMetaDataList = new ArrayList<>(); 
        }
        
        int item30Index = -1;
        for(int i=0 ; i<U10000TrxFeeMetaDataList.size() ; i++) {
        	if("30".equals(String.valueOf(U10000TrxFeeMetaDataList.get(i).get("sort")))) {
        		item30Index = i;
        		break;
        	}
        }
        
        trxFee.put("list", U10000TrxFeeMetaDataList);
        trxFee.put("count", U10000TrxFeeMetaDataList.size());
        trxFee.put("item30Index", item30Index);
        result.put("trxFee", trxFee);
        
		Map<String, Object> other_c = new HashMap<>();
        List<Map<String,Object>> otherCL = new ArrayList<>();
		other_c.put("S1", new HashMap<>());
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000OtherCMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000OtherCData", queryMap);
        if(U10000OtherCMetaDataList == null) {
            U10000OtherCMetaDataList = new ArrayList<>(); 
        }
		for(Map<String,Object> map : U10000OtherCMetaDataList) {
            if("其他费用".equals(map.get("item"))) {
                other_c.put("S1", map);
            }else {
                otherCL.add(map);
            }
        }
		other_c.put("otherCL", otherCL);
        other_c.put("list", U10000OtherCMetaDataList);
        other_c.put("otherCL_count", otherCL.size());
        result.put("other_c", other_c);
        return result;
    }
    
}
