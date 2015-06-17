package com.purplesq.purplesq.vos;

/**
 * Created by nishant on 01/06/15.
 */
public class RegistrationVo {
    private Relative relative;
    private Absolute absolute;

    public Relative getRelative() {
        return relative;
    }

    public void setRelative(Relative relative) {
        this.relative = relative;
    }

    public Absolute getAbsolute() {
        return absolute;
    }

    public void setAbsolute(Absolute absolute) {
        this.absolute = absolute;
    }

    
    public class Relative {
        private String until;
        private String since;

        public String getUntil() {
            return until;
        }

        public void setUntil(String until) {
            this.until = until;
        }

        public String getSince() {
            return since;
        }

        public void setSince(String since) {
            this.since = since;
        }
    }

    public class Absolute {
        private String until;
        private String since;

        public String getUntil() {
            return until;
        }

        public void setUntil(String until) {
            this.until = until;
        }

        public String getSince() {
            return since;
        }

        public void setSince(String since) {
            this.since = since;
        }
    }
}
