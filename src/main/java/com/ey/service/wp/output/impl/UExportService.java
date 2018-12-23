package com.ey.service.wp.output.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
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
        dataMap.put("U600Test", this.getU600TestData(fundId, periodStr));
        dataMap.put("U800", this.getU800Data(fundId, periodStr));
        dataMap.put("U900", this.getU900Data(fundId, periodStr));
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
        M6111.put("S7", new HashMap<String,Object>());
        
        result.put("KM6101", new HashMap<String,Object>());
        result.put("KM6302", new HashMap<String,Object>());
        result.put("KM6403", new HashMap<String,Object>());
        result.put("KM6404", new HashMap<String,Object>());
        result.put("KM6406", new HashMap<String,Object>());
        result.put("KM6407", new HashMap<String,Object>());
        result.put("KM6411", new HashMap<String,Object>());
        result.put("KM6802", new HashMap<String,Object>());
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
                }else if("贵金属投资收益".equals(map.get("item"))) {
                    M6111.put("S5", map);
                }else if("衍生工具收益".equals(map.get("item"))) {
                    M6111.put("S6", map);
                }else if("股利收益".equals(map.get("item"))) {
                    M6111.put("S7", map);
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
        main.put("S3", temp.get("资产支持证券投资收益")==null?new HashMap<String,Object>():temp.get("资产支持证券投资收益"));
        main.put("S4", temp.get("基金投资收益")==null?new HashMap<String,Object>():temp.get("基金投资收益"));
        main.put("S5", temp.get("贵金属投资收益")==null?new HashMap<String,Object>():temp.get("贵金属投资收益"));
        main.put("S6", temp.get("衍生工具收益")==null?new HashMap<String,Object>():temp.get("衍生工具收益"));
        main.put("S7", temp.get("股利收益")==null?new HashMap<String,Object>():temp.get("股利收益"));
        main.put("S8", temp.get("存款利息收入")==null?new HashMap<String,Object>():temp.get("存款利息收入"));
        main.put("S9", temp.get("债券利息收入")==null?new HashMap<String,Object>():temp.get("债券利息收入"));
        main.put("S10", temp.get("资产支持证券利息收入")==null?new HashMap<String,Object>():temp.get("资产支持证券利息收入"));
        main.put("S11", temp.get("买入返售金融资产收入")==null?new HashMap<String,Object>():temp.get("买入返售金融资产收入"));
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
        result.put("S1", temp.get("基金赎回费收入")==null?new HashMap<String,Object>():temp.get("基金赎回费收入"));
        result.put("S2", temp.get("基金转换费收入")==null?new HashMap<String,Object>():temp.get("基金转换费收入"));
        result.put("S3", temp.get("印花税返还收入")==null?new HashMap<String,Object>():temp.get("印花税返还收入"));
        result.put("S4", temp.get("其他")==null?new HashMap<String,Object>():temp.get("其他"));
        
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
        Map<String, Object> fundTrade = new HashMap<String,Object>();
        Map<String, Object> cffe = new HashMap<String,Object>();
        Map<String, Object> KM6407 = new HashMap<String,Object>();
        Map<String, Object> KM6411 = new HashMap<String,Object>();
        
        trade.put("S1", new HashMap<String,Object>());
        trade.put("S2", new HashMap<String,Object>());
        trade.put("S3", new HashMap<String,Object>());
        trade.put("S4", new HashMap<String,Object>());
        trade.put("S5", new HashMap<String,Object>());
        bank.put("S1", new HashMap<String,Object>());
        bank.put("S2", new HashMap<String,Object>());
        fundTrade.put("S1", new HashMap<String,Object>());
        fundTrade.put("S2", new HashMap<String,Object>());
        cffe.put("S1", new HashMap<String,Object>());
        KM6411.put("S1", new HashMap<String,Object>());
        KM6411.put("S2", new HashMap<String,Object>());
        
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U500MainMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU500MainData", queryMap);
        if(U500MainMetaDataList == null) {
            U500MainMetaDataList = new ArrayList<>(); 
        }
        
        for(Map<String,Object> map : U500MainMetaDataList) {
            if("6407".equals(map.get("accountNum"))) {
            	if("基金交易费用".equals(map.get("item")) && map.get("type") == null) {
            		if("申购费".equals(map.get("subType"))) {
            			fundTrade.put("S1", map);
            		}else if("赎回费".equals(map.get("subType"))) {
            			fundTrade.put("S2", map);
            		}
            	}else {
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
            		}else if("中金所".equals(map.get("type"))) {
            			if("期货交易费用".equals(map.get("item"))) {
            				cffe.put("S1", map);
            			}
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
        KM6407.put("fundTrade", fundTrade);
        KM6407.put("cffe", cffe);
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
        
        result.put("S1", temp.get("审计费用")==null?new HashMap<String,Object>():temp.get("审计费用"));
        result.put("S2", temp.get("信息披露费")==null?new HashMap<String,Object>():temp.get("信息披露费"));
        
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
        
//        result.put("S3", temp.get("上市年费")==null?new HashMap<String,Object>():temp.get("上市年费"));
//        result.put("S4", temp.get("分红手续费")==null?new HashMap<String,Object>():temp.get("分红手续费"));
//        result.put("S5", temp.get("指数使用费")==null?new HashMap<String,Object>():temp.get("指数使用费"));
//        result.put("S6", temp.get("银行划款费用")==null?new HashMap<String,Object>():temp.get("银行划款费用"));
//        result.put("S7", temp.get("账户维护费")==null?new HashMap<String,Object>():temp.get("账户维护费"));
//        result.put("S8", temp.get("交易费用")==null?new HashMap<String,Object>():temp.get("交易费用"));
//        result.put("S9", temp.get("回购手续费")==null?new HashMap<String,Object>():temp.get("回购手续费"));
//        result.put("S10", temp.get("其他")==null?new HashMap<String,Object>():temp.get("其他"));

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
        Map<String, Object> result = new HashMap<String,Object>();
        
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
        Map<String, Object> result = new HashMap<String,Object>();
        
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
        	} else if("教育附加".equals(item.get("itemName"))) {
        		JYFJ = item;
        	} else if("地方教育附加".equals(item.get("itemName"))) {
        		DFJYFJ = item;
        	} else if("城建费附加_未实现".equals(item.get("itemName"))) {
        		CJFFJ_WSX = item;
        	} else if("教育附加_未实现".equals(item.get("itemName"))) {
        		JYFJ_WSX = item;
        	} else if("地方教育附加_未实现".equals(item.get("itemName"))) {
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
    	Map<String, Object> result = new HashMap<String,Object>();
    	
    	Map<String, Object> I1010 = new HashMap<>();// 股票投资
    	Map<String, Object> I1020 = new HashMap<>();// 债券投资
    	Map<String, Object> I1030 = new HashMap<>();// 资产支持证券投资
    	Map<String, Object> I1040 = new HashMap<>();// 基金投资
    	Map<String, Object> I1050 = new HashMap<>();// 贵金属投资
    	Map<String, Object> I1060 = new HashMap<>();// 其他
    	Map<String, Object> I2010 = new HashMap<>();// 权证投资
    	Map<String, Object> I3010 = new HashMap<>();// 减：应税金融商品公允价值变动产生的预估增值税(增值税)
    	
    	@SuppressWarnings("unchecked")
    	List<Map<String,Object>> U900Data = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU900Data", queryMap);
    	if(U900Data == null) {
    		U900Data = Collections.emptyList();
    	}
    	
    	for(Map<String,Object> item : U900Data) {
    		if("股票投资".equals(item.get("item"))) {
    			I1010 = item;
    		} else if("债券投资".equals(item.get("item"))) {
    			I1020 = item;
    		} else if("资产支持证券投资".equals(item.get("item"))) {
    			I1030 = item;
    		} else if("基金投资".equals(item.get("item"))) {
    			I1040 = item;
    		} else if("贵金属投资".equals(item.get("item"))) {
    			I1050 = item;
    		} else if("其他".equals(item.get("item"))) {
    			I1060 = item;
    		} else if("权证投资".equals(item.get("item"))) {
    			I2010 = item;
    		} else if("增值税".equals(item.get("item"))) {
    			I3010 = item;
    		}
    	}
    	
    	result.put("I1010", I1010);
    	result.put("I1020", I1020);
    	result.put("I1030", I1030);
    	result.put("I1040", I1040);
    	result.put("I1050", I1050);
    	result.put("I1060", I1060);
    	result.put("I2010", I2010);
    	result.put("I3010", I3010);
    	
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
        
        @SuppressWarnings("unchecked")
        Map<String, Object> ETF_MAP = (Map<String, Object>)this.dao.findForObject("FundMapper.selectETFFlag", queryMap);
        String ETF = "N";
        if("Y".equals(ETF_MAP.get("ETF")) || "Y".equals(ETF_MAP.get("ETF_CONNECTION"))) {
            ETF = "Y";
        }
        
        result.put("ETF", ETF);
        
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
        
        Map<String, Object> stocks = new HashMap<String,Object>();
        stocks.put("S1", new HashMap<String,Object>());
        stocks.put("S2", new HashMap<String,Object>());
        stocks.put("S3", new HashMap<String,Object>());
        stocks.put("S4", new HashMap<String,Object>());
        stocks.put("S5", new HashMap<String,Object>());
        stocks.put("S6", new HashMap<String,Object>());
        stocks.put("S7", new HashMap<String,Object>());
        stocks.put("S8", new HashMap<String,Object>());
        stocks.put("S9", new HashMap<String,Object>());
        stocks.put("S10", new HashMap<String,Object>());
        stocks.put("S11", new HashMap<String,Object>());
        stocks.put("S12", new HashMap<String,Object>());
        queryMap.put("type", "STOCKS");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000StocksMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryMap);
        if(U10000StocksMetaDataList == null) {
            U10000StocksMetaDataList = new ArrayList<>(); 
        }
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000StocksSummaryMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportSummaryData", queryMap);
        if(U10000StocksSummaryMetaDataList == null) {
            U10000StocksSummaryMetaDataList = new ArrayList<>(); 
        }
        U10000StocksMetaDataList.addAll(U10000StocksSummaryMetaDataList);
        for(Map<String,Object> map : U10000StocksMetaDataList) {
            //S1--S3
            if ("股票投资收益——买卖股票差价收入".equals(map.get("item")) && "STOCKS".equals(map.get("type"))) {
                stocks.put("S1", map);
            } else if ("股票投资收益——赎回差价收入".equals(map.get("item")) && "STOCKS".equals(map.get("type"))) {
                stocks.put("S2", map);
            } else if ("股票投资收益——申购差价收入".equals(map.get("item")) && "STOCKS".equals(map.get("type"))) {
                stocks.put("S3", map);
            //S4--S5[公共]
            } else if ("卖出股票成交总额".equals(map.get("item")) && "STOCKS_BS".equals(map.get("type"))) {
                stocks.put("S4", map);
            } else if ("减：卖出股票成本总额".equals(map.get("item")) && "STOCKS_BS".equals(map.get("type"))) {
                stocks.put("S5", map);
            //S6--S8
            } else if ("赎回基金份额对价总额".equals(map.get("item")) && "STOCKS_R".equals(map.get("type"))) {
                stocks.put("S6", map);
            } else if ("减：现金支付赎回款总额".equals(map.get("item")) && "STOCKS_R".equals(map.get("type"))) {
                stocks.put("S7", map);
            } else if ("减：卖出股票成本总额".equals(map.get("item")) && "STOCKS_R".equals(map.get("type"))) {
                stocks.put("S8", map);
            //S9--S12
            } else if ("申购基金份额总额".equals(map.get("item")) && "STOCKS_P".equals(map.get("type"))) {
                stocks.put("S9", map);
            } else if ("减：现金支付申购款总额".equals(map.get("item")) && "STOCKS_P".equals(map.get("type"))) {
                stocks.put("S10", map);
            } else if ("减：申购股票成本总额".equals(map.get("item")) && "STOCKS_P".equals(map.get("type"))) {
                stocks.put("S11", map);
            } else if ("其他".equals(map.get("item")) && "STOCKS_P".equals(map.get("type"))) {
                stocks.put("S12", map);
            }
        }
        result.put("stocks", stocks);
        
        Map<String, Object> fund = new HashMap<String,Object>();
        fund.put("S1", new HashMap<String,Object>());
        fund.put("S2", new HashMap<String,Object>());
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
            }
        }
        result.put("fund", fund);
        
        Map<String, Object> bond = new HashMap<String,Object>();
        bond.put("S1", new HashMap<String,Object>());
        bond.put("S2", new HashMap<String,Object>());
        bond.put("S3", new HashMap<String,Object>());
        bond.put("S4", new HashMap<String,Object>());
        bond.put("S5", new HashMap<String,Object>());
        bond.put("S6", new HashMap<String,Object>());
        bond.put("S7", new HashMap<String,Object>());
        bond.put("S8", new HashMap<String,Object>());
        bond.put("S9", new HashMap<String,Object>());
        bond.put("S10", new HashMap<String,Object>());
        bond.put("S11", new HashMap<String,Object>());
        bond.put("S12", new HashMap<String,Object>());
        bond.put("S13", new HashMap<String,Object>());
        bond.put("S14", new HashMap<String,Object>());
        bond.put("S15", new HashMap<String,Object>());
        queryMap.put("type", "BOND");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000BondMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryMap);
        if(U10000BondMetaDataList == null) {
            U10000BondMetaDataList = new ArrayList<>(); 
        }
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000BondSummaryMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportSummaryData", queryMap);
        if(U10000BondSummaryMetaDataList == null) {
            U10000BondSummaryMetaDataList = new ArrayList<>(); 
        }
        U10000BondMetaDataList.addAll(U10000BondSummaryMetaDataList);
        for(Map<String,Object> map : U10000BondMetaDataList) {
            //S1--S3
            if ("债券投资收益——买卖债券（、债转股及债券到期兑付）差价收入".equals(map.get("item")) && "BOND".equals(map.get("type"))) {
                bond.put("S1", map);
            } else if ("债券投资收益——赎回差价收入".equals(map.get("item")) && "BOND".equals(map.get("type"))) {
                bond.put("S2", map);
            } else if ("债券投资收益——申购差价收入".equals(map.get("item")) && "BOND".equals(map.get("type"))) {
                bond.put("S3", map);
            //S4--S6[公共]
            } else if ("卖出债券（、债转股及债券到期兑付）成交金额".equals(map.get("item")) && "BOND_BS".equals(map.get("type"))) {
                bond.put("S4", map);
            } else if ("减：卖出债券（、债转股及债券到期兑付）成本总额".equals(map.get("item")) && "BOND_BS".equals(map.get("type"))) {
                bond.put("S5", map);
            } else if ("减：应收利息总额".equals(map.get("item")) && "BOND_BS".equals(map.get("type"))) {
                bond.put("S6", map);
            //S7--S10  
            } else if ("赎回基金份额对价总额".equals(map.get("item")) && "BOND_R".equals(map.get("type"))) {
                bond.put("S7", map);
            } else if ("减：现金支付赎回款总额".equals(map.get("item")) && "BOND_R".equals(map.get("type"))) {
                bond.put("S8", map);
            } else if ("减：赎回债券成本总额".equals(map.get("item")) && "BOND_R".equals(map.get("type"))) {
                bond.put("S9", map);
            } else if ("减：赎回债券应收利息总额".equals(map.get("item")) && "BOND_R".equals(map.get("type"))) {
                bond.put("S10", map);
            //S11--S15
            } else if ("申购基金份额对价总额".equals(map.get("item")) && "BOND_P".equals(map.get("type"))) {
                bond.put("S11", map);
            } else if ("减：现金支付申购款总额".equals(map.get("item")) && "BOND_P".equals(map.get("type"))) {
                bond.put("S12", map);
            } else if ("减：申购债券成本总额".equals(map.get("item")) && "BOND_P".equals(map.get("type"))) {
                bond.put("S13", map);
            } else if ("减：申购债券应收利息总额".equals(map.get("item")) && "BOND_P".equals(map.get("type"))) {
                bond.put("S14", map);
            } else if ("其他".equals(map.get("item")) && "BOND_P".equals(map.get("type"))) {
                bond.put("S15", map);
            }
        }
        result.put("bond", bond);
        
        Map<String, Object> abs = new HashMap<String,Object>();
        abs.put("S1", new HashMap<String,Object>());
        abs.put("S2", new HashMap<String,Object>());
        abs.put("S3", new HashMap<String,Object>());
        queryMap.put("type", "ABS");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000AbsMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryMap);
        if(U10000AbsMetaDataList == null) {
            U10000AbsMetaDataList = new ArrayList<>(); 
        }
        for(Map<String,Object> map : U10000AbsMetaDataList) {
            if ("卖出资产支持证券成交总额".equals(map.get("item")) && "ABS".equals(map.get("type"))) {
                abs.put("S1", map);
            } else if ("减：卖出资产支持证券成本总额".equals(map.get("item")) && "ABS".equals(map.get("type"))) {
                abs.put("S2", map);
            } else if ("减：应收利息总额".equals(map.get("item")) && "ABS".equals(map.get("type"))) {
                abs.put("S3", map);
            }
        }
        result.put("abs", abs);
        
        Map<String, Object> gold = new HashMap<String,Object>();
        gold.put("S1", new HashMap<String,Object>());
        gold.put("S2", new HashMap<String,Object>());
        gold.put("S3", new HashMap<String,Object>());
        gold.put("S4", new HashMap<String,Object>());
        gold.put("S5", new HashMap<String,Object>());
        gold.put("S6", new HashMap<String,Object>());
        gold.put("S7", new HashMap<String,Object>());
        gold.put("S8", new HashMap<String,Object>());
        gold.put("S9", new HashMap<String,Object>());
        gold.put("S10", new HashMap<String,Object>());
        gold.put("S11", new HashMap<String,Object>());
        gold.put("S12", new HashMap<String,Object>());
        queryMap.put("type", "GOLD");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000GoldMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportData", queryMap);
        if(U10000GoldMetaDataList == null) {
            U10000GoldMetaDataList = new ArrayList<>(); 
        }
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000GoldSummaryMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000ImportSummaryData", queryMap);
        if(U10000GoldSummaryMetaDataList == null) {
            U10000GoldSummaryMetaDataList = new ArrayList<>(); 
        }
        U10000GoldMetaDataList.addAll(U10000GoldSummaryMetaDataList);
        for(Map<String,Object> map : U10000GoldMetaDataList) {
            //S1--S3
            if ("贵金属投资收益——买卖贵金属差价收入".equals(map.get("item")) && "GOLD".equals(map.get("type"))) {
                gold.put("S1", map);
            } else if ("贵金属投资收益——赎回差价收入".equals(map.get("item")) && "GOLD".equals(map.get("type"))) {
                gold.put("S2", map);
            } else if ("贵金属投资收益——申购差价收入".equals(map.get("item")) && "GOLD".equals(map.get("type"))) {
                gold.put("S3", map);
            //S4--S5.5[公共,但是非ETF的好像被干掉了]
            } else if ("卖出贵金属成交总额".equals(map.get("item")) && "GOLD_BS".equals(map.get("type"))) {
                gold.put("S4", map);
            } else if ("减：卖出贵金属成本总额".equals(map.get("item")) && "GOLD_BS".equals(map.get("type"))) {
                gold.put("S5", map);
            }else if ("减：买卖贵金属差价收入应缴纳增值税额".equals(map.get("item")) && "GOLD_BS".equals(map.get("type"))) {
                gold.put("S5_1", map);
            //S6--S8
            } else if ("赎回贵金属份额对价总额".equals(map.get("item")) && "GOLD_R".equals(map.get("type"))) {
                gold.put("S6", map);
            } else if ("减：现金支付赎回款总额".equals(map.get("item")) && "GOLD_R".equals(map.get("type"))) {
                gold.put("S7", map);
            } else if ("减：赎回贵金属成本总额".equals(map.get("item")) && "GOLD_R".equals(map.get("type"))) {
                gold.put("S8", map);
            //S9--S12
            } else if ("申购贵金属份额总额".equals(map.get("item")) && "GOLD_P".equals(map.get("type"))) {
                gold.put("S9", map);
            } else if ("减：现金支付申购款总额".equals(map.get("item")) && "GOLD_P".equals(map.get("type"))) {
                gold.put("S10", map);
            } else if ("减：申购贵金属成本总额".equals(map.get("item")) && "GOLD_P".equals(map.get("type"))) {
                gold.put("S11", map);
            } else if ("其他".equals(map.get("item")) && "GOLD_P".equals(map.get("type"))) {
                gold.put("S12", map);
            }
        }
        result.put("gold", gold);
        
        Map<String, Object> di = new HashMap<String,Object>();
        di.put("S1", new HashMap<String,Object>());
        di.put("S2", new HashMap<String,Object>());
        di.put("S3", new HashMap<String,Object>());
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
        result.put("diCount", diCount>0?1:0);
        result.put("di", di);
        
        result.put("di_other_list", U10000DiOtherMetaDataList);
        result.put("di_other_count", U10000DiOtherMetaDataList.size());
        
        Map<String, Object> dividend = new HashMap<String,Object>();
        dividend.put("S1", new HashMap<String,Object>());
        dividend.put("S2", new HashMap<String,Object>());
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000DividendMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000DividendData", queryMap);
        if(U10000DividendMetaDataList == null) {
            U10000DividendMetaDataList = new ArrayList<>(); 
        }
        for(Map<String,Object> map : U10000DividendMetaDataList) {
            if("10".equals(String.valueOf(map.get("sort")))) {
                dividend.put("S1", map);
            }else if("20".equals(String.valueOf(map.get("sort")))) {
                dividend.put("S2", map);
            }
        }
        result.put("dividend", dividend);
        
        Map<String, Object> other_r = new HashMap<String,Object>();
        other_r.put("S1", new HashMap<String,Object>());
        other_r.put("S2", new HashMap<String,Object>());
        List<Map<String,Object>> SL = new ArrayList<>();
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
                SL.add(map);
            }
        }
        other_r.put("SL", SL);
        other_r.put("SL_count", SL.size());
        result.put("other_r", other_r);
        
        Map<String, Object> trxFee = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> U10000TrxFeeMetaDataList = (List<Map<String,Object>>)this.dao.findForList("UExportMapper.selectU10000TrxFeeData", queryMap);
        if(U10000TrxFeeMetaDataList == null) {
            U10000TrxFeeMetaDataList = new ArrayList<>(); 
        }
        Map<String,Map<String,Object>> mapping = new HashMap<>();
        for(Map<String,Object> map : U10000TrxFeeMetaDataList) {
        	mapping.put(String.valueOf(map.get("sort")), map);
        }
        Map<String,Object> emptyMap = new HashMap<>();
        List<Map<String,Object>> U10000TrxFeeDataList = new ArrayList<>();
        U10000TrxFeeDataList.add(ObjectUtils.defaultIfNull(mapping.get("10"), emptyMap)); //        交易所市场交易费用	10
        U10000TrxFeeDataList.add(ObjectUtils.defaultIfNull(mapping.get("20"), emptyMap)); //        银行间市场交易费用	20
        U10000TrxFeeDataList.add(ObjectUtils.defaultIfNull(mapping.get("30"), emptyMap)); //        交易基金产生的费用	30
        U10000TrxFeeDataList.add(ObjectUtils.defaultIfNull(mapping.get("31"), emptyMap)); //        其中：申购费	31
        U10000TrxFeeDataList.add(ObjectUtils.defaultIfNull(mapping.get("32"), emptyMap)); //        赎回费	32
        U10000TrxFeeDataList.add(ObjectUtils.defaultIfNull(mapping.get("40"), emptyMap)); //        中金所交易费用	40
        
        trxFee.put("list", U10000TrxFeeDataList);
        trxFee.put("count", U10000TrxFeeDataList.size());
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
