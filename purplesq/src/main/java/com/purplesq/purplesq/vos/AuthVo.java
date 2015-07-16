package com.purplesq.purplesq.vos;

/**
 * Created by nishant on 16/07/15.
 */
public class AuthVo {

    String token;
    UserVo user;
    long expiry;
    long expiry_time;

    public AuthVo() {
    }

    public AuthVo(String token, UserVo user, long expiry, long expiry_time) {
        this.token = token;
        this.user = user;
        this.expiry = expiry;
        this.expiry_time = expiry_time;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserVo getUser() {
        return user;
    }

    public void setUser(UserVo user) {
        this.user = user;
    }

    public long getExpiry() {
        return expiry;
    }

    public void setExpiry(long expiry) {
        this.expiry = expiry;
    }

    public long getExpiryTime() {
        return expiry_time;
    }

    public void setExpiryTime(long expiry_time) {
        this.expiry_time = expiry_time;
    }
}
