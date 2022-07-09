package com.wkclz.redis.entity;

public class RedisMsgBody<T>  {

    private String tag;

    private T msg;

    public RedisMsgBody(){
    }

    public RedisMsgBody(String tag, T msg){
        this.tag = tag;
        this.msg = msg;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public T getMsg() {
        return msg;
    }

    public void setMsg(T msg) {
        this.msg = msg;
    }

}
