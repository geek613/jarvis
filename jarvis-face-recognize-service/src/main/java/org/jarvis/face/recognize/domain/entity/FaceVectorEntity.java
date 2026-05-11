package org.jarvis.face.recognize.domain.entity;

import java.util.List;

public class FaceVectorEntity {
    /** 人脸特征向量 **/
    private List<Float> vector;
    /** 维度 **/
    private Integer dimension;

    public List<Float> getVector() {
        return vector;
    }

    public void setVector(List<Float> vector) {
        this.vector = vector;
    }

    public Integer getDimension() {
        return dimension;
    }

    public void setDimension(Integer dimension) {
        this.dimension = dimension;
    }
}
