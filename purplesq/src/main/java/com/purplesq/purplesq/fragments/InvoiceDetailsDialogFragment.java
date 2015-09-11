package com.purplesq.purplesq.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.purplesq.purplesq.R;
import com.purplesq.purplesq.utils.PSQConsts;
import com.purplesq.purplesq.vos.InvoicesVo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by nishant on 09/09/15.
 */

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InvoiceDetailsDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InvoiceDetailsDialogFragment extends DialogFragment {

    private InvoicesVo invoicesVo;
    private AppCompatActivity mActivity;

    public InvoiceDetailsDialogFragment() {
        // Required empty public constructor
    }

    public static InvoiceDetailsDialogFragment newInstance(InvoicesVo invoicesVo) {
        InvoiceDetailsDialogFragment fragment = new InvoiceDetailsDialogFragment();
        fragment.invoicesVo = invoicesVo;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_invoice_dialog, container, false);

        if (mActivity == null) {
            mActivity = (AppCompatActivity) getActivity();
        }

        setupToolBar(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateUi(view);
    }

    /**
     * Set up the {@link android.support.v7.widget.Toolbar}.
     */
    private void setupToolBar(View rootView) {
        final Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.fragment_invoice_dialog_toolbar);
        if (toolbar != null) {
            mActivity.setSupportActionBar(toolbar);
            ActionBar actionBar = mActivity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle(mActivity.getString(R.string.title_fragment_invoice_details));
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            dismiss();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void updateUi(View rootView) {

        CardView cardView = (CardView) rootView.findViewById(R.id.fragment_invoice_dialog_cardview);
        TextView tvEventName = (TextView) cardView.findViewById(R.id.item_invoices_detailed_cardview_tv_eventname);
        TextView tvEventDate = (TextView) cardView.findViewById(R.id.item_invoices_detailed_cardview_tv_eventdate);
        TextView tvEventAmount = (TextView) cardView.findViewById(R.id.item_invoices_detailed_cardview_tv_eventamount);
        TextView tvEventCardName = (TextView) cardView.findViewById(R.id.item_invoices_detailed_cardview_tv_cardname);
        LinearLayout layoutParticipants = (LinearLayout) cardView.findViewById(R.id.item_invoices_detailed_cardview_layout_participants);

        tvEventName.setText(invoicesVo.getProduct().getName());

        String dateLocation = "";
        try {
            Date date = new Date(invoicesVo.getProduct().getSchedule().getStart_date());
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
            dateLocation = sdf1.format(date) + ", ";
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }

        dateLocation = dateLocation + invoicesVo.getProduct().getLocation().getCity();
        tvEventDate.setText(dateLocation);

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fontawesome.ttf");
        tvEventAmount.setTypeface(font);
        tvEventAmount.setText(PSQConsts.UNICODE_RUPEE + " " + (int) invoicesVo.getAmount().getTotal());

        tvEventCardName.setText("ADMITS " + invoicesVo.getStudents().size());

        for (int j = 0; j < invoicesVo.getStudents().size(); j++) {

            InvoicesVo.StudentsVo student = invoicesVo.getStudents().get(j);
            View participantView = getActivity().getLayoutInflater().inflate(R.layout.item_invoices_participants, layoutParticipants, false);

            ((TextView) participantView.findViewById(R.id.item_invoices_participants_tv_number)).setText((j + 1) + "");
            ((TextView) participantView.findViewById(R.id.item_invoices_participants_tv_name)).setText(student.getFname() + " " + student.getLname());

            if (student.getProfile() != null) {
                ((TextView) participantView.findViewById(R.id.item_invoices_participants_tv_insitute)).setText(student.getProfile().getInstitute());
            } else {
                participantView.findViewById(R.id.item_invoices_participants_tv_insitute).setVisibility(View.GONE);
            }

            layoutParticipants.addView(participantView);
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.FullScreenBorderlessDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setGravity(Gravity.TOP);

        return dialog;
    }


}
