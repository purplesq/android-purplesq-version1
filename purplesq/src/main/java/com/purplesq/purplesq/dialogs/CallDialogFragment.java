package com.purplesq.purplesq.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.purplesq.purplesq.R;
import com.purplesq.purplesq.activities.NavigationDrawerActivity;

/**
 * Created by nishant on 25/08/15.
 */
public class CallDialogFragment extends DialogFragment {

    public static final String CALL_TYPE_EVENT = "call_type_event";
    public static final String CALL_TYPE_QUERY = "call_type_query";
    private String mType;

    public CallDialogFragment() {
        // Required empty public constructor
    }

    public static CallDialogFragment newInstance(String type) {
        CallDialogFragment fragment = new CallDialogFragment();
        fragment.mType = type;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_dialog_calls, container, false);
        getDialog().setCanceledOnTouchOutside(false);

        TextView tvTitle = (TextView) rootView.findViewById(R.id.fragment_dialog_calls_tv_title);
        TextView tvContent = (TextView) rootView.findViewById(R.id.fragment_dialog_calls_tv_content);
        Button btnCall = (Button) rootView.findViewById(R.id.fragment_dialog_calls_btn_call);
        Button btnCancel = (Button) rootView.findViewById(R.id.fragment_dialog_calls_btn_cancel);

        if (mType.equalsIgnoreCase(CALL_TYPE_EVENT)) {
            tvTitle.setText("Call for Event Queries");
            tvContent.setText("This call will charge you as per your service providers plan.");
            btnCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                        phoneIntent.setData(Uri.parse("tel:+918080809339"));
                        startActivity(phoneIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Crashlytics.logException(ex);
                    }
                    dismiss();
                }
            });
        } else if (mType.equalsIgnoreCase(CALL_TYPE_QUERY)) {
            tvTitle.setText("Call for General Queries");
            tvContent.setText("This call will charge you as per your service providers plan.");
            btnCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                        phoneIntent.setData(Uri.parse("tel:+912261491313"));
                        startActivity(phoneIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        ex.printStackTrace();
                        Crashlytics.logException(ex);
                    }

                    if (getActivity() instanceof NavigationDrawerActivity) {
                        ((NavigationDrawerActivity)getActivity()).selectDefaultPage();
                    }
                    dismiss();

                }
            });
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);

        return dialog;
    }


}
