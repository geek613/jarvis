package org.Jarvis.common.result;

import org.Jarvis.common.enums.StatusCode;

public class JarvisResult<T> {
    private Integer code;
    private String message;
    private T data;

    public JarvisResult() {}
    public JarvisResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public JarvisResult(T Data){
        this(StatusCode.SUCCESS.getCode(), StatusCode.SUCCESS.getMsg(), Data);
    }

    public JarvisResult(Integer code, String message) {
        this(code, message, null);
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> JarvisResult<T> success() {
        return new JarvisResult<T>();
    }

    public static <T> JarvisResult<T> success(T data) {
        return new JarvisResult<T>(data);
    }
    public static <T> JarvisResult<T> error(){
        return new JarvisResult<T>(StatusCode.FAIL.getCode(), StatusCode.FAIL.getMsg());
    }
    public static JarvisResult<String> error(String message) {
        return new JarvisResult<>(StatusCode.FAIL.getCode(), message);
    }


}
