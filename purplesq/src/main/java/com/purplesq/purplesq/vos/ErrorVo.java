package com.purplesq.purplesq.vos;

/**
 * Created by nishant on 29/07/15.
 */
public class ErrorVo {
    private int code;
    private String message;
    private String body;

    public ErrorVo() {

    }

    public ErrorVo(int code, String message, String body) {
        this.code = code;
        this.message = message;
        this.body = body;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
