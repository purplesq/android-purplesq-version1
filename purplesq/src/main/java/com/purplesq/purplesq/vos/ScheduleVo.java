package com.purplesq.purplesq.vos;

/**
 * Created by nishant on 01/06/15.
 */
public class ScheduleVo {
    private String end_date;
    private String start_date;
    private RegistrationVo registration;

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public RegistrationVo getRegistration() {
        return registration;
    }

    public void setRegistration(RegistrationVo registration) {
        this.registration = registration;
    }
}
