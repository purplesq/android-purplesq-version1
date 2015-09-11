package com.purplesq.purplesq.vos;

import java.util.List;

/**
 * Created by nishant on 17/08/15.
 */
public class InvoicesVo {

    private String id;
    private String status;
    private EventCostVo amount;
    private EventsVo product;
    private List<StudentsVo> students;
    private long created_at;

    public InvoicesVo() {
    }

    public class StudentsVo {
        private String fname;
        private String lname;
        private String phone;
        private String email;
        private ProfileVo profile;

        public class ProfileVo {
            private String institute;

            public ProfileVo() {
            }

            public String getInstitute() {
                return institute;
            }

            public void setInstitute(String institute) {
                this.institute = institute;
            }
        }

        public StudentsVo() {
        }

        public String getFname() {
            return fname;
        }

        public void setFname(String fname) {
            this.fname = fname;
        }

        public String getLname() {
            return lname;
        }

        public void setLname(String lname) {
            this.lname = lname;
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

        public ProfileVo getProfile() {
            return profile;
        }

        public void setProfile(ProfileVo profile) {
            this.profile = profile;
        }
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

    public EventCostVo getAmount() {
        return amount;
    }

    public void setAmount(EventCostVo amount) {
        this.amount = amount;
    }

    public EventsVo getProduct() {
        return product;
    }

    public void setProduct(EventsVo product) {
        this.product = product;
    }

    public List<StudentsVo> getStudents() {
        return students;
    }

    public void setStudents(List<StudentsVo> students) {
        this.students = students;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }
}
