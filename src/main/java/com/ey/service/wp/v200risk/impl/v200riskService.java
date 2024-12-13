package com.ey.service.wp.v200risk.impl;

import com.ey.dao.DaoSupport;
import com.ey.entity.Page;
import com.ey.service.wp.v200risk.v200riskManager;
import com.ey.util.AppUtil;
import com.ey.util.PageData;
import com.ey.util.UuidUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

@Service("v200riskService")
public class v200riskService implements v200riskManager {
  @Resource(name = "daoSupport")
  private DaoSupport dao;
  
  public void save(PageData pd) throws Exception {
    this.dao.save("v200riskMapper.save", pd);
  }
  
  public void delete(PageData pd) throws Exception {
    this.dao.delete("v200riskMapper.delete", pd);
  }
  
  public void edit(PageData pd) throws Exception {
    this.dao.update("v200riskMapper.edit", pd);
  }
  
  public List<PageData> list(Page page) throws Exception {
    return (List<PageData>)this.dao.findForList("v200riskMapper.datalistPage", page);
  }
  
  public List<PageData> listAll(PageData pd) throws Exception {
    return (List<PageData>)this.dao.findForList("v200riskMapper.listAll", pd);
  }
  
  public PageData findById(PageData pd) throws Exception {
    return (PageData)this.dao.findForObject("v200riskMapper.findById", pd);
  }
  
  public void deleteAll(String[] ArrayDATA_IDS) throws Exception {
    this.dao.delete("v200riskMapper.deleteAll", ArrayDATA_IDS);
  }
  
  public void saveBatch(List<Map> maps) throws Exception {
    int idx = 1;
    List<PageData> pds = new ArrayList<>();
    for (Map<String, Object> map : maps) {
      if (null != map.get("PERIOD")) {
        PageData pd = new PageData();
        pd.put("V200RISK_ID", UuidUtil.get32UUID());
        pd.put("FUND_ID", map.get("FUND_ID"));
        pd.put("PERIOD", map.get("PERIOD"));
        pd.put("FIRM_CODE", map.get("FIRM_CODE"));
        pd.put("MMF", map.get("MMF"));
        pd.put("MAC", map.get("MAC"));
        pd.put("ACCOUNT_NUM", map.get("ACCOUNT_NUM"));
        pd.put("BOND_CODE", map.get("BOND_CODE"));
        pd.put("BOND_NAME", map.get("BOND_NAME"));
        pd.put("MARKET", map.get("MARKET"));
        pd.put("TYPE", map.get("TYPE"));
        pd.put("SUB_TYPE", map.get("SUB_TYPE"));
        pd.put("V_TYPE", map.get("V_TYPE"));
        pd.put("QUANTITY", map.get("QUANTITY"));
        pd.put("MKT_VALUE_CLIENT", map.get("MKT_VALUE_CLIENT"));
        pd.put("BOND_RATING_ORG", map.get("BOND_RATING_ORG"));
        pd.put("BOND_RATING", map.get("BOND_RATING"));
        pd.put("ENTITY_RATING_ORG", map.get("ENTITY_RATING_ORG"));
        pd.put("ENTITY_RATING", map.get("ENTITY_RATING"));
        pd.put("DURATING_NUM", map.get("DURATING_NUM"));
        pd.put("DURATION", map.get("DURATION"));
        pd.put("V_RATING", map.get("V_RATING"));
        pd.put("RATING", map.get("RATING"));
        pd.put("DESCRIPTION", map.get("DESCRIPTION"));
        pd.put("STATUS", map.get("STATUS"));
        pds.add(pd);
        idx++;
      } 
      if (idx % AppUtil.BATCH_INSERT_COUNT == 0) {
        this.dao.save("v200riskMapper.saveBatch", pds);
        pds.clear();
      } 
    } 
    if (pds.size() > 0)
      this.dao.save("v200riskMapper.saveBatch", pds); 
  }
}
