package com.yuevision.sample.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 统一接收数据的bean
 */

public class GetMessagBean<T> implements Serializable {
    String code;
    String msg;
    String group_id;
    List<T> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
