package com.purplesq.purplesq.vos;

import java.util.List;

/**
 * Created by nishant on 19/09/15.
 */
public class CouponsVo {
    private int trigger_amount;
    private List<OffersVo> offers;

    public CouponsVo() {
    }

    public CouponsVo(int trigger_amount, List<OffersVo> offers) {
        this.trigger_amount = trigger_amount;
        this.offers = offers;
    }

    public int getTrigger_amount() {
        return trigger_amount;
    }

    public void setTrigger_amount(int trigger_amount) {
        this.trigger_amount = trigger_amount;
    }

    public List<OffersVo> getOffers() {
        return offers;
    }

    public void setOffers(List<OffersVo> offers) {
        this.offers = offers;
    }

    public class OffersVo {
        private int discount;
        private String _id;
        private String type;

        public OffersVo() {
        }

        public OffersVo(int discount, String _id, String type) {
            this.discount = discount;
            this._id = _id;
            this.type = type;
        }

        public int getDiscount() {
            return discount;
        }

        public void setDiscount(int discount) {
            this.discount = discount;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
