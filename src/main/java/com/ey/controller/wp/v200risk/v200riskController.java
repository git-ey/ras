package com.ey.controller.wp.v200risk;

import com.ey.controller.base.BaseController;
import com.ey.entity.Page;
import com.ey.service.system.loger.LogerManager;
import com.ey.service.wp.v200risk.v200riskManager;
import com.ey.util.AppUtil;
import com.ey.util.FileDownload;
import com.ey.util.Jurisdiction;
import com.ey.util.ObjectExcelView;
import com.ey.util.PageData;
import com.ey.util.PathUtil;
import com.ey.util.fileimport.MapResult;
import java.beans.PropertyEditor;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

@Controller
@RequestMapping({"/v200risk"})
public class v200riskController extends BaseController {
  String menuUrl = "v200risk/list.do";
  
  @Resource(name = "v200riskService")
  private v200riskManager v200riskService;
  
  @Resource(name = "logService")
  private LogerManager logManager;
  
  @RequestMapping({"/save"})
  public ModelAndView save() throws Exception {
    logBefore(this.logger, Jurisdiction.getUsername() + "新增v200risk");
    if (!Jurisdiction.buttonJurisdiction(this.menuUrl, "add"))
      return null; 
    ModelAndView mv = getModelAndView();
    PageData pd = new PageData();
    pd = getPageData();
    pd.put("v200risk_ID", get32UUID());
    pd.put("ACTIVE", "Y");
    pd.put("STATUS", "");
    this.v200riskService.save(pd);
    mv.addObject("msg", "success");
    mv.setViewName("save_result");
    return mv;
  }
  
  @RequestMapping({"/delete"})
  public void delete(PrintWriter out) throws Exception {
    logBefore(this.logger, Jurisdiction.getUsername() + "删除v200risk");
    if (!Jurisdiction.buttonJurisdiction(this.menuUrl, "del"))
      return; 
    PageData pd = new PageData();
    pd = getPageData();
    this.v200riskService.delete(pd);
    out.write("success");
    out.close();
  }
  
  @RequestMapping({"/edit"})
  public ModelAndView edit() throws Exception {
    logBefore(this.logger, Jurisdiction.getUsername() + "修改v200risk");
    if (!Jurisdiction.buttonJurisdiction(this.menuUrl, "edit"))
      return null; 
    ModelAndView mv = getModelAndView();
    PageData pd = new PageData();
    pd = getPageData();
    this.v200riskService.edit(pd);
    mv.addObject("msg", "success");
    mv.setViewName("save_result");
    return mv;
  }
  
  @RequestMapping({"/list"})
  public ModelAndView list(Page page) throws Exception {
    logBefore(this.logger, Jurisdiction.getUsername() + "列表v200risk");
    ModelAndView mv = getModelAndView();
    PageData pd = new PageData();
    pd = getPageData();
    String keywords = pd.getString("keywords");
    if (null != keywords && !"".equals(keywords))
      pd.put("keywords", keywords.trim()); 
    page.setPd(pd);
    List<PageData> varList = this.v200riskService.list(page);
    mv.setViewName("wp/v200risk/v200risk_list");
    mv.addObject("varList", varList);
    mv.addObject("pd", pd);
    mv.addObject("QX", Jurisdiction.getHC());
    return mv;
  }
  
  @RequestMapping({"/goAdd"})
  public ModelAndView goAdd() throws Exception {
    ModelAndView mv = getModelAndView();
    PageData pd = new PageData();
    pd = getPageData();
    mv.setViewName("wp/v200risk/v200risk_edit");
    mv.addObject("msg", "save");
    mv.addObject("pd", pd);
    return mv;
  }
  
  @RequestMapping({"/goEdit"})
  public ModelAndView goEdit() throws Exception {
    ModelAndView mv = getModelAndView();
    PageData pd = new PageData();
    pd = getPageData();
    pd = this.v200riskService.findById(pd);
    mv.setViewName("wp/v200risk/v200risk_edit");
    mv.addObject("msg", "edit");
    mv.addObject("pd", pd);
    return mv;
  }
  
  @RequestMapping({"/deleteAll"})
  @ResponseBody
  public Object deleteAll() throws Exception {
    logBefore(this.logger, Jurisdiction.getUsername() + "批量删除");
    if (!Jurisdiction.buttonJurisdiction(this.menuUrl, "del"))
      return null; 
    PageData pd = new PageData();
    Map<String, Object> map = new HashMap<>();
    pd = getPageData();
    List<PageData> pdList = new ArrayList<>();
    String DATA_IDS = pd.getString("DATA_IDS");
    if (null != DATA_IDS && !"".equals(DATA_IDS)) {
      String[] ArrayDATA_IDS = DATA_IDS.split(",");
      this.v200riskService.deleteAll(ArrayDATA_IDS);
      pd.put("msg", "ok");
    } else {
      pd.put("msg", "no");
    } 
    pdList.add(pd);
    map.put("list", pdList);
    return AppUtil.returnObject(pd, map);
  }
  
  @RequestMapping({"/goUploadExcel"})
  public ModelAndView goUploadExcel() throws Exception {
    ModelAndView mv = getModelAndView();
    mv.setViewName("wp/v200risk/uploadexcel");
    return mv;
  }
  
