package com.ey.service.wp.v200risk;

import com.ey.entity.Page;
import com.ey.util.PageData;
import java.util.List;
import java.util.Map;

public interface v200riskManager {
  void save(PageData paramPageData) throws Exception;
  
  void delete(PageData paramPageData) throws Exception;
  
  void edit(PageData paramPageData) throws Exception;
  
  List<PageData> list(Page paramPage) throws Exception;
  
  List<PageData> listAll(PageData paramPageData) throws Exception;
  
  PageData findById(PageData paramPageData) throws Exception;
  
  void deleteAll(String[] paramArrayOfString) throws Exception;
  
  void saveBatch(List<Map> paramList) throws Exception;
}
