package com.yuevision.sample.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 图片比对结果bean
 */

public class ImageResultBean implements Serializable {
    String Code;
    String Message;
    List<Img> Result;

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public List<Img> getResult() {
        return Result;
    }

    public void setResult(List<Img> result) {
        Result = result;
    }

    class Img implements Serializable {
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
