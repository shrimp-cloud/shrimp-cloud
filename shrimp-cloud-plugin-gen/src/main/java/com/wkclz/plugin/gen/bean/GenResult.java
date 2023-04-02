package com.wkclz.plugin.gen.bean;

/**
 * Description:
 * Created: wangkaicun @ 2017-10-20 下午9:11
 */
public class GenResult<T> {

    private Integer code = -1;

    private String msg = "success";

    private T data = (T)"error";

    public GenResult() {
    }

    public GenResult(T data) {
        this.code = 1;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public T getData() {
        if (code > 0 && data != null) {
            return data;
        }
        return null;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess(){
        if (this.code != null && this.code == 1){
            return true;
        }
        return false;
    }

}
