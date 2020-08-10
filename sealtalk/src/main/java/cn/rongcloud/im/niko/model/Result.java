package cn.rongcloud.im.niko.model;

import cn.rongcloud.im.niko.common.ErrorCode;
import cn.rongcloud.im.niko.common.NetConstant;

/**
 * 网络请求结果基础类
 * @param <T> 请求结果的实体类
 */
public class Result<T> {
    public int RsCode;
    public T RsData;
    public String RsMsg;


    public Result(){
    }

    public Result(int code){
        this.RsCode = code;
    }

    public int getRsCode() {
        return RsCode;
    }

    public void setRsCode(int rsCode) {
        this.RsCode = rsCode;
    }

    public T getRsData() {
        return RsData;
    }

    public void setRsData(T rsData) {
        this.RsData = rsData;
    }

    public boolean isSuccess(){
        return RsCode == NetConstant.REQUEST_SUCCESS_CODE;
    }

    public String getErrorMessage(){
        return ErrorCode.fromCode(RsCode).getMessage();
    }

    public String getRsMsg() {
        return RsMsg;
    }

    public void setRsMsg(String rsMsg) {
        RsMsg = rsMsg;
    }
}
