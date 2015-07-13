package com.purplesq.purplesq.vos;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nishant on 07/07/15.
 */
public class TransactionVo {
    String id;
    String status;
    String email;
    long phone = -1;
    String mode;
    float amount = -1;

    public TransactionVo(String id, String status, String email, long phone, String mode, float amount) {
        this.id = id;
        this.status = status;
        this.email = email;
        this.phone = phone;
        this.mode = mode;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        try {
            JSONObject jsonTransaction = new JSONObject();
            if (!TextUtils.isEmpty(id)) {
                jsonTransaction.put("id", id);
            }
            if (!TextUtils.isEmpty(status)) {
                jsonTransaction.put("status", status);
            }
            if (!TextUtils.isEmpty(email)) {
                jsonTransaction.put("email", email);
            }
            if (phone >= 0) {
                jsonTransaction.put("phone", phone);
            }
            if (!TextUtils.isEmpty(mode)) {
                jsonTransaction.put("mode", mode);
            }
            if (amount >= 0) {
                jsonTransaction.put("amount", amount);
            }

            return jsonTransaction.toString();

        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}
