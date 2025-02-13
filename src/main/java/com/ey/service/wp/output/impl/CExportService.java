package com.ey.service.wp.output.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import com.ey.service.wp.output.CExportManager;
import com.ey.util.fileexport.Constants;
import com.ey.util.fileexport.FileExportUtils;
import com.ey.util.fileexport.FreeMarkerUtils;

/**
 * @name CExportService
 * @description 底稿输出服务--C
 * @author Dai Zong	2017年8月26日
 */
@Service("cExportService")
public class CExportService extends BaseExportService implements CExportManager{

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

        dataMap.put("C", this.getCData(fundId, periodStr));
        dataMap.put("C300", this.getC300Data(fundId, periodStr));
        dataMap.put("C400", this.getC400Data(fundId, periodStr));
        dataMap.put("C310", this.getC310Data(fundId, periodStr));
        dataMap.put("C10000", this.getC10000Data(fundId, periodStr));

        return FreeMarkerUtils.processTemplateToString(dataMap, templatePath, Constants.EXPORT_TEMPLATE_FILE_NAME_C);
    }

    @Override
    public boolean doExport(HttpServletRequest request, HttpServletResponse response, String fundId, String periodStr, String templatePath) throws Exception {
        Map<String, String> fundInfo = this.selectFundInfo(fundId);
        fundInfo.put("periodStr", periodStr);
        String xmlStr = this.generateFileContent(fundId, periodStr, fundInfo, templatePath);
        FileExportUtils.writeFileToHttpResponse(request, response, FreeMarkerUtils.simpleReplace(Constants.EXPORT_AIM_FILE_NAME_C, fundInfo), xmlStr);
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
     * 处理sheet页C的数据
     * @author Dai Zong 2017年8月27日
     *
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    private Map<String,Object> getCData(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> resMapList = (List<Map<String,Object>>)this.dao.findForList("CExportMapper.selectCData", queryMap);
        if(resMapList == null) {
            resMapList = new ArrayList<>();
        }

        for(Map<String, Object> resMap : resMapList) {
            if(resMap == null || resMap.get("accountNum") == null) {continue;}
            result.put("KM" + (String) resMap.get("accountNum"), resMap);
        }

        if(result.get("KM1002") == null) {
            result.put("KM1002", new HashMap<>());
        }
        if(result.get("KM1021") == null) {
            result.put("KM1021", new HashMap<>());
        }
        if(result.get("KM1031") == null) {
            result.put("KM1031", new HashMap<>());
        }

        return result;
    }

    /**
     * 处理sheet页C300的数据
     * @author Dai Zong 2017年8月27日
     *
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */

    @SuppressWarnings("unchecked")
    private Map<String,Object> getC300Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();

        Map<String, Object> mainMap = new HashMap<String,Object>();
        Map<String, Object> intRiskPeriodMap = new HashMap<String,Object>();

        //========process dataMap for main view begin========
        List<Map<String,Object>> demandDepositsList = new ArrayList<>();//one part of KM1002
        List<Map<String,Object>> timeDepositsList = new ArrayList<>();// another part of KM1002
        List<Map<String,Object>> otherDepositsList = new ArrayList<>();// another part of KM1002,chenhy,231205
        List<Map<String,Object>> ttyqDepositsList = new ArrayList<>();// another part of KM1002,chenhy,231205
        List<Map<String,Object>> futuresKM1021List = new ArrayList<>();// ,chenhy,231205,期货备付金
        List<Map<String,Object>> KM1021List = new ArrayList<>();
        List<Map<String,Object>> KM1031List = new ArrayList<>();
        int demandDepositsListCount = 0;
        int timeDepositsListCount = 0;
        int KM1021ListCount = 0;
        int KM1031ListCount = 0;
        int otherDepositsListCount = 0;
        int futuresKM1021ListCount = 0;

        List<Map<String,Object>> mainData = (List<Map<String,Object>>)this.dao.findForList("CExportMapper.selectC300MainData", queryMap);
        if(CollectionUtils.isEmpty(mainData)) {
            mainData = new ArrayList<Map<String,Object>>();
        }
        // classification
        for(Map<String,Object> map : mainData) {
            if("1002".equals(map.get("accountNum")) && "活期".equals(map.get("type"))) {
                demandDepositsList.add(map);
            }else if("1002".equals(map.get("accountNum")) && "定期".equals(map.get("type"))) {
                timeDepositsList.add(map);
            }else if("1002".equals(map.get("accountNum")) && "券商结算资金".equals(map.get("type"))) {
                otherDepositsList.add(map);
            }else if("1002".equals(map.get("accountNum")) && "同业约期存款".equals(map.get("type"))) {
                ttyqDepositsList.add(map);
            }else if("1021".equals(map.get("accountNum"))) {
                KM1021List.add(map);
            }else if("1031".equals(map.get("accountNum"))) {
                KM1031List.add(map);
            }
        }

        // chenhy,20231214,期货备付金
        for(Map<String,Object> map : mainData) {
            if("1021".equals(map.get("accountNum")) && "期货".equals(map.get("type"))) {
                futuresKM1021List.add(map);
            }
        }

        int otherDepositsListForDisclosureCount = otherDepositsList.size();
        int ttyqDepositsListForDisclosureCount = ttyqDepositsList.size();
        int ttyqDepositsListCount = ttyqDepositsList.size();
        //Anti Null
        if(demandDepositsList.size() == 0) {demandDepositsList.add(new HashMap<String,Object>());}
        if(timeDepositsList.size() == 0) {timeDepositsList.add(new HashMap<String,Object>());}
        if(otherDepositsList.size() == 0) {otherDepositsList.add(new HashMap<String,Object>());}
        if(ttyqDepositsList.size() == 0) {ttyqDepositsList.add(new HashMap<String,Object>());}
        if(KM1021List.size() == 0) {KM1021List.add(new HashMap<String,Object>());}
        if(KM1031List.size() == 0) {KM1031List.add(new HashMap<String,Object>());}
        // calculate count
        demandDepositsListCount = demandDepositsList.size();
        timeDepositsListCount = timeDepositsList.size();
        otherDepositsListCount = otherDepositsList.size();
        futuresKM1021ListCount = futuresKM1021List.size();
        KM1021ListCount = KM1021List.size();
        KM1031ListCount = KM1031List.size();
        // store
        Map<String,Object> KM1002Map = new HashMap<>();
        KM1002Map.put("demandDepositsList", demandDepositsList);
        KM1002Map.put("demandDepositsCount", demandDepositsListCount);
        KM1002Map.put("timeDepositsList", timeDepositsList);
        KM1002Map.put("timeDepositsCount", timeDepositsListCount);
        KM1002Map.put("otherDepositsList", otherDepositsList);
        KM1002Map.put("otherDepositsCount", otherDepositsListCount);
        KM1002Map.put("ttyqDepositsList", ttyqDepositsList);
        KM1002Map.put("ttyqDepositsCount", ttyqDepositsListCount);
        KM1002Map.put("otherDepositsForDisclosureCount", otherDepositsListForDisclosureCount);
        KM1002Map.put("ttyqDepositsForDisclosureCount", ttyqDepositsListForDisclosureCount);

        Map<String,Object> futuresKM1021Map = new HashMap<>();
        futuresKM1021Map.put("list", futuresKM1021List);
        futuresKM1021Map.put("count", futuresKM1021ListCount);

        Map<String,Object> KM1021Map = new HashMap<>();
        KM1021Map.put("list", KM1021List);
        KM1021Map.put("count", KM1021ListCount);

        Map<String,Object> KM1031Map = new HashMap<>();
        KM1031Map.put("list", KM1031List);
        KM1031Map.put("count", KM1031ListCount);

        mainMap.put("futuresKM1021", futuresKM1021Map);
        mainMap.put("KM1002", KM1002Map);
        mainMap.put("KM1021", KM1021Map);
        mainMap.put("KM1031", KM1031Map);
        //========process dataMap for main view end========

        //========process dataMap for related view begin========
        Map<String,Object> RelatedData = new HashMap<>();
        List<Map<String,Object>> RelatedMetaData = (List<Map<String,Object>>)this.dao.findForList("CExportMapper.selectC300RelatedData", queryMap);
        List<Map<String,Object>> RdemandDepositsList = new ArrayList<>();
        List<Map<String,Object>> RTimeDepositsList = new ArrayList<>();
        List<Map<String,Object>> ROtherDepositsList = new ArrayList<>();
        List<Map<String,Object>> TYYQDepositsList = new ArrayList<>(); //chenhy,20240624,新增同业约期存款
        List<Map<String,Object>> RKM1021List = new ArrayList<>();
        List<Map<String,Object>> RKM1031List = new ArrayList<>();
        for(Map<String,Object> map : RelatedMetaData) {
            if("活期存款".equals(map.get("depositType"))) {
                RdemandDepositsList.add(map);
            }else if("定期存款".equals(map.get("depositType"))) {
                RTimeDepositsList.add(map);
            }else if("券商结算资金".equals(map.get("depositType"))) {
                ROtherDepositsList.add(map);
            }else if("同业约期存款".equals(map.get("depositType"))) {  //chenhy,20240624,新增同业约期存款
                TYYQDepositsList.add(map);
            }else if("结算备付金".equals(map.get("depositType"))) {
                RKM1021List.add(map);
            }else if("存出保证金".equals(map.get("depositType"))) {
                RKM1031List.add(map);
            }
        }
        int RdemandDepositsCount = RdemandDepositsList.size();
        int RTimeDepositsCount = RTimeDepositsList.size();
        int ROtherDepositsCount = ROtherDepositsList.size();
        int TYYQDepositCount = TYYQDepositsList.size(); //chenhy,20240624,新增同业约期存款
        int RKM1021Count = RKM1021List.size();
        int RKM1031Count = RKM1031List.size();

        RelatedData.put("demandDepositsList", RdemandDepositsList);
        RelatedData.put("demandDepositsCount", RdemandDepositsCount);
        RelatedData.put("timeDepositsList", RTimeDepositsList);
        RelatedData.put("timeDepositsCount", RTimeDepositsCount);
        RelatedData.put("otherDepositsList", ROtherDepositsList);
        RelatedData.put("TYYQDepositsCount", TYYQDepositCount); //chenhy,20240624,新增同业约期存款
        RelatedData.put("TYYQDepositsList", TYYQDepositsList);
        RelatedData.put("otherDepositsCount", ROtherDepositsCount);
        RelatedData.put("KM1021", RKM1021List);
        RelatedData.put("KM1021Count", RKM1021Count);
        RelatedData.put("KM1031", RKM1031List);
        RelatedData.put("KM1031Count", RKM1031Count);
        //========process dataMap for related view end========

        //========process dataMap for intRistPeriod view begin========
        List<String> intRistPeriods = (List<String>)this.dao.findForList("CExportMapper.selectC300IntRiskPeriods", queryMap);
        List<Map<String,Object>> timeDepositsDataList = (List<Map<String,Object>>)this.dao.findForList("CExportMapper.selectC300IntRiskTimeDepositsData", queryMap);
        List<Double> timeDepositsData = new ArrayList<>();
        intRistPeriods = intRistPeriods==null?new ArrayList<String>():intRistPeriods;
        timeDepositsDataList = timeDepositsDataList==null?new ArrayList<Map<String,Object>>():timeDepositsDataList;
        Map<String,Object> temp = new HashMap<>();

        //对于定期存款取C400数据，按【FUND_ID】【PERIOD】【PERIOD_LEFT】字段汇总【AMOUNT】写入对应的利率风险敞口，不计息类型默认为0
        temp.put("intRiskPeriod", "不计息");
        temp.put("amount", 0D);

        timeDepositsDataList.add(temp);
        int noInterestColIndex = intRistPeriods.size() - 1;
        //find no-interest col's index
        for(int i=0;i<intRistPeriods.size();i++) {
            if("不计息".equals(intRistPeriods.get(i))) {
                noInterestColIndex = i;
                break;
            }
        }
        //map timeDepositsData to right position
        for (String periodName : intRistPeriods) {
            boolean hitFlag = false;
            for (Map<String, Object> map : timeDepositsDataList) {
                if (periodName.equals(map.get("intRiskPeriod"))) {
                    Double amount = Double.parseDouble(String.valueOf(map.get("amount")));
                    timeDepositsData.add(amount);
                    hitFlag = true;
                    break;
                }
            }
            if(!hitFlag) {
                timeDepositsData.add(0D);
            }
        }

        intRiskPeriodMap.put("intRistPeriods", intRistPeriods);
        intRiskPeriodMap.put("noInterestColIndex", noInterestColIndex);
        intRiskPeriodMap.put("timeDepositsData", timeDepositsData);
        intRiskPeriodMap.put("intRistPeriodsCount", intRistPeriods.size());
        intRiskPeriodMap.put("timeDepositsCount", timeDepositsData.size());
        //========process dataMap for intRistPeriod view end========

        result.put("main", mainMap);
        result.put("related", RelatedData);
        result.put("intRiskPeriod", intRiskPeriodMap);
        return result;
    }

    /**
     * 处理sheet页C310的数据
     * @author chenhy 20231207
     *
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private Map<String,Object> getC310Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String, Object> result = new HashMap<String,Object>();

         Map<String,Object> C310MainMap = new HashMap<>();

        List<Map<String,Object>> monetaryCapitalList = new ArrayList<>();//货币资金
        List<Map<String,Object>> receivablesList = new ArrayList<>();//应收申购款

        List<Map<String,Object>> C310MainData = (List<Map<String,Object>>)this.dao.findForList("CExportMapper.selectC310MainData", queryMap);
        if(CollectionUtils.isEmpty(C310MainData)) {
            C310MainData = new ArrayList<Map<String,Object>>();
        }
        // classification
        for(Map<String,Object> map : C310MainData) {
            if("应收申购款".equals(map.get("type"))) {
                receivablesList.add(map);
            }else  {
                monetaryCapitalList.add(map);
            }
        }
        // store
        C310MainMap.put("monetaryCapitalList", monetaryCapitalList);
        C310MainMap.put("monetaryCapitalListCount", monetaryCapitalList.size());
        C310MainMap.put("receivablesList", receivablesList);

        result.put("main", C310MainMap);
        return result;
    }

    /**
     * 处理sheet页C400的数据
     * @author Dai Zong 2017年8月30日
     *
     * @param fundId
     * @param periodStr
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private Map<String,Object> getC400Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String,Object> result = new HashMap<>();

        Map<String,Object> mainData = new HashMap<>();
        Map<String,Object> bankData = new HashMap<>();
        Map<String,Object> termData = new HashMap<>();
        //========process dataMap for main view begin========
        List<Map<String,Object>> mainMetaData = (List<Map<String,Object>>)this.dao.findForList("CExportMapper.selectC400MainData", queryMap);
        if(mainMetaData == null) {
            mainMetaData = new ArrayList<Map<String,Object>>();
        }
        int mainListCount = mainMetaData.size();
        mainData.put("list", mainMetaData);
        mainData.put("count", mainListCount);
        //========process dataMap for main view end========
        //========process dataMap for test view begin========
        Map<String,Object> test = (Map<String,Object>)this.dao.findForObject("CExportMapper.selectC400TestData", queryMap);
        if(test == null) {
            test = new HashMap<>();
        }
        //========process dataMap for test view end========
        //========process dataMap for bank view begin========
        List<Map<String,Object>> bankList = (List<Map<String,Object>>)this.dao.findForList("CExportMapper.selectC400BankData", queryMap);
        if(bankList == null) {
            bankList = new ArrayList<Map<String,Object>>();
        }
        bankData.put("list", bankList);
        bankData.put("count", bankList.size());
        //========process dataMap for bank view end========
        //========process dataMap for term view begin========
        List<String> termList = (List<String>)this.dao.findForList("CExportMapper.selectC400TermData", queryMap);
        if(termList == null) {
            termList = new ArrayList<String>();
        }
        termData.put("list", termList);
        termData.put("count", termList.size());
        //========process dataMap for term view end========
        result.put("main", mainData);
        result.put("test", test);
        result.put("groupByBank", bankData);
        result.put("groupByTerm", termData);
        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<String,Object> getC10000Data(String fundId, String periodStr) throws Exception{
        Map<String, Object> queryMap = this.createBaseQueryMap(fundId, periodStr);
        Map<String,Object> result = new HashMap<>();

        Map<String,Object> demandDeposits = new HashMap<>();
        Map<String,Object> timeDeposits = new HashMap<>();
        Map<String,Object> other = new HashMap<>();
        List<Map<String, Object>> detailList = new ArrayList<>();
        //========process dataMap for main view begin========
        List<Map<String,Object>> metaData = (List<Map<String,Object>>)this.dao.findForList("CExportMapper.selectC10000MainData", queryMap);
        if(metaData == null) {metaData = new ArrayList<>();}
        for(Map<String,Object> map : metaData) {
            if("Y".equals(map.get("detailFlag"))) {
                detailList.add(map);
            }else {
                if("活期存款".equals(map.get("item"))) {
                    demandDeposits = map;
                }else if("定期存款".equals(map.get("item"))) {
                    timeDeposits = map;
                }else if("其他存款".equals(map.get("item"))) {
                    other = map;
                }
            }
        }
        result.put("demandDeposits", demandDeposits);
        result.put("timeDeposits", timeDeposits);
        result.put("other", other);
        result.put("detailList", detailList);
        result.put("detailListCount", detailList.size());

		//--------20220901,chenhy,新增各项目明细项--------
        List<Map<String,Object>> C10000ItemData = (List<Map<String,Object>>)this.dao.findForList("CExportMapper.selectC10000ItemData", queryMap);
        if(C10000ItemData == null) {
			C10000ItemData = new ArrayList<>();
		}
		Map<String,Object> C101 = new HashMap<>();
        Map<String,Object> C102 = new HashMap<>();
        Map<String,Object> C103 = new HashMap<>();
        Map<String,Object> C201 = new HashMap<>();
        Map<String,Object> C202 = new HashMap<>();
        Map<String,Object> C203 = new HashMap<>();
        Map<String,Object> C301 = new HashMap<>();
        Map<String,Object> C302 = new HashMap<>();
        Map<String,Object> C303 = new HashMap<>();
        for(Map<String,Object> map : C10000ItemData) {
            if(Integer.valueOf(101).equals(map.get("sort"))) {
                    C101 = map;
            }else {
				if(Integer.valueOf(102).equals(map.get("sort"))) {
                    C102 = map;
				}else if(Integer.valueOf(103).equals(map.get("sort"))) {
                    C103 = map;
                }else if(Integer.valueOf(201).equals(map.get("sort"))) {
                    C201 = map;
				}else if(Integer.valueOf(202).equals(map.get("sort"))) {
                    C202 = map;
				}else if(Integer.valueOf(203).equals(map.get("sort"))) {
                    C203 = map;
				}else if(Integer.valueOf(301).equals(map.get("sort"))) {
                    C301 = map;
				}else if(Integer.valueOf(302).equals(map.get("sort"))) {
                    C302 = map;
				}else if(Integer.valueOf(303).equals(map.get("sort"))) {
                    C303 = map;
				}
            }
        }
        result.put("C101", C101);
        result.put("C102", C102);
		result.put("C103", C103);
		result.put("C201", C201);
		result.put("C202", C202);
		result.put("C203", C203);
		result.put("C301", C301);
		result.put("C302", C302);
		result.put("C303", C303);
        //========process dataMap for main view end========
        return result;
    }
}
