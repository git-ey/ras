package com.ey.controller.system.head;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.shiro.session.Session;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ey.controller.base.BaseController;
import com.ey.service.system.user.UserManager;
import com.ey.util.AppUtil;
import com.ey.util.Const;
import com.ey.util.Jurisdiction;
import com.ey.util.PageData;
import com.ey.util.Tools;

/** 
 * 类名称：HeadController
 */
@Controller
@RequestMapping(value="/head")
public class HeadController extends BaseController {
	
	@Resource(name="userService")
	private UserManager userService;	
	
	/**去编辑头像页面
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/editPhoto")
	public ModelAndView editPhoto() throws Exception{
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		mv.setViewName("system/userphoto/userphoto_edit");
		mv.addObject("pd", pd);
		return mv;
	}
	
	/**获取头部信息
	 * @return
	 */
	@RequestMapping(value="/getList")
	@ResponseBody
	public Object getList() {
		PageData pd = new PageData();
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			pd = this.getPageData();
			List<PageData> pdList = new ArrayList<PageData>();
			Session session = Jurisdiction.getSession();
			PageData pds = new PageData();
			pds = (PageData)session.getAttribute(Const.SESSION_userpds);
			if(null == pds){
				pd.put("USERNAME", Jurisdiction.getUsername());//当前登录者用户名
				pds = userService.findByUsername(pd);
				session.setAttribute(Const.SESSION_userpds, pds);
			}
			pdList.add(pds);
			map.put("list", pdList);
		} catch (Exception e) {
			logger.error(e.toString(), e);
		} finally {
			logAfter(logger);
		}
		return AppUtil.returnObject(pd, map);
	}

	/**去系统设置页面
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/goSystem")
	public ModelAndView goEditEmail() throws Exception{
		if(!"admin".equals(Jurisdiction.getUsername())){return null;}	//非admin用户不能修改
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("YSYNAME", Tools.readTxtFile(Const.SYSNAME));	 //读取系统名称
		pd.put("COUNTPAGE", Tools.readTxtFile(Const.PAGE));		 //读取每页条数
		mv.setViewName("system/head/sys_edit");
		mv.addObject("pd", pd);
		return mv;
	}
	
	/**保存系统设置
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/saveSys")
	public ModelAndView saveSys() throws Exception{
		if(!"admin".equals(Jurisdiction.getUsername())){return null;}	//非admin用户不能修改
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Tools.writeFile(Const.SYSNAME,pd.getString("YSYNAME"));	//写入系统名称
		Tools.writeFile(Const.PAGE,pd.getString("COUNTPAGE"));	//写入每页条数
		mv.addObject("msg","OK");
		mv.setViewName("save_result");
		return mv;
	}
	
}