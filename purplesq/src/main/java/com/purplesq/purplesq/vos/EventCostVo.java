
package com.purplesq.purplesq.vos;

/**
 * Created by nishant on 01/06/15.
 */
public class EventCostVo {
    private float base;
    private float discount;
    private float others;
    private float service;
    private float total;

    public float getBase() {
        return base;
    }

    public void setBase(float base) {
        this.base = base;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public float getOthers() {
        return others;
    }

    public void setOthers(float others) {
        this.others = others;
    }

    public float getService() {
        return service;
    }

    public void setService(float service) {
        this.service = service;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }
}
