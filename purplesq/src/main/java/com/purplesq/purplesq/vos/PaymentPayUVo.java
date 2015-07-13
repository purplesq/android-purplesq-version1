package com.purplesq.purplesq.vos;

/**
 * Created by nishant on 08/07/15.
 */
public class PaymentPayUVo {

    /*
    {
        "request": {
                "hash": "c7af2832a456eef85fffd1c48e89dacc6a4e83a5c732116abab4b7b58e970e3fe6631ecf1c426f5349ea04a17ceb0b2f74d7c47f053ec88f069bfc2d79d0311e",
                "udf1": "8d798d156fe3feb6a9c2ff2155f4760e",
                "furl": "http://dev.purplesq.com:5000/pay/confirm",
                "curl": "http://dev.purplesq.com:5000/pay/confirm",
                "surl": "http://dev.purplesq.com:5000/pay/confirm",
                "key": "IaSFVo",
                "phone": 9975851484,
                "email": "nishant@purplesq.com",
                "lastname": "Patil",
                "firstname": "Nishant",
                "productinfo": "This is a Test Event",
                "amount": 120,
                "txnid": "559bdf10fc27a88c4a7ad8d9"
        }
    }
    */

    private PaymentRequstVo request;

    public PaymentPayUVo(PaymentRequstVo request) {
        this.request = request;
    }

    public PaymentRequstVo getRequest() {
        return request;
    }

    public void setRequest(PaymentRequstVo request) {
        this.request = request;
    }


    public class PaymentRequstVo {
        private String hash;
        private String udf1;
        private String furl;
        private String curl;
        private String surl;
        private String key;
        private long phone;
        private String email;
        private String lastname;
        private String firstname;
        private String productinfo;
        private double amount;
        private String txnid;

        public PaymentRequstVo(String hash, String udf1, String furl, String curl, String surl, String key, long phone, String email, String lastname, String firstname, String productinfo, double amount, String txnid) {
            this.hash = hash;
            this.udf1 = udf1;
            this.furl = furl;
            this.curl = curl;
            this.surl = surl;
            this.key = key;
            this.phone = phone;
            this.email = email;
            this.lastname = lastname;
            this.firstname = firstname;
            this.productinfo = productinfo;
            this.amount = amount;
            this.txnid = txnid;
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public String getUdf1() {
            return udf1;
        }

        public void setUdf1(String udf1) {
            this.udf1 = udf1;
        }

        public String getFurl() {
            return furl;
        }

        public void setFurl(String furl) {
            this.furl = furl;
        }

        public String getCurl() {
            return curl;
        }

        public void setCurl(String curl) {
            this.curl = curl;
        }

        public String getSurl() {
            return surl;
        }

        public void setSurl(String surl) {
            this.surl = surl;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public long getPhone() {
            return phone;
        }

        public void setPhone(long phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getProductinfo() {
            return productinfo;
        }

        public void setProductinfo(String productinfo) {
            this.productinfo = productinfo;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getTxnid() {
            return txnid;
        }

        public void setTxnid(String txnid) {
            this.txnid = txnid;
        }
    }


}
