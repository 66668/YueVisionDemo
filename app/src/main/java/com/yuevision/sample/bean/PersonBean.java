package com.yuevision.sample.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 统一接收数据的bean
 */

public class PersonBean implements Serializable {
    String name;
    String ids;
    List<String> faceid;
    List<String> faceimage;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public List<String> getFaceid() {
        return faceid;
    }

    public void setFaceid(List<String> faceid) {
        this.faceid = faceid;
    }

    public List<String> getFaceimage() {
        return faceimage;
    }

    public void setFaceimage(List<String> faceimage) {
        this.faceimage = faceimage;
    }
}
