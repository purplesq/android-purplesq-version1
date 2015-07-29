package com.purplesq.purplesq.vos;

/**
 * Created by nishant on 25/06/15.
 */
public class UserVo {

    String _id;
    String fname;
    String lname;
    String email;
    long dob;
    //TODO : DoB to long
    String phone;
    String gender;
    String imageurl;
    UserStatusVo status;
    String institute;

    public UserVo() {
    }

    public UserVo(String id, String firstName, String lastName, String email, String phone, String institute) {
        this._id = id;
        this.fname = firstName;
        this.lname = lastName;
        this.email = email;
        this.phone = phone;
        this.institute = institute;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getFirstName() {
        return fname;
    }

    public void setFirstName(String firstName) {
        this.fname = firstName;
    }

    public String getLastName() {
        return lname;
    }

    public void setLastName(String lastName) {
        this.lname = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public long getDob() {
        return dob;
    }

    public void setDob(long dob) {
        this.dob = dob;
    }

    public UserStatusVo getStatus() {
        return status;
    }

    public void setStatus(UserStatusVo status) {
        this.status = status;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public class UserStatusVo {
        String account;
        String phone;
        String email;

        public UserStatusVo(String account, String phone, String email) {
            this.account = account;
            this.phone = phone;
            this.email = email;
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
