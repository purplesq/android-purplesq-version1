package com.purplesq.purplesq.vos;

/**
 * Created by nishant on 25/06/15.
 */
public class ParticipantVo {

    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String institute;
    private int position = -1;
    private boolean isFixed = false;


    public ParticipantVo() {
    }

    public ParticipantVo(int position) {
        this.position = position;
    }

    public ParticipantVo(int position, String firstname, String lastname, String email, String phone, String institute) {
        this.position = position;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.institute = institute;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isFixed() {
        return isFixed;
    }

    public void setIsFixed(boolean isFixed) {
        this.isFixed = isFixed;
    }
}
