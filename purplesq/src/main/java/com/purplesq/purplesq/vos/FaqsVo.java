
package com.purplesq.purplesq.vos;

/**
 * Created by nishant on 01/06/15.
 */
public class FaqsVo {
    private String _id;
    private FaqAnswerVo ans;
    private String q;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public FaqAnswerVo getFaqAns() {
        return ans;
    }

    public void setFaqAns(FaqAnswerVo ans) {
        this.ans = ans;
    }

    public String getFaqQue() {
        return q;
    }

    public void setFaqQue(String que) {
        this.q = que;
    }
}
