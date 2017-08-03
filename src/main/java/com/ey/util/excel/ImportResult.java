package com.ey.util.excel;

import java.util.List;

/**
 * 导入结果类
 * @author andyChen
 * @param <E>
 */
public abstract class ImportResult<E> extends BaseModel {
    public abstract List<E> getResult();
    private String resMsg;//返回的信息
    public String getResMsg() {
        return resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg;
    }

}