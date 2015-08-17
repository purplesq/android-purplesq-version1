package com.purplesq.purplesq.vos;

import java.util.List;

/**
 * Created by nishant on 17/08/15.
 */
public class InvoicesVo {

    private String id;
    private String status;
    private EventCostVo amount;
    private ProductVo product;
    private CustomerVo customer;
    private List<StudentsVo> students;

    public InvoicesVo() {
    }

    public class MethodVo {
        String mode;

        public MethodVo() {
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }
    }

    public class ProductVo {
        private String id;
        private String name;
        private String type;

        public ProductVo() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public class CustomerVo {
        private String id;
        private String fname;
        private String lname;
        private String phone;
        private String email;

        public CustomerVo() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

    public ProductVo getProduct() {
        return product;
    }

    public void setProduct(ProductVo product) {
        this.product = product;
    }

    public CustomerVo getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerVo customer) {
        this.customer = customer;
    }

    public List<StudentsVo> getStudents() {
        return students;
    }

    public void setStudents(List<StudentsVo> students) {
        this.students = students;
    }
}
