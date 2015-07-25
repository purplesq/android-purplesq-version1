package com.purplesq.purplesq.vos;

/**
 * Created by nishant on 16/07/15.
 */
public class AuthVo {

    String token;
    UserVo user;
    long expiry;
    long expiryTime;

    public AuthVo() {
    }

    public AuthVo(String token, UserVo user, long expiry, long expiryTime) {
        this.token = token;
        this.user = user;
        this.expiry = expiry;
        this.expiryTime = expiryTime;
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
        return expiryTime;
    }

    public void setExpiryTime(long expiryTime) {
        this.expiryTime = expiryTime;
    }
}
