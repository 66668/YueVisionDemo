package com.yuevision.sample.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 图片比对结果bean
 */

public class ImageResultBean implements Serializable {
    String code;
    String message;
    List<Img> result;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Img> getResult() {
        return result;
    }

    public void setResult(List<Img> result) {
        this.result = result;
    }

   public class Img implements Serializable {
        float Score;
        String FaceId;

        public float getScore() {
            return Score;
        }

        public void setScore(float score) {
            Score = score;
        }

        public String getFaceId() {
            return FaceId;
        }

        public void setFaceId(String faceId) {
            FaceId = faceId;
        }
    }

}
