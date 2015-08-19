package com.purplesq.purplesq.fragments;


import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.purplesq.purplesq.R;
import com.purplesq.purplesq.application.PurpleSQ;
import com.purplesq.purplesq.datamanagers.AuthDataManager;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.tasks.InvoicesTask;
import com.purplesq.purplesq.utils.PSQConsts;
import com.purplesq.purplesq.vos.AuthVo;
import com.purplesq.purplesq.vos.ErrorVo;
import com.purplesq.purplesq.vos.InvoicesVo;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InvoicesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InvoicesFragment extends Fragment implements GenericAsyncTaskListener {

    private LinearLayout transactionsLayout;
    private FragmentActivity mActivity;
    private List<InvoicesVo> mInvoices;

    public static InvoicesFragment newInstance() {
        InvoicesFragment fragment = new InvoicesFragment();
        return fragment;
    }

    public InvoicesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_invoices, container, false);

        if (mActivity == null) {
            mActivity = getActivity();
        }

        transactionsLayout = (LinearLayout) rootView.findViewById(R.id.fragment_invoices_layout);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
    }

    @Override
    public void onStart() {
        super.onStart();
        AuthVo authVo = AuthDataManager.getAuthData(mActivity);
        if (authVo != null) {
            PurpleSQ.showLoadingDialog(mActivity);
            new InvoicesTask(authVo.getToken(), InvoicesFragment.this).execute((Void) null);
        }
    }

    private void populateTransactios() {
        transactionsLayout.removeAllViews();

        for (int i = 0; i < mInvoices.size(); i++) {

            InvoicesVo invoice = mInvoices.get(i);
            CardView cardView = (CardView) getActivity().getLayoutInflater().inflate(R.layout.item_invoices_cardview, transactionsLayout, false);
            TextView tvEventName = (TextView) cardView.findViewById(R.id.item_invoices_cardview_tv_eventname);
            TextView tvEventAmount = (TextView) cardView.findViewById(R.id.item_invoices_cardview_tv_eventamount);
            LinearLayout layoutParticipants = (LinearLayout) cardView.findViewById(R.id.item_invoices_cardview_layout_participants);

            tvEventName.setText(invoice.getProduct().getName());

            Typeface font = Typeface.createFromAsset(mActivity.getAssets(), "fontawesome.ttf");
            tvEventAmount.setTypeface(font);
            tvEventAmount.setText(PSQConsts.UNICODE_RUPEE + " " + (int) invoice.getAmount().getTotal());

            for (int j = 0; j < invoice.getStudents().size(); j++) {

                InvoicesVo.StudentsVo student = invoice.getStudents().get(j);
                View participantView = getActivity().getLayoutInflater().inflate(R.layout.item_payment_participants, layoutParticipants, false);

                ((TextView) participantView.findViewById(R.id.item_payment_participants_tv_number)).setText((j + 1) + "");
                ((TextView) participantView.findViewById(R.id.item_payment_participants_tv_name)).setText(student.getFname() + " " + student.getLname());

                if (student.getProfile() != null) {
                    ((TextView) participantView.findViewById(R.id.item_payment_participants_tv_insitute)).setText(student.getProfile().getInstitute());
                } else {
                    participantView.findViewById(R.id.item_payment_participants_tv_insitute).setVisibility(View.GONE);
                }

                layoutParticipants.addView(participantView);
            }

            transactionsLayout.addView(cardView);
        }
    }

    @Override
    public void genericAsyncTaskOnSuccess(Object obj) {
        if (obj instanceof List) {
            mInvoices = (List<InvoicesVo>) obj;
            populateTransactios();
        }

        if (PurpleSQ.isLoadingDialogVisible()) {
            PurpleSQ.dismissLoadingDialog();
        }
    }

    @Override
    public void genericAsyncTaskOnError(Object obj) {
        if (PurpleSQ.isLoadingDialogVisible()) {
            PurpleSQ.dismissLoadingDialog();
        }
        if (obj instanceof ErrorVo) {
            ErrorVo errorVo = (ErrorVo) obj;

            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag(PSQConsts.DIALOG_FRAGMENT_ERROR);
            if (prev != null) {
                ft.remove(prev);
            }

            ErrorDialogFragment errorDialogFragment = ErrorDialogFragment.newInstance(errorVo);
            errorDialogFragment.show(ft, "error_dialog");
        }
    }

    @Override
    public void genericAsyncTaskOnProgress(Object obj) {

    }

    @Override
    public void genericAsyncTaskOnCancelled(Object obj) {
        if (PurpleSQ.isLoadingDialogVisible()) {
            PurpleSQ.dismissLoadingDialog();
        }
    }
}
