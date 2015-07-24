package com.purplesq.purplesq.vos;

import java.util.List;

/**
 * Created by nishant on 25/06/15.
 */
public class UserVo {

//    "user": {"_id": "5589688ce372cf3202cbe71f","fname": "Nishant","lname": "Patil","email": "nishant@purplesq.com",
//              "dob": null,"qualifications": [],"phone": "9975851484","roles": [{"_id": "553e0a9599cfc9064cf1d32e","role": "normal"}],
//              "status": {"account": "Enabled","phone": "Unverified","email": "Unverified"}}

    String _id;
    String fname;
    String lname;
    String email;
    String dob;
    //TODO : DoB to long
    List<String> qualifications;
    String phone;
    String gender;
    String imageurl;
    List<UserRolesVo> roles;
    UserStatusVo status;
    String institute;

    public class UserRolesVo {
        String _id;
        String role;

        public UserRolesVo(String _id, String role) {
            this._id = _id;
            this.role = role;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
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

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public List<String> getQualifications() {
        return qualifications;
    }

    public void setQualifications(List<String> qualifications) {
        this.qualifications = qualifications;
    }

    public List<UserRolesVo> getRoles() {
        return roles;
    }

    public void setRoles(List<UserRolesVo> roles) {
        this.roles = roles;
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
}
