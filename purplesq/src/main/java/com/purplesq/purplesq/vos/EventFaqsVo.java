
package com.purplesq.purplesq.vos;

/**
 * Created by nishant on 01/06/15.
 */
public class EventFaqsVo {
    private String _id;
    private String ans;
    private String q;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getFaqAns() {
        return ans;
    }

    public void setFaqAns(String ans) {
        this.ans = ans;
    }

    public String getFaqQue() {
        return q;
    }

    public void setFaqQue(String que) {
        this.q = que;
    }
}
