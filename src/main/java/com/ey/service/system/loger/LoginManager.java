package com.ey.service.system.loger;

import com.ey.util.PageData;

/** 
 * 说明： 操作日志记录接口
 */
public interface LoginManager{

	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(String USERNAME,String loginDate)throws Exception;
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(String USERNAME)throws Exception;
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void update(String USERNAME,String loginDate)throws Exception;
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(String USERNAME)throws Exception;
	
}

