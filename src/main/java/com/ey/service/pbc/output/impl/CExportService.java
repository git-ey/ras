package com.ey.service.pbc.output.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
import com.ey.service.pbc.output.CExportManager;
import com.ey.util.PageData;
import com.ey.util.fileexport.Constants;
import com.ey.util.fileexport.FileExportUtils;
import com.ey.util.fileexport.FreeMarkerUtils;

/** 
 * 说明： 底稿输出服务--C
 * 创建人：daizong
 * 创建时间：2017-08-26
 * @version
 */
@Service("cExportService")
public class CExportService implements CExportManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
		
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("EyBalanceMapper.listAll", pd);
	}

    @Override
    public boolean doExport(HttpServletRequest request, HttpServletResponse response, String fundId, Long peroid) throws Exception {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        
        dataMap.put("C", this.getCData(fundId, peroid));
        
        String xmlStr = FreeMarkerUtils.processTemplateToString(dataMap, Constants.EXPORT_TEMPLATE_FOLDER_PATH, Constants.EXPORT_TEMPLATE_FILE_NAME_C);
        FileExportUtils.writeFileToHttpResponse(request, response, "C.xls", xmlStr);
        
        return true;
    }
	
    @SuppressWarnings("unchecked")
    private Map<String,Object> getCData(String fundId, Long period) throws Exception{
        Map<String, Object> queryMap = new HashMap<String,Object>();
        Map<String, Object> result = new HashMap<String,Object>();
        
        queryMap.put("fundId", fundId);
        queryMap.put("period", period);
        List<Map<String,Object>> resMapList = (List<Map<String,Object>>)dao.findForList("CExportMapper.selectCData", queryMap);
        
        for(Map<String, Object> resMap : resMapList) {
            if(resMap == null || resMap.get("accountNum") == null) {continue;}
            result.put("KM" + (String) resMap.get("accountNum"), resMap);
        }
        
        return result;
    }
}