  @RequestMapping({"/downExcel"})
  public void downExcel(HttpServletResponse response) throws Exception {
    FileDownload.fileDownload(response, PathUtil.getClasspath() + "uploadFiles/file/" + "i_refinancing.xlsx", "i_refinancing.xlsx");
  }
  
  @RequestMapping({"/readExcel"})
  public ModelAndView readExcel(@RequestParam(value = "excel", required = false) MultipartFile file) throws Exception {
    this.logManager.save(Jurisdiction.getUsername(), "从EXCEL导入表信用风险评级清单到数据库");
    ModelAndView mv = getModelAndView();
    if (!Jurisdiction.buttonJurisdiction(this.menuUrl, "add"))
      return null; 
    MapResult mapResult = readExcel(file, "EVR");
    List<Map> maps = mapResult.getResult();
    this.v200riskService.saveBatch(maps);
    mv.addObject("msg", "success");
    mv.setViewName("save_result");
    return mv;
  }
  
  @RequestMapping({"/excel"})
  public ModelAndView exportExcel() throws Exception {
    logBefore(this.logger, Jurisdiction.getUsername() + "导出v200risk到excel");
    if (!Jurisdiction.buttonJurisdiction(this.menuUrl, "cha"))
      return null; 
    ModelAndView mv = new ModelAndView();
    PageData pd = new PageData();
    pd = getPageData();
    Map<String, Object> dataMap = new HashMap<>();
    List<String> titles = new ArrayList<>();
    titles.add("公司代码");
    titles.add("基金代码");
    titles.add("期间");
    titles.add("期货基金");
    titles.add("摊余成本法");
    titles.add("科目代码");
    titles.add("债券代码");
    titles.add("债券简称");
    titles.add("市场");
    titles.add("类型");
    titles.add("子类型");
    titles.add("V200分类");
    titles.add("数量");
    titles.add("市值perClient");
    titles.add("最新债券评级机构");
    titles.add("最新债券评级");
    titles.add("发债主体最新评级机构");
    titles.add("发债主体最新评级");
    titles.add("期限值");
    titles.add("期限分类");
    titles.add("评级取值");
    titles.add("评级分类");
    titles.add("备注");
    titles.add("状态");
    dataMap.put("titles", titles);
    List<PageData> varOList = this.v200riskService.listAll(pd);
    List<PageData> varList = new ArrayList<>();
    for (int i = 0; i < varOList.size(); i++) {
      PageData vpd = new PageData();
      vpd.put("var1", ((PageData)varOList.get(i)).getString("FIRM_CODE"));
      vpd.put("var2", ((PageData)varOList.get(i)).getString("FUND_ID"));
      vpd.put("var3", ((PageData)varOList.get(i)).getString("PERIOD"));
      vpd.put("var4", ((PageData)varOList.get(i)).getString("MMF"));
      vpd.put("var5", ((PageData)varOList.get(i)).getString("MAC"));
      vpd.put("var6", ((PageData)varOList.get(i)).getString("ACCOUNT_NUM"));
      vpd.put("var7", ((PageData)varOList.get(i)).getString("BOND_CODE"));
      vpd.put("var8", ((PageData)varOList.get(i)).getString("BOND_NAME"));
      vpd.put("var9", ((PageData)varOList.get(i)).getString("MARKET"));
      vpd.put("var10", ((PageData)varOList.get(i)).getString("TYPE"));
      vpd.put("var11", ((PageData)varOList.get(i)).getString("SUB_TYPE"));
      vpd.put("var12", ((PageData)varOList.get(i)).get("V_TYPE").toString());
      vpd.put("var10", ((PageData)varOList.get(i)).getString("QUANTITY"));
      vpd.put("var11", ((PageData)varOList.get(i)).getString("MKT_VALUE_CLIENT"));
      vpd.put("var12", ((PageData)varOList.get(i)).get("BOND_RATING_ORG").toString());
      vpd.put("var10", ((PageData)varOList.get(i)).getString("BOND_RATING"));
      vpd.put("var11", ((PageData)varOList.get(i)).getString("ENTITY_RATING_ORG"));
      vpd.put("var12", ((PageData)varOList.get(i)).get("ENTITY_RATING").toString());
      vpd.put("var10", ((PageData)varOList.get(i)).getString("DURATING_NUM"));
      vpd.put("var11", ((PageData)varOList.get(i)).getString("DURATION"));
      vpd.put("var12", ((PageData)varOList.get(i)).get("V_RATING").toString());
      vpd.put("var10", ((PageData)varOList.get(i)).getString("RATING"));
      vpd.put("var11", ((PageData)varOList.get(i)).getString("DESCRIPTION"));
      vpd.put("var12", ((PageData)varOList.get(i)).get("STATUS").toString());
      varList.add(vpd);
    } 
    dataMap.put("varList", varList);
    ObjectExcelView erv = new ObjectExcelView();
    mv = new ModelAndView((View)erv, dataMap);
    return mv;
  }
  
  @InitBinder
  public void initBinder(WebDataBinder binder) {
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    binder.registerCustomEditor(Date.class, (PropertyEditor)new CustomDateEditor(format, true));
  }
}
