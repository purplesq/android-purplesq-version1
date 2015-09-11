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

import com.crashlytics.android.Crashlytics;
import com.purplesq.purplesq.R;
import com.purplesq.purplesq.application.PurpleSQ;
import com.purplesq.purplesq.datamanagers.AuthDataManager;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.tasks.InvoicesTask;
import com.purplesq.purplesq.utils.PSQConsts;
import com.purplesq.purplesq.vos.AuthVo;
import com.purplesq.purplesq.vos.ErrorVo;
import com.purplesq.purplesq.vos.InvoicesVo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InvoicesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InvoicesFragment extends Fragment implements GenericAsyncTaskListener {

    private LinearLayout transactionsLayout;
    private FragmentActivity mActivity;
    private List<InvoicesVo> mInvoices;
    private InvoiceDetailsDialogFragment invoiceDetailsDialogFragment;

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

    @Override
    public void onResume() {
        super.onResume();
        if (invoiceDetailsDialogFragment != null && !invoiceDetailsDialogFragment.isVisible()) {
            invoiceDetailsDialogFragment = null;
        }
    }

    private void populateTransactios() {
        transactionsLayout.removeAllViews();

        for (int i = 0; i < mInvoices.size(); i++) {
            final int position = i;

            InvoicesVo invoice = mInvoices.get(i);
            CardView cardView = (CardView) getActivity().getLayoutInflater().inflate(R.layout.item_invoices_cardview, transactionsLayout, false);
            TextView tvEventName = (TextView) cardView.findViewById(R.id.item_invoices_cardview_tv_eventname);
            TextView tvEventDate = (TextView) cardView.findViewById(R.id.item_invoices_cardview_tv_eventdate);
            TextView tvEventAmount = (TextView) cardView.findViewById(R.id.item_invoices_cardview_tv_eventamount);
            TextView tvEventCardName = (TextView) cardView.findViewById(R.id.item_invoices_cardview_tv_cardname);
//            LinearLayout layoutParticipants = (LinearLayout) cardView.findViewById(R.id.item_invoices_cardview_layout_participants);

            tvEventName.setText(invoice.getProduct().getName());

            String dateLocation = "";
            try {
                Date date = new Date(invoice.getProduct().getSchedule().getStart_date());
                SimpleDateFormat sdf1 = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
                dateLocation = sdf1.format(date) + ", ";
            } catch (Exception e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }

            dateLocation = dateLocation + invoice.getProduct().getLocation().getCity();
            tvEventDate.setText(dateLocation);

            Typeface font = Typeface.createFromAsset(mActivity.getAssets(), "fontawesome.ttf");
            tvEventAmount.setTypeface(font);
            tvEventAmount.setText(PSQConsts.UNICODE_RUPEE + " " + (int) invoice.getAmount().getTotal());

            tvEventCardName.setText("ADMITS " + invoice.getStudents().size());

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cardClicked(position);
                }
            });

            transactionsLayout.addView(cardView);
        }
    }

    private void cardClicked(int position) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag(PSQConsts.DIALOG_FRAGMENT_INVOICES);
        if (prev != null) {
            ft.remove(prev);
        }

        invoiceDetailsDialogFragment = InvoiceDetailsDialogFragment.newInstance(mInvoices.get(position));
        invoiceDetailsDialogFragment.show(ft, PSQConsts.DIALOG_FRAGMENT_INVOICES);
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

    public boolean homeButtonPressed() {
        if (invoiceDetailsDialogFragment != null) {
            if (invoiceDetailsDialogFragment.isVisible()) {
                invoiceDetailsDialogFragment.dismiss();
                invoiceDetailsDialogFragment = null;
                return true;
            }
        }

        return false;
    }
}